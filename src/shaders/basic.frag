#version 410 core

in vec3 vertexColor;
in vec3 vertexNormal;

uniform mat4 cameraEye;

out vec4 fragColor;

void main() {
    fragColor = vec4(0.0, 1.0, 0.0, 1.0);
}