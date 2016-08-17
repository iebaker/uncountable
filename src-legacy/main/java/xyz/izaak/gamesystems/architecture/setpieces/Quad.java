package xyz.izaak.gamesystems.architecture.setpieces;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;

import xyz.izaak.gamesystems.rendering.Points;
import xyz.izaak.gamesystems.rendering.Renderable;

import org.joml.Vector3f;

public class Quad extends Renderable implements FilledStroked {

    @Override
    public void build() {
        next("vertexPosition", Points.get(Points.xy_).mul(0.5f));
        next("vertexPosition", Points.get(Points.Xy_).mul(0.5f));
        next("vertexPosition", Points.get(Points.xY_).mul(0.5f));
        next("vertexPosition", Points.get(Points.XY_).mul(0.5f));
        next("vertexPosition", Points.get(Points.xY_).mul(0.5f));
        next("vertexPosition", Points.get(Points.Xy_).mul(0.5f));
        addInterval(GL_TRIANGLES, 6);

        next("vertexPosition", Points.get(Points.xy_).mul(0.5f));
        next("vertexPosition", Points.get(Points.Xy_).mul(0.5f));
        next("vertexPosition", Points.get(Points.XY_).mul(0.5f));
        next("vertexPosition", Points.get(Points.xY_).mul(0.5f));
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
