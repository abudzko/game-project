package com.game.utils;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Use com.game.utils.BufferUtils#memFree(java.nio.FloatBuffer) when buffer in no needed
 * to prevent memory leaks
 */
public class BufferUtils {

    public static ByteBuffer createByteBuffer(byte[] array) {
        return MemoryUtil.memAlloc(array.length).put(array).flip();
    }

    public static FloatBuffer createFloatBuffer4f(float[] array) {
        return MemoryUtil.memAllocFloat(array.length).put(array).flip();
    }

    public static IntBuffer createIntBuffer(int[] array) {
        return MemoryUtil.memAllocInt(array.length).put(array).flip();
    }

    public static FloatBuffer toFloatBuffer(Matrix4f matrix4f) {
        FloatBuffer floatBuffer = createFloatBuffer4f();
        floatBuffer.clear();

        floatBuffer.put(matrix4f.m00());
        floatBuffer.put(matrix4f.m01());
        floatBuffer.put(matrix4f.m02());
        floatBuffer.put(matrix4f.m03());
        floatBuffer.put(matrix4f.m10());
        floatBuffer.put(matrix4f.m11());
        floatBuffer.put(matrix4f.m12());
        floatBuffer.put(matrix4f.m13());
        floatBuffer.put(matrix4f.m20());
        floatBuffer.put(matrix4f.m21());
        floatBuffer.put(matrix4f.m22());
        floatBuffer.put(matrix4f.m23());
        floatBuffer.put(matrix4f.m30());
        floatBuffer.put(matrix4f.m31());
        floatBuffer.put(matrix4f.m32());
        floatBuffer.put(matrix4f.m33());
        floatBuffer.flip();
        return floatBuffer;
    }

    private static FloatBuffer createFloatBuffer4f() {
        return MemoryUtil.memAllocFloat(4 * 4 * Float.BYTES);
    }

    public static void memFree(FloatBuffer floatBuffer) {
        MemoryUtil.memFree(floatBuffer);
    }

    public static void memFree(IntBuffer intBuffer) {
        MemoryUtil.memFree(intBuffer);
    }
}
