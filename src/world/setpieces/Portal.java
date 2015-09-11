package world.setpieces;

import gamesystems.rendering.Points;
import joml.AxisAngle4f;
import joml.Matrix4f;
import joml.Quaternionf;
import joml.Vector3f;
import joml.Vector4f;
import world.Basis;
import world.Ray;

public class Portal extends BasicColoredQuad {

    private String m_name;
    private Vector3f m_basePosition;
    private Vector3f m_normal;
    private Vector3f m_up;

    public Portal(String name, Vector3f basePosition, Vector3f normal, Vector3f up) {
        super(new Vector3f(0.7f, 0.5f, 0.2f));
        setShader("portal1");

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

    public boolean crossedBy(Vector3f startPosition, Vector3f endPosition) {
        Vector4f start = Points.homogeneousPoint(startPosition.get());
        Vector4f ray = Points.homogeneousVector(endPosition.get().sub(startPosition));

        Vector4f normal = new Vector4f(0, 0, 1, 0);
        Vector4f point = new Vector4f(0, 0, 0, 1);

        Matrix4f inverse = new Matrix4f(getModelMatrix()).invert();
        inverse.transform(start);
        inverse.transform(ray);

        float term1 = normal.dot(point);
        float term2 = normal.dot(start);
        float term3 = normal.dot(ray);

        if(term3 >= 0) return false;

        float t = (term1 - term2) / term3;
        if(t >= 0 && t <= 1) {
            start.add(ray.mul(t));
            if(Math.abs(start.x) <= 1 && Math.abs(start.y) <= 1) {
                return true;
            }
        }

        return false;
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
