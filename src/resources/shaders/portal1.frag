#version 330

in vec3 position;

out vec4 fragColor;

void main() {
    if(position.z > 0) {
        fragColor = 0.5f * vec4(vec3(position.z), 1);
    } else if(position.z == 0) {
        fragColor = vec4(0, 1, 0, 1);
    } else {
        fragColor = vec4(0, 0, 1, 1);
    }
}