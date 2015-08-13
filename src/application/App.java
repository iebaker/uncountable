package application;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback.SAM;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;

public class App{
    private GLFWErrorCallback errorCallback;
    private long window;
    boolean resized=false;
    int WIDTH = 600;
    int HEIGHT = 600;


    public App() {
        try{
            glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
            if ( glfwInit() != GL11.GL_TRUE )throw new IllegalStateException("Unable to initialize GLFW");
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GL11.GL_FALSE); // the window will stay hidden after creation
            glfwWindowHint(GLFW_RESIZABLE, GL11.GL_TRUE); // the window will be resizable

            window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL);
            if ( window == MemoryUtil.NULL )
                throw new RuntimeException("Failed to create the GLFW window");
            // Get the resolution of the primary monitor
            ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            // Center our window
            glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH) / 2,
                (GLFWvidmode.height(vidmode) - HEIGHT) / 2
            );
            glfwSetCallback(window, GLFWWindowSizeCallback(new SAM() {
                @Override
                public void invoke(long window, int width, int height) {
                    resized=true;
                    WIDTH=width;
                    HEIGHT=height;
                }
            }));
            // Make the OpenGL context current
            glfwMakeContextCurrent(window);
            // Enable v-sync
            glfwSwapInterval(1);

            // Make the window visible
            glfwShowWindow(window);
            GLContext.createFromCurrent();
            ////////////////Prepare the Data////////////////
            float[] data= new float[]{
                    -0.5f, -0.5f,
                    -0.5f,  0.5f,
                     0.5f, -0.5f,
                     0.5f,  0.5f,
                    -0.5f,  0.5f,
                     0.5f, -0.5f,
            };
            FloatBuffer DataBuffer= BufferUtils.createFloatBuffer(data.length);//position at 0.
            DataBuffer.put(data);//put all the data in the buffer, position at the end of the data
            DataBuffer.flip();//set the limit at the position=end of the data(ie no effect right now),and sets the position at 0 again

            int buffer = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, DataBuffer, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            ////////////////End////////////////
            ////////////////Prepare the Shader////////////////
            String vert=
                    "#version 330                                \n"+
                    "in vec2 position;                            \n"+
                    "void main(){                                \n"+
                    "    gl_Position= vec4(position,0,1);        \n"+
                    "}                                            \n";
            String frag=
                    "#version 330                                \n"+
                    "out vec4 out_color;                        \n"+
                    "void main(){                                \n"+
                    "    out_color= vec4(0f, 1f, 1f, 1f);        \n"+
                    "}                                            \n";
            int shader = createShaderProgramme(new int[]{
                    GL20.GL_VERTEX_SHADER,GL20.GL_FRAGMENT_SHADER
            }, new String[]{
                    vert,frag
            });

            ////////////////End////////////////
            GL11.glClearColor(0.0f, 0.0f, 0.5f, 1.0f);

            while ( glfwWindowShouldClose(window) == GL11.GL_FALSE ) {
                if(resized){
                    GL11.glViewport(0, 0, WIDTH, HEIGHT);
                    resized=false;
                }
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
                GL20.glUseProgram(shader);

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
                GL20.glBindAttribLocation(shader, 0, "position");
                GL20.glEnableVertexAttribArray(0);
                GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);


                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

            GL20.glDisableVertexAttribArray(0);
            GL20.glUseProgram(0);


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

    }
    finally{
        glfwTerminate();
        errorCallback.release();
    }
}

int CreateShader(int shadertype,String shaderString){
    int shader = GL20.glCreateShader(shadertype);
    GL20.glShaderSource(shader, shaderString);
    GL20.glCompileShader(shader);
    int status = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
    if (status == GL11.GL_FALSE){

        String error=GL20.glGetShaderInfoLog(shader);

        String ShaderTypeString = null;
        switch(shadertype)
        {
        case GL20.GL_VERTEX_SHADER: ShaderTypeString = "vertex"; break;
        case GL32.GL_GEOMETRY_SHADER: ShaderTypeString = "geometry"; break;
        case GL20.GL_FRAGMENT_SHADER: ShaderTypeString = "fragment"; break;
        }

        System.err.println( "Compile failure in %s shader:\n%s\n"+ShaderTypeString+error);
    }
    return shader;
}

public int createShaderProgramme(int[] shadertypes, String[] shaders){
    int[] shaderids= new int[shaders.length];
    for (int i = 0; i < shaderids.length; i++) {
        shaderids[i]=CreateShader(shadertypes[i], shaders[i]);
    }
    return createShaderProgramme(shaderids);
}

public int createShaderProgramme(int[] shaders){
    int program = GL20.glCreateProgram();
    for (int i = 0; i < shaders.length; i++) {
        GL20.glAttachShader(program, shaders[i]);
    }
    GL20.glLinkProgram(program);

    int status = GL20.glGetShaderi(program, GL20.GL_LINK_STATUS);
    if (status == GL11.GL_FALSE){
        String error=GL20.glGetProgramInfoLog(program);
        System.err.println( "Linker failure: %s\n"+error);
    }
    for (int i = 0; i < shaders.length; i++) {
        GL20.glDetachShader(program, shaders[i]);
    }
    return program;
}

public static void main(String... args) {
    new App();
}
}