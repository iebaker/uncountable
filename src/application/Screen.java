package application;

import org.lwjgl.glfw.GLFWKeyCallback;

public class Screen {

    protected Application m_parentApplication;
    protected GLFWKeyCallback m_keyCallback;

    public Screen(Application parent) {
        m_parentApplication = parent;
    }

    public void initialize(long windowHandle) {

    }

    public void cleanUp() {

    }

    public void update(float seconds) {

    }

    public void render() {

    }

}
