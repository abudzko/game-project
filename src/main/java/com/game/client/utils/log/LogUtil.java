package com.game.client.utils.log;

import org.apache.commons.math3.util.Precision;
import org.joml.Vector3f;

public class LogUtil {
    private static final boolean debug = true;

    public static void logInfo(String message) {
        System.out.println("INFO: " + message);
    }

    public static void logError(String message, Throwable ex) {
        System.out.println(message);
        ex.printStackTrace();
    }

    public static void logError(String message) {
        System.out.println("ERROR: " + message);
    }

    public static void logWarn(String message) {
        System.out.println("WARN: " + message);
    }

    public static void logDebug(String message) {
        if (debug) {
            System.out.println("DEBUG: " + message);
        }
    }

    public static String toStr(Vector3f point) {
        int scale = 3;
        return String.format("%s %s %s",
                Precision.round(point.x, scale),
                Precision.round(point.y, scale),
                Precision.round(point.z, scale)
        );
    }
}
