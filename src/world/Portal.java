package world;

import world.setpieces.AbstractQuad;

public class Portal extends AbstractQuad {

    private String m_name;

    public Portal(String name) {
        m_name = name;
    }

    public boolean visibleFrom(Ray lineOfSight) {
        return true;
    }

    public String getName() {
        return m_name;
    }
}
