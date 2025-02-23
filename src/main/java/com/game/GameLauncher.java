package com.game;

import org.lwjgl.Version;

import static com.game.utils.log.LogUtil.logInfo;

/**
 * Launch new Game instance
 */
public class GameLauncher {
    public static void main(String[] args) {
        new GameLauncher().launch();
    }

    public void launch() {
        logInfo(String.format("Starting LWJGL: %s version.", Version.getVersion()));
        new Game().start();
    }
}
