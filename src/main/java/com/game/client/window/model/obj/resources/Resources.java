package com.game.client.window.model.obj.resources;

import com.game.client.utils.log.LogUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Resources {
    private static final Map<String, byte[]> cache = new ConcurrentHashMap<>();

    public byte[] getResource(String resourcePath) {
        return cache.computeIfAbsent(
                resourcePath,
                path -> {
                    try {
                        var start = System.currentTimeMillis();
                        var resource = IOUtils.resourceToByteArray(path);
                        LogUtil.logDebug("getResource: " + (System.currentTimeMillis() - start) + "ms");
                        return resource;
                    } catch (IOException e) {
                        throw new RuntimeException(String.format("Failed to read resource %s", resourcePath), e);
                    }
                }
        );
    }
}
