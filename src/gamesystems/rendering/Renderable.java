package gamesystems.rendering;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import joml.AxisAngle4f;
import joml.Matrix4f;
import joml.Vector2f;
import joml.Vector3f;
import joml.Vector4f;

public abstract class Renderable {

    private boolean m_bufferStatus = false;
    private int m_vertexArrayId;
    private Map<String, List<Float>> m_vertexData = new HashMap<String, List<Float>>();
    private String m_shaderName;
    private Matrix4f m_modelMatrix;
    private int m_drawingMode;
    private int m_vertexCount;

    public Renderable(int count, int drawingMode) {
        m_vertexCount = count;
        m_drawingMode = drawingMode;
        m_modelMatrix = new Matrix4f();
        m_vertexArrayId = GL30.glGenVertexArrays();
    }

    public abstract void build();

    public void setVertexAttribute(String attributeName, Vector2f value) {
        setVertexAttribute(attributeName, value.x, value.y);
    }

    public void setVertexAttribute(String attributeName, Vector3f value) {
        setVertexAttribute(attributeName, value.x, value.y, value.z);
    }

    public void setVertexAttribute(String attributeName, Vector4f value) {
        setVertexAttribute(attributeName, value.x, value.y, value.z, value.w);
    }

    public void scale(float factor) {
        m_modelMatrix = new Matrix4f().scaling(factor).mul(m_modelMatrix);
    }

    public void scale(float x, float y, float z) {
        m_modelMatrix = new Matrix4f().scaling(x, y, z).mul(m_modelMatrix);
    }

    public void scale(Vector3f factor) {
        m_modelMatrix = new Matrix4f().scaling(factor.x, factor.y, factor.z).mul(m_modelMatrix);
    }

    public void translate(float x, float y, float z) {
        m_modelMatrix = new Matrix4f().translation(x, y, z).mul(m_modelMatrix);
    }

    public void translate(Vector3f distance) {
        m_modelMatrix = new Matrix4f().translation(distance).mul(m_modelMatrix);
    }

    public void rotate(float amount, Vector3f axis) {
        m_modelMatrix = new Matrix4f().rotation(new AxisAngle4f(amount, axis)).mul(m_modelMatrix);
    }

    public void rotate(float amount, float x, float y, float z) {
        m_modelMatrix = new Matrix4f().rotation(new AxisAngle4f(amount, x, y, z)).mul(m_modelMatrix);
    }

    public void clearTransforms() {
        m_modelMatrix = new Matrix4f();
    }

    public String getActiveShaderName() {
        return m_shaderName;
    }

    public int getVertexArrayId() {
        return m_vertexArrayId;
    }

    public int getDrawingMode() {
        return m_drawingMode;
    }

    public int getVertexCount() {
        return m_vertexCount;
    }

    public void onBuffer() {
        m_bufferStatus = true;
    }

    public boolean needsToBeBuffered() {
        return !m_bufferStatus;
    }

    public Matrix4f getModelMatrix() {
        return m_modelMatrix;
    }

    public void setVertexAttribute(String attributeName, float... values) {
        if(!m_vertexData.containsKey(attributeName)) {
            m_vertexData.put(attributeName, new ArrayList<Float>());
        }
        for(float value : values) {
            m_vertexData.get(attributeName).add(value);
        }
    }

    public void setShader(String shaderName) {
        if(Shaders.exists(shaderName)) {
            m_shaderName = shaderName;
        } else {
            throw new IllegalArgumentException(String.format("%s is not a valid shader", shaderName));
        }
    }

    public FloatBuffer getVertexData() throws RenderingException {
        if(!Shaders.exists(m_shaderName)) return BufferUtils.createFloatBuffer(0);

        //Allocate a float buffer to store the vertex data
        int bufferSize = m_vertexCount * Shaders.getAttributeStrideFor(m_shaderName);
        float data[] = new float[bufferSize];
        int index = 0;

        // Grab the attributes for this shader, and sort them in increasing order by offset
        List<VertexAttribute> attributes = Shaders.getVertexAttributesFor(m_shaderName);
        Collections.sort(attributes, new Comparator<VertexAttribute>() {
            @Override
            public int compare(VertexAttribute va1, VertexAttribute va2) {
                Integer offset1 = va1.getOffset();
                Integer offset2 = va2.getOffset();
                return offset1.compareTo(offset2);
            }
        });

        // Fill in vertex data for each vertex
        for(int i = 0; i < m_vertexCount; ++i) {
            for(VertexAttribute attribute : attributes) {
                String attributeName = attribute.getName();
                int attributeLength = attribute.getLength();

                if(!m_vertexData.containsKey(attributeName)) {
                    String message = String.format("%s does not have data for attribute '%s' required by shader '%s'",
                            getClass().getName(), attributeName, m_shaderName);
                    throw new RenderingException(message);
                }

                for(int j = 0; j < attributeLength; ++j) {
                    float datum = m_vertexData.get(attributeName).remove(0);
                    data[index++] = datum;
                    m_vertexData.get(attributeName).add(datum);
                }
            }
        }

        FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(bufferSize);
        dataBuffer.put(data);
        dataBuffer.flip();
        return dataBuffer;
    }
}
