package gamesystems.texture;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class ProceduralTexture {

    public enum Type {
        DOTS(new DotsTexture());

        ProceduralTexture m_texture;
        Type(ProceduralTexture t) {
            m_texture = t;
        }
        public ProceduralTexture getProcedure() {
            return m_texture;
        }
    }

    public class Seed {
        public Seed(Type t) {
            type = t;
        }
        public Type type;
    }

    private Map<Seed, Framebuffer> m_framebuffers = new HashMap<>();

    public abstract Seed issueSeed();
    public abstract void update(Seed seed, float seconds);
    public abstract void render(Seed seed);

    public void tick(float seconds) {
        for(Seed seed : m_framebuffers.keySet()) {
            m_framebuffers.get(seed).bind();
            update(seed, seconds);
            render(seed);
            m_framebuffers.get(seed).free();
        }
    }

    public void bindTexture(Seed seed) {
        bindTextureAt(seed, 0);
    }

    public void bindTextureAt(Seed seed, int where) {
        m_framebuffers.get(seed).bindAllTexturesAt(where);
    }
}