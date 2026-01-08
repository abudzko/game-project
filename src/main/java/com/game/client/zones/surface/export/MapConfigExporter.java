package com.game.client.zones.surface.export;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MapConfigExporter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void export(MapConfig mapConfig, Path path) {

        try {
            Files.write(path.resolve("conf.json"), MAPPER.writeValueAsBytes(mapConfig));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
