package application;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;

import joml.Vector2f;
import rendering.Graphics;
import rendering.Points;
import world.World;

public class Uncountable {

    public static Uncountable game;

    private long m_window;
    private int m_width = 600;
    private int m_height = 600;
    private boolean m_resized;
    private World m_world;

    private float m_prevTime = 0.0f;
    private Vector2f m_prevMousePos = Points.ORIGIN_2D;

    private GLFWErrorCallback m_errorCallback;
    private List<GameSystem> m_gameSystems = new ArrayList<GameSystem>();

    public Uncountable() {
        initGLFW();
        createWindow();
        setCallbacks();
        initGraphics();

        addGameSystem(new CameraControlSystem());
        addGameSystem(new RenderingSystem());

        gameLoop();
    }

    public static void main(String... args) {
        Uncountable.game = new Uncountable();
    }

    private void addGameSystem(GameSystem system) {
        m_gameSystems.add(system);
    }

    private void initGLFW() {
        if(glfwInit() != GL11.GL_TRUE) throw new IllegalStateException("Unable to initialize GLFW");
        m_prevTime = (float)glfwGetTime();
    }

    private void createWindow() {
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

        glfwMakeContextCurrent(m_window);
        glfwSwapInterval(1);
        glfwShowWindow(m_window);
        GLContext.createFromCurrent();
    }

    private void setCallbacks() {
        glfwSetErrorCallback(m_errorCallback = errorCallbackPrint(System.err));

        glfwSetKeyCallback(m_window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                switch(action) {
                case GLFW_PRESS:
                   for(GameSystem system : m_gameSystems) {
                       system.onKeyDown(key);
                       system.onKeyHeld(key);
                   }
                   break;
                case GLFW_REPEAT:
                    for(GameSystem system : m_gameSystems) {
                        system.onKeyRepeat(key);
                        system.onKeyHeld(key);
                    }
                    break;
                case GLFW_RELEASE:
                    for(GameSystem system : m_gameSystems) {
                        system.onKeyUp(key);
                    }
                    break;
                }
            }
        });

        glfwSetCursorPosCallback(m_window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                Vector2f delta = new Vector2f((float)xpos, (float)ypos).sub(m_prevMousePos);
                m_prevMousePos = new Vector2f((float)xpos, (float)ypos);
                for(GameSystem system : m_gameSystems) {
                    system.onMouseMove(m_prevMousePos, delta);
                }
            }
        });

        glfwSetMouseButtonCallback(m_window, new GLFWMouseButtonCallback() {
           @Override
           public void invoke(long window, int button, int action, int mods) {
               switch(action) {
               case GLFW_PRESS:
                   for(GameSystem system : m_gameSystems) {
                       system.onMouseDown(m_prevMousePos, button);
                   }
                   break;
               case GLFW_RELEASE:
                   for(GameSystem system : m_gameSystems) {
                       system.onMouseUp(m_prevMousePos, button);
                   }
               }
           }
        });
    }

    private void initGraphics() {
        try {
            Graphics.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GL11.glClearColor(0.0f,  0.0f,  0.0f,  0.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void gameLoop() {
        m_world = new World();
        while(glfwWindowShouldClose(m_window) == GL11.GL_FALSE) {

            float rightNow = (float)glfwGetTime();
            float elapsedTime = rightNow - m_prevTime;
            m_prevTime = rightNow;

            for(GameSystem system : m_gameSystems) {
                system.tick(m_world, elapsedTime);
            }

            glfwSwapBuffers(m_window);
            glfwPollEvents();

            Uncountable.exitOnGLErrorWithMessage("Error!");
        }
    }

    public static String stringFromFile(String filepath) {
        Scanner scanner = Uncountable.scannerForFile(filepath, "\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return result;
    }

    public static Scanner scannerForFile(String filepath) {
        return Uncountable.scannerForFile(filepath, null);
    }

    public static Scanner scannerForFile(String filepath, String delimiter) {
        InputStream inputStream = Uncountable.class.getClassLoader().getResourceAsStream(filepath);
        Scanner scanner = new Scanner(inputStream);
        if(delimiter != null) scanner.useDelimiter(delimiter);
        return scanner;
    }

    public static void exitOnGLErrorWithMessage(String message) {
        Uncountable.exitOnGLErrorWithMessage(message, false);
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
