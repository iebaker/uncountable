package gamesystems.rendering;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;


import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class Graphics {

    public static int theVAO;

    public static void initialize() throws IOException {
        Shaders.createShader("basic", "resources/shaders/basic.vert", "resources/shaders/basic.frag");
        Shaders.createShader("stenciler", "resources/shaders/stenciler.vert", "resources/shaders/stenciler.frag");
        Shaders.createShader("flat", "resources/shaders/flat.vert", "resources/shaders/flat.frag");
    }

    public static void buffer(Renderable renderable) throws RenderingException {

        GL30.glBindVertexArray(renderable.getVertexArrayId());

        renderable.build();
        FloatBuffer vertexData = renderable.getVertexData();

        int vertexBufferId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);

        String shaderName = renderable.getActiveShaderName();
        int floatSize = 4;

        for(VertexAttribute attribute : Shaders.getVertexAttributesFor(shaderName)) {

            int attributeLocation = GL20.glGetAttribLocation(
                    Shaders.getProgram(shaderName),
                    attribute.getName());

            GL20.glEnableVertexAttribArray(attributeLocation);

            GL20.glVertexAttribPointer(
                    attributeLocation,
                    attribute.getLength(),
                    GL11.GL_FLOAT,
                    false,
                    floatSize * Shaders.getAttributeStrideFor(shaderName),
                    floatSize * attribute.getOffset());
        }

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        renderable.onBuffer();
    }

    public static void draw(Renderable renderable) {
        GL30.glBindVertexArray(renderable.getVertexArrayId());

        GL20.glValidateProgram(Graphics.getActiveShader());
        int status = GL20.glGetProgrami(Graphics.getActiveShader(), GL20.GL_VALIDATE_STATUS);
        if(status != GL11.GL_TRUE) {
            throw new RuntimeException(GL20.glGetProgramInfoLog(Graphics.getActiveShader()));
        }

        GL11.glDrawArrays(renderable.getDrawingMode(), 0, renderable.getVertexCount());
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, renderable.getPolygonMode());
        GL30.glBindVertexArray(0);
    }

    public static int getVertexArrayBound() {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL30.GL_VERTEX_ARRAY_BINDING, buffer);
        buffer.rewind();
        return buffer.get();
    }

    public static int getActiveShader() {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL20.GL_CURRENT_PROGRAM, buffer);
        buffer.rewind();
        return buffer.get();
    }
}
