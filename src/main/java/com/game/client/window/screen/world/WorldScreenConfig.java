package com.game.client.window.screen.world;

import com.game.client.utils.ConfigUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorldScreenConfig {
    private int width;
    private int height;

    WorldScreenConfig() {
        var configMap = ConfigUtil.readConfig("/config/screen/world.conf");
        setWidth(Integer.parseInt(configMap.get("width")));
        setHeight(Integer.parseInt(configMap.get("height")));
    }
}
