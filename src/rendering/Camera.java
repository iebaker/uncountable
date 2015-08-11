package rendering;

import joml.Matrix4f;
import joml.Vector3f;
import world.Module;
import world.Portal;

public class Camera {

    public static final int YAW = 0;
    public static final int PITCH = 1;
    public static final int HEIGHT_ANGLE = 2;
    public static final int NEAR_PLANE = 3;
    public static final int FAR_PLANE = 4;
    public static final int PITCH_LIMIT = 5;
    public static final int ASPECT_RATIO = 6;

    private Module m_previous;
    private Vector3f m_eye = Points.___;
    private float[] m_params = new float[7];

    public float get(int parameterIndex) {
        return m_params[parameterIndex];
    }

    public void set(int parameterIndex, float value) {
        m_params[parameterIndex] = value;
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
        m_params[PITCH] = m_params[PITCH] > m_params[PITCH_LIMIT] ?
            m_params[PITCH_LIMIT] : m_params[PITCH];
        m_params[PITCH] = m_params[PITCH] < -m_params[PITCH_LIMIT] ?
            -m_params[PITCH_LIMIT] : m_params[PITCH];
    }

    public void translate(Vector3f vector) {
        m_eye = m_eye.add(vector);
    }

    public void translateTo(Vector3f vector) {
        m_eye = new Vector3f(vector);
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f();
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f();
    }

    public void capture(Module module) throws RenderingException {
        captureToScreen(module);
    }

    public void captureToScreen(Module module) throws RenderingException {
       for(Renderable renderable : module.getStagedRenderables()) {

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
       module.clearStagedRenderables();
    }

    public void captureToConsole(Module module) {
        if(module == m_previous) return;
        System.out.print("You are in " + module.getName() + ", \nexits:");
        int portalIndex = 0;
        for (Portal portal : module.getTemplate().getPortals()) {
            System.out.print("\t" + (portalIndex++) + " " + portal.getName() + " to ");
            if (module.hasNeighbor(portal)) {
                System.out.println(module.getNeighbor(portal).getName());
            } else {
                System.out.println("UNLOADED");
            }
        }
        m_previous = module;
    }
}
