#version 430 core
precision mediump float;
in vec2 fragmentTextureAttribute;
in vec3 worldPosition;
in vec3 fragmentNormalAttribute;

uniform sampler2D textureSampler;
uniform sampler2D shadowMap;
in vec4 lightMatrixPosition;
uniform vec3 lightPosition[3];
uniform vec3 lightColor[3];
uniform int lightCount;
uniform vec3 cameraPosition;
uniform int useShading;

out vec4 FragColor;


float ShadowCalculation(vec4 fragPosLightSpace, vec3 normal, vec3 lightDir) {
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;

    float currentDepth = projCoords.z;
    float bias = 0.003; // Const bias for Sun

    // PCF 3x3
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);

    for (int x = -1; x <= 1; ++x) {
        for (int y = -1; y <= 1; ++y) {
            float depth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > depth ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;
    return shadow;
}

vec3 phongShadingLighting(vec4 textureColor) {
    vec3 result = vec3(0.0);
    for (int i = 0; i < lightCount; i++) {

        float ambientStrength = 0.5;
        vec3 color = lightColor[i];
        vec3 ambient = ambientStrength * color;

        vec3 norm = normalize(fragmentNormalAttribute);
        vec3 lightDir = normalize(lightPosition[i] - worldPosition);
        float diff = max(dot(norm, lightDir), 0.0);
        vec3 diffuse = diff * color;

        float specularStrength = 0.1;
        vec3 viewDir = normalize(cameraPosition - worldPosition);
        vec3 reflectDir = reflect(-lightDir, norm);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), 16);
        vec3 specular = specularStrength * spec * color;
        float shadow = ShadowCalculation(lightMatrixPosition, norm, lightDir);
        result += (ambient + (1.0 - shadow) * (diffuse + specular));
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
