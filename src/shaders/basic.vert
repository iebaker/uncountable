#version 330

in vec2 position;
in vec3 color;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 cameraEye;

out vec3 vertexColor;

void main() {
    vertexColor = color;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 0, 1);
}