package com.game.app.window.lwjgl.program.shader;

import com.game.utils.log.LogUtil;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;


public class Shader {
    private static final String SHADER_WAS_DELETED_TEMPLATE = "Shader was deleted. Id [%s], path [%s], type [%s]";
    private final String resourcePath;
    private final int shaderType;
    private final int id;
    private boolean isDeleted = false;

    public Shader(String path, int type) {
        this.id = GL30.glCreateShader(type);
        this.resourcePath = path;
        this.shaderType = type;
        GL30.glShaderSource(id, resource(path));
        GL30.glCompileShader(id);
        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            LogUtil.logError(String.format("Failed to compile [%s] shader %s", type, glGetShaderInfoLog(id)));
        }
    }

    private String resource(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to load resource %s", path), e);
        }
    }

    public void deleteShader() {
        GL30.glDeleteShader(getId());
        isDeleted = true;
    }

    public int getId() {
        if (isDeleted) {
            throw new IllegalStateException(String.format(SHADER_WAS_DELETED_TEMPLATE, id, resourcePath, shaderType));
        }
        return id;
    }
}
