package gamesystems.architecture;

import gamesystems.rendering.Points;
import gamesystems.rendering.Renderable;
import joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;

public class Prism extends Renderable {

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
    }

    private void buildCaps() {
        next("vertexPosition", Points._Y_.get().mul(0.5f));
        for(int i = 0; i <= m_sides; ++i) {
            float angle = m_sidesf * 2 * Points.piOver(1) / m_sidesf;
            next("vertexPosition", (float) Math.cos(angle), 0.5f, (float)Math.sin(angle));
        }
        addInterval(GL_TRIANGLE_FAN, m_sides + 2);

        next("vertexPosition", Points._y_.get().mul(0.5f));
        for(int i = m_sides; i >= 0; --i) {
            float angle = m_sidesf * 2 * Points.piOver(1) / m_sidesf;
            next("vertexPosition", (float) Math.cos(angle), -0.5f, (float) Math.sin(angle));
        }
        addInterval(GL_TRIANGLE_FAN, m_sides + 2);
    }

    private void buildSides() {
        for(int i = 0; i <= m_sides; ++i) {
            float angle = m_sidesf * 2 * Points.piOver(1) / m_sidesf;
            next("vertexPosition", (float) Math.cos(angle), 0.5f, (float) Math.sin(angle));
            next("vertexPosition", (float) Math.cos(angle), -0.5f, (float) Math.sin(angle));
        }
        addInterval(GL_TRIANGLE_STRIP, 2 * (m_sides + 1));
    }
}
