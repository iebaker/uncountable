package xyz.izaak.gamesystems.rendering;

public class VertexAttribute {

    private final String m_name;
    private final int m_length;
    private final int m_offset;

    public VertexAttribute(String name, int length, int offset) {
        m_name = name;
        m_length = length;
        m_offset = offset;
    }

    public String getName() {
        return m_name;
    }

    public int getLength() {
        return m_length;
    }

    public int getOffset() {
        return m_offset;
    }
}
