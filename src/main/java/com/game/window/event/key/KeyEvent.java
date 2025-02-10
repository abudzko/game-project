package com.game.window.event.key;

public class KeyEvent {
    private final Keys key;
    private final KeyActionType keyActionType;
    private final int keyDeprecated;
    private final int scanCode;
    private final int actionDeprecated;
    private final int mods;

    public KeyEvent(
            Keys key,
            KeyActionType actionType,
            int keyDeprecated,
            int scanCode,
            int actionDeprecated,
            int mods
    ) {
        this.key = key;
        this.keyActionType = actionType;
        this.keyDeprecated = keyDeprecated;
        this.scanCode = scanCode;
        this.actionDeprecated = actionDeprecated;
        this.mods = mods;
    }

    public Keys getKey() {
        return key;
    }

    public KeyActionType getKeyActionType() {
        return keyActionType;
    }

    public int getKeyDeprecated() {
        return keyDeprecated;
    }

    public int getScanCode() {
        return scanCode;
    }

    public int getActionDeprecated() {
        return actionDeprecated;
    }

    public int getMods() {
        return mods;
    }
}
