#version 330

in vec3 vertexPosition;
in vec3 vertexColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 cameraEye;

out vec3 color;
out vec3 position;

void main() {
    mat4 mvp = projection * view * model;
    gl_Position = (mvp * vec4(vertexPosition, 1));
    
    color = vertexColor;
    position = gl_Position.xyz;
}