package gamesystems.architecture.setpieces;

import org.lwjgl.opengl.GL11;

import gamesystems.rendering.Points;
import gamesystems.rendering.Renderable;

public abstract class AbstractQuad extends Renderable {

    public AbstractQuad() {
        this(GL11.GL_FILL);
    }

    public AbstractQuad(int polgyonMode) {
        super(6, GL11.GL_TRIANGLES, polgyonMode);
    }

    @Override
    public void build() {
        setVertexAttribute("vertexPosition", Points.aug3f("xy_", 0.5f));
        setVertexAttribute("vertexPosition", Points.aug3f("Xy_", 0.5f));
        setVertexAttribute("vertexPosition", Points.aug3f("xY_", 0.5f));
        setVertexAttribute("vertexPosition", Points.aug3f("XY_", 0.5f));
        setVertexAttribute("vertexPosition", Points.aug3f("xY_", 0.5f));
        setVertexAttribute("vertexPosition", Points.aug3f("Xy_", 0.5f));
    }
}