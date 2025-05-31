#version 330
precision mediump float;
in vec2 fragmentTextureAttribute;
in vec3 fragmentPositionAttribute;
in vec3 fragmentNormalAttribure;

uniform sampler2D textureSampler;
uniform vec3 lightPosition[3];
uniform vec3 lightColor[3];
uniform int lightCount;
uniform int useShading;
uniform vec3 cameraPosition;

vec3 phongShadingLighting(vec4 textureColor) {
    vec3 result = vec3(0.0);
    for (int i = 0; i < lightCount; i++) {

        float ambientStrength = 0.2;
        vec3 color = lightColor[i];
        vec3 ambient = ambientStrength * color;

        vec3 norm = normalize(fragmentNormalAttribure);
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
    if (useShading == 0) {
        gl_FragColor = textureColor;
    } else {
        vec4 textureColor = texture(textureSampler, fragmentTextureAttribute);
        vec3 result = phongShadingLighting(textureColor);
        gl_FragColor = vec4(result, textureColor.a);
    }
}
