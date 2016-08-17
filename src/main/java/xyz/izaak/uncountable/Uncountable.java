package xyz.izaak.uncountable;

import xyz.izaak.radon.core.Game;
import xyz.izaak.radon.gamesystem.KeyReleaseQuitSystem;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class Uncountable {
    public static void main(String... args) {
        System.setProperty("java.library.path", "native");

        Game uncountable = new Game("Uncountable", 800, 800, new Vector3f(0, 0, 0));
        uncountable.addGameSystem(new KeyReleaseQuitSystem(GLFW_KEY_ESCAPE));
        uncountable.run();
    }
}
