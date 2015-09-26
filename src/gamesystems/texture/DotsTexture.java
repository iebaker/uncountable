package gamesystems.texture;

import gamesystems.rendering.Points;
import joml.Vector2f;

import java.util.HashSet;
import java.util.Set;

public class DotsTexture extends ProceduralTexture {

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

    public ProceduralTexture.Seed issueSeed() {
        return new DotsTexture.Seed(5);
    }

    public void update(ProceduralTexture.Seed seed, float seconds) {
        DotsTexture.Seed dts = (DotsTexture.Seed)seed;
        dts.dots.forEach(dot -> {
            dot.position.add(dot.velocity.get().mul(seconds));
            if(dot.position.x > 1) dot.position.x = 0;
            if(dot.position.x < 0) dot.position.x = 1;
            if(dot.position.y > 1) dot.position.y = 0;
            if(dot.position.y < 0) dot.position.y = 1;
        });
    }

    public void render(ProceduralTexture.Seed seed) {
        DotsTexture.Seed dts = (DotsTexture.Seed)seed;
        dts.dots.forEach(dot -> {

        });
    }
}
