package rendering;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import application.App;

public class Graphics {

    public static int theVAO;

    public static void initialize() throws IOException {
        Shaders.createShader("basic", "shaders/basic.vert", "shaders/basic.frag");
        Shaders.addVertexAttribute("basic", "position", 3);
        Shaders.addVertexAttribute("basic", "normal", 3);
        Shaders.addVertexAttribute("basic", "color", 3);

        System.out.println("Woot!");

//        theVAO = GL30.glGenVertexArrays();
//        GL30.glBindVertexArray(theVAO);
//
//        FloatBuffer vertices = BufferUtils.createFloatBuffer(3 * 6);
//        vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
//        vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
//        vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f);
//        vertices.flip();
//
//        int vbo = GL15.glGenBuffers();
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
//        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
//
//        int floatSize = 4;
//
//        int posAttrib = GL20.glGetAttribLocation(Shaders.getProgram("basic"), "position");
//        GL20.glEnableVertexAttribArray(posAttrib);
//        GL20.glVertexAttribPointer(posAttrib, 3, GL11.GL_FLOAT, false, 6 * floatSize, 0);
//
//        int colAttrib = GL20.glGetAttribLocation(Shaders.getProgram("basic"), "color");
//        GL20.glEnableVertexAttribArray(colAttrib);
//        GL20.glVertexAttribPointer(colAttrib, 3, GL11.GL_FLOAT, false, 6 * floatSize, 3 * floatSize);
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
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL30.glBindVertexArray(renderable.getVertexArrayId());
        GL11.glDrawArrays(renderable.getDrawingMode(), 0, renderable.getVertexCount());
        GL30.glBindVertexArray(0);
    }

    public static int getVertexArrayBound() {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL30.GL_VERTEX_ARRAY_BINDING, buffer);
        buffer.rewind();
        return buffer.get();
    }
}
