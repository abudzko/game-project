package com.game.client.window.screen.world.engine.action;

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
    private final float speed = 0.05f;
    private final Camera camera;
    @Builder.Default
    private boolean move = true;

    @Override
    public boolean act(GameUnit gameUnit) {
        if (move) {
            calculateNextPosition(gameUnit.getSharedUnitState().getPosition(), targetPosition)
                    .ifPresentOrElse(position -> {
                        gameUnit.getSharedUnitState().setPosition(position);
                        gameUnit.getSharedUnitState().updateWorldMatrix();
                        camera.follow(position);
                    }, () -> move = false);
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
