#version 330

in vec3 vertexPosition;
in vec3 vertexColor;
//in vec2 vertexTextureCoordinate;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform bool flatRender;

out vec3 color;
out vec3 screenPosition;
out vec3 worldPosition;
out vec2 textureCoordinate;

void main() {

    if(flatRender) {
        gl_Position = (model * vec4(vertexPosition, 1));
    } else {
        mat4 mvp = projection * view * model;
        gl_Position = (mvp * vec4(vertexPosition, 1));
    }
    
    color = vertexColor;
    //textureCoordinate = vertexTextureCoordinate;
    screenPosition = gl_Position.xyz;
    worldPosition = (model * vec4(vertexPosition, 1)).xyz;
}