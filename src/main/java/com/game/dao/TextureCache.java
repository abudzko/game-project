package com.game.dao;

import com.game.lwjgl.texture.TextureProperties;
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
                        return IOUtils.resourceToByteArray(path);
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
