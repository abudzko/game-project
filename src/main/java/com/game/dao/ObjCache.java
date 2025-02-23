package com.game.dao;

import com.game.utils.log.LogUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjCache {

    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public String getObject(String objPath) {
        return cache.computeIfAbsent(
                objPath,
                path -> {
                    try {
                        var start = System.currentTimeMillis();
                        String resource = IOUtils.resourceToString(path, StandardCharsets.UTF_8);
                        LogUtil.logDebug("getObject: " + (System.currentTimeMillis() - start) + "ms");
                        return resource;
                    } catch (IOException e) {
                        LogUtil.logError(e.getMessage(), e);
                        return "";
                    }

                }
        );
    }
}
