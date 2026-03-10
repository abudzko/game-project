package com.game.client.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtil {

    public static Map<String, String> readConfig(String path) {
        var config = new HashMap<String, String>();
        var configSource = FileUtils.resource(path);
        Arrays.stream(configSource.split(System.lineSeparator())).forEach(l -> {
                    var keyValue = l.split("=");
                    config.put(keyValue[0].trim(), keyValue[1].trim());
                }
        );
        return config;
    }
}
