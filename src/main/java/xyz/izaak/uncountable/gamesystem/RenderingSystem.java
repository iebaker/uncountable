package xyz.izaak.uncountable.gamesystem;

import xyz.izaak.radon.exception.RenderingException;
import xyz.izaak.radon.gamesystem.GameSystem;
import xyz.izaak.radon.math.Points;
import xyz.izaak.radon.primitive.FilledStroked;
import xyz.izaak.radon.primitive.Primitive;
import xyz.izaak.radon.shading.Shader;
import xyz.izaak.radon.shading.ShaderCompiler;
import xyz.izaak.radon.world.Camera;
import xyz.izaak.radon.world.Entity;
import xyz.izaak.radon.world.Scene;
import xyz.izaak.radon.world.arg.CameraConstructionArg;
import xyz.izaak.uncountable.rendering.BasicShaderComponents;

import java.io.IOException;

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

    public RenderingSystem(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void initialize() {
        Shader shader = null;
        try {
            shader = ShaderCompiler.instance()
                    .with(Primitive.class)
                    .with(FilledStroked.class)
                    .with(Entity.class)
                    .with(BasicShaderComponents.class)
                    .with(Camera.class)
                    .compile("basic");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        CameraConstructionArg cameraArg = new CameraConstructionArg.Builder().aspectRatio(1).build();
        camera = new Camera(shader, cameraArg);
        camera.translate(Points.copyOf(Points.__z).mul(10));
    }

    @Override
    public void update(float seconds) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        try {
            camera.capture(scene);
        } catch (RenderingException e) {
            e.printStackTrace();
        }
    }
}
