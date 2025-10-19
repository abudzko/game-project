package com.game.client.window;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WindowState {
    private final String name = "GAME_WINDOW";
    private int swapInterval = 1;
    private boolean isFullScreen = false;
    private int width = 500;
    private int height = 500;
}
