package gamesystems.quit;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import gamesystems.GameSystem;

public class QuitSystem extends GameSystem {
    @Override
    public void onKeyUp(int key) {
        if(key == GLFW_KEY_ESCAPE) {
            System.exit(0);
        }
    }
}