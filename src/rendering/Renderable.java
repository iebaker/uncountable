package rendering;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL30;

import joml.Matrix4f;

public class Renderable {

    private Map<String, Boolean> m_bufferStatus = new HashMap<String, Boolean>();
    private Map<String, Integer> m_vertexArrayIds = new HashMap<String, Integer>();
    protected FloatBuffer m_vertexData;
    protected String m_activeShaderName;
    protected Matrix4f m_modelMatrix;
    protected int m_drawingMode;
    protected int m_vertexCount;

    public Renderable() {

    }

    public void addShader(String shaderName) {
        m_bufferStatus.put(shaderName, false);
        m_vertexArrayIds.put(shaderName, GL30.glGenVertexArrays());
    }

    public void setAttribute() {

    }

    public FloatBuffer getVertexData() {
        return m_vertexData;
    }

    public String getActiveShaderName() {
        return m_activeShaderName;
    }

    public int getVertexArrayId() {
        return m_vertexArrayIds.get(m_activeShaderName);
    }

    public int getDrawingMode() {
        return m_drawingMode;
    }

    public int getVertexCount() {
        return m_vertexCount;
    }

    public void onBuffer() {
        m_bufferStatus.put(m_activeShaderName, true);
    }

    public boolean needsToBeBuffered() {
        return !m_bufferStatus.get(m_activeShaderName);
    }

    public Matrix4f getModelMatrix() {
        return m_modelMatrix;
    }
}
