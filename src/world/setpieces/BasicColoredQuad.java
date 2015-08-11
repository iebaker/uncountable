package world.setpieces;

import joml.Vector3f;

public class BasicColoredQuad extends Quad {

    private Vector3f m_color;

    public BasicColoredQuad(Vector3f color) {
        super();
        System.out.println("BasicColoredQuad constructor");
        m_color = color;
        setShader("basic");
    }

    @Override
    public void build() {
        super.build();
        System.out.println("BasicColoredQuad");
        setVertexAttribute("color", m_color);
        setVertexAttribute("color", m_color);
        setVertexAttribute("color", m_color);
        setVertexAttribute("color", m_color);
    }
}
