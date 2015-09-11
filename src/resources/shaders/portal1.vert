#version 330

in vec3 vertexPosition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 cameraEye;

out vec3 position;

void main() {
    mat4 mvp = projection * view * model;
    vec4 actualPosition = (mvp * vec4(vertexPosition, 1));
    actualPosition.z = 0.f;
    gl_Position = actualPosition;

    position = gl_Position.xyz;
}
