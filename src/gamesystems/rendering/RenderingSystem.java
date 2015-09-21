package gamesystems.rendering;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DECR;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import core.Uncountable;
import gamesystems.GameSystem;
import joml.Vector3f;
import gamesystems.architecture.Module;
import gamesystems.architecture.setpieces.BasicColoredQuad;
import portals.Portal;

public class RenderingSystem extends GameSystem {

    private int m_maxDepth = 5;
    private boolean m_useNewRenderer = true;
    private Set<Module> m_visitedModules = new HashSet<>();

    private BasicColoredQuad m_quad = new BasicColoredQuad(Points.WHITE);
    private List<Vector3f> m_randomColors = new ArrayList<>();

    private int m_colorIndex = 0;

    public RenderingSystem() {
        super();
        m_quad.setShader("flat");
        m_quad.scale(2.0f);

        for(int i = 0; i < 100; ++i) {
            m_randomColors.add(Points.randomUnit3f());
        }
    }

    @Override
    public void tick(float seconds) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        if(m_useNewRenderer) {
            try {
                glEnable(GL_STENCIL_TEST);
                reset();
                render(Uncountable.game.world.currentModule);
                glDisable(GL_STENCIL_TEST);
            } catch (RenderingException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            Camera camera = Uncountable.game.world.camera;
            Module module = Uncountable.game.world.currentModule;

            try {
                module.stageScene();
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
    }

    public void reset() {
        m_visitedModules.stream().forEach(Module::clearScene);
        m_visitedModules.clear();
        m_colorIndex = 0;
    }

    public void render(Module initialModule) throws RenderingException {
        m_visitedModules.add(initialModule);
        initialModule.stageScene();

        Camera camera = Uncountable.game.world.camera;
        UniformSettings settings = Uncountable.game.world.uniforms;

        render(camera, settings, 1, initialModule, null);
    }

    private Vector3f getNextColor() {
        return m_randomColors.get(m_colorIndex++);
    }

    private void render(Camera camera,
                        UniformSettings settings,
                        int depth,
                        Module currentModule,
                        Portal previousRemotePortal)
            throws RenderingException {

        if (depth > m_maxDepth || currentModule == null) return;
        for (Portal localPortal : currentModule.getTemplate().getPortals()) {
            if (localPortal == previousRemotePortal) continue;

            glColorMask(false, false, false, false);
            glDepthMask(false);
            glStencilFunc(GL_EQUAL, depth, 0xFF);
            glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);

            localPortal.setShader("stenciler");
            camera.capture(localPortal);

            glStencilFunc(GL_EQUAL, depth + 1, 0xFF);
            glStencilOp(GL_KEEP, GL_KEEP, GL_DECR);

            //camera.setDiscardPlane(localPortal.getFrontPlane());
            //currentModule.setShaderForStagedRenderables("stenciler");
            //camera.capture(currentModule.getStagedRenderables());

            Module nextModule = currentModule.getNeighbor(localPortal);
            if (nextModule != null) {
                Portal remotePortal = currentModule.getLinkedPortal(localPortal);
                if (!m_visitedModules.contains(nextModule)) {
                    m_visitedModules.add(nextModule);
                    nextModule.stageScene();
                }
                render(camera.proxy(localPortal, remotePortal), settings, depth + 1, nextModule, remotePortal);
            }

            glColorMask(false, false, false, false);
            glDepthMask(false);
            glStencilFunc(GL_EQUAL, depth + 1, 0xFF);
            glStencilOp(GL_KEEP, GL_KEEP, GL_ZERO);

            localPortal.setShader("stenciler");
            camera.capture(settings, localPortal);
        }

        glColorMask(true, true, true, true);
        glDepthMask(true);
        glStencilFunc(GL_EQUAL, depth, 0xFF);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        camera.setDiscardPlane(null);

        currentModule.setShaderForStagedRenderables("basic");
        camera.capture(settings, currentModule.getStagedRenderables());
    }
}
