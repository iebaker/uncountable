#version 330

in vec3 vertexColor;

out vec4 out_color;

void main() {
    out_color = vec4(vertexColor, 1);
}