package gamesystems.architecture;

import gamesystems.architecture.Basis;
import gamesystems.architecture.Quad;
import gamesystems.rendering.Points;
import joml.Matrix4f;
import joml.Vector3f;
import joml.Vector4f;

public class Portal extends Quad {

    private String m_name;
    private Vector3f m_basePosition;
    private Vector3f m_normal;
    private Vector3f m_up;

    public Portal(String name, Vector3f basePosition, Vector3f normal, Vector3f up) {
        super();
        setShader("stenciler");

        m_name = name;

        translate(Points.aug3f("_Y_", 0.5f));
        scale(1.0f, 1.5f, 1.0f);

        m_basePosition = basePosition.get();
        m_normal = normal.get();
        m_up = up.get();
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

    public Vector4f getFrontPlane() {
        return new Vector4f(m_normal.x, m_normal.y, m_normal.z, -m_normal.dot(m_basePosition));
    }

    public Vector4f getBackPlane() {
        return new Vector4f();
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

    @Override
    public String toString() {
        return m_name;
    }
}
