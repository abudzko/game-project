package com.game.app.window.lwjgl.texture;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

public class TextureDao {
    private final ConcurrentHashMap<String, Integer> textures = new ConcurrentHashMap<>();

    public Integer getTexture(String textureKey) {
        return textures.computeIfAbsent(textureKey, this::loadTexture);
    }

    private void deleteTexture(String textureKey) {
        var id = textures.remove(textureKey);
        if (id != null) {
            GL30.glDeleteTextures(id);
        }
    }

    private Integer loadTexture(String imagePath) {
        try {
            // Load png file
            var decoder = new PNGDecoder(getClass().getResourceAsStream(imagePath));
            // Create a byte buffer big enough to store RGBA values
            var buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());

            // Decode
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);

            // Flip the buffer(prepare for reading)
            buffer.flip();

            // Create a texture
            var id = GL30.glGenTextures();

            // Bind the texture
            GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);

            // Tell opengl how to unpack bytes
            GL30.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 1);

            // Set the texture parameters, can be GL_LINEAR or GL_NEAREST
            GL30.glTexParameterf(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
            GL30.glTexParameterf(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);

            // Upload texture
            GL30.glTexImage2D(
                    GL30.GL_TEXTURE_2D,
                    0,
                    GL30.GL_RGBA,
                    decoder.getWidth(),
                    decoder.getHeight(),
                    0, GL30.GL_RGBA,
                    GL30.GL_UNSIGNED_BYTE,
                    buffer
            );

            GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
            return id;
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to load texture by path %s", imagePath));
        }
    }
}
