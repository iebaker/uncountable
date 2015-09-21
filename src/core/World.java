package core;

import gamesystems.architecture.Module;
import gamesystems.rendering.Camera;
import gamesystems.rendering.Points;
import gamesystems.rendering.Shaders;
import gamesystems.rendering.UniformSettings;
import joml.Vector3f;

public class World {

    public Module currentModule;
    public Camera camera;
    public UniformSettings uniforms;
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
        public float fogDensity = 0.09f;
        public float fogBegin = 0.1f;
        public float fogEnd = 100.0f;
        public Vector3f fogColor = Points.GRAY.get();
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