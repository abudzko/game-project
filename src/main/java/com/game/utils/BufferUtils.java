package com.game.utils;

import com.game.utils.log.LogUtil;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Use com.game.utils.BufferUtils#memFree(java.nio.FloatBuffer) when buffer in no needed
 * to prevent memory leaks
 */
public class BufferUtils {

    private static final AtomicInteger allocatedBuffers = new AtomicInteger(0);

    public static int openBuffersCount() {
        return getAllocatedBuffers().get();
    }

    public static ByteBuffer createByteBuffer(byte[] array) {
        counter();
        return MemoryUtil.memAlloc(array.length).put(array).flip();
    }

    public static FloatBuffer createFloatBuffer4f(float[] array) {
        counter();
        return MemoryUtil.memAllocFloat(array.length).put(array).flip();
    }

    public static IntBuffer createIntBuffer(int[] array) {
        counter();
        return MemoryUtil.memAllocInt(array.length).put(array).flip();
    }

    public static FloatBuffer toFloatBuffer(Matrix4f matrix4f) {
        var floatBuffer = createFloatBuffer4f();
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
        counter();
        return MemoryUtil.memAllocFloat(4 * 4 * Float.BYTES);
    }

    private static void counter() {
        getAllocatedBuffers().incrementAndGet();
        if (openBuffersCount() > 100) {
            LogUtil.logWarn(String.format("Looks like memory leak, allocated buffers count:  %s", openBuffersCount()));
        }
    }

    private static AtomicInteger getAllocatedBuffers() {
        return allocatedBuffers;
    }

    public static void memFree(FloatBuffer floatBuffer) {
        getAllocatedBuffers().decrementAndGet();
        MemoryUtil.memFree(floatBuffer);
    }

    public static void memFree(IntBuffer intBuffer) {
        getAllocatedBuffers().decrementAndGet();
        MemoryUtil.memFree(intBuffer);
    }

    public static void memFree(ByteBuffer buffer) {
        getAllocatedBuffers().decrementAndGet();
        MemoryUtil.memFree(buffer);
    }
}
