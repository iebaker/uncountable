#version 330

in vec3 color;
in vec3 worldPosition;
in vec3 screenPosition;

uniform vec3 cameraEye;
uniform bool useDiscardPlane;
uniform vec4 discardPlane;

uniform int fogFunction;
uniform float fogDensity;
uniform float fogBegin;
uniform float fogEnd;
uniform vec3 fogColor;
uniform int useDistanceFog;

out vec4 fragColor;

float fogFactor(float depth)
{
    float result = 0.0;
    switch(fogFunction) {
        case 0: result = ( fogEnd - depth ) / ( fogEnd - fogBegin ); break;
        case 1: result = exp( -fogDensity * depth ); break;
        case 2: result = exp( -pow( fogDensity * depth, 2.0 ) );
    }
    return 1.0 - clamp(result, 0.0, 1.0);
}

void main() {
	if(useDiscardPlane) {
		bool camFront = dot(discardPlane, vec4(cameraEye, 1.0)) > 0;
		bool posFront = dot(discardPlane, vec4(worldPosition, 1.0)) > 0;
		if(camFront != posFront) discard;
	}

	fragColor = vec4(mix(color, fogColor, useDistanceFog * fogFactor( length( cameraEye - worldPosition ))), 1);
}