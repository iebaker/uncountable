#version 330

in vec3 worldPosition;

uniform vec3 cameraEye;
uniform vec4 discardPlane;
uniform bool useDiscardPlane;

out vec4 fragColor;

void main() {
	if(useDiscardPlane) {
		bool camFront = dot(discardPlane, vec4(cameraEye, 1.0)) > 0;
		bool posFront = dot(discardPlane, vec4(worldPosition, 1.0)) > 0;
		if(camFront != posFront) discard;
	}
	fragColor = vec4(1);
}