package xyz.izaak.gamesystems.rendering;

import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.izaak.core.Settings;
import org.lwjgl.BufferUtils;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class Renderable {

    public class Interval {
        public Interval(int m, int f, int c) {
            mode = m;
            first = f;
            count = c;
        }
        public int mode;
        public int first;
        public int count;
    }

    private boolean m_bufferStatus = false;
    private int m_vertexArrayId = glGenVertexArrays();
    private Map<String, List<Float>> m_vertexData = new HashMap<>();
    private Map<String, List<Float>> m_defaultVertexData = new HashMap<>();
    private List<Interval> m_intervals = new ArrayList<>();
    private String m_shaderName;
    private Matrix4f m_modelMatrix = new Matrix4f();
    private int m_vertexCount = 0;
    private Settings m_uniforms;
    private Settings m_renderSettings;

    public Renderable() {
        m_uniforms = () -> Shaders.setShaderUniform("model", getModelMatrix());
        m_renderSettings = () -> {};
    }

    public abstract void build();

    public void next(String attributeName, Vector2f value) {
        next(1, attributeName, value.x, value.y);
    }

    public void next(String attributeName, Vector3f value) {
        next(1, attributeName, value.x, value.y, value.z);
    }

    public void next(String attributeName, Vector4f value) {
        next(1, attributeName, value.x, value.y, value.z, value.w);
    }

    public void next(int count, String attributeName, Vector2f value) {
        next(count, attributeName, value.x, value.y);
    }

    public void next(int count, String attributeName, Vector3f value) {
        next(count, attributeName, value.x, value.y, value.z);
    }

    public void next(int count, String attributeName, Vector4f value) {
        next(count, attributeName, value.x, value.y, value.z, value.w);
    }

    public void range(int start, int count, String attributeName, Vector2f value) {
        range(start, count, attributeName, value.x, value.y);
    }

    public void range(int start, int count, String attributeName, Vector3f value) {
        range(start, count, attributeName, value.x, value.y, value.z);
    }

    public void range(int start, int count, String attributeName, Vector4f value) {
        range(start, count, attributeName, value.x, value.y, value.z, value.w);
    }

    public void all(String attributeName, Vector2f value) {
        all(attributeName, value.x, value.y);
    }

    public void all(String attributeName, Vector3f value) {
        all(attributeName, value.x, value.y, value.z);
    }

    public void all(String attributeName, Vector4f value) {
        all(attributeName, value.x, value.y, value.z, value.w);
    }

    public void next(String attributeName, float... values) {
        next(1, attributeName, values);
    }

    public void next(int count, String attributeName, float... values) {
        m_vertexData.putIfAbsent(attributeName, new ArrayList<>());
        for(int i = 0; i < count; ++i) {
            for (float value : values) {
                m_vertexData.get(attributeName).add(value);
            }
        }
    }

    public void range(int start, int count, String attributeName, float... values) {
        m_vertexData.putIfAbsent(attributeName, new ArrayList<>());
        List<Float> existingData = m_vertexData.get(attributeName);
        int arity = values.length;

        if(existingData.size() % arity != 0) {
            System.err.println("Vertex attribute range arity failure");
            return;
        }

        if(start <= existingData.size() / arity) {
            for(int i = 0; i < count; ++i) {
                for(int j = 0; j < values.length; ++j) {
                    existingData.add(((start + i) * arity) + j, values[j]);
                }
            }
        } else {
            System.err.println("Vertex attribute range start failure");
        }
    }

    public void all(String attributeName, float... values) {
        m_defaultVertexData.putIfAbsent(attributeName, new ArrayList<>());
        m_defaultVertexData.get(attributeName).clear();
        for(float value : values) {
            m_defaultVertexData.get(attributeName).add(value);
        }
    }

    public void around(int n, int max, int direction, Settings settings) {

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

    public void onBuffer() {
        m_bufferStatus = true;
    }

    public boolean needsToBeBuffered() {
        return !m_bufferStatus;
    }

    public Matrix4f getModelMatrix() {
        return m_modelMatrix;
    }

    public void setShader(String shaderName) {
        if(Shaders.exists(shaderName)) {
            m_shaderName = shaderName;
        } else {
            throw new IllegalArgumentException(String.format("%s is not a valid shader", shaderName));
        }
    }

    public Settings getUniforms() {
        return m_uniforms;
    }

    public void setRenderSettings(Settings settings) {
        m_renderSettings = settings;
    }

    public Settings getRenderSettings() {
        return m_renderSettings;
    }

    protected void addInterval(int mode, int count) {
        m_intervals.add(new Interval(mode, m_vertexCount, count));
        m_vertexCount += count;
    }

    public List<Interval> getIntervals() {
        return m_intervals;
    }

    public FloatBuffer getVertexData() throws RenderingException {
        if(!Shaders.exists(m_shaderName)) throw new RenderingException("Nonexistent shader " + m_shaderName);

        int bufferSize = m_vertexCount * Shaders.getAttributeStrideFor(m_shaderName);
        float data[] = new float[bufferSize];

        List<VertexAttribute> attributes = Shaders.getVertexAttributesFor(m_shaderName);
        attributes.sort((va1, va2) -> {
            Integer offset1 = va1.getOffset();
            Integer offset2 = va2.getOffset();
            return offset1.compareTo(offset2);
        });

        int index = 0;
        for(int i = 0; i < m_vertexCount; ++i) {
            for(VertexAttribute attribute : attributes) {
                String attributeName = attribute.getName();
                int attributeLength = attribute.getLength();

                List<Float> values;
                if(m_defaultVertexData.containsKey(attributeName)) {
                    values = m_defaultVertexData.get(attributeName);
                } else if(m_vertexData.containsKey(attributeName)) {
                    values = m_vertexData.get(attributeName);
                } else {
                    String message = String.format("%s does not have data for attribute '%s' required by shader '%s'",
                            getClass().getName(), attributeName, m_shaderName);
                    throw new RenderingException(message);
                }

                if(values.size() < attributeLength) {
                    String message = String.format("%s does not have enough data for attribute '%s' required by shader '%s'",
                            getClass().getName(), attributeName, m_shaderName);
                    throw new RenderingException(message);
                }

                for(int j = 0; j < attributeLength; ++j) {
                    float datum = values.remove(0);
                    data[index++] = datum;
                    values.add(datum);
                }
            }
        }

        FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(bufferSize);
        dataBuffer.put(data);
        dataBuffer.flip();
        return dataBuffer;
    }
}
