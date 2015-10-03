package gamesystems.texture;

import java.util.HashMap;
import java.util.Map;

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

    public abstract Seed doIssueSeed();
    public abstract void doUpdate(Seed seed, float seconds);
    public abstract void doRender(Seed seed);

    public void tick(float seconds) {
        for (Seed seed : m_framebuffers.keySet()) {
            m_framebuffers.get(seed).bind();
            doUpdate(seed, seconds);
            doRender(seed);
            m_framebuffers.get(seed).free();
        }
    }

    public Seed issueSeed() {
        Seed seed = doIssueSeed();
        m_framebuffers.put(seed, new Framebuffer(1024, 1024, 1));
        return seed;
    }

    public void bindTexture(Seed seed) {
        bindTextureAt(seed, 0);
    }

    public void bindTextureAt(Seed seed, int where) {
        m_framebuffers.get(seed).bindAllTexturesAt(where);
    }
}