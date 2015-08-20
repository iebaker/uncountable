package world;

import gamesystems.rendering.Points;
import joml.Vector3f;
import world.setpieces.BasicColoredQuad;

public class Portal extends BasicColoredQuad {

    private String m_name;
    private Vector3f m_basePosition;
    private Vector3f m_normal;

    public Portal(String name, Vector3f basePosition, Vector3f normal) {
        super(new Vector3f(1.0f, 0.5f, 0.0f));
        m_name = name;

        translate(Points._Y_);
        scale(0.5f, 0.75f, 0.5f);

        m_basePosition = basePosition;
        m_normal = normal;
    }

    public boolean visibleFrom(Ray lineOfSight) {
        return true;
    }

    public String getName() {
        return m_name;
    }

    public Vector3f getBasePosition() {
        return m_basePosition;
    }

    public Vector3f getNormal() {
        return m_normal;
    }
}
