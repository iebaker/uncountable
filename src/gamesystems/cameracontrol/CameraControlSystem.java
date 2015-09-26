package gamesystems.cameracontrol;

import core.Uncountable;
import gamesystems.GameSystem;
import gamesystems.rendering.Camera;
import gamesystems.rendering.Points;
import joml.Vector2f;
import joml.Vector3f;
import gamesystems.architecture.Module;
import gamesystems.architecture.Seam;
import core.World;
import gamesystems.architecture.setpieces.Portal;

import static org.lwjgl.glfw.GLFW.*;

/**
 * This is a GameSystem responsible for collecting input events and using them to move the camera
 * around the scene. It probably won't exist in the final game, as there will be a Player class which
 * is responsible for maintaining camera motion, but in the mean time this will do for debugging the
 * rendering and world generation systems.
 */
public class CameraControlSystem extends GameSystem {

    private Vector3f m_goalVelocity = Points.ORIGIN_3D.get();
    private Vector3f m_realVelocity = Points.ORIGIN_3D.get();
    private Vector3f m_previousLocation = Points.ORIGIN_3D.get();
    private float m_accelerationFactor = 0.2f;
    private float m_rotationFactor = 2.4f;
    private float m_speed = 4.0f;

    public CameraControlSystem() {
        super();
    }

    @Override
    public void initialize() {
        Uncountable.game.world.camera = new Camera() {{
            set(Camera.PITCH_LIMIT, Points.piOver(2) - 0.001f);
            set(Camera.FOV, Points.piOver(2.0f));
            set(Camera.NEAR_PLANE, 0.1f);
            set(Camera.FAR_PLANE, 500.0f);
            set(Camera.ASPECT_RATIO, Uncountable.game.getWidth() / Uncountable.game.getHeight());
            translateTo(Points.XYZ);
            setLook(Points.__z.get());
        }};
    }

    @Override
    public void tick(float seconds) {
        Camera camera = Uncountable.game.world.camera;
        m_previousLocation = camera.getEye();

        m_realVelocity.add(m_goalVelocity.get().sub(m_realVelocity).mul(m_accelerationFactor));
        camera.translate(m_realVelocity.get().mul(seconds));
        checkPortals();
        m_goalVelocity = Points.ORIGIN_3D.get();
    }

    public void checkPortals() {
        World world = Uncountable.game.world;
        Camera camera = Uncountable.game.world.camera;
        Module currentModule = world.currentModule;

        for(Portal portal : currentModule.getTemplate().getPortals()) {
            if(portal.crossedBy(m_previousLocation, camera.getEye())) {
                Seam seam = new Seam(portal, currentModule.getLinkedPortal(portal));

                camera.translateTo(seam.transformPoint(camera.getEye()));
                camera.setLook(seam.transformVector(camera.getLook()));

                m_realVelocity = seam.transformVector(m_realVelocity);
                m_goalVelocity = seam.transformVector(m_goalVelocity);

                Module nextModule = currentModule.getNeighbor(portal);
                if(nextModule != null) {
                    world.currentModule = nextModule;
                } else {
                    System.out.println("Null next");
                }
                break;
            }
        }
    }

    @Override
    public void onMouseMove(Vector2f delta) {
        Camera camera = Uncountable.game.world.camera;
        float width = Uncountable.game.getWidth();
        float height = Uncountable.game.getHeight();

        float xDiff = m_rotationFactor * (delta.x / width) * camera.get(Camera.FOV);
        float yDiff = m_rotationFactor * (delta.y / height) * camera.get(Camera.FOV) / camera.get(Camera.ASPECT_RATIO);

        camera.add(Camera.YAW, xDiff);
        camera.sub(Camera.PITCH, yDiff);
    }

    @Override
    public void onKeyHeld(int key) {
        doKeys(key);
    }

    @Override
    public void onKeyDown(int key) {
        doKeys(key);
    }

    private void doKeys(int key) {
        Camera camera = Uncountable.game.world.camera;
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
        case GLFW_KEY_Q:
            m_goalVelocity = new Vector3f(Points._y_).mul(m_speed);
            break;
        }
    }
}
