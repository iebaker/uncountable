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

/**
 * This is the main class that kicks everything off, gets a window set up, creates an OpenGL context,
 * sets up callback functions for collecting input, and then launches the main game loop so that things
 * actually happen in the game. If you're exploring this code base, here is a good place to start. The
 * game is run mostly by a set of GameSystems, which are objects that receive input events and are
 * called each frame in sequence in order to execute some specific game functionality, for example
 * moving the Camera around in response to keyboard and mouse input, or drawing the world to the screen
 */
public class Uncountable {

    // A public variable representing the running Uncountable instance, so that game systems
    // have access to and can modify game state.
    public static Uncountable game;

    // A window ID, this is something GLFW uses to identify the window
    private long m_window;

    // Some window properties and timing variables
    private int m_width = 800;
    private int m_height = 800;
    private boolean m_resized;
    private float m_prevTime = 0.0f;
    private Vector2f m_prevMousePos = Points.ORIGIN_2D;

    // callback objects which are executed when GLFW detects input or error
    private GLFWErrorCallback m_errorCallback;
    private GLFWKeyCallback m_keyCallback;
    private GLFWCursorPosCallback m_cursorPosCallback;
    private GLFWMouseButtonCallback m_mouseButtonCallback;

    // All the processes which run the game, and the world representation
    private List<GameSystem> m_gameSystems = new ArrayList<GameSystem>();
    private World m_world;

    /**
     * The Constructor, sets everything in motion, adds the proper game systems in the order they should
     * be called (first, move the camera around, then draw a picture of what the world looks like), and
     * initiates the game loop.
     */
    public Uncountable() {
        initGLFW();
        createWindow();
        setCallbacks();
        initGraphics();

        addGameSystem(new CameraControlSystem());
        addGameSystem(new RenderingSystem());

        Uncountable.game = this;    // <-- Important, setting this variable lets game systems refer to the running
        gameLoop();                 //     game instance in order to read and write to it. Dangerous? Maybe...
    }

    /**
     * The beginning of the program. Just instantiates an Uncountable object, because that's all that needs
     * to happen for everything else to get moving.
     */
    public static void main(String... args) {
        new Uncountable();
    }

    /**
     * Adds a new game system to the list. Game systems are ticked and informed of events in the order they
     * are added with this function.
     */
    private void addGameSystem(GameSystem system) {
        m_gameSystems.add(system);
    }

    /**
     * Starts up GLFW, an OpenGL library, which we need in order to obtain a window, an OpenGL context, and
     * listen for things like mouse clicks and keyboard presses.
     */
    private void initGLFW() {
        if(glfwInit() != GL11.GL_TRUE) throw new IllegalStateException("Unable to initialize GLFW");
        m_prevTime = (float)glfwGetTime();
    }

    /**
     * Uses GLFW to create an OpenGL context and a window.
     */
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

    /**
     * Sets up callback objects which will listen for mouse motion, mouse presses, and keyboard events, and
     * dispatch those events to appropriate functions in each active GameSystem.
     */
    private void setCallbacks() {
        glfwSetErrorCallback(m_errorCallback = errorCallbackPrint(System.err));

        glfwSetKeyCallback(m_window, m_keyCallback = new GLFWKeyCallback() {
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

        glfwSetCursorPosCallback(m_window, m_cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                Vector2f delta = new Vector2f((float)xpos, (float)ypos).sub(m_prevMousePos);
                m_prevMousePos = new Vector2f((float)xpos, (float)ypos);
                for(GameSystem system : m_gameSystems) {
                    system.onMouseMove(m_prevMousePos, delta);
                }
            }
        });

        glfwSetMouseButtonCallback(m_window, m_mouseButtonCallback = new GLFWMouseButtonCallback() {
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

    /**
     * Initializes the Graphics engine, which at the moment just compiles and links a shader program,
     * but in the future might do more than that. Also sets the background color, and makes sure the
     * depth test is turned on.
     */
    private void initGraphics() {
        try {
            Graphics.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GL11.glClearColor(0.0f,  0.0f,  0.0f,  0.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * This is the base of the game itself, the main loop. This executes a sequence of commands every
     * frame until the game is over. From the end of the constructor to when the window is closed, this
     * method is where every call is ultimately dispatched from.
     */
    private void gameLoop() {
        m_world = new World();

        while(glfwWindowShouldClose(m_window) == GL11.GL_FALSE) {

            float rightNow = (float)glfwGetTime();
            float elapsedTime = rightNow - m_prevTime;
            m_prevTime = rightNow;

            for(GameSystem system : m_gameSystems) {
                system.tick(elapsedTime);
            }

            glfwSwapBuffers(m_window);
            glfwPollEvents();

            Uncountable.exitOnGLErrorWithMessage("Error!");     // <-- At the end of each loop, make sure that no OpenGL
        }                                                       //     errors have occurred. If they have, exit the program
                                                                //     with an informative message.
        m_errorCallback.release();
        m_keyCallback.release();
        m_cursorPosCallback.release();
        m_mouseButtonCallback.release();
    }


    /**
     * @return the game's world representation
     */
    public World getWorld() {
        return m_world;
    }

    /**
     * @return the width of the game window
     */
    public int getWidth() {
        return m_width;
    }

    /**
     * @return the height of the game window
     */
    public int getHeight() {
        return m_height;
    }

    /**
     * Returns the entire contents of a specified file as a string. As it turns out there's
     * no canon way to do this in Java, so I create a scanner whose delimiting character is
     * the beginning of file character so that it parses the entire file as one token. Silly,
     * right? Where's Python when you need it?
     *
     * @param filepath  A path to the file (root is the src directory)
     * @return          A string containing the entire contents of the file
     */
    public static String stringFromFile(String filepath) {
        Scanner scanner = Uncountable.scannerForFile(filepath, "\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return result;
    }

    /**
     * Returns a Scanner for a specific file, with the default delimiter
     */
    public static Scanner scannerForFile(String filepath) {
        return Uncountable.scannerForFile(filepath, null);
    }

    /**
     * Returns a scanner for a specific file, with a specified delimiter
     */
    public static Scanner scannerForFile(String filepath, String delimiter) {
        InputStream inputStream = Uncountable.class.getClassLoader().getResourceAsStream(filepath);
        Scanner scanner = new Scanner(inputStream);
        if(delimiter != null) scanner.useDelimiter(delimiter);
        return scanner;
    }

    /**
     * If GL is in an error state, this method will print a specified message followed by the GL
     * error state. It will then exit the program.
     */
    public static void exitOnGLErrorWithMessage(String message) {
        Uncountable.exitOnGLErrorWithMessage(message, false);
    }

    /**
     * If GL is in an error state, this method will print a specified message, followed by the GL
     * error state. It will then exit the program. If GL is not in an error state, will print the
     * message followed by "No Error Detected" if the second argument is "true."
     */
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
