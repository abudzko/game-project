package com.game.client.window;

import com.game.client.utils.ConfigUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WindowConfig {
    private String name;
    private int swapInterval;
    private boolean isFullScreen;
    private int width;
    private int height;

    public WindowConfig() {
        var configMap = ConfigUtil.readConfig("/config/window.conf");
        setName(configMap.get("name"));
        setWidth(Integer.parseInt(configMap.get("width")));
        setHeight(Integer.parseInt(configMap.get("height")));
        setSwapInterval(Integer.parseInt(configMap.get("swapInterval")));
        setFullScreen(Boolean.parseBoolean(configMap.get("isFullScreen")));
    }
}
