#version 330

in vec3 vertexPosition;

uniform mat4 model;

void main() {
	gl_Position = model * (vec4(vertexPosition, 1));
}
