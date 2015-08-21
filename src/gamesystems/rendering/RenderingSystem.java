package gamesystems.rendering;

//import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.Uncountable;
import gamesystems.GameSystem;
import world.Module;
import world.Portal;

/**
 * This is where some crazy stuff is starting to go down. This is the rendering system which must
 * eventually support recursively rendering through multiple portals, some of which may open back
 * onto rooms which have already been rendered in that sequence. As of right now, the skeletal
 * recursive rendering code does not get used and many methods it requires are stubs. In its place,
 * a simple renderer uses the basic shader to draw the colorful cube defined in Module.
 */
public class RenderingSystem extends GameSystem {

    private int m_maxDepth = 10;
    private boolean m_useNewRenderer = true;
    private RecursiveRenderer m_renderer = new RecursiveRenderer();

    public RenderingSystem() {
        super();
    }

    @Override
    public void tick(float seconds) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        if(m_useNewRenderer) {
            try {
                glEnable(GL_STENCIL_TEST);
                m_renderer.reset();
                m_renderer.setCamera(Uncountable.game.getWorld().getCamera());
                m_renderer.setInitialModule(Uncountable.game.getWorld().getCurrentModule());
                m_renderer.setMaxDepth(m_maxDepth);
                m_renderer.render();
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
                camera.capture(module.getStagedRenderables());
                module.clearScene();
            } catch (RenderingException e) {
               e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * This is an inner class which will eventually support functionality for performing recursive
     * rendering of many modules connected together with portals. The structure is here, but a lot
     * of the methods it depends on are only skeletally implemented.
     */
    private class RecursiveRenderer {

        // The camera used for rendering
        private Camera m_camera;

        // The maximum recursion depth for portal rendering
        private int m_maxDepth;

        // Any module that's already been visited during this rendering cycle
        private Set<Module> m_visitedModules = new HashSet<Module>();

        /**
         * Sets the Camera to begin the rendering cycle with
         */
        public void setCamera(Camera camera) {
            m_camera = camera;
        }

        /**
         * Sets the module to begin rendering from. This is the module the Camera is
         * currently in.
         */
        public void setInitialModule(Module module) {
            m_visitedModules.add(module);
            module.stageScene();
        }

        /**
         * Sets the maximum number of portals to render through
         */
        public void setMaxDepth(int maxDepth) {
            m_maxDepth = maxDepth;
        }

        public void render() throws RenderingException {
            Module first = (Module)(m_visitedModules.toArray()[0]);
            render(new Camera(m_camera), 0, first, null);
        }

        public void reset() {
            for(Module module : m_visitedModules) {
                module.clearScene();
            }
            m_visitedModules.clear();
        }

        /**
         * Actually performs recursive rendering through portals using stencil buffering
         * to render the view through portals on only part of the screen.
         * @throws RenderingException
         */
        private void render(Camera camera, int depth, Module currentModule, Portal previousRemotePortal)
                throws RenderingException {

            if(depth == m_maxDepth || currentModule == null) return;
            for(Portal localPortal : currentModule.getTemplate().getPortals()) {

                if(localPortal == previousRemotePortal) {
                    continue;
                }

                glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);
                glColorMask(false, false, false, false);
                glDepthMask(false);
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

                glClear(GL_DEPTH_BUFFER_BIT);
                glStencilOp(GL_KEEP, GL_KEEP, GL_DECR);
                glColorMask(false, false, false, false);
                glDepthMask(true);
                camera.capture(localPortal);

                glStencilFunc(GL_EQUAL, depth, 0xFF);
            }

            glColorMask(true, true, true, true);
            glDepthMask(true);
            camera.capture(currentModule.getStagedRenderables());
        }
    }
}
