package world;

import rendering.Ray;

public class Portal {

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
