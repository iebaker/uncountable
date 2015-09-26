package gamesystems.architecture.setpieces;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;

import gamesystems.rendering.Points;
import gamesystems.rendering.Renderable;
import joml.Vector3f;

public class Quad extends Renderable implements FilledStroked {

    @Override
    public void build() {
        next("vertexPosition", Points.xy_.get().mul(0.5f));
        next("vertexPosition", Points.Xy_.get().mul(0.5f));
        next("vertexPosition", Points.xY_.get().mul(0.5f));
        next("vertexPosition", Points.XY_.get().mul(0.5f));
        next("vertexPosition", Points.xY_.get().mul(0.5f));
        next("vertexPosition", Points.Xy_.get().mul(0.5f));
        addInterval(GL_TRIANGLES, 6);

        next("vertexPosition", Points.xy_.get().mul(0.5f));
        next("vertexPosition", Points.Xy_.get().mul(0.5f));
        next("vertexPosition", Points.XY_.get().mul(0.5f));
        next("vertexPosition", Points.xY_.get().mul(0.5f));
        addInterval(GL_LINE_LOOP, 4);
    }

    @Override
    public void setFillColor(Vector3f color) {
        range(0, 6, "vertexColor", color);
    }

    @Override
    public void setStrokeColor(Vector3f color) {
        range(6, 4, "vertexColor", color);
    }
}