package application;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

import joml.Vector2f;
import joml.Vector3f;
import rendering.Camera;
import rendering.Points;

/**
 * This is a GameSystem responsible for collecting input events and using them to move the camera
 * around the scene. It probably won't exist in the final game, as there will be a Player class which
 * is responsible for maintaining camera motion, but in the mean time this will do for debugging the
 * rendering and world generation systems.
 */
public class CameraControlSystem extends GameSystem {

    // two velocity parameters used for smooth acceleration
    private Vector3f m_goalVelocity = new Vector3f(Points.ORIGIN_3D);
    private Vector3f m_realVelocity = new Vector3f(Points.ORIGIN_3D);

    // how rapidly the camera accelerates and decelerates
    private float m_accelerationFactor = 0.1f;

    // how rapidly the camera can rotate
    private float m_rotationFactor = 2.0f;

    // the maximum speed of the camera
    private float m_speed = 5.0f;

    public CameraControlSystem() {
        super();
    }

    /**
     * During each frame, accelerate the camera by some fraction of the difference between its current
     * velocity and a goal velocity, which is set by key presses. This lets the camera smoothly
     * accelerate and decelerate.
     */
    @Override
    public void tick(float seconds) {
        Camera camera = Uncountable.game.getWorld().getCamera();
        m_realVelocity.add(new Vector3f(m_goalVelocity).sub(m_realVelocity).mul(m_accelerationFactor));
        camera.translate(new Vector3f(m_realVelocity).mul(seconds));
    }

    /**
     * Mouse motion rotates the camera around the world by altering the pitch (vertical) and yaw
     * (horizontal) components of the camera's orientation.
     */
    @Override
    public void onMouseMove(Vector2f position, Vector2f delta) {
        Camera camera = Uncountable.game.getWorld().getCamera();
        float width = Uncountable.game.getWidth();
        float height = Uncountable.game.getHeight();

        float xDiff = m_rotationFactor * (delta.x / width) * camera.get(Camera.FOV);
        float yDiff = m_rotationFactor * (delta.y / height) * camera.get(Camera.FOV) / camera.get(Camera.ASPECT_RATIO);

        camera.add(Camera.YAW, xDiff);
        camera.sub(Camera.PITCH, yDiff);
    }

    /**
     * Key presses move the camera laterally by setting the goal velocity to cardinal directions relative
     * to where the camera is pointing. Camera responds to WASD/SHIFT/SPACE.
     */
    @Override
    public void onKeyDown(int key) {
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
        case GLFW_KEY_SPACE:
            m_goalVelocity = new Vector3f(Points._Y_).mul(m_speed);
            break;
        case GLFW_KEY_LEFT_SHIFT:
            m_goalVelocity = new Vector3f(Points._y_).mul(m_speed);
            break;
        }
    }

    /**
     * Once the user lets go of the key, set the goal velocity back to 0, so that the camera
     * will drift to a stop.
     */
    @Override
    public void onKeyUp(int key) {
        m_goalVelocity = new Vector3f(Points.ORIGIN_3D);
    }
}
