package com.game.app.window.lwjgl.texture;

import com.game.app.window.model.obj.ObjModelParameters;
import com.game.app.window.model.texture.Texture;
import com.game.utils.BufferUtils;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.opengl.GL30;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static de.matthiasmann.twl.utils.PNGDecoder.Format.RGBA;

public class PngTexture implements Texture {

    private final byte[] source;
    private final Object modelKey;
    private ByteBuffer buffer;
    private Integer textureId;
    private int textureWidth;
    private int textureHeight;

    private PngTexture(ObjModelParameters objModelParameters) {
        this.source = objModelParameters.getTextureSource();
        this.modelKey = objModelParameters.getModelKey();
        decodePng();
    }

    public static PngTexture createPngTexture(ObjModelParameters objModelParameters) {
        return new PngTexture(objModelParameters);
    }

    private void decodePng() {
        try (var inputStream = new ByteArrayInputStream(source)) {
            var pngDecoder = new PNGDecoder(inputStream);
            // Create a byte buffer big enough to store RGBA values
            this.buffer = BufferUtils.createByteBuffer(new byte[RGBA.getNumComponents() * pngDecoder.getWidth() * pngDecoder.getHeight()]);
            // Decode
            pngDecoder.decode(buffer, pngDecoder.getWidth() * RGBA.getNumComponents(), RGBA);
            this.textureWidth = pngDecoder.getWidth();
            this.textureHeight = pngDecoder.getHeight();
            // Flip the buffer(prepare for reading)
            buffer.flip();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to decode png for model %s", modelKey));
        }
    }

    public int getTextureId() {
        if (textureId == null) {
            loadTexture();
        }
        return textureId;
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
                textureWidth,
                textureHeight,
                0, GL30.GL_RGBA,
                GL30.GL_UNSIGNED_BYTE,
                buffer
        );

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
        textureId = id;
        // Free
        BufferUtils.memFree(buffer);
    }
}
