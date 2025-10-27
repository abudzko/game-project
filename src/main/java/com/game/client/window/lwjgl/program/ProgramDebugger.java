package com.game.client.window.lwjgl.program;

import com.game.client.utils.BufferUtils;
import com.game.client.utils.log.LogUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class ProgramDebugger {

    static void checkIndividualShaders(int programId) {
        var shaders = BufferUtils.createIntBuffer(10);
        var count = BufferUtils.createIntBuffer(1);
        GL20.glGetAttachedShaders(programId, count, shaders);

        LogUtil.logDebug("Actually attached shaders: " + count.get(0));

        for (int i = 0; i < count.get(0); i++) {
            int shader = shaders.get(i);
            int type = GL20.glGetShaderi(shader, GL20.GL_SHADER_TYPE);
            String typeName = getShaderTypeName(type);
            int compileStatus = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
            String shaderLog = GL20.glGetShaderInfoLog(shader);

            LogUtil.logDebug("=== " + typeName + " SHADER ===");
            LogUtil.logDebug("Compile status: " + (compileStatus == GL11.GL_TRUE ? "SUCCESS" : "FAILED"));

            LogUtil.logDebug("Compile log: '" + shaderLog + "'");

            if (type == GL20.GL_FRAGMENT_SHADER && compileStatus == GL11.GL_FALSE) {
                debugFragmentShader(shader);
            }
        }
    }

    private static void debugFragmentShader(int shader) {
        LogUtil.logDebug("--- FRAGMENT SHADER DEBUG INFO ---");
        try {
            String source = GL20.glGetShaderSource(shader);
            if (source != null) {
                LogUtil.logDebug("Source code:");
                LogUtil.logDebug(source);
            }
        } catch (Exception e) {
            LogUtil.logDebug("Could not retrieve shader source: " + e.getMessage());
        }
    }

    private static String getShaderTypeName(int type) {
        switch (type) {
            case GL20.GL_VERTEX_SHADER:
                return "VERTEX";
            case GL20.GL_FRAGMENT_SHADER:
                return "FRAGMENT";
            case GL32.GL_GEOMETRY_SHADER:
                return "GEOMETRY";
            default:
                return "UNKNOWN (" + type + ")";
        }
    }
}
