package application;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import rendering.Graphics;

public class App {

    private List<Screen> m_screens = new ArrayList<Screen>();

    private GLFWErrorCallback m_errorCallback;
    private GLFWKeyCallback m_keyCallback;

    private long m_window;

    public static String stringFromFile(String filepath) {
        Scanner scanner = App.scannerForFile(filepath, "\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return result;
    }

    public static Scanner scannerForFile(String filepath) {
        return App.scannerForFile(filepath, null);
    }

    public static Scanner scannerForFile(String filepath, String delimiter) {
        InputStream inputStream = App.class.getClassLoader().getResourceAsStream(filepath);
        Scanner scanner = new Scanner(inputStream);
        if(delimiter != null) scanner.useDelimiter(delimiter);
        return scanner;
    }

    public void pushScreen(Screen newScreen) {
        newScreen.initialize(m_window);
        m_screens.add(newScreen);
    }

    public void reapScreen(Screen oldScreen) {
        oldScreen.cleanUp();
        m_screens.remove(oldScreen);
    }

    public void start() {
        System.out.println("Running LWJGL " + Sys.getVersion());

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
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

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

        System.out.println("Using OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));

        try {
            Graphics.initialize();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        while (glfwWindowShouldClose(m_window) == GL_FALSE) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glfwSwapBuffers(m_window);
            glfwPollEvents();

            render();
            update(0);
        }
    }
}
