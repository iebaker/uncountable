in vec3 vertexColor;

uniform mat4 cameraEye;

out vec4 fragColor;

void main() {
    fragColor = vec4(vertexColor, 1.0);
}