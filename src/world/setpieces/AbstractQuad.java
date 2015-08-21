package world.setpieces;

import org.lwjgl.opengl.GL11;

import gamesystems.rendering.Points;
import gamesystems.rendering.Renderable;

public abstract class AbstractQuad extends Renderable {

    public AbstractQuad() {
        super(6, GL11.GL_TRIANGLES);
    }

    @Override
    public void build() {
        // First triangle
        setVertexAttribute("position", Points.aug3f("xy_", 0.5f));
        setVertexAttribute("position", Points.aug3f("Xy_", 0.5f));
        setVertexAttribute("position", Points.aug3f("xY_", 0.5f));
        setVertexAttribute("position", Points.aug3f("XY_", 0.5f));
        setVertexAttribute("position", Points.aug3f("xY_", 0.5f));
        setVertexAttribute("position", Points.aug3f("Xy_", 0.5f));

    }
}