package gamesystems.texture;

import gamesystems.GameSystem;

public class TextureSystem extends GameSystem {

    public static ProceduralTexture.Seed issueSeed(ProceduralTexture.Type type) {
        return type.getProcedure().issueSeed();
    }

    public static void bindTexture(ProceduralTexture.Seed seed) {
        seed.type.getProcedure().bindTexture(seed);
    }

    public static void bindTextureAt(ProceduralTexture.Seed seed, int where) {
        seed.type.getProcedure().bindTextureAt(seed, where);
    }

    public TextureSystem() {
        super();
    }

    @Override
    public void tick(float seconds) {
        for(ProceduralTexture.Type type : ProceduralTexture.Type.class.getEnumConstants()) {
            type.getProcedure().tick(seconds);
        }
    }
}
