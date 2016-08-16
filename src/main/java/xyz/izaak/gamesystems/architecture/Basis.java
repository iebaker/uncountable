package xyz.izaak.gamesystems.architecture;

import xyz.izaak.gamesystems.rendering.Points;

import org.joml.Matrix3f;
import org.joml.Vector3f;

public class Basis {

    private final Vector3f m_xAxis;
    private final Vector3f m_yAxis;
    private final Vector3f m_zAxis;
    public static final Basis STANDARD = new Basis(Points.X__, Points._Y_);

    public static Vector3f change(Vector3f vector, Basis from, Basis to) {
        Vector3f result = Points.get(vector);
        to.getMatrix().invert().mul(from.getMatrix()).transform(result);
        return result;
    }

    public Basis(Vector3f xAxis, Vector3f yAxis) {
        m_xAxis = Points.get(xAxis);
        m_yAxis = Points.get(yAxis);
        m_zAxis = Points.get(m_xAxis).cross(m_yAxis);
    }

    public Vector3f getXAxis() {
        return Points.get(m_xAxis);
    }

    public Vector3f getYAxis() {
        return Points.get(m_yAxis);
    }

    public Vector3f getZAxis() {
        return Points.get(m_zAxis);
    }

    public Matrix3f getMatrix() {
        return new Matrix3f(
           m_xAxis.x, m_yAxis.x, m_zAxis.x,
           m_xAxis.y, m_yAxis.y, m_zAxis.y,
           m_xAxis.z, m_yAxis.z, m_zAxis.z
        ).transpose();
    }
}
