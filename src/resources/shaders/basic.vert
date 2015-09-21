#version 330

in vec3 vertexPosition;
in vec3 vertexColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 color;
out vec3 screenPosition;
out vec3 worldPosition;

void main() {
    mat4 mvp = projection * view * model;
    gl_Position = (mvp * vec4(vertexPosition, 1));
    gl_Position.z = 0;
    
    color = vertexColor;
    screenPosition = gl_Position.xyz;
    worldPosition = (model * vec4(vertexPosition, 1)).xyz;
}