package xyz.izaak.uncountable.gamesystem;

import org.joml.Matrix4f;
import xyz.izaak.radon.exception.RenderingException;
import xyz.izaak.radon.gamesystem.GameSystem;
import xyz.izaak.radon.math.Points;
import xyz.izaak.radon.rendering.Camera;
import xyz.izaak.radon.rendering.primitive.FilledStroked;
import xyz.izaak.radon.rendering.primitive.Primitive;
import xyz.izaak.radon.rendering.primitive.Quad;
import xyz.izaak.radon.rendering.shading.Shader;
import xyz.izaak.radon.rendering.shading.ShaderCompiler;
import xyz.izaak.radon.world.Entity;
import xyz.izaak.radon.world.Scene;
import xyz.izaak.uncountable.rendering.BasicShaderComponents;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static xyz.izaak.radon.rendering.Camera.ASPECT_RATIO;
import static xyz.izaak.radon.rendering.Camera.FAR_PLANE;
import static xyz.izaak.radon.rendering.Camera.FOV;
import static xyz.izaak.radon.rendering.Camera.NEAR_PLANE;

/**
 * Created by ibaker on 26/11/2016.
 */
public class RenderTestGameSystem implements GameSystem {
    private Camera camera;
    private Scene testScene;
    private Quad testQuad;
    private Entity testEntity;

    public RenderTestGameSystem() {
        this.testScene = new Scene();
    }

    @Override
    public void initialize() {
        try {
            System.out.println(Shader.class.getMethod("setUniform", String.class, Matrix4f.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Shader shader = null;
        try {
            shader = ShaderCompiler.instance().with(Primitive.class).with(FilledStroked.class).with(Camera.class)
                    .with(Entity.class).with(BasicShaderComponents.class).compile("basic");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (shader != null) {
            System.out.println(shader.getFragmentSource());
            System.out.println(shader.getVertexSource());
        } else {
            System.exit(1);
        }

        camera = new Camera(shader);
        camera.set(NEAR_PLANE, 0.2f);
        camera.set(FAR_PLANE, 1000.0f);
        camera.set(ASPECT_RATIO, 1);
        camera.set(FOV, Points.piOver(2));
        camera.translate(Points.__z);

        testQuad = new Quad();
        testQuad.setFillColor(Points.RED);
        testQuad.setStrokeColor(Points.WHITE);

        testEntity = new Entity();
        testEntity.addPrimitives(testQuad);
        testScene.addEntity(testEntity);
    }

    @Override
    public void update(float seconds) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        testQuad.rotate(Points.piOver(100), Points._Y_);
        testEntity.rotate(Points.piOver(80), Points.__Z);
        try {
            camera.capture(testScene);
        } catch (RenderingException e) {
            e.printStackTrace();
        }
    }
}
