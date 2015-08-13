#version 330

in vec2 position;
in vec3 color;

out vec3 vertexColor;

void main() {
    vertexColor = color;
    gl_Position = vec4(position, 0, 1);
}