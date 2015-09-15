package gamesystems.rendering;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import joml.Matrix4f;
import joml.Vector3f;
import joml.Vector4f;
import world.Seam;
import world.setpieces.Portal;

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

    private Optional<Vector4f> m_discardPlane = Optional.empty();

    public Camera() {

    }

    public Camera(Camera other) {
        translateTo(other.getEye());
        setDiscardPlane(other.getDiscardPlane());
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
        return m_eye.get();
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
        m_eye = vector.get();
    }

    public void setLook(Vector3f vector) {
        vector.normalize();
        set(Camera.PITCH, (float)Math.asin(vector.y));
        set(Camera.YAW, (float)Math.atan2(vector.z, vector.x));
    }

    public void setDiscardPlane(Vector4f plane) {
        m_discardPlane = Optional.ofNullable(plane);
    }

    public Vector4f getDiscardPlane() {
        return m_discardPlane.orElse(null);
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().setLookAt(m_eye, new Vector3f(m_eye).add(getLook()), Points._Y_);
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().setPerspective(m_params[FOV], m_params[ASPECT_RATIO], m_params[NEAR_PLANE], m_params[FAR_PLANE]);
    }

    public void capture(UniformSettings uniforms, Renderable... renderables) throws RenderingException {
        capture(uniforms, Arrays.asList(renderables));
    }

    public void capture(Renderable... renderables) throws RenderingException {
        capture(() -> {}, Arrays.asList(renderables));
    }

    public void capture(List<Renderable> renderables) throws RenderingException {
        capture(() -> {}, renderables);
    }

    private void capture(UniformSettings uniforms, List<Renderable> renderables) throws RenderingException {
        for(Renderable renderable : renderables) {

            if(renderable.needsToBeBuffered()) {
                Graphics.buffer(renderable);
            }

            Shaders.useShader(renderable.getActiveShaderName());
            renderable.setShaderUniforms();
            uniforms.set();

            Shaders.setShaderUniform("view", getViewMatrix());
            Shaders.setShaderUniform("projection", getProjectionMatrix());
            Shaders.setShaderUniform("cameraEye", getEye());
            if(m_discardPlane.isPresent()) {
                Shaders.setShaderUniform("discardPlane", m_discardPlane.get());
                Shaders.setShaderUniform("useDiscardPlane", true);
            } else {
                Shaders.setShaderUniform("useDiscardPlane", false);
            }

            Graphics.draw(renderable);
        }
    }

    public Camera proxy(Portal local, Portal remote) {
        Camera proxyCamera = new Camera(this);
        Seam seam = new Seam(local, remote);

        proxyCamera.translateTo(seam.transformPoint(proxyCamera.getEye()));
        proxyCamera.setLook(seam.transformVector(proxyCamera.getLook()));

        return proxyCamera;
    }
}
