package application;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import rendering.Camera;
import rendering.RenderingException;
import world.Module;

class Link {}

public class RenderingSystem extends GameSystem {
    public RenderingSystem() {
        super();
    }

    @Override
    public void tick(float seconds) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Camera camera = Uncountable.game.getWorld().getCamera();
        Module module = Uncountable.game.getWorld().getCurrentModule();

        module.stageScene();

        try {
            camera.capture(module);
        } catch (RenderingException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
