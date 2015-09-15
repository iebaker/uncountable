package gamesystems.rendering;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DECR;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_INCR;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_ZERO;
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
import joml.Vector4f;
import world.Module;
import world.setpieces.Portal;

public class RenderingSystem extends GameSystem {

    private int m_maxDepth = 5;
    private boolean m_useNewRenderer = true;
    private Set<Module> m_visitedModules = new HashSet<>();

    private int m_portalState = 0;
    private int m_debugPortal = 0;
    private boolean m_stop = true;

    public RenderingSystem() {
        super();
    }

    @Override
    public void tick(float seconds) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        if(m_useNewRenderer) {
            try {
                glEnable(GL_STENCIL_TEST);
                reset();
                render(Uncountable.game.getWorld().getCurrentModule());
                glDisable(GL_STENCIL_TEST);
            } catch (RenderingException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            Camera camera = Uncountable.game.getWorld().getCamera();
            Module module = Uncountable.game.getWorld().getCurrentModule();

            try {
                module.stageScene();
                camera.setDiscardPlane(new Vector4f(9, 9, 9, -27));
                camera.capture(module.getStagedRenderables());
                camera.setDiscardPlane(null);
                module.clearScene();
            } catch (RenderingException e) {
               e.printStackTrace();
               System.exit(1);
            }
        }
    }

    @Override
    public void onKeyUp(int key) {
        if(key == GLFW_KEY_E) m_useNewRenderer = !m_useNewRenderer;
        if(key == GLFW_KEY_0) m_maxDepth++;
        if(key == GLFW_KEY_9) m_maxDepth--;
        if(key == GLFW_KEY_P) m_debugPortal++;
        if(key == GLFW_KEY_O) m_debugPortal--;
    }

    public void reset() {
        m_visitedModules.stream().forEach(Module::clearScene);
        m_visitedModules.clear();
        m_stop = false;
        m_portalState = 0;
    }

    public void render(Module initialModule) throws RenderingException {
        m_visitedModules.add(initialModule);
        initialModule.stageScene();
        render(new Camera(Uncountable.game.getWorld().getCamera()), 1, initialModule, null);
    }

    private void render2(Camera camera, int depth, Module currentModule, Portal previousRemotePortal)
            throws RenderingException {

        if(depth > m_maxDepth || currentModule == null) return;
        for(Portal localPortal : currentModule.getTemplate().getPortals()) {
            if(localPortal == previousRemotePortal) continue;

            glDisable(GL_DEPTH_TEST);
            glColorMask(false, false, false, false);
            glDepthMask(false);
            glStencilFunc(GL_EQUAL, depth, 0xFF);
            glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);

            localPortal.setShader("stenciler");
            camera.capture(localPortal);

            glStencilFunc(GL_EQUAL, depth + 1, 0xFF);
            glStencilOp(GL_KEEP, GL_KEEP, GL_DECR);

            camera.setDiscardPlane(localPortal.getFrontPlane());
            currentModule.setShaderForStagedRenderables("stenciler");
            camera.capture(currentModule.getStagedRenderables());

            Module nextModule = currentModule.getNeighbor(localPortal);
            if(nextModule != null) {
                Portal remotePortal = currentModule.getLinkedPortal(localPortal);
                if(!m_visitedModules.contains(nextModule)) {
                    m_visitedModules.add(nextModule);
                    nextModule.stageScene();
                }
                render(camera.proxy(localPortal, remotePortal), depth + 1, nextModule, remotePortal);
            }

            glDisable(GL_DEPTH_TEST);
            glColorMask(false, false, false, false);
            glDepthMask(false);
            glStencilFunc(GL_EQUAL, depth + 1, 0xFF);
            glStencilOp(GL_KEEP, GL_KEEP, GL_ZERO);

            localPortal.setShader("stenciler");
            camera.capture(localPortal);
        }

        glEnable(GL_DEPTH_TEST);
        glColorMask(true, true, true, true);
        glDepthMask(true);
        glStencilFunc(GL_EQUAL, depth, 0xFF);
        camera.setDiscardPlane(null);

        currentModule.setShaderForStagedRenderables("basic");
        camera.capture(currentModule.getStagedRenderables());
    }

    private void render(Camera camera, int depth, Module currentModule, Portal previousRemotePortal)
            throws RenderingException {

        if(depth > m_maxDepth || currentModule == null) {
            return;
        }

        for(Portal localPortal : currentModule.getTemplate().getPortals()) {

            if(depth == 1) {
                m_portalState++;
            }

            if(localPortal == previousRemotePortal) {
                continue;
            }

            glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);
            glColorMask(false, false, false, false);
            glDepthMask(false);
            if(!m_stop) camera.capture(localPortal);

            if(depth == m_maxDepth && m_portalState == m_debugPortal) {
                DebugArtist.renderStencil(camera, depth);
                m_stop = true;
            }

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
            if(!m_stop) camera.capture(localPortal);
        }

        glColorMask(true, true, true, true);
        glDepthMask(true);

        if(!m_stop) camera.capture(currentModule.getStagedRenderables());
    }
}
