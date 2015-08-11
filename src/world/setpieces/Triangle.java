package world.setpieces;

import org.lwjgl.opengl.GL11;

import rendering.Points;
import rendering.Renderable;

public class Triangle extends Renderable {

    public Triangle() {
        super(3, GL11.GL_TRIANGLES);
        setShader("basic");
    }

    @Override
    public void build() {
        setVertexAttribute("position", -0.6f, -0.4f,  0.0f);
        setVertexAttribute("color", Points.RED);
        setVertexAttribute("normal", Points.___);
        setVertexAttribute("position",  0.6f, -0.4f,  0.0f);
        setVertexAttribute("color", Points.GREEN);
        setVertexAttribute("normal", Points.___);
        setVertexAttribute("position",  0.0f,  0.6f,  0.0f);
        setVertexAttribute("color", Points.BLUE);
        setVertexAttribute("normal", Points.___);
    }
}
