package xyz.izaak.gamesystems.texture;

import xyz.izaak.core.Uncountable;
import xyz.izaak.gamesystems.architecture.setpieces.Polygon;
import xyz.izaak.gamesystems.rendering.Points;
import xyz.izaak.gamesystems.rendering.RenderingException;
import xyz.izaak.gamesystems.rendering.Shaders;
import org.joml.Vector2f;

import java.util.HashSet;
import java.util.Set;

public class DotsTexture extends ProceduralTexture {

    private Polygon circle = new Polygon(50);

    private class Dot {
        public Dot(Vector2f p, Vector2f v, float r) {
            position = p;
            velocity = v;
            radius = r;
        }
        public Vector2f position;
        public Vector2f velocity;
        public float radius;
    }

    private class Seed extends ProceduralTexture.Seed {
        public Seed(int n) {
            super(Type.DOTS);
            for(int i = 0; i < n; ++i) {
                dots.add(new Dot(Points.randomUnit2f(), Points.randomUnit2f().mul(0.1f), 0.5f));
            }
        }
        public Set<Dot> dots = new HashSet<>();
    }

    @Override
    public ProceduralTexture.Seed doIssueSeed() {
        return new DotsTexture.Seed(5);
    }

    @Override
    public void doUpdate(ProceduralTexture.Seed seed, float seconds) {
        DotsTexture.Seed dts = (DotsTexture.Seed)seed;
        dts.dots.forEach(dot -> {
            dot.position.add(Points.get(dot.velocity).mul(seconds));
            if(dot.position.x > 1) dot.position.x = 0;
            if(dot.position.x < 0) dot.position.x = 1;
            if(dot.position.y > 1) dot.position.y = 0;
            if(dot.position.y < 0) dot.position.y = 1;
        });
    }

    @Override
    public void doRender(ProceduralTexture.Seed seed) {
        DotsTexture.Seed dts = (DotsTexture.Seed)seed;
        dts.dots.forEach(dot -> {
            try {
                circle.clearTransforms();
                circle.scale(dot.radius);
                circle.translate(dot.position.x, dot.position.y, 0.0f);
                Uncountable.game.world.camera.capture(() -> {
                    Shaders.setShaderUniform("flatRender", true);
                }, circle);
            } catch (RenderingException e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
