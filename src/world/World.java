package world;

import application.Application;
import rendering.Camera;
import rendering.Points;
import rendering.RenderingException;

public class World {

    private Module m_currentModule;
    private Camera m_camera;

    public World() {

        m_camera = new Camera() {{
           set(Camera.YAW, (float)Math.PI);
           set(Camera.PITCH, 0.0f);
           set(Camera.HEIGHT_ANGLE, 60.0f);
           set(Camera.NEAR_PLANE, 1.0f);
           set(Camera.FAR_PLANE, 500.0f);
           set(Camera.PITCH_LIMIT, (float)(Math.PI/2.0f - 0.001f));
           set(Camera.ASPECT_RATIO, 1.0f);

           translateTo(Points.ORIGIN_3D);
        }};

        m_currentModule = Treadmill.getInitialModule();
    }

    public void update(float seconds) {

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
