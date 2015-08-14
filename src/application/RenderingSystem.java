package application;

import org.lwjgl.opengl.GL11;

import rendering.Camera;
import rendering.RenderingException;
import world.Module;
import world.World;

public class RenderingSystem extends GameSystem {
    public RenderingSystem() {
        super();
    }

    public void tick(World world, float seconds) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        Camera camera = world.getCamera();
        Module module = world.getCurrentModule();

        module.stageScene();

        try {
            camera.capture(module);
        } catch (RenderingException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
