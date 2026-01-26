#version 430 core
layout (std430, binding = 0) buffer MatrixBuffer {
    mat4 worldMatrices[];
};

uniform int baseInstance;

uniform mat4 lightSpaceMatrix;

in vec3 positionAttribute;

void main() {
    int instanceIndex = baseInstance + gl_InstanceID;
    mat4 worldMatrix = worldMatrices[instanceIndex];
    gl_Position = lightSpaceMatrix * worldMatrix * vec4(positionAttribute, 1.0);
}
