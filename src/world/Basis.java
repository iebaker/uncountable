package world;

import gamesystems.rendering.Points;
import joml.Matrix3f;
import joml.Vector3f;

public class Basis {

    private final Vector3f m_xAxis;
    private final Vector3f m_yAxis;
    private final Vector3f m_zAxis;
    public static final Basis STANDARD = new Basis(Points.X__, Points._Y_);

    public static Vector3f change(Vector3f vector, Basis from, Basis to) {
        Vector3f result = vector.get();
        to.getMatrix().invert().mul(from.getMatrix()).transform(result);
        return result;
    }

    public Basis(Vector3f xAxis, Vector3f yAxis) {
        m_xAxis = xAxis.get();
        m_yAxis = yAxis.get();
        m_zAxis = m_xAxis.get().cross(m_yAxis);
    }

    public Vector3f getXAxis() {
        return m_xAxis.get();
    }

    public Vector3f getYAxis() {
        return m_yAxis.get();
    }

    public Vector3f getZAxis() {
        return m_zAxis.get();
    }

    public Matrix3f getMatrix() {
        return new Matrix3f(
           m_xAxis.x, m_yAxis.x, m_zAxis.x,
           m_xAxis.y, m_yAxis.y, m_zAxis.y,
           m_xAxis.z, m_yAxis.z, m_zAxis.z
        ).transpose();
    }
}
