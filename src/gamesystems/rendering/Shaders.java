package gamesystems.rendering;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2fv;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniform4fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Uncountable;
import joml.Matrix3f;
import joml.Matrix4f;
import joml.Vector2f;
import joml.Vector3f;
import joml.Vector4f;

@SuppressWarnings("unused")
public class Shaders {

    private static Map<String, Integer> m_shaderPrograms = new HashMap<String, Integer>();
    private static Map<String, List<VertexAttribute>> m_vertexAttributes = new HashMap<String, List<VertexAttribute>>();
    private static String m_activeShader;

    public static void createShader(String shaderName, String vertexFilename, String fragmentFilename)
            throws IOException, RuntimeException {

        int status;

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        String vertexShaderSource = Uncountable.stringFromFile(vertexFilename);
        parseForVertexAttributes(shaderName, vertexShaderSource);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if(status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(vertexShader));
        }

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        String fragmentShaderSource = Uncountable.stringFromFile(fragmentFilename);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        status = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if(status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(fragmentShader));
        }

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glBindFragDataLocation(shaderProgram, 0, "fragColor");
        glLinkProgram(shaderProgram);

        status = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(status != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(shaderProgram));
        }

        glDetachShader(shaderProgram, vertexShader);
        glDetachShader(shaderProgram, fragmentShader);

        m_shaderPrograms.put(shaderName, shaderProgram);
    }

    private static void parseForVertexAttributes(String shaderName, String shaderSource) {
        String attributeName;
        int attributeLength;
        for(String line : shaderSource.split("\n")) {
            if(line.startsWith("in")) {
                String[] tokens = line.split(" ");
                attributeName = tokens[2].substring(0, tokens[2].length() - 1);
                try {
                    attributeLength = Integer.parseInt(tokens[1].substring(tokens[1].length() - 1));
                } catch (NumberFormatException e) {
                    attributeLength = 1;
                }
                addVertexAttribute(shaderName, attributeName, attributeLength);
            }
        }
    }

    private static void addVertexAttribute(String shaderName, String attributeName, int attributeLength) {
        if(!m_vertexAttributes.containsKey(shaderName)) {
            m_vertexAttributes.put(shaderName, new ArrayList<>());
        }
        int computedOffset = getAttributeStrideFor(shaderName);
        m_vertexAttributes.get(shaderName).add(new VertexAttribute(attributeName, attributeLength, computedOffset));
    }

    public static List<VertexAttribute> getVertexAttributesFor(String shaderName) {
        if(!m_vertexAttributes.containsKey(shaderName)) {
            return new ArrayList<>();
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
        glUseProgram(getProgram(name));
        m_activeShader = name;
    }

    public static int getProgram(String name) {
        return m_shaderPrograms.get(name);
    }

    public static int getUniformLocation(String name) {
        return glGetUniformLocation(getProgram(m_activeShader), name);
    }

    public static void setShaderUniform(String name, Matrix4f value) {
        int uniformLocation = getUniformLocation(name);
        if(uniformLocation >= 0) glUniformMatrix4fv(uniformLocation, false, value.getBuffer());
    }

    public static void setShaderUniform(String name, Matrix3f value) {
        int uniformLocation = getUniformLocation(name);
        if(uniformLocation >= 0) glUniformMatrix3fv(uniformLocation, false, value.getBuffer());
    }

    public static void setShaderUniform(String name, Vector4f value) {
        int uniformLocation = getUniformLocation(name);
        if(uniformLocation >= 0) glUniform4fv(uniformLocation, value.getBuffer());
    }

    public static void setShaderUniform(String name, Vector3f value) {
        int uniformLocation = getUniformLocation(name);
        if(uniformLocation >= 0) glUniform3fv(uniformLocation, value.getBuffer());
    }

    public static void setShaderUniform(String name, Vector2f value) {
        int uniformLocation = getUniformLocation(name);
        if(uniformLocation >= 0) glUniform2fv(uniformLocation, value.getBuffer());
    }

    public static void setShaderUniform(String name, float value) {
        int uniformLocation = getUniformLocation(name);
        if(uniformLocation >= 0) glUniform1f(uniformLocation, value);
    }

    public static void setShaderUniform(String name, int value) {
        int uniformLocation = getUniformLocation(name);
        if(uniformLocation >= 0) glUniform1i(uniformLocation, value);
    }

    public static void setShaderUniform(String name, boolean value) {
        int uniformLocation = getUniformLocation(name);
        if(uniformLocation >= 0) glUniform1i(uniformLocation, value ? 1 : 0);
    }

    public static boolean exists(String shaderName) {
        return m_shaderPrograms.containsKey(shaderName);
    }
}
