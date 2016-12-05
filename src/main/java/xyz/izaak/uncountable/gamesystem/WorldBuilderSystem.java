package xyz.izaak.uncountable.gamesystem;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import xyz.izaak.radon.gamesystem.GameSystem;
import xyz.izaak.radon.math.Points;
import xyz.izaak.radon.primitive.Primitive;
import xyz.izaak.radon.primitive.geometry.PolarSphereGeometry;
import xyz.izaak.radon.primitive.geometry.PolarSphereOutlineGeometry;
import xyz.izaak.radon.primitive.geometry.QuadGeometry;
import xyz.izaak.radon.primitive.material.NormalMaterial;
import xyz.izaak.radon.primitive.material.PhongMaterial;
import xyz.izaak.radon.primitive.material.SolidColorMaterial;
import xyz.izaak.radon.world.DirectionalLight;
import xyz.izaak.radon.world.Entity;
import xyz.izaak.radon.world.PointLight;
import xyz.izaak.radon.world.Scene;

import javax.vecmath.Vector3f;
import java.util.Random;

/**
 * Created by ibaker on 28/11/2016.
 */
public class WorldBuilderSystem implements GameSystem {
    private Scene scene;
    private Random random;

    public WorldBuilderSystem(Scene scene) {
        this.scene = scene;
        this.random = new Random();
    }

    public void addSphere() {
        Entity sphereEntity = Entity.builder().mass(4.5f).restitution(0.7f).build();

        PolarSphereGeometry polarSphereGeometry = new PolarSphereGeometry(20, 20);
        PhongMaterial phongMaterial = new PhongMaterial(Points.GRAY, Points.WHITE, Points.WHITE, 100.0f);
        Primitive blackBallPrimitive = new Primitive(polarSphereGeometry, phongMaterial);

        sphereEntity.addPrimitives(blackBallPrimitive);

        CollisionShape sphereShape = new SphereShape(1.0f);
        sphereEntity.setCollisionShape(sphereShape);

        float locationX = random.nextFloat() * 100 - 50;
        float locationY = random.nextFloat() * 100 - 50;
        float locationZ = random.nextFloat() * 100 + 5;

        sphereEntity.translate(locationX, locationY, locationZ);

        scene.addEntity(sphereEntity);
    }

    public void addLight() {
        float locationX = random.nextFloat() * 100 - 50;
        float locationY = random.nextFloat() * 100 - 50;
        float locationZ = random.nextFloat() * 6;

        PointLight pointLight = new PointLight(Points.randomUnit3f(), new org.joml.Vector3f(locationX, locationY, locationZ));
        scene.addPointLight(pointLight);
    }

    @Override
    public void initialize() {
        Entity floorEntity = Entity.builder().mass(0).build();

        QuadGeometry quadGeometry = new QuadGeometry();
        PhongMaterial phongMaterial = new PhongMaterial(Points.GRAY, Points.WHITE, Points.WHITE, 100.0f);
        Primitive floorPrimitive = new Primitive(quadGeometry, phongMaterial);
        floorPrimitive.scale(500);
        floorEntity.addPrimitives(floorPrimitive);

        CollisionShape floorShape = new StaticPlaneShape(new Vector3f(0, 0, 1), 0.0f);
        floorEntity.setCollisionShape(floorShape);
        scene.addEntity(floorEntity);

        for (int i = 0; i < 100; i++) {
            addSphere();
        }

        for (int i = 0; i < 50; i++) {
            addLight();
        }
    }

    @Override
    public void update(float seconds) {

    }
}
