package com.game.window.screen.world;

import com.game.window.event.mouse.MouseButtonEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CameraToWorldConverter {
    private final double mouseX;
    private final double mouseY;
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;

    /**
     * @param projectionMatrix - the copy of projectionMatrix is required
     * @param viewMatrix - the copy of viewMatrix is required
     */
    public CameraToWorldConverter(
            MouseButtonEvent mouseButtonEvent,
            Matrix4f projectionMatrix,
            Matrix4f viewMatrix
    ) {
        this.mouseX = mouseButtonEvent.getX();
        this.mouseY = mouseButtonEvent.getY();
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
    }

    public Vector3f directionPoint(WorldScreenState worldScreenState) {
        // Get the width and height of the window
        float width = worldScreenState.getWidth()/* Your window width */;
        float height = worldScreenState.getHeight()/* Your window height */;

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
        Vector3f direction = new Vector3f(worldRay.x, worldRay.y, worldRay.z);
        direction.normalize();

        return direction;
    }
}
