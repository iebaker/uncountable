package gamesystems.rendering;

import joml.Vector3f;
import world.setpieces.BasicColoredQuad;

import static org.lwjgl.opengl.GL11.*;

public class DebugArtist {

    private static BasicColoredQuad m_quad = new BasicColoredQuad(Points.BLUE);

    static {
        m_quad.setShader("flat");
        m_quad.scale(2.0f);
    }

    public static void renderStencil(Camera camera, int depth) throws RenderingException {

        float hue = 0.0f;
        float hueDelta = 1.0f / (float)depth;

        for(int i = 0; i < depth; ++i) {
            glStencilFunc(GL_EQUAL, i, 0xFF);
            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);

            glColorMask(true, true, true, true);
            glDepthMask(false);

            final Vector3f drawColor = Points.hsbToRgb(hue);
            camera.capture(() -> {
                Shaders.setShaderUniform("drawColor", drawColor);
            }, m_quad);

            hue += hueDelta;
        }
    }
}
