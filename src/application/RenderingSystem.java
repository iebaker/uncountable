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

class Link {}

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

    private class RecursiveRenderer {

        private Camera m_camera;
        private int m_maxDepth;
        private List<Module> m_moduleStack = new ArrayList<Module>();
        private Set<Module> m_visitedModules = new HashSet<Module>();

        public void setCamera(Camera camera) {
            m_camera = camera;
        }

        public void setInitialModule(Module module) {
            m_moduleStack.add(0, module);
            m_visitedModules.add(module);
            getCurrentModule().stageScene();
        }

        public void setMaxDepth(int maxDepth) {
            m_maxDepth = maxDepth;
        }

        public void render() throws RenderingException {
            Camera external;
            for(Portal portal : getForwardPortals()) {

                glStencilOp(GL_KEEP, GL_KEEP, GL_INCR);
                glColorMask(false, false, false, false);
                glDepthMask(false);
                m_camera.capture(portal);

                glStencilFunc(GL_EQUAL, m_moduleStack.size(), 0xFF);
                Module next = getCurrentModule().getNeighbor(portal);
                if(!m_visitedModules.contains(next)) {
                    m_visitedModules.add(next);
                    next.stageScene();
                }
                m_moduleStack.add(0, next);
                external = m_camera.exchangeForProxy(portal);
                if(m_moduleStack.size() < m_maxDepth) render();
                m_camera = external;

                glStencilOp(GL_KEEP, GL_KEEP, GL_DECR);
                glColorMask(false, false, false, false);
                glDepthMask(true);
                m_camera.capture(portal);
            }

            m_camera.capture(getCurrentModule().getStagedRenderables());
            m_moduleStack.remove(0);
            glStencilFunc(GL_EQUAL, m_moduleStack.size(), 0xFF);

        }

        private Module getCurrentModule() {
            return m_moduleStack.get(0);
        }

        private List<Portal> getForwardPortals() {
            return new ArrayList<Portal>();
        }
    }
}
