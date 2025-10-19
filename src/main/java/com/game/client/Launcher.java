package com.game.client;

import com.game.client.window.Game;
import org.lwjgl.Version;

import static com.game.client.utils.log.LogUtil.logInfo;

/**
 * Launch new Game instance
 */
public class Launcher {
    public static void main(String[] args) {
        new Launcher().launch();
    }

    public void launch() {
        logInfo(String.format("Starting LWJGL: %s version.", Version.getVersion()));
        new Game().start();
    }
}
