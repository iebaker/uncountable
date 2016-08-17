#version 330

in vec3 vertexPosition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
	gl_Position = projection * view * model * (vec4(vertexPosition, 1));
}