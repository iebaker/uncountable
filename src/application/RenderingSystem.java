package application;

//import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rendering.Camera;
import rendering.RenderingException;
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

    private static int m_maxDepth = 10;
    private static boolean m_useNewRenderer = false;

    public RenderingSystem() {
        super();
    }

    @Override
    public void tick(float seconds) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Definitely not ready to bust out the recursive portal rendering yet
        if(m_useNewRenderer) {
            try {
                new RecursiveRenderer() {{
                    setCamera(Uncountable.game.getWorld().getCamera());
                    setInitialModule(Uncountable.game.getWorld().getCurrentModule());
                    setMaxDepth(m_maxDepth);
                }}.render();
            } catch (RenderingException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        // Instead, do something more simple. This is where the currently rendered scene happens.
        else {
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
            render(0, first, null);
        }

        /**
         * Actually performs recursive rendering through portals using stencil buffering
         * to render the view through portals on only part of the screen.
         * @throws RenderingException
         */
        private void render(int depth, Module currentModule, Portal previousRemotePortal) throws RenderingException {

            // For every portal in the room, except the one we just came through...
            for(Portal localPortal : currentModule.getTemplate().getPortals()) {

                // If this is the portal we just looked through, ignore it
                if(localPortal == previousRemotePortal) continue;

                // Write the portal to the stencil buffer, incrementing the values under it
                glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);
                glColorMask(false, false, false, false);
                glDepthMask(false);
                m_camera.capture(localPortal);

                // Now, only allow drawing in the stenciled out region we just wrote
                glStencilFunc(GL_EQUAL, depth + 1, 0xFF);

                // Figure out which Module we're going to be rendering through this portal, and
                // prepare for that render
                Module nextModule = currentModule.getNeighbor(localPortal);
                Portal remotePortal = currentModule.getLinkedPortal(localPortal);
                if(!m_visitedModules.contains(nextModule)) {
                    m_visitedModules.add(nextModule);
                    nextModule.stageScene();
                }

                // Swap the current camera for a camera in the frame of reference of the the adjacent Module
                Camera external = m_camera.exchangeForProxy(remotePortal);

                // Render the world through that portal onto the stenciled part of the screen
                if(depth < m_maxDepth) render(depth + 1, nextModule, remotePortal);

                // Swap the camera back, move back into the external module
                m_camera = external;

                // Draw the portal to the depth buffer and erase it from the stencil buffer
                glStencilOp(GL_KEEP, GL_KEEP, GL_DECR);
                glColorMask(false, false, false, false);
                glDepthMask(true);
                m_camera.capture(localPortal);

                // Allow drawing outside the stenciled region
                glStencilFunc(GL_EQUAL, depth, 0xFF);
            }

            // Render the world outside the portals, and
            m_camera.capture(currentModule.getStagedRenderables());
        }
    }
}
