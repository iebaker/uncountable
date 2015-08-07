package rendering;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class Renderable {

    Map<String, Boolean> m_bufferStatus = new HashMap<String, Boolean>();
    private FloatBuffer m_vertexData;

    public Renderable() {

    }

    public void addShader(String shaderName) {
        m_bufferStatus.put(shaderName, false);
    }

    public void setAttribute() {

    }

    public FloatBuffer getVertexData() {
        return m_vertexData;
    }
}
