package xyz.izaak.uncountable.gamesystem;

import xyz.izaak.radon.Resource;
import xyz.izaak.radon.exception.RenderingException;
import xyz.izaak.radon.gamesystem.GameSystem;
import xyz.izaak.radon.math.Points;
import xyz.izaak.radon.rendering.Camera;
import xyz.izaak.radon.rendering.primitive.FilledStroked;
import xyz.izaak.radon.rendering.primitive.Primitive;
import xyz.izaak.radon.rendering.primitive.Quad;
import xyz.izaak.radon.rendering.shading.Shader;
import xyz.izaak.radon.rendering.shading.ShaderCompiler;
import xyz.izaak.radon.rendering.shading.ShaderComponents;
import xyz.izaak.radon.rendering.shading.ShaderVariableType;
import xyz.izaak.radon.rendering.shading.annotation.FragmentShaderBlock;
import xyz.izaak.radon.rendering.shading.annotation.ProvidesShaderComponents;
import xyz.izaak.radon.rendering.shading.annotation.ShaderUniform;
import xyz.izaak.radon.rendering.shading.annotation.VertexShaderBlock;
import xyz.izaak.radon.rendering.shading.annotation.VertexShaderOutput;
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

    public RenderTestGameSystem() {
        this.testScene = new Scene();
    }

    @Override
    public void initialize() {
        Shader shader = null;
        try {
            shader = ShaderCompiler.instance()
                    .with(Primitive.class)
                    .with(FilledStroked.class)
                    .with(Camera.class)
                    .with(BasicShaderComponents.class)
                    .compile("basic");
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

        Quad testQuad = new Quad();
        testQuad.setFillColor(Points.RED);
        testQuad.setStrokeColor(Points.WHITE);
        //testQuad.rotate(Points.piOver(8), Points._Y_);

        Entity testEntity = new Entity();
        testEntity.addPrimitives(testQuad);
        testScene.addEntity(testEntity);
    }

    @Override
    public void update(float seconds) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        try {
            camera.capture(testScene);
        } catch (RenderingException e) {
            e.printStackTrace();
        }
    }
}
