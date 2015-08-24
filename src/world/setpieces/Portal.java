package world.setpieces;

import gamesystems.rendering.Points;
import joml.AxisAngle4f;
import joml.Quaternionf;
import joml.Vector3f;
import world.Basis;
import world.Ray;

public class Portal extends BasicColoredQuad {

    private String m_name;
    private Vector3f m_basePosition;
    private Vector3f m_normal;
    private Vector3f m_up;

    public Portal(String name, Vector3f basePosition, Vector3f normal, Vector3f up) {
        super(new Vector3f(1.0f, 0.5f, 0.0f));
        m_name = name;

        translate(Points.aug3f("_Y_", 0.5f));
        scale(1.0f, 1.5f, 1.0f);

        m_basePosition = basePosition.get();
        m_normal = normal.get();
        m_up = up.get();
    }

    public boolean visibleFrom(Ray lineOfSight) {
        return true;
    }

    public String getName() {
        return m_name;
    }

    public Vector3f getBasePosition() {
        return m_basePosition.get();
    }

    public Vector3f getNormal() {
        return m_normal.get();
    }

    public Vector3f getUp() {
        return m_up.get();
    }

    public Vector3f getLeft() {
        return m_normal.get().cross(m_up);
    }

    public Basis getFrontBasis() {
        return new Basis(m_normal, m_up);
    }

    public Basis getBackBasis() {
        return new Basis(m_normal.get().negate(), m_up);
    }

    // DEBUG METHODS
    public void debugTranslate(Vector3f vector) {
        m_basePosition.add(vector);
    }

    public void rotateAboutUp(float angle) {
        m_normal.rotate(new Quaternionf(new AxisAngle4f(angle, m_up)));
        System.out.println("Normal is now " + m_normal);
    }

    public void rotateAboutNormal(float angle) {
        m_up.rotate(new Quaternionf(new AxisAngle4f(angle, m_normal)));
    }

    public void rotateAboutLeft(float angle) {

    }
}
