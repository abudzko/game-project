package com.game.app.window.lwjgl.texture;

import com.game.utils.BufferUtils;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static de.matthiasmann.twl.utils.PNGDecoder.Format.RGBA;

public class PngTexture {

    private final InputStream imageInputStream;
    private final String imagePath;
    private ByteBuffer buffer;
    private PNGDecoder pngDecoder;
    private Integer textureId;

    private PngTexture(TextureProperties textureProperties) {
        this.imageInputStream = textureProperties.getTextureImage();
        this.imagePath = textureProperties.getTextureImagePath();
        readImageInputStream();
    }

    public static PngTexture createPngTexture(TextureProperties properties) {
        return new PngTexture(properties);
    }

    public void readImageInputStream() {
        try {
            // Load png file
            this.pngDecoder = new PNGDecoder(imageInputStream);
            // Create a byte buffer big enough to store RGBA values

            this.buffer = BufferUtils.createByteBuffer(new byte[RGBA.getNumComponents() * pngDecoder.getWidth() * pngDecoder.getHeight()]);
            // Decode
            pngDecoder.decode(buffer, pngDecoder.getWidth() * RGBA.getNumComponents(), RGBA);
            // Flip the buffer(prepare for reading)
            buffer.flip();
            imageInputStream.close();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to load image %s", imagePath));
        }
    }

    private void loadTexture() {
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
                pngDecoder.getWidth(),
                pngDecoder.getHeight(),
                0, GL30.GL_RGBA,
                GL30.GL_UNSIGNED_BYTE,
                buffer
        );

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
        textureId = id;
        // Free
        BufferUtils.memFree(buffer);
    }

    public int getTextureId() {
        if (textureId == null) {
            loadTexture();
        }
        return textureId;
    }
}
