#version 330
uniform mat4 worldMatrix;
uniform mat4 projectionMatrix;
uniform mat4 cameraViewMatrix;

attribute vec3 positionAttribute;
attribute vec2 textureAttribute;

out vec2 fragmentTextureAttribute;

void main() {
    gl_Position = projectionMatrix * cameraViewMatrix * worldMatrix * vec4(positionAttribute, 1);

    fragmentTextureAttribute = textureAttribute;
}
