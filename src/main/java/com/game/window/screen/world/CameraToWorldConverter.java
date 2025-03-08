package com.game.window.screen.world;

import lombok.Builder;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Getter
@Builder
public class CameraToWorldConverter {
    private final double mouseX;
    private final double mouseY;
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final int width;
    private final int height;

    public Vector3f directionPoint() {
        // Normalize mouse coordinates
        float x = (float) (2.0 * mouseX / width - 1.0);
        float y = (float) (1.0 - 2.0 * mouseY / height);

        // Create the clip coordinates
        var clipCoordinates = new Vector4f(x, y, -1.0f, 1.0f);

        var invertedProjection = projectionMatrix.invert();
        var eyeCoordinates = invertedProjection.transform(clipCoordinates);
        eyeCoordinates.z = -1.0f; // Set Z to -1.0 for the ray
        eyeCoordinates.w = 0.0f; // Set W to 0 to make it a direction vector

        // Convert to world coordinates
        var viewMatrixInverse = viewMatrix.invert();
        var worldRay = viewMatrixInverse.transform(eyeCoordinates);

        // Create a direction vector from the ray
        var direction = new Vector3f(worldRay.x, worldRay.y, worldRay.z);
        direction.normalize();

        return direction;
    }
}
