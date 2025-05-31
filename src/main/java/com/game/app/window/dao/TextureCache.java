package com.game.app.window.dao;

import com.game.app.window.lwjgl.texture.TextureProperties;
import com.game.utils.log.LogUtil;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextureCache {
    private static final Map<String, byte[]> cache = new ConcurrentHashMap<>();

    public TextureProperties getTexture(String imagePath) {
        var bytes = cache.computeIfAbsent(
                imagePath,
                path -> {
                    try {
                        var start = System.currentTimeMillis();
                        byte[] texture = IOUtils.resourceToByteArray(path);
                        LogUtil.logDebug("getTexture: " + (System.currentTimeMillis() - start) + "ms");
                        return texture;
                    } catch (IOException e) {
                        LogUtil.logError(e.getMessage(), e);
                        return new byte[0];
                    }

                }
        );
        return TextureProperties.create()
                .setImagePath(imagePath)
                .setTextureImageInputStream(new ByteArrayInputStream(Arrays.copyOf(bytes, bytes.length)));
    }
}
