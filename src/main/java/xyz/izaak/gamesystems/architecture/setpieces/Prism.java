package xyz.izaak.gamesystems.architecture.setpieces;

import xyz.izaak.gamesystems.rendering.Points;
import xyz.izaak.gamesystems.rendering.Renderable;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINES;

public class Prism extends Renderable implements FilledStroked {

    private int m_sides;
    private float m_sidesf;

    public Prism(int sides) {
        m_sides = sides;
        m_sidesf = (float)sides;
    }

    @Override
    public void build() {
        buildCaps();
        buildSides();
        buildCapStroke();
        buildSidesStroke();
    }

    private void buildCaps() {
        next("vertexPosition", Points.get(Points._y_).mul(0.5f));
        for(float i = 0; i <= m_sidesf; ++i) {
            float angle = i * 2 * Points.piOver(1) / m_sidesf;
            next("vertexPosition", (float)Math.cos(angle), -0.5f, (float)Math.sin(angle));
        }
        addInterval(GL_TRIANGLE_FAN, m_sides + 2);

        next("vertexPosition", Points.get(Points._Y_).mul(0.5f));
        for(float i = m_sidesf; i >= 0; --i) {
            float angle = i * 2 * Points.piOver(1) / m_sidesf;
            next("vertexPosition", (float)Math.cos(angle), 0.5f, (float)Math.sin(angle));
        }
        addInterval(GL_TRIANGLE_FAN, m_sides + 2);
    }

    private void buildSides() {
        for(float i = 0; i <= m_sidesf; ++i) {
            float angle = i * 2 * Points.piOver(1) / m_sidesf;
            next("vertexPosition", (float)Math.cos(angle), -0.5f, (float)Math.sin(angle));
            next("vertexPosition", (float)Math.cos(angle), 0.5f, (float)Math.sin(angle));
        }
        addInterval(GL_TRIANGLE_STRIP, 2 * (m_sides + 1));
    }

    private void buildCapStroke() {
        for(float i = 0; i < m_sidesf; ++i) {
            float angle = i * 2 * Points.piOver(1) / m_sidesf;
            next("vertexPosition", (float)Math.cos(angle), 0.5f, (float)Math.sin(angle));
        }
        addInterval(GL_LINE_LOOP, m_sides);
        for(float i = 0; i < m_sidesf; ++i) {
            float angle = i * 2 * Points.piOver(1) / m_sidesf;
            next("vertexPosition", (float)Math.cos(angle), -0.5f, (float)Math.sin(angle));
        }
        addInterval(GL_LINE_LOOP, m_sides);
    }

    private void buildSidesStroke() {
        for(float i = 0; i < m_sidesf; ++i) {
            float angle = i * 2 * Points.piOver(1) / m_sidesf;
            next("vertexPosition", (float)Math.cos(angle), 0.5f, (float)Math.sin(angle));
            next("vertexPosition", (float)Math.cos(angle), -0.5f, (float)Math.sin(angle));
        }
        addInterval(GL_LINES, 2 * m_sides);
    }

    public void setFillColor(Vector3f color) {
        range(0, 4 * m_sides + 6, "vertexColor", color);
    }

    public void setStrokeColor(Vector3f color) {
        range(4 * m_sides + 6, 8 * m_sides + 6, "vertexColor", color);
    }
}
