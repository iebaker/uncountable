package world.setpieces;

import org.lwjgl.opengl.GL11;

import rendering.Points;
import rendering.Renderable;

public abstract class Quad extends Renderable {

    public Quad() {
        super(4, GL11.GL_TRIANGLE_STRIP);
    }

    @Override
    public void build() {
        setVertexAttribute("position", Points.xY_);
        setVertexAttribute("normal",   Points.__z);

        setVertexAttribute("position", Points.xy_);
        setVertexAttribute("normal",   Points.__z);

        setVertexAttribute("position", Points.XY_);
        setVertexAttribute("normal",   Points.__z);

        setVertexAttribute("position", Points.Xy_);
        setVertexAttribute("normal",   Points.__z);
    }
}