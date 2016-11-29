package xyz.izaak.uncountable.gamesystem;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import xyz.izaak.radon.gamesystem.GameSystem;
import xyz.izaak.radon.math.Points;
import xyz.izaak.radon.primitive.PolarSpherePrimitive;
import xyz.izaak.radon.primitive.QuadPrimitive;
import xyz.izaak.radon.world.Entity;
import xyz.izaak.radon.world.Scene;
import xyz.izaak.radon.world.arg.EntityConstructionArg;
import xyz.izaak.radon.world.arg.SceneConstructionArg;

import javax.vecmath.Vector3f;

/**
 * Created by ibaker on 28/11/2016.
 */
public class WorldBuilderSystem implements GameSystem {
    private Scene scene;

    public WorldBuilderSystem(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void initialize() {
        EntityConstructionArg floorEntityArg = new EntityConstructionArg.Builder().mass(0).build();
        Entity floorEntity = new Entity(floorEntityArg);

        QuadPrimitive floorPrimitive = new QuadPrimitive();
        floorPrimitive.setFillColor(new org.joml.Vector3f(0.2f, 0.2f, 0.2f));
        floorPrimitive.setStrokeColor(Points.WHITE);
        floorPrimitive.scale(10);
        floorPrimitive.rotate(Points.piOver(2), Points.X__);
        floorEntity.addPrimitives(floorPrimitive);

        CollisionShape floorShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 0.0f);
        floorEntity.setCollider(floorShape);

        EntityConstructionArg sphereEntityArg = new EntityConstructionArg.Builder().mass(4.5f).build();
        Entity sphereEntity = new Entity(sphereEntityArg);

        PolarSpherePrimitive spherePrimitive = new PolarSpherePrimitive(20, 20);
        spherePrimitive.setFillColor(Points.BLACK);
        spherePrimitive.setStrokeColor(Points.WHITE);
        sphereEntity.addPrimitives(spherePrimitive);

        CollisionShape sphereShape = new SphereShape(1.0f);
        sphereEntity.setCollider(sphereShape);

        sphereEntity.translate(Points.copyOf(Points._Y_).mul(10));

        EntityConstructionArg redSphereEntityArg = new EntityConstructionArg.Builder().mass(1.0f).build();
        Entity redSphereEntity = new Entity(redSphereEntityArg);

        PolarSpherePrimitive redSpherePrimitive = new PolarSpherePrimitive(20, 20);
        redSpherePrimitive.setFillColor(Points.RED);
        redSpherePrimitive.setStrokeColor(Points.BLACK);
        redSphereEntity.addPrimitives(redSpherePrimitive);

        CollisionShape redSphereShape = new SphereShape(1.0f);
        redSphereEntity.setCollider(redSphereShape);

        redSphereEntity.translate(0.2f, 5.0f, 0.3f);

        scene.addEntity(floorEntity);
        scene.addEntity(sphereEntity);
        scene.addEntity(redSphereEntity);
    }

    public Scene getScene() {
        return scene;
    }
}
