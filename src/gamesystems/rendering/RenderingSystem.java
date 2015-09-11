package gamesystems.rendering;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_9;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DECR;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_INCR;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilOp;

import java.util.HashSet;
import java.util.Set;

import application.Uncountable;
import gamesystems.GameSystem;
import world.Module;
import world.setpieces.Portal;

public class RenderingSystem extends GameSystem {

    private int m_maxDepth = 5;
    private boolean m_useNewRenderer = false;
    private boolean m_portalDebug = true;
    private boolean m_stencilOn = true;
    private Set<Module> m_visitedModules = new HashSet<Module>();

    public RenderingSystem() {
        super();
    }

    @Override
    public void tick(float seconds) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        if(m_useNewRenderer) {
            try {
                if(m_stencilOn) glEnable(GL_STENCIL_TEST);
                reset();
                render(Uncountable.game.getWorld().getCurrentModule());
                if(m_stencilOn) glDisable(GL_STENCIL_TEST);
            } catch (RenderingException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            Camera camera = Uncountable.game.getWorld().getCamera();
            Module module = Uncountable.game.getWorld().getCurrentModule();

            try {
                module.stageScene();
                camera.capture(module.getStagedRenderables());
                module.clearScene();
            } catch (RenderingException e) {
               e.printStackTrace();
                System.exit(1);
            }
        }
    }

    @Override
    public void onKeyUp(int key) {
        if(key == GLFW_KEY_Q) m_stencilOn = !m_stencilOn;
        if(key == GLFW_KEY_E) m_useNewRenderer = !m_useNewRenderer;
        if(key == GLFW_KEY_0) m_maxDepth++;
        if(key == GLFW_KEY_9) m_maxDepth--;
        if(key == GLFW_KEY_R) m_portalDebug = !m_portalDebug;
    }

    public void render(Module initialModule) throws RenderingException {
        m_visitedModules.add(initialModule);
        initialModule.stageScene();
        render(new Camera(Uncountable.game.getWorld().getCamera()), 0, initialModule, null);
    }

    public void reset() {
        for(Module module : m_visitedModules) {
            module.clearScene();
        }
        m_visitedModules.clear();
    }

    private void render(Camera camera, int depth, Module currentModule, Portal previousRemotePortal)
            throws RenderingException {

        if(depth > m_maxDepth || currentModule == null) return;
        for(Portal localPortal : currentModule.getTemplate().getPortals()) {

            if(localPortal == previousRemotePortal) {
                continue;
            }

            glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);
            glColorMask(false, false, false, false);
            glDepthMask(false);

            localPortal.setShader("portal1");
            camera.capture(localPortal);

            glStencilFunc(GL_EQUAL, depth + 1, 0xFF);

            Module nextModule = currentModule.getNeighbor(localPortal);
            if(nextModule != null) {
                Portal remotePortal = currentModule.getLinkedPortal(localPortal);
                if(!m_visitedModules.contains(nextModule)) {
                    m_visitedModules.add(nextModule);
                    nextModule.stageScene();
                }
                render(camera.proxy(localPortal, remotePortal), depth + 1, nextModule, remotePortal);
            }

            glStencilFunc(GL_EQUAL, depth, 0xFF);

            glStencilOp(GL_KEEP, GL_KEEP, GL_DECR);
            glColorMask(false, false, false, false);
            glDepthMask(true);

            if(m_portalDebug) localPortal.setShader("basic");
            camera.capture(localPortal);
        }

        glColorMask(true, true, true, true);
        glDepthMask(true);
        camera.capture(currentModule.getStagedRenderables());
    }
}
