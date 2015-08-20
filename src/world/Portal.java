package world;

import gamesystems.rendering.Points;
import joml.Vector3f;
import world.setpieces.BasicColoredQuad;

public class Portal extends BasicColoredQuad {

    private String m_name;

    public Portal(String name) {
        super(new Vector3f(1.0f, 0.5f, 0.0f));
        m_name = name;

        translate(Points._Y_);
        scale(0.5f, 0.75f, 0.5f);
    }

    public boolean visibleFrom(Ray lineOfSight) {
        return true;
    }

    public String getName() {
        return m_name;
    }
}
