package gamesystems.portaldebug;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;

import gamesystems.GameSystem;
import gamesystems.rendering.Points;
import world.Architect;
import world.setpieces.Portal;

public class PortalDebugSystem extends GameSystem {
    private Portal m_portal;
    private boolean m_active = true;
    private float m_translationalStep = 0.1f;
    private float m_rotationalStep = Points.piOver(10);

    @Override
    public void tick(float seconds) {
        if(m_portal == null) m_portal = Architect.getModuleTemplate("leafroom").getPortal("0");
    }

    @Override
    public void onKeyUp(int key) {
        if(!m_active) {
            if(key == GLFW_KEY_P) {
                m_active = true;
                return;
            }
        } else {
            switch(key) {
            case GLFW_KEY_I:
                m_portal.debugTranslate(m_portal.getNormal().mul(m_translationalStep));
                break;
            case GLFW_KEY_K:
                m_portal.debugTranslate(m_portal.getNormal().negate().mul(m_translationalStep));
                break;
            case GLFW_KEY_L:
                m_portal.debugTranslate(m_portal.getLeft().mul(m_translationalStep));
                break;
            case GLFW_KEY_J:
                m_portal.debugTranslate(m_portal.getLeft().negate().mul(m_translationalStep));
                break;
            case GLFW_KEY_U:
                m_portal.rotateAboutUp(m_rotationalStep);
                break;
            case GLFW_KEY_O:
                m_portal.rotateAboutUp(-m_rotationalStep);
                break;
            case GLFW_KEY_8:
                m_portal.rotateAboutNormal(m_rotationalStep);
                break;
            case GLFW_KEY_0:
                m_portal.rotateAboutNormal(-m_rotationalStep);
                break;
            case GLFW_KEY_P:
                m_active = false;
                return;
            }
        }
    }
}
