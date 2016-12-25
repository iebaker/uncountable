package xyz.izaak.uncountable.gamesystem;

import org.joml.Vector3f;
import xyz.izaak.radon.Channel;
import xyz.izaak.radon.exception.RadonException;
import xyz.izaak.radon.gamesystem.GameSystem;
import xyz.izaak.radon.math.Points;
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

    @Override
    public void initialize() {
        camera = Camera.builder()
                .aspectRatio(1)
                .fov(Points.piOver(3))
                .eye(new Vector3f(10, 0, 1))
                .look(new Vector3f(-1, 0, 0).normalize())
                .up(Points.__Z)
                .maxPortalDepth(5)
                .build();

        Channel.request(Scene.class, Channel.CURRENT_SCENE).subscribe(scene -> this.scene = scene);
        Channel.request(Camera.class, Channel.CURRENT_CAMERA).publish(camera);
    }

    @Override
    public void update(float seconds) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        try {
            camera.capture(scene);
        } catch (RadonException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
