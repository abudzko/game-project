package com.game.client.window.screen.world.engine.action;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.screen.world.camera.Camera;
import com.game.client.window.screen.world.engine.unit.GameUnit;
import com.game.client.window.screen.world.surface.Intersection;
import lombok.Builder;
import lombok.Getter;
import org.joml.Vector3f;

import java.util.Optional;

@Getter
@Builder
public class MoveAction implements GameUnitAction {
    private final long gameUnitId;
    private final Vector3f targetPosition;
    private final float speed = 0.01f;
    private final Camera camera;
    @Builder.Default
    private boolean move = true;

    private boolean rotateToTarget = false;

    public static Vector3f calculateRotationToFacePoint(GameUnit gameUnit, Vector3f targetPosition) {
        var unitPosition = gameUnit.getSharedUnitState().getPosition();
        float dx = targetPosition.x - unitPosition.x;
        float dz = targetPosition.z - unitPosition.z;

        float targetAngleRad = (float) Math.atan2(dx, dz);
        float targetAngleDeg = (float) Math.toDegrees(targetAngleRad);
        targetAngleDeg += 180f;
        targetAngleDeg = normalizeAngle(targetAngleDeg);
        LogUtil.logDebug(targetAngleDeg + "");
        var currentRotation = gameUnit.getSharedUnitState().getRotation();
        return new Vector3f(currentRotation.x, targetAngleDeg, currentRotation.z);
    }

    private static float normalizeAngle(float angleDeg) {
        angleDeg %= 360f;
        if (angleDeg < 0) {
            angleDeg += 360f;
        }
        return angleDeg;
    }

    @Override
    public boolean act(GameUnit gameUnit) {
        if (move) {
            if (!rotateToTarget) {
                rotateToTarget = true;
                var rotation = calculateRotationToFacePoint(gameUnit, targetPosition);
                gameUnit.getSharedUnitState().setRotation(rotation);
            }
            calculateNextPosition(gameUnit.getSharedUnitState().getPosition(), targetPosition)
                    .ifPresentOrElse(position -> {
                                camera.follow(position);// Don't reorder to avoid camera jerks
                                gameUnit.getSharedUnitState().setPosition(position);
                                gameUnit.getSharedUnitState().updateWorldMatrix();
                            }, () -> move = false
                    );
        }
        return move;
    }

    public Optional<Vector3f> calculateNextPosition(Vector3f currentPosition, Vector3f targetPosition) {
        float dx = targetPosition.x - currentPosition.x;
        float dz = targetPosition.z - currentPosition.z;
        float distance = (float) Math.sqrt(dx * dx + dz * dz);

        if (distance <= speed) {
            move = false;
            return Optional.empty();
        }

        float ratio = speed / distance;
        Vector3f tmpPosition = new Vector3f(
                currentPosition.x + dx * ratio,
                currentPosition.y,
                currentPosition.z + dz * ratio
        );

        return Optional.ofNullable(camera.findIntersection(tmpPosition))
                .map(Intersection::getPoint);
    }
}
