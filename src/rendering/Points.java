package rendering;

import joml.Vector2f;
import joml.Vector3f;
import joml.Vector4f;

public class Points {

    // Corners of cube
    public static final Vector3f XYZ = new Vector3f( 1.0f,  1.0f,  1.0f);
    public static final Vector3f XYz = new Vector3f( 1.0f,  1.0f, -1.0f);
    public static final Vector3f XyZ = new Vector3f( 1.0f, -1.0f,  1.0f);
    public static final Vector3f xYZ = new Vector3f(-1.0f,  1.0f,  1.0f);
    public static final Vector3f Xyz = new Vector3f( 1.0f, -1.0f, -1.0f);
    public static final Vector3f xYz = new Vector3f(-1.0f,  1.0f, -1.0f);
    public static final Vector3f xyZ = new Vector3f(-1.0f, -1.0f,  1.0f);
    public static final Vector3f xyz = new Vector3f(-1.0f, -1.0f, -1.0f);

    // Center of cube
    public static final Vector3f ___ = new Vector3f( 0.0f,  0.0f,  0.0f);
    public static final Vector3f ORIGIN_3D = ___;

    // Centers of cube faces
    public static final Vector3f X__ = new Vector3f( 1.0f,  0.0f,  0.0f);
    public static final Vector3f x__ = new Vector3f(-1.0f,  0.0f,  0.0f);
    public static final Vector3f _Y_ = new Vector3f( 0.0f,  1.0f,  0.0f);
    public static final Vector3f _y_ = new Vector3f( 0.0f, -1.0f,  0.0f);
    public static final Vector3f __Z = new Vector3f( 0.0f,  0.0f,  1.0f);
    public static final Vector3f __z = new Vector3f( 0.0f,  0.0f, -1.0f);

    // Midpoints of cube edges
    public static final Vector3f XY_ = new Vector3f( 1.0f,  1.0f,  0.0f);
    public static final Vector3f Xy_ = new Vector3f( 1.0f, -1.0f,  0.0f);
    public static final Vector3f xY_ = new Vector3f(-1.0f,  1.0f,  0.0f);
    public static final Vector3f xy_ = new Vector3f(-1.0f, -1.0f,  0.0f);
    public static final Vector3f X_Z = new Vector3f( 1.0f,  0.0f,  1.0f);
    public static final Vector3f X_z = new Vector3f( 1.0f,  0.0f, -1.0f);
    public static final Vector3f x_Z = new Vector3f(-1.0f,  0.0f,  1.0f);
    public static final Vector3f x_z = new Vector3f(-1.0f,  0.0f, -1.0f);
    public static final Vector3f _YZ = new Vector3f( 0.0f,  1.0f,  1.0f);
    public static final Vector3f _Yz = new Vector3f( 0.0f,  1.0f, -1.0f);
    public static final Vector3f _yZ = new Vector3f( 0.0f, -1.0f,  1.0f);
    public static final Vector3f _yz = new Vector3f( 0.0f, -1.0f, -1.0f);

    // Center (2D)
    public static final Vector2f __ = new Vector2f( 0.0f, 0.0f);
    public static final Vector2f ORIGIN_2D = __;

    // Square (2D)
    public static final Vector2f XY = new Vector2f( 1.0f,  1.0f);
    public static final Vector2f Xy = new Vector2f( 1.0f, -1.0f);
    public static final Vector2f xY = new Vector2f(-1.0f,  1.0f);
    public static final Vector2f xy = new Vector2f(-1.0f, -1.0f);
    public static final Vector2f X_ = new Vector2f( 1.0f,  0.0f);
    public static final Vector2f x_ = new Vector2f(-1.0f,  0.0f);
    public static final Vector2f _Y = new Vector2f( 0.0f,  1.0f);
    public static final Vector2f _y = new Vector2f( 0.0f, -1.0f);

    // Colors
    public static final Vector3f RED = X__;
    public static final Vector3f GREEN = _Y_;
    public static final Vector3f BLUE = __Z;
    public static final Vector3f YELLOW = XY_;
    public static final Vector3f MAGENTA = X_Z;
    public static final Vector3f CYAN = _YZ;
    public static final Vector3f WHITE = XYZ;
    public static final Vector3f BLACK = ___;

    public static Vector3f aug3f(String name, float aug) {
        try {
            return new Vector3f((Vector3f)(Points.class.getField(name).get(null))).mul(aug);
        } catch (NoSuchFieldException e) {
            System.err.println("Unknown point " + name);
            return ORIGIN_3D;
        } catch (IllegalAccessException e) {
            System.err.println("Illegal access on " + name);
            return ORIGIN_3D;
        }
    }

    public static Vector2f aug2f(String name, float aug) {
        try {
            return new Vector2f((Vector2f)(Points.class.getField(name).get(null))).mul(aug);
        } catch (NoSuchFieldException e) {
            System.err.println("Unknown point " + name);
            return ORIGIN_2D;
        } catch (IllegalAccessException e) {
            System.err.println("Illegal access on " + name);
            return ORIGIN_2D;
        }
    }

    public static float piOver(float denominator) {
        return (float)(Math.PI/denominator);
    }

    public static Vector4f homogeneousVector(Vector3f vector)
    {
        return new Vector4f(vector.x, vector.y, vector.z, 0.0f);
    }

    public static Vector4f homogeneousPoint(Vector3f vector) {
        return new Vector4f(vector.x, vector.y, vector.z, 1.0f);
    }
}
