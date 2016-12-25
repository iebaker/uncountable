package xyz.izaak.uncountable.gamesystem;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import xyz.izaak.radon.Channel;
import xyz.izaak.radon.gamesystem.GameSystem;
import xyz.izaak.radon.math.OrthonormalBasis;
import xyz.izaak.radon.math.Points;
import xyz.izaak.radon.math.field.ScalarVolume;
import xyz.izaak.radon.mesh.Mesh;
import xyz.izaak.radon.mesh.geometry.IsosurfaceGeometry;
import xyz.izaak.radon.mesh.geometry.PolarSphereGeometry;
import xyz.izaak.radon.mesh.geometry.PolarSphereOutlineGeometry;
import xyz.izaak.radon.mesh.geometry.QuadGeometry;
import xyz.izaak.radon.mesh.geometry.QuadOutlineGeometry;
import xyz.izaak.radon.mesh.material.NormalMaterial;
import xyz.izaak.radon.mesh.material.PhongMaterial;
import xyz.izaak.radon.mesh.material.PortalMaterial;
import xyz.izaak.radon.mesh.material.SolidColorMaterial;
import xyz.izaak.radon.world.Camera;
import xyz.izaak.radon.world.DirectionalLight;
import xyz.izaak.radon.world.Entity;
import xyz.izaak.radon.world.PointLight;
import xyz.izaak.radon.world.Portal;
import xyz.izaak.radon.world.Scene;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;

/**
 * Created by ibaker on 28/11/2016.
 */
public class WorldBuilderSystem implements GameSystem {
    private Scene scene;
    private Random random;
    private Camera camera;

    public WorldBuilderSystem() {
        this.random = new Random();
    }

    public void addSphere() {
        Entity sphereEntity = Entity.builder().mass(2.5f).friction(0.1f).restitution(0.7f).build();

        float size = random.nextFloat() * 0.6f;
        PolarSphereGeometry polarSphereGeometry = new PolarSphereGeometry(20, 20);
        Vector3f color = Points.randomUnit3f();
        PhongMaterial phongMaterial = new PhongMaterial(color, color, color, 100.0f);
        Mesh blackBallMesh = new Mesh(polarSphereGeometry, phongMaterial);
        blackBallMesh.scale(size);

        Mesh outlineMesh = new Mesh(new PolarSphereOutlineGeometry(20, 20), new SolidColorMaterial(Points.WHITE));
        outlineMesh.scale(size);

        sphereEntity.addMeshes(blackBallMesh);

        CollisionShape sphereShape = new SphereShape(size);
        sphereEntity.setCollisionShape(sphereShape);

        float locationX = random.nextFloat() * 20 - 10;
        float locationY = random.nextFloat() * 20 - 10;
        float locationZ = random.nextFloat() * 10 + 1;

        sphereEntity.translate(locationX, locationY, locationZ);

        scene.addEntity(sphereEntity);
    }

    @Override
    public void initialize() {
        scene = Scene.builder().gravity(new Vector3f(0, 0, -10)).build();
        Channel.request(Scene.class, Channel.CURRENT_SCENE).publish(scene);
        Channel.request(Camera.class, Channel.CURRENT_CAMERA).subscribe(camera -> this.camera = camera);

        Mesh quadMesh = new Mesh(new QuadGeometry(), new PhongMaterial(Points.WHITE, Points.WHITE, Points.WHITE, 50.0f));
        quadMesh.scale(20.0f, 20.0f, 1.0f);
        Entity quadEntity = Entity.builder().build();
        quadEntity.addMeshes(quadMesh);
        quadEntity.setCollisionShape(new StaticPlaneShape(Points.toJavax(Points.__Z), 0.0f));

        scene.addEntity(quadEntity);
        for (int i = 0; i < 100; i++) addSphere();

        OrthonormalBasis portal1Basis = new OrthonormalBasis(Points._y_, Points.__Z);
        Portal portal1 = new Portal(new Vector3f(1, 0, 4.1f), portal1Basis);

        OrthonormalBasis portal2Basis = new OrthonormalBasis(Points._Y_, Points.__Z);
        Portal portal2 = new Portal(new Vector3f(-1, 0, 4.1f), portal2Basis);

//        scene.addPortal(portal1);
//        scene.addPortal(portal2);

        portal2.link(portal1);
        portal1.link(portal2);

        DirectionalLight sun = new DirectionalLight(Points.GRAY, Points.XYZ);
        //scene.addDirectionalLight(sun);
    }

    @Override
    public void update(float seconds) {
    }

    @Override
    public void onKeyUp(int key) {
        if (key == GLFW_KEY_P) {
            PointLight pointLight = new PointLight(Points.randomUnit3f().mul(5), Points.copyOf(camera.getEye()));
            scene.addPointLight(pointLight);
        } else if (key == GLFW_KEY_O) {
            PointLight pointLight = new PointLight(Points.copyOf(Points.xyz).mul(0.5f), Points.copyOf(camera.getEye()));
            scene.addPointLight(pointLight);
        }
    }
}
