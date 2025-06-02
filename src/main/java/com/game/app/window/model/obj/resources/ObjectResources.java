package com.game.app.window.model.obj.resources;

import com.game.utils.log.LogUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectResources {

    private static final Map<String, byte[]> cache = new ConcurrentHashMap<>();

    public byte[] getObjectSource(String objPath) {
        return cache.computeIfAbsent(
                objPath,
                path -> {
                    try {
                        var start = System.currentTimeMillis();
                        var resource = IOUtils.resourceToByteArray(path);
                        LogUtil.logDebug("getObject: " + (System.currentTimeMillis() - start) + "ms");
                        return resource;
                    } catch (IOException e) {
                        throw new RuntimeException(String.format("Failed to read resource %s", objPath), e);
                    }
                }
        );
    }
}
