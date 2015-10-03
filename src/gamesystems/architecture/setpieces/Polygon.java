package gamesystems.architecture.setpieces;

import gamesystems.rendering.Points;
import gamesystems.rendering.Renderable;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import joml.Vector3f;

public class Polygon extends Renderable implements FilledStroked {

    private int m_sides;
    private float m_sidesf;

    public Polygon(int sides) {
        super();
        m_sides = sides;
        m_sidesf = (float)sides;
    }

    @Override
    public void build() {
        next("vertexPosition", Points.ORIGIN_3D.get());
        for(float i = 0; i <= m_sidesf; ++i) {
            float angle = i * 2 * Points.piOver(1)/ m_sidesf;
            next("vertexPosition", (float)Math.cos(angle), (float) Math.sin(angle), 0.0f);
        }
        addInterval(GL_TRIANGLE_FAN, m_sides + 2);
        for(float i = 0; i < m_sidesf; ++i) {
            float angle = i * 2 * Points.piOver(1)/ m_sidesf;
            next("vertexPosition", (float)Math.cos(angle), (float)Math.sin(angle), 0.0f);
        }
        addInterval(GL_LINE_LOOP, m_sides);
    }

    @Override
    public void setFillColor(Vector3f color) {
        range(0, m_sides + 2, "vertexColor", color);
    }

    @Override
    public void setStrokeColor(Vector3f color) {
        range(m_sides + 2, m_sides + m_sides + 2, "vertexColor", color);
    }
}
