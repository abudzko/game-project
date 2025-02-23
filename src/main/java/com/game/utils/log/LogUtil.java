package com.game.utils.log;

public class LogUtil {
    private static boolean debug = true;

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
}
