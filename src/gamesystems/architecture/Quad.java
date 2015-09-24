package gamesystems.architecture;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

import gamesystems.rendering.Points;
import gamesystems.rendering.Renderable;

public class Quad extends Renderable {

    @Override
    public void build() {
        next("vertexPosition", Points.xy_.get().mul(0.5f));
        next("vertexPosition", Points.Xy_.get().mul(0.5f));
        next("vertexPosition", Points.xY_.get().mul(0.5f));
        next("vertexPosition", Points.XY_.get().mul(0.5f));
        next("vertexPosition", Points.xY_.get().mul(0.5f));
        next("vertexPosition", Points.Xy_.get().mul(0.5f));
        addInterval(GL_TRIANGLES, 6);
    }
}