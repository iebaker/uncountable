package core.rendering;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Graphics {

    public static void initialize() throws IOException {
        Shaders.createShader("basic", "basic.vert", "basic.frag");
        Shaders.addVertexAttribute("basic", "position", 3);
        Shaders.addVertexAttribute("basic", "normal", 3);
        Shaders.addVertexAttribute("basic", "fillColor", 3);
        Shaders.addVertexAttribute("basic", "strokeColor", 3);
    }

    public static void buffer(Renderable renderable) {
        GL30.glBindVertexArray(renderable.getVertexArrayId());

        int vertexBufferId;
        GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, renderable.getVertexData(), GL15.GL_STATIC_DRAW);

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
                    Shaders.getAttributeStrideFor(shaderName),
                    attribute.getOffset());
        }

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        renderable.onBuffer();
    }
}
