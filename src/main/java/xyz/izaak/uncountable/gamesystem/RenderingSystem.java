package xyz.izaak.uncountable.gamesystem;

import xyz.izaak.radon.exception.RadonException;
import xyz.izaak.radon.gamesystem.GameSystem;
import xyz.izaak.radon.world.Camera;
import xyz.izaak.radon.world.Scene;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

/**
 * Created by ibaker on 28/11/2016.
 */
public class RenderingSystem implements GameSystem {
    private Scene scene;
    private Camera camera;

    public RenderingSystem(Scene scene, Camera camera) {
        this.scene = scene;
        this.camera = camera;
    }

    @Override
    public void update(float seconds) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        try {
            camera.capture(scene);
        } catch (RadonException e) {
            e.printStackTrace();
        }
    }
}
