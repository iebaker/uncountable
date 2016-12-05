package xyz.izaak.uncountable.gamesystem;

import xyz.izaak.radon.gamesystem.GameSystem;
import xyz.izaak.radon.math.Points;
import xyz.izaak.radon.world.Camera;
import xyz.izaak.radon.world.PointLight;
import xyz.izaak.radon.world.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

/**
 * Created by ibaker on 04/12/2016.
 */
public class DropLightSystem implements GameSystem {
    private Camera camera;
    private Scene scene;

    public DropLightSystem(Scene scene, Camera camera) {
        this.camera = camera;
        this.scene = scene;
    }

    @Override
    public void onKeyUp(int key) {
        if (key == GLFW_KEY_SPACE) {
            PointLight pointLight = new PointLight(Points.randomUnit3f(), Points.copyOf(camera.getEye()));
            scene.addPointLight(pointLight);
        }
    }
}
