package gamesystems.rendering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import joml.AxisAngle4f;
import joml.Matrix4f;
import joml.Quaternionf;
import joml.Vector3f;
import world.Portal;

public class Camera {

    public static final int YAW = 0;
    public static final int PITCH_LIMIT = 1;
    public static final int HEIGHT_ANGLE = 2;
    public static final int NEAR_PLANE = 3;
    public static final int FAR_PLANE = 4;
    public static final int PITCH = 5;
    public static final int ASPECT_RATIO = 6;
    public static final int FOV = 7;

    private Vector3f m_eye = Points.___;
    private float[] m_params = new float[8];

    public Camera() {

    }

    public Camera(Camera other) {
        translateTo(other.getEye());
        for(int i = 0; i < 8; ++i) {
            set(i, other.get(i));
        }
    }

    public float get(int parameterIndex) {
        return m_params[parameterIndex];
    }

    public void set(int parameterIndex, float value) {
        m_params[parameterIndex] = value;
        limitPitch();
    }

    public void add(int parameterIndex, float value) {
        m_params[parameterIndex] += value;
        limitPitch();
    }

    public void sub(int parameterIndex, float value) {
        m_params[parameterIndex] -= value;
        limitPitch();
    }

    public Vector3f getEye() {
        return m_eye;
    }

    public Vector3f getLook() {
        float x = (float)(Math.cos(m_params[YAW]) * Math.cos(m_params[PITCH]));
        float y = (float)(Math.sin(m_params[PITCH]));
        float z = (float)(Math.sin(m_params[YAW]) * Math.cos(m_params[PITCH]));
        return new Vector3f(x, y, z).normalize();
    }

    public Vector3f getHeading() {
        float x = (float)(Math.cos(m_params[YAW]));
        float y = 0.0f;
        float z = (float)(Math.sin(m_params[YAW]));
        return new Vector3f(x, y, z).normalize();
    }

    public Vector3f getRight() {
        Vector3f heading = getHeading();
        return new Vector3f(heading.z, 0.0f, -heading.x);
    }

    public Vector3f getUp() {
        if(m_params[PITCH] < 0) {
            Vector3f look = getLook();
            return new Vector3f(look.x, 1.0f, look.z).normalize();
        } else {
            return new Vector3f(0.0f, 1.0f, 0.0f);
        }
    }

    public void rotate(float yaw, float pitch) {
        m_params[PITCH] += pitch;
        m_params[YAW] += yaw;
        limitPitch();
    }

    private void limitPitch() {
        if(m_params[PITCH] > m_params[PITCH_LIMIT]) {
            m_params[PITCH] = m_params[PITCH_LIMIT];
        }
        if(m_params[PITCH] < -m_params[PITCH_LIMIT]) {
            m_params[PITCH] = -m_params[PITCH_LIMIT];
        }
    }

    public void translate(Vector3f vector) {
        m_eye.add(vector);
    }

    public void translateTo(Vector3f vector) {
        m_eye = new Vector3f(vector);
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().setLookAt(m_eye, new Vector3f(m_eye).add(getLook()), Points._Y_);
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().setPerspective(m_params[FOV], m_params[ASPECT_RATIO], m_params[NEAR_PLANE], m_params[FAR_PLANE]);
    }

    public void capture(Renderable... renderables) throws RenderingException {
        captureToScreen(Arrays.asList(renderables));
    }

    public void capture(List<Renderable> renderables) throws RenderingException {
        captureToScreen(renderables);
    }

    private void captureToScreen(List<Renderable> renderables) throws RenderingException {
        for(Renderable renderable : renderables) {

            if(renderable.needsToBeBuffered()) {
                Graphics.buffer(renderable);
            }

            Shaders.useShader(renderable.getActiveShaderName());

            Shaders.setShaderUniform("model", renderable.getModelMatrix());
            Shaders.setShaderUniform("view", getViewMatrix());
            Shaders.setShaderUniform("projection", getProjectionMatrix());
            Shaders.setShaderUniform("cameraEye", getEye());

            Graphics.draw(renderable);
        }
    }

    public Camera proxy(Portal local, Portal remote) {
        Camera proxyCamera = new Camera(this);

        Vector3f localNormal = new Vector3f(local.getNormal());
        Vector3f negativeRemoteNormal = new Vector3f(remote.getNormal()).mul(-1.0f);

        float sign = Math.signum(new Vector3f(localNormal).cross(negativeRemoteNormal).y);
        float angle = sign * (float)Math.acos(localNormal.dot(negativeRemoteNormal));

        proxyCamera.translate(new Vector3f(local.getBasePosition()).mul(-1.0f));
        Vector3f newCameraEye = new Vector3f(proxyCamera.getEye());
        newCameraEye.rotate(new Quaternionf(new AxisAngle4f(angle, Points._Y_)));
        proxyCamera.translateTo(newCameraEye);
        proxyCamera.add(Camera.YAW, angle + Points.piOver(1));
        proxyCamera.translate(new Vector3f(remote.getBasePosition()));

        return proxyCamera;
    }
}
