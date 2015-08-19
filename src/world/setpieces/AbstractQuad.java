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
        setVertexAttribute("position", Points.xy_);
        setVertexAttribute("position", Points.xY_);
        setVertexAttribute("position", Points.Xy_);
        setVertexAttribute("position", Points.XY_);
        setVertexAttribute("position", Points.xY_);
        setVertexAttribute("position", Points.Xy_);
    }
}