package xyz.izaak.uncountable;

import xyz.izaak.radon.core.Game;
import xyz.izaak.radon.gamesystem.KeyReleaseQuitSystem;
import xyz.izaak.radon.math.Points;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class Uncountable {
    public static void main(String... args) {
        System.setProperty("java.library.path", "native");

        Game uncountable = new Game("Uncountable", 800, 800, Points.BLACK);
        uncountable.addGameSystem(new KeyReleaseQuitSystem(GLFW_KEY_ESCAPE));
        uncountable.run();
    }
}
