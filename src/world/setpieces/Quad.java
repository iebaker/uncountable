package world.setpieces;

import org.lwjgl.opengl.GL11;

import rendering.Points;
import rendering.Renderable;

public abstract class Quad extends Renderable {

    public Quad() {
        super(6, GL11.GL_TRIANGLES);
    }

    @Override
    public void build() {
        // First triangle
        setVertexAttribute("position", Points.xy);
        setVertexAttribute("position", Points.xY);
        setVertexAttribute("position", Points.Xy);
        setVertexAttribute("position", Points.XY);
        setVertexAttribute("position", Points.xY);
        setVertexAttribute("position", Points.Xy);
    }
}