package world.setpieces;

import org.lwjgl.opengl.GL11;

import rendering.Points;
import rendering.Renderable;

public abstract class Quad extends Renderable {

    public Quad() {
        super(6, GL11.GL_TRIANGLE_STRIP);
    }

    @Override
    public void specifyVertices() {
        setVertexAttribute("position", Points.___);
        setVertexAttribute("normal",   Points._Y_);
        setVertexAttribute("position", Points.X__);
        setVertexAttribute("normal",   Points._Y_);
        setVertexAttribute("position", Points.__Z);
        setVertexAttribute("normal",   Points._Y_);
        setVertexAttribute("position", Points.X_Z);
        setVertexAttribute("normal",   Points._Y_);
    }
}