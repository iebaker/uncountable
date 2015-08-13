package world.setpieces;

import joml.Vector3f;
import rendering.Points;

public class BasicColoredQuad extends Quad {

    private Vector3f m_color;

    public BasicColoredQuad(Vector3f color) {
        super();
        m_color = color;
        setShader("basic");
    }

    @Override
    public void build() {
        super.build();
        setVertexAttribute("color", m_color);
        setVertexAttribute("color", Points.GREEN);
        setVertexAttribute("color", Points.BLACK);
        setVertexAttribute("color", m_color);
        setVertexAttribute("color", Points.GREEN);
        setVertexAttribute("color", Points.BLACK);
    }
}
