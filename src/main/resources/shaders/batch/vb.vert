#version 430 core
layout (std430, binding = 0) buffer MatrixBuffer {
    mat4 worldMatrices[];
};

uniform mat4 projectionMatrix;
uniform mat4 cameraViewMatrix;
uniform int baseInstance;

in vec2 textureAttribute;
in vec3 positionAttribute;
in vec3 normalAttribute;

out vec2 fragmentTextureAttribute;
out vec3 fragmentPositionAttribute;
out vec3 fragmentNormalAttribute;

void main() {
    int instanceIndex = baseInstance + gl_InstanceID;
    mat4 worldMatrix = worldMatrices[instanceIndex];

    fragmentTextureAttribute = textureAttribute;
    fragmentPositionAttribute = vec3(worldMatrix * vec4(positionAttribute, 1.0));
    fragmentNormalAttribute = mat3(transpose(inverse(worldMatrix))) * normalAttribute;
    gl_Position = projectionMatrix * cameraViewMatrix * worldMatrix * vec4(positionAttribute, 1.0);
}
