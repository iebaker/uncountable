package world;

import org.lwjgl.opengl.GL11;

import application.Application;
import rendering.Camera;
import rendering.Points;
import rendering.RenderingException;

public class World {

    private Module m_currentModule;
    private Camera m_camera;

    public World() {

        m_camera = new Camera() {{
           set(Camera.YAW, 0.0f);
           set(Camera.PITCH, 0.0f);
           set(Camera.FOV, Points.piOver(1.5f));
           set(Camera.NEAR_PLANE, 0.1f);
           set(Camera.FAR_PLANE, 500.0f);
           set(Camera.PITCH_LIMIT, Points.piOver(2) - 0.001f);
           set(Camera.ASPECT_RATIO, 1.0f);

           translateTo(Points.ORIGIN_3D);
        }};

        m_currentModule = Treadmill.getInitialModule();
    }

    public void update(float seconds) {
        m_camera.add(Camera.YAW, Points.piOver(50));
        //m_camera.translate(Points.aug3f("x__", 0.1f));
    }

    public void render() {
        m_currentModule.stageScene();
        try {
            m_camera.capture(m_currentModule);
        } catch (RenderingException e) {
            e.printStackTrace();
        }
    }
}