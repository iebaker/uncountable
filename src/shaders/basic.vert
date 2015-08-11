#version 410 core

in vec3 position;
in vec3 normal;
in vec3 color;

out vec3 vertexColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    vertexColor = color;
    gl_Position = projection * view * model * vec4(position, 1.0);
}
