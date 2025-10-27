#version 430 core
precision mediump float;
in vec2 fragmentTextureAttribute;
in vec3 fragmentPositionAttribute;
in vec3 fragmentNormalAttribute;

uniform sampler2D textureSampler;
uniform vec3 lightPosition[3];
uniform vec3 lightColor[3];
uniform int lightCount;
uniform vec3 cameraPosition;
uniform int useShading;

out vec4 FragColor;

vec3 phongShadingLighting(vec4 textureColor) {
    vec3 result = vec3(0.0);
    for (int i = 0; i < lightCount; i++) {

        float ambientStrength = 0.5;
        vec3 color = lightColor[i];
        vec3 ambient = ambientStrength * color;

        vec3 norm = normalize(fragmentNormalAttribute);
        vec3 lightDir = normalize(lightPosition[i] - fragmentPositionAttribute);
        float diff = max(dot(norm, lightDir), 0.0);
        vec3 diffuse = diff * color;

        float specularStrength = 0.1;
        vec3 viewDir = normalize(cameraPosition - fragmentPositionAttribute);
        vec3 reflectDir = reflect(-lightDir, norm);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), 16);
        vec3 specular = specularStrength * spec * color;
        result += (ambient + diffuse + specular);
    }
    return result * textureColor.rgb;
}

void main() {
    vec4 textureColor = texture(textureSampler, fragmentTextureAttribute);
    if (useShading < 1) {
        FragColor = textureColor;
    } else {
        vec3 result = phongShadingLighting(textureColor);
        FragColor = vec4(result, textureColor.a);
    }
}
