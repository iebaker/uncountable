#version 330

uniform vec3 drawColor;

out vec4 fragColor;

void main() {
	fragColor = vec4(drawColor, 1);
}
