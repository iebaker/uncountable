package gamesystems.architecture.setpieces;

import joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class BasicColoredQuad extends AbstractQuad {

    private Vector3f m_color;

    public BasicColoredQuad(Vector3f color) {
        this(color, GL11.GL_FILL);
    }

    public BasicColoredQuad(Vector3f color, int polygonMode) {
        super(polygonMode);
        m_color = color;
        setShader("basic");
    }

    @Override
    public void build() {
        super.build();
        setVertexAttribute("vertexColor", m_color);
        setVertexAttribute("vertexColor", m_color);
        setVertexAttribute("vertexColor", m_color);
        setVertexAttribute("vertexColor", m_color);
        setVertexAttribute("vertexColor", m_color);
        setVertexAttribute("vertexColor", m_color);
    }
}
