package application;

import org.lwjgl.opengl.GL11;

public class UncountableGame extends Application {
    public static void main(String[] args) {
        new UncountableGame() {
            @Override
            public void initialize() {
                GL11.glClearColor(0.0f,  0.0f,  0.0f,  0.0f);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                pushScreen(new UncountableGameScreen(this));
            }
        };
    }
}
