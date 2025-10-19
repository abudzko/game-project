package com.game.client.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileUtils {

    public static String loadAsString(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
