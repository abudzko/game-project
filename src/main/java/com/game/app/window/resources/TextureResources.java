package com.game.app.window.resources;

import com.game.utils.log.LogUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextureResources {
    private static final Map<String, byte[]> cache = new ConcurrentHashMap<>();

    public byte[] getTextureSource(String imagePath) {
        return cache.computeIfAbsent(
                imagePath,
                path -> {
                    try {
                        var start = System.currentTimeMillis();
                        byte[] texture = IOUtils.resourceToByteArray(path);
                        LogUtil.logDebug("getTexture: " + (System.currentTimeMillis() - start) + "ms");
                        return texture;
                    } catch (IOException e) {
                        throw new RuntimeException(String.format("Failed to read resource %s", imagePath), e);
                    }

                }
        );
    }
}
