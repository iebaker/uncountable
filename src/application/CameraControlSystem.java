package application;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import joml.Vector2f;
import joml.Vector3f;
import rendering.Camera;

public class CameraControlSystem extends GameSystem {

    private float m_movementSpeedFactor = 0.1f;

    public CameraControlSystem() {
        super();
    }

    @Override
    public void onMouseMove(Vector2f position, Vector2f delta) {
        Camera camera = Uncountable.game.getWorld().getCamera();
        float width = Uncountable.game.getWidth();
        float height = Uncountable.game.getHeight();

        if(!inWindow(position)) return;

        float xDiff = (delta.x / width) * camera.get(Camera.FOV);
        float yDiff = (delta.y / height) * camera.get(Camera.FOV) / camera.get(Camera.ASPECT_RATIO);

        camera.sub(Camera.YAW, xDiff);
        camera.add(Camera.PITCH, yDiff);
    }

    @Override
    public void onKeyHeld(int key) {
        Camera camera = Uncountable.game.getWorld().getCamera();
        switch(key) {
        case GLFW_KEY_W:
            camera.translate(new Vector3f(camera.getHeading()).mul(m_movementSpeedFactor));
            break;
        case GLFW_KEY_A:
            camera.translate(new Vector3f(camera.getRight()).mul(m_movementSpeedFactor));
            break;
        case GLFW_KEY_S:
            camera.translate(new Vector3f(camera.getHeading()).mul(-m_movementSpeedFactor));
            break;
        case GLFW_KEY_D:
            camera.translate(new Vector3f(camera.getRight()).mul(-m_movementSpeedFactor));
            break;
        }
    }

    private boolean inWindow(Vector2f position) {
        return position.x > 0 && position.x < Uncountable.game.getWidth() &&
               position.y > 0 && position.y < Uncountable.game.getHeight();
    }
}
