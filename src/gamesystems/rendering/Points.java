package gamesystems.rendering;

import java.awt.Color;
import java.util.Random;

import joml.Vector2f;
import joml.Vector3f;
import joml.Vector4f;

@SuppressWarnings("unused")
public class Points {

    private static final Random random = new Random();

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
    public static final Vector3f GRAY = WHITE.get().mul(0.5f);

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

    public static Vector4f homogeneousVector(Vector3f vector) {
        return new Vector4f(vector.x, vector.y, vector.z, 0.0f);
    }

    public static Vector4f homogeneousPoint(Vector3f vector) {
        return new Vector4f(vector.x, vector.y, vector.z, 1.0f);
    }

    public static Vector3f randomUnit3f() {
        return new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()).normalize();
    }

    public static Vector2f randomUnit2f() {
        return new Vector2f(random.nextFloat(), random.nextFloat()).normalize();
    }

    public static Vector3f hsbToRgb(float hue) {
        return hsbToRgb(hue, 1.0f, 1.0f);
    }

    public static Vector3f hsbToRgb(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if(saturation == 0) {
            r = g = b = (int)(brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float)java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
            case 0:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 4:
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 5:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
                break;
            }
        }
        return new Vector3f((float)r/255.0f, (float)g/255.0f, (float)b/255.0f);
    }

    public static Vector3f rgbToHsb(float red, float green, float blue) {
        return new Vector3f();
    }
 }
