package xyz.izaak.core;

import xyz.izaak.gamesystems.architecture.Module;
import xyz.izaak.gamesystems.rendering.Camera;
import xyz.izaak.gamesystems.rendering.Points;
import xyz.izaak.gamesystems.rendering.Shaders;

import org.joml.Vector3f;

public class World {

    public Module currentModule;
    public Camera camera;
    public Settings uniforms;
    public FogData fogData = new FogData();

    public enum FogFunction {
        LINEAR(0),
        EXPONENTIAL(1),
        EXPONENTIAL2(2);
        public int id;
        FogFunction(int i) {
            id = i;
        }
    }

    public class FogData {
        public boolean on = true;
        public FogFunction fogFunction = FogFunction.EXPONENTIAL;
        public float fogDensity = 0.2f;
        public float fogBegin = 0.1f;
        public float fogEnd = 100.0f;
        public Vector3f fogColor = Points.get(Points.WHITE);
    }

    public World() {
        uniforms = () -> {
            Shaders.setShaderUniform("useDistanceFog", fogData.on);
            Shaders.setShaderUniform("fogFunction", fogData.fogFunction.id);
            Shaders.setShaderUniform("fogDensity", fogData.fogDensity);
            Shaders.setShaderUniform("fogBegin", fogData.fogBegin);
            Shaders.setShaderUniform("fogEnd", fogData.fogEnd);
            Shaders.setShaderUniform("fogColor", fogData.fogColor);
        };
    }
}
