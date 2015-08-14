package application;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import joml.Vector2f;
import joml.Vector3f;
import rendering.Camera;
import rendering.Points;

public class CameraControlSystem extends GameSystem {

    private Vector3f m_goalVelocity = new Vector3f(Points.ORIGIN_3D);
    private Vector3f m_realVelocity = new Vector3f(Points.ORIGIN_3D);
    private float m_accelerationFactor = 0.1f;
    private float m_speed = 5.0f;

    public CameraControlSystem() {
        super();
    }

    @Override
    public void tick(float seconds) {
        Camera camera = Uncountable.game.getWorld().getCamera();
        m_realVelocity.add(new Vector3f(m_goalVelocity).sub(m_realVelocity).mul(m_accelerationFactor));
        camera.translate(new Vector3f(m_realVelocity).mul(seconds));
        m_goalVelocity = Points.ORIGIN_3D;
    }

    @Override
    public void onMouseMove(Vector2f position, Vector2f delta) {
        Camera camera = Uncountable.game.getWorld().getCamera();
        float width = Uncountable.game.getWidth();
        float height = Uncountable.game.getHeight();

        if(!inWindow(position)) return;

        float xDiff = (delta.x / width) * camera.get(Camera.FOV);
        float yDiff = (delta.y / height) * camera.get(Camera.FOV) / camera.get(Camera.ASPECT_RATIO);

        camera.add(Camera.YAW, xDiff);
        camera.sub(Camera.PITCH, yDiff);
    }

    @Override
    public void onKeyRepeat(int key) {
        Camera camera = Uncountable.game.getWorld().getCamera();
        switch(key) {
        case GLFW_KEY_W:
            m_goalVelocity = new Vector3f(camera.getHeading()).mul(m_speed);
            break;
        case GLFW_KEY_A:
            m_goalVelocity = new Vector3f(camera.getRight()).mul(m_speed);
            break;
        case GLFW_KEY_S:
            m_goalVelocity = new Vector3f(camera.getHeading()).mul(-m_speed);
            break;
        case GLFW_KEY_D:
            m_goalVelocity = new Vector3f(camera.getRight()).mul(-m_speed);
            break;
        }
    }

    private boolean inWindow(Vector2f position) {
        return position.x > 0 && position.x < Uncountable.game.getWidth() &&
               position.y > 0 && position.y < Uncountable.game.getHeight();
    }
}
