package world;

import application.Uncountable;
import rendering.Camera;
import rendering.Points;

public class World {

    private Module m_currentModule;
    private Camera m_camera;

    public World() {

        m_camera = new Camera() {{
           set(Camera.PITCH_LIMIT, Points.piOver(2) - 0.001f);
           set(Camera.YAW, 0.0f);
           set(Camera.PITCH, 0.0f);
           set(Camera.FOV, Points.piOver(1.5f));
           set(Camera.NEAR_PLANE, 0.1f);
           set(Camera.FAR_PLANE, 500.0f);
           set(Camera.ASPECT_RATIO, Uncountable.game.getWidth() / Uncountable.game.getHeight());

           translateTo(Points.ORIGIN_3D);
        }};

        Treadmill.importModuleTemplates("/Users/ibaker/modules_test_1.xml");
        m_currentModule = Treadmill.getInitialModule();
    }

    public Camera getCamera() {
        return m_camera;
    }

    public Module getCurrentModule() {
        return m_currentModule;
    }
}