package gamesystems.architecture.setpieces;

import joml.Vector3f;

public class BasicColoredQuad extends AbstractQuad {

    private Vector3f m_color;

    public BasicColoredQuad(Vector3f color) {
        super();
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
