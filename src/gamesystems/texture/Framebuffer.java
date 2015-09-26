package gamesystems.texture;

import com.sun.deploy.util.BufferUtil;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL20.glDrawBuffers;

public class Framebuffer {

    private int m_framebuffer;
    private int m_depthbuffer;
    private int[] m_colorbuffers;
    private Map<Integer, Integer> m_bindingLocations = new HashMap<>();

    public Framebuffer(int width, int height) {
        this(width, height, 1);
    }

    public Framebuffer(int width, int height, int count) {
        m_framebuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, m_framebuffer);

        m_depthbuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, m_depthbuffer);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        ByteBuffer empty = null;
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, empty);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, m_depthbuffer, 0);

        m_colorbuffers = new int[count];
        IntBuffer drawbuffers = BufferUtils.createIntBuffer(count);

        for(int i = 0; i < count; ++i) {
            m_colorbuffers[i] = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, m_colorbuffers[i]);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, empty);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, m_colorbuffers[i], 0);

            drawbuffers.put(GL_COLOR_ATTACHMENT0 + i);
        }

        glDrawBuffers(drawbuffers);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bindAllTextures() {
        bindAllTexturesAt(0);
    }

    public void bindAllTexturesAt(int where) {
        for(int i = 0; i < m_colorbuffers.length; ++i) {
            bindTextureAt(i, where + i);
        }
    }

    public void bindTexture(int which) {
        bindTextureAt(which, 0);
    }

    public void bindTextureAt(int which, int where) {
        int location = GL_TEXTURE0 + where;
        m_bindingLocations.put(location, which);
        glActiveTexture(location);
        glBindTexture(GL_TEXTURE_2D, m_colorbuffers[which]);
    }

    public void freeAllTextures() {
        m_bindingLocations.forEach((location, which ) -> {
            glActiveTexture(location);
            glBindTexture(GL_TEXTURE_2D, 0);
        });
    }

    public void bind() {

    }

    public void free() {

    }
}
