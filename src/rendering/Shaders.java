package rendering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import application.App;
import joml.Matrix3f;
import joml.Matrix4f;
import joml.Vector2f;
import joml.Vector3f;
import joml.Vector4f;

public class Shaders {
    private static Map<String, Integer> m_shaderPrograms = new HashMap<String, Integer>();
    private static Map<String, List<VertexAttribute>> m_vertexAttributes = new HashMap<String, List<VertexAttribute>>();
    private static String m_activeShader;

    public static void createShader(String shaderName, String vertexFilename, String fragmentFilename)
            throws IOException, RuntimeException {

        int status;

        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        String vertexShaderSource = App.stringFromFile(shaderName);
        GL20.glShaderSource(vertexShader, vertexShaderSource);
        GL20.glCompileShader(vertexShader);

        status = GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS);
        if(status != GL11.GL_TRUE) {
            throw new RuntimeException(GL20.glGetShaderInfoLog(vertexShader));
        }

        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        String fragmentShaderSource = App.stringFromFile(shaderName);
        GL20.glShaderSource(fragmentShader, fragmentShaderSource);
        GL20.glCompileShader(fragmentShader);

        status = GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS);
        if(status != GL11.GL_TRUE) {
            throw new RuntimeException(GL20.glGetShaderInfoLog(fragmentShader));
        }

        int shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL30.glBindFragDataLocation(shaderProgram, 0, "fragColor");
        GL20.glLinkProgram(shaderProgram);

        status = GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS);
        if(status != GL11.GL_TRUE) {
            throw new RuntimeException(GL20.glGetProgramInfoLog(shaderProgram));
        }

        m_shaderPrograms.put(shaderName, shaderProgram);
    }

    public static void addVertexAttribute(String shaderName, String attributeName, int attributeLength) {
        if(!m_vertexAttributes.containsKey(shaderName)) {
            m_vertexAttributes.put(shaderName, new ArrayList<VertexAttribute>());
        }
        int computedOffset = getAttributeStrideFor(shaderName);
        m_vertexAttributes.get(shaderName).add(new VertexAttribute(attributeName, attributeLength, computedOffset));
    }

    public static List<VertexAttribute> getVertexAttributesFor(String shaderName) {
        if(!m_vertexAttributes.containsKey(shaderName)) {
            return new ArrayList<VertexAttribute>();
        }
        return m_vertexAttributes.get(shaderName);
    }

    public static int getAttributeStrideFor(String shaderName) {
        if(!m_vertexAttributes.containsKey(shaderName)) {
            return 0;
        }
        int stride = 0;
        for(VertexAttribute attribute : m_vertexAttributes.get(shaderName)) {
            stride += attribute.getLength();
        }
        return stride;
    }

    public static void useShader(String name) {
        GL20.glUseProgram(getProgram(name));
    }

    public static void useProgram(int shaderProgram) {
        GL20.glUseProgram(shaderProgram);
    }

    public static int getProgram(String name) {
        return m_shaderPrograms.get(name);
    }

    public static int getUniformLocation(String name) {
        return GL20.glGetUniformLocation(getProgram(m_activeShader), name);
    }

    public static void setShaderUniform(String name, Matrix4f value) {
        GL20.glUniformMatrix4fv(getUniformLocation(name), false, value.getBuffer());
    }

    public static void setShaderUniform(String name, Matrix3f value) {
        GL20.glUniformMatrix3fv(getUniformLocation(name), false, value.getBuffer());
    }

    public static void setShaderUniform(String name, Vector4f value) {
        GL20.glUniform4fv(getUniformLocation(name), value.getBuffer());
    }

    public static void setShaderUniform(String name, Vector3f value) {
        GL20.glUniform3fv(getUniformLocation(name), value.getBuffer());
    }

    public static void setShaderUniform(String name, Vector2f value) {
        GL20.glUniform2fv(getUniformLocation(name), value.getBuffer());
    }

    public static void setShaderUniform(String name, float value) {
        GL20.glUniform1f(getUniformLocation(name), value);
    }

    public static void setShaderUniform(String name, int value) {
        GL20.glUniform1i(getUniformLocation(name), value);
    }

    public static boolean exists(String shaderName) {
        return m_shaderPrograms.containsKey(shaderName);
    }
}
