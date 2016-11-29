package xyz.izaak.uncountable;

import xyz.izaak.radon.Game;
import xyz.izaak.radon.gamesystem.JBulletPhysicsSystem;
import xyz.izaak.radon.gamesystem.KeyReleaseQuitSystem;
import xyz.izaak.radon.math.Points;
import xyz.izaak.radon.world.Scene;
import xyz.izaak.radon.world.arg.SceneConstructionArg;
import xyz.izaak.uncountable.gamesystem.RenderingSystem;
import xyz.izaak.uncountable.gamesystem.WorldBuilderSystem;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class Uncountable {

    public static void main(String... args) {
        System.setProperty("java.library.path", "native");

        Game uncountable = new Game("Uncountable", 800, 800, Points.BLUE);
        uncountable.addGameSystem(new KeyReleaseQuitSystem(uncountable.getWindow(), GLFW_KEY_ESCAPE));

        SceneConstructionArg sceneArg = new SceneConstructionArg.Builder().build();
        Scene scene = new Scene(sceneArg);
        uncountable.addGameSystem(new WorldBuilderSystem(scene));
        uncountable.addGameSystem(new RenderingSystem(scene));
        uncountable.addGameSystem(new JBulletPhysicsSystem(scene));

        uncountable.run();
    }
}
