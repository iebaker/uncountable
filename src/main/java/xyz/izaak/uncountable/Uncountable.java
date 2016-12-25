package xyz.izaak.uncountable;

import xyz.izaak.radon.Game;
import xyz.izaak.radon.gamesystem.FlyingCameraSystem;
import xyz.izaak.radon.gamesystem.KeyReleaseQuitSystem;
import xyz.izaak.radon.math.Points;
import xyz.izaak.uncountable.gamesystem.RenderingSystem;
import xyz.izaak.uncountable.gamesystem.WorldBuilderSystem;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class Uncountable {

    public static void main(String... args) {
        System.setProperty("java.library.path", "native");

        Game uncountable = new Game("Uncountable", 800, 800, Points.BLACK);
        uncountable.addGameSystem(new KeyReleaseQuitSystem(uncountable.getWindow(), GLFW_KEY_ESCAPE));
        uncountable.addGameSystem(new WorldBuilderSystem());
        uncountable.addGameSystem(new RenderingSystem());
        uncountable.addGameSystem(new FlyingCameraSystem(Points.__Z));

        uncountable.run();
    }
}
