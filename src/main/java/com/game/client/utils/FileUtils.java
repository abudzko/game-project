package com.game.client.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public static String resource(String path) {
        try {
            return IOUtils.resourceToString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
