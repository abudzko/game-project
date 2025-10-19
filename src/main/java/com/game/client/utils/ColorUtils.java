package com.game.client.utils;

import java.util.Random;

public class ColorUtils {

    public static float[] randomColor4f() {
        final Random random = new Random();
        float[] randomColor = new float[4];
        for (int i = 0; i < randomColor.length - 1; i++) {
            randomColor[i] = random.nextFloat();
        }
        randomColor[randomColor.length - 1] = 1f;
        return randomColor;
    }
}
