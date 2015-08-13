package application;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback.SAM;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;

import rendering.Graphics;
import java.io.IOException;
import java.io.InputStream;

public class Application {

    private GLFWErrorCallback m_errorCallback;
    private long m_window;
    private boolean m_resized = false;
    private int m_width = 600;
    private int m_height = 600;

    private List<Screen> m_screens = new ArrayList<Screen>();

    private static Application m_application;

    public Application() {

        try {
            glfwSetErrorCallback(m_errorCallback = errorCallbackPrint(System.err));
            if(glfwInit() != GL11.GL_TRUE) throw new IllegalStateException("Unable to initialize GLFW");

            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GL11.GL_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GL11.GL_TRUE);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

            m_window = glfwCreateWindow(m_width, m_height, "Uncountable", MemoryUtil.NULL, MemoryUtil.NULL);
            if(m_window == MemoryUtil.NULL) throw new RuntimeException("Failed to create the GLFW window");

            ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                m_window,
                (GLFWvidmode.width(vidmode) - m_width) / 2,
                (GLFWvidmode.height(vidmode) - m_height) / 2);

            glfwSetCallback(m_window, GLFWWindowSizeCallback(new SAM() {
                @Override
                public void invoke(long window, int width, int height) {
                    m_resized = true;
                    m_width = width;
                    m_height = height;
                }
            }));

            glfwMakeContextCurrent(m_window);
            glfwSwapInterval(1);
            glfwShowWindow(m_window);
            GLContext.createFromCurrent();

            GL11.glClearColor(0.0f,  0.0f,  0.0f,  0.0f);
            try {
                Graphics.initialize();
            } catch (IOException e) {
                e.printStackTrace();
            }

            m_application = this;
            initialize();

            while(glfwWindowShouldClose(m_window) == GL11.GL_FALSE) {
                if(m_resized) {
                    GL11.glViewport(0, 0, m_width, m_height);
                    m_resized = false;
                }

                render();
                update(0);

                glfwSwapBuffers(m_window);
                glfwPollEvents();

            }

        } finally {
            glfwTerminate();
            m_errorCallback.release();
        }
    }

    public void initialize() {}

    public static String stringFromFile(String filepath) {
        Scanner scanner = Application.scannerForFile(filepath, "\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return result;
    }

    public static Scanner scannerForFile(String filepath) {
        return Application.scannerForFile(filepath, null);
    }

    public static Scanner scannerForFile(String filepath, String delimiter) {
        InputStream inputStream = Application.class.getClassLoader().getResourceAsStream(filepath);
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

    protected void update(float seconds) {
        for (Screen screen : m_screens) {
            screen.update(seconds);
        }
    }

    protected void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        for (Screen screen : m_screens) {
            screen.render();
        }
    }

    public static float getAppWidth() {
        return m_application.getWidth();
    }

    public static float getAppHeight() {
        return m_application.getHeight();
    }

    public float getWidth() {
        return m_width;
    }

    public float getHeight() {
        return m_height;
    }

    public static void exitOnGLErrorWithMessage(String message) {
        Application.exitOnGLErrorWithMessage(message, false);
    }

    public static void exitOnGLErrorWithMessage(String message, boolean printSuccessOutput) {
        int errorValue = GL11.glGetError();

        switch(errorValue) {
        case GL11.GL_INVALID_ENUM:
            System.err.println(message + "(Invalid Enum, an unacceptable value is specified for an enumerated argument)"); break;
        case GL11.GL_INVALID_VALUE:
            System.err.println(message + " (Invalid Value, a numeric argument is out of range)"); break;
        case GL11.GL_INVALID_OPERATION:
            System.err.println(message + " (Invalid Operation, the specified operation is not allowed in the current state)"); break;
        case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
            System.err.println(message + " (Invalid Framebuffer Operation, the framebuffer object is not complete)"); break;
        case GL11.GL_OUT_OF_MEMORY:
            System.err.println(message + " (Out of Memory, there is not enough memory left to execute the command)"); break;
        case GL11.GL_STACK_OVERFLOW:
            System.err.println(message + " (An attempt has been made to perform an operation that would cause an internal stack to underflow)"); break;
        case GL11.GL_STACK_UNDERFLOW:
            System.err.println(message + " (An attempt has been made to perform an operation that would cause an internal stack to overflow)"); break;
        case GL11.GL_NO_ERROR:
            if(printSuccessOutput) {
                System.out.println(message + " (No GL Error Detected!)");
            }
            return;
        }

        System.exit(errorValue);
    }

}
