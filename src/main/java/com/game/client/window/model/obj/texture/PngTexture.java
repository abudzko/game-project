package com.game.client.window.model.obj.texture;

import com.game.client.window.model.obj.ObjModelParameters;
import de.matthiasmann.twl.utils.PNGDecoder;
import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static de.matthiasmann.twl.utils.PNGDecoder.Format.RGBA;

@Getter
public class PngTexture implements Texture {

    private byte[] decodedPng;
    private int textureWidth;
    private int textureHeight;

    private PngTexture(ObjModelParameters objModelParameters) {
        decodePng(objModelParameters);
    }

    public static PngTexture createPngTexture(ObjModelParameters objModelParameters) {
        return new PngTexture(objModelParameters);
    }

    private void decodePng(ObjModelParameters objModelParameters) {
        try (var inputStream = new ByteArrayInputStream(objModelParameters.getTextureSource())) {
            var pngDecoder = new PNGDecoder(inputStream);
            // Create a byte buffer big enough to store RGBA values
            // HeapByteBuffer is used
            var buffer = ByteBuffer.wrap(new byte[RGBA.getNumComponents() * pngDecoder.getWidth() * pngDecoder.getHeight()]);
            // Decode
            pngDecoder.decode(buffer, pngDecoder.getWidth() * RGBA.getNumComponents(), RGBA);
            this.textureWidth = pngDecoder.getWidth();
            this.textureHeight = pngDecoder.getHeight();
            decodedPng = buffer.array();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to decode png for model %s", objModelParameters.getModelKey()));
        }
    }
}
