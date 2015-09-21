#version 330

in vec3 vertexPosition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 worldPosition;

void main() {
    mat4 mvp = projection * view * model;
    gl_Position = (mvp * vec4(vertexPosition, 1));
    gl_Position.z = 0;

    worldPosition = (model * vec4(vertexPosition, 1)).xyz;
}
