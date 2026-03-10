package com.game.client;

import com.game.client.window.WindowContainer;
import org.lwjgl.Version;

import static com.game.client.utils.log.LogUtil.logInfo;

public class GameClient {

    public static void main(String[] args) {
        logInfo(String.format("Starting LWJGL: %s version.", Version.getVersion()));
        new GameClient().start();
    }

    public void start() {
        var windowContainer = new WindowContainer();
        try {
            windowContainer.startWindows();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
