package world;

import application.Uncountable;
import gamesystems.rendering.Camera;
import gamesystems.rendering.Points;

public class World {

    private Module m_currentModule;
    private Camera m_camera;

    public World() {

        m_camera = new Camera() {{
           set(Camera.PITCH_LIMIT, Points.piOver(2) - 0.001f);
           set(Camera.FOV, Points.piOver(2.0f));
           set(Camera.NEAR_PLANE, 0.1f);
           set(Camera.FAR_PLANE, 500.0f);
           set(Camera.ASPECT_RATIO, Uncountable.game.getWidth() / Uncountable.game.getHeight());

           translateTo(Points.X_Z.get().mul(1.5f));
           setLook(Points.__z.get());
        }};

        Architect.importModuleTemplates();
        m_currentModule = Architect.getInitialModule();
    }

    public Camera getCamera() {
        return m_camera;
    }

    public Module getCurrentModule() {
        return m_currentModule;
    }
}