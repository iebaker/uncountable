package application;

import java.util.List;
import java.util.ArrayList;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class App {

    private List<Screen> m_screens = new ArrayList<Screen>();

    private GLFWErrorCallback m_errorCallback;
    private GLFWKeyCallback m_keyCallback;

    private long m_window;

    public void pushScreen(Screen newScreen) {
        newScreen.initialize(m_window);
        m_screens.add(newScreen);
    }

    public void reapScreen(Screen oldScreen) {
        oldScreen.cleanUp();
        m_screens.remove(oldScreen);
    }

    public void start() {
        System.out.println("Starting application (running LWJGL " + Sys.getVersion() + ")");

        try {
            initialize();
            execute();
            glfwDestroyWindow(m_window);
            m_keyCallback.release();
        } finally {
            glfwTerminate();
            m_errorCallback.release();
        }
    }

    private void initialize() {
        glfwSetErrorCallback(m_errorCallback = errorCallbackPrint(System.err));

        if (glfwInit() != GL11.GL_TRUE) {
            throw new IllegalStateException("Couldn't initialize GLFW");
        }

        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        int WIDTH = 600;
        int HEIGHT = 600;

        m_window = glfwCreateWindow(WIDTH, HEIGHT, "Uncountable", NULL, NULL);
        if (m_window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(m_window, m_keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, GL_TRUE);
                }
            }
        });

        ByteBuffer videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                m_window,
                (GLFWvidmode.width(videoMode) - WIDTH) / 2,
                (GLFWvidmode.height(videoMode) - HEIGHT) / 2);
        glfwMakeContextCurrent(m_window);
        glfwSwapInterval(1);
        glfwShowWindow(m_window);
    }

    protected void update(float seconds) {
        for (Screen screen : m_screens) {
            screen.update(seconds);
        }
    }

    protected void render() {
        for (Screen screen : m_screens) {
            screen.render();
        }
    }

    private void execute() {
        GLContext.createFromCurrent();
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        while (glfwWindowShouldClose(m_window) == GL_FALSE) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glfwSwapBuffers(m_window);
            glfwPollEvents();

            render();
            update(0);
        }
    }
}
