package xyz.izaak.uncountable;

import org.joml.Vector3f;
import xyz.izaak.radon.Game;
import xyz.izaak.radon.gamesystem.FirstPersonCameraSystem;
import xyz.izaak.radon.gamesystem.JBulletPhysicsSystem;
import xyz.izaak.radon.gamesystem.KeyReleaseQuitSystem;
import xyz.izaak.radon.math.Points;
import xyz.izaak.radon.world.Camera;
import xyz.izaak.radon.world.Scene;
import xyz.izaak.uncountable.gamesystem.DropLightSystem;
import xyz.izaak.uncountable.gamesystem.RenderingSystem;
import xyz.izaak.uncountable.gamesystem.WorldBuilderSystem;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class Uncountable {

    public static void main(String... args) {
        System.setProperty("java.library.path", "native");

        Game uncountable = new Game("Uncountable", 800, 800, Points.BLACK);
        uncountable.addGameSystem(new KeyReleaseQuitSystem(uncountable.getWindow(), GLFW_KEY_ESCAPE));

        Scene scene = Scene.builder().gravity(new Vector3f(0, 0, -10)).build();
        Camera camera = Camera.builder()
                .aspectRatio(1)
                .fov(Points.piOver(3))
                .eye(new Vector3f(0, 0, 10))
                .look(Points.__z)
                .up(Points.X__)
                .build();

        uncountable.addGameSystem(new WorldBuilderSystem(scene));
        uncountable.addGameSystem(new RenderingSystem(scene, camera));
        uncountable.addGameSystem(new JBulletPhysicsSystem(scene));
        uncountable.addGameSystem(new FirstPersonCameraSystem(camera));
        uncountable.addGameSystem(new DropLightSystem(scene, camera));

        uncountable.run();
    }
}
