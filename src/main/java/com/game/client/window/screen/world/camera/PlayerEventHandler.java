package com.game.client.window.screen.world.camera;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.event.cursor.CursorPositionEvent;
import com.game.client.window.event.key.KeyEvent;
import com.game.client.window.event.listener.WindowEventListener;
import com.game.client.window.event.mouse.MouseButton;
import com.game.client.window.event.mouse.MouseButtonAction;
import com.game.client.window.event.mouse.MouseButtonEvent;
import com.game.client.window.event.scroll.ScrollEvent;
import com.game.client.window.screen.world.camera.rotation.Rotation3D;
import com.game.client.window.screen.world.surface.Intersection;
import org.joml.Vector3f;

import java.util.Optional;

class PlayerEventHandler implements WindowEventListener {
    private final CameraState state;
    private final SurfaceIntersectionFinder surfaceIntersectionFinder;
    private boolean isRightMousePressed = false;
    private float cursorPositionX;
    private float cursorPositionY;

    PlayerEventHandler(CameraState cameraState, SurfaceIntersectionFinder surfaceIntersectionFinder) {
        this.state = cameraState;
        this.surfaceIntersectionFinder = surfaceIntersectionFinder;
        look();
    }

    private static double angleBetweenLineAndZAxis(Vector3f center, Vector3f eye) {
        // Direction vector of the line
        double dx = eye.x() - center.x();
        double dz = eye.z() - center.z();
        return Math.atan2(dx, dz);
    }

    /**
     * Calculates a point on the line segment [eyeCameraPosition to centerCameraPosition],
     * located at a distance l from point eyeCameraPosition
     */
    private static Vector3f findPosition(
            Vector3f eyeCameraPosition,
            Vector3f centerCameraPosition,
            float step
    ) {
        float vectorX = centerCameraPosition.x - eyeCameraPosition.x;
        float vectorY = centerCameraPosition.y - eyeCameraPosition.y;
        float vectorZ = centerCameraPosition.z - eyeCameraPosition.z;

        float segmentLength = (float) Math.sqrt(vectorX * vectorX + vectorY * vectorY + vectorZ * vectorZ);

        float unitVectorX = vectorX / segmentLength;
        float unitVectorY = vectorY / segmentLength;
        float unitVectorZ = vectorZ / segmentLength;

        float resultX = eyeCameraPosition.x + unitVectorX * step;
        float resultY = eyeCameraPosition.y + unitVectorY * step;
        float resultZ = eyeCameraPosition.z + unitVectorZ * step;

        return new Vector3f(resultX, resultY, resultZ);
    }

    private void look() {
        state.look();
    }

    @Override
    public void event(KeyEvent keyEvent) {
        switch (keyEvent.getKeyActionType()) {
            case PRESSED:
                switch (keyEvent.getKey()) {
                    case KEY_UP:
                        break;
                    case KEY_DOWN:
                        break;
                    case KEY_LEFT:
                        break;
                    case KEY_RIGHT:
                        break;
                }
                break;
            case REPEAT:
                switch (keyEvent.getKey()) {
                    case KEY_UP:
                        break;
                    case KEY_DOWN:
                        break;
                    case KEY_RIGHT:
                        break;
                    case KEY_LEFT:
                        break;
                }
                break;
            case RELEASED:
                break;
        }
    }

    @Override
    public void event(ScrollEvent scrollEvent) {
        zoom(scrollEvent);
    }

    private void zoom(ScrollEvent scrollEvent) {
        var step = getState().getMoveStep();
        if (scrollEvent.getOffsetY() < 0) {
            step = -step;
        }
        var currentCameraPosition = getState().getCameraPosition();
        var cameraPosition = findPosition(currentCameraPosition.getEyePosition(), currentCameraPosition.getCenterPosition(), step);
        cameraPosition = CameraUtils.resolveCameraPositionIfUnderSurface(cameraPosition, surfaceIntersectionFinder, getState());
        getState().setCameraPosition(ImmutableCameraPosition.builder()
                .centerPosition(currentCameraPosition.getCenterPosition())
                .eyePosition(cameraPosition)
                .build());
        look();
    }

    @Override
    public void event(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.getButton() == MouseButton.RIGHT) {
            switch (mouseButtonEvent.getAction()) {
                case PRESSED:
                    isRightMousePressed = true;
                    break;
                case RELEASED:
                    isRightMousePressed = false;
                    break;
            }
        } else if (mouseButtonEvent.getButton() == MouseButton.WHEEL) {
            if (mouseButtonEvent.getAction() == MouseButtonAction.PRESSED) {
                defaultCameraPosition();
            }
        }
        LogUtil.logDebug(false, String.format("%s, X: %s, Y: %s", MouseButtonEvent.class.getSimpleName(), mouseButtonEvent.getX(), mouseButtonEvent.getY()));
    }

    private void defaultCameraPosition() {
        var position = new Vector3f(getState().getCameraPosition().getCenterPosition());
        var moveDirection = getState().moveDirection();
        float dx = moveDirection.x;
        float dz = moveDirection.z;
        var a = Math.atan2(dz, dx);
        position.x -= 2f * (float) Math.cos(a);
        position.y += 1f;
        position.z -= 2f * (float) Math.sin(a);
        setCameraPosition(position);
    }

    /**
     * Rotation camera around player position
     */
    @Override
    public void event(CursorPositionEvent event) {
        var previousCursorPositionX = cursorPositionX;
        var previousCursorPositionY = cursorPositionY;
        cursorPositionX = ((float) event.getX());
        cursorPositionY = ((float) event.getY());
        if (isRightMousePressed) {
            var start = System.currentTimeMillis();
            var rotationResult = Optional.<Vector3f>empty();

            // Calculate new Y camera position
            if (cursorPositionY != previousCursorPositionY) {
                var rotationDirection = -1f;
                if (previousCursorPositionY > cursorPositionY) {
                    rotationDirection = 1f;
                }
                var rotationAngle = getState().getRotationStepDegree() * rotationDirection;
                rotationResult = Optional.of(rotateAroundCustomAxis((float) Math.toRadians(rotationAngle)))
                        /* prevent undesired behavior of camera when exceeding of 90 degrees */
                        .filter(p -> {
                            var cameraPosition = getState().getCameraPosition();
                            var centerPosition = cameraPosition.getCenterPosition();
                            var centerX = centerPosition.x;
                            var eyePosition = cameraPosition.getEyePosition();
                            var eyeX = eyePosition.x;
                            var resultX = centerX > eyeX ? centerX > p.x() : centerX < p.x();
                            if (resultX) {
                                var centerZ = centerPosition.z;
                                var eyeZ = eyePosition.z;
                                return centerZ > eyeZ ? centerZ > p.z() : centerZ < p.z();
                            }
                            return false;
                        });
            }

            // Calculate new X and Z camera position
            if (previousCursorPositionX != cursorPositionX) {
                var rotationDirection = -1f;
                if (previousCursorPositionX > cursorPositionX) {
                    rotationDirection = 1f;
                }
                var rotationAngle = getState().getRotationStepDegree() * rotationDirection;
                rotationResult = Optional.of(
                        rotateDeltaOy(
                                (float) Math.toRadians(rotationAngle),
                                rotationResult.orElse(getState().getCameraPosition().getEyePosition())
                        )
                );

            }
            rotationResult.ifPresent(rotationPosition -> {
                Optional.ofNullable(surfaceIntersectionFinder.findIntersection(rotationPosition))
                        .map(Intersection::getPoint)
                        .filter(intersectionPoint -> rotationPosition.y() > intersectionPoint.y() + getState().getMoveStep())
                        .ifPresent(intersectionPosition -> setCameraPosition(rotationPosition));
            });
        }
    }

    private void setCameraPosition(Vector3f eyePosition) {
        getState().setCameraPosition(ImmutableCameraPosition.builder()
                .eyePosition(eyePosition)
                .centerPosition(getState().getCameraPosition().getCenterPosition())
                .build());
        LogUtil.logDebug(false, String.format("setCameraPosition x = %s y = %s z = %s", eyePosition.x(), eyePosition.y(), eyePosition.z()));
        look();
    }

    private Vector3f rotateAroundCustomAxis(float angleDeltaRadians) {
        var center = getState().getCameraPosition().getCenterPosition();
        var cameraEye = getState().getCameraPosition().getEyePosition();

        var axis = Rotation3D.calculatePerpendicularAxisZXPlane(center, cameraEye);

        // Perform rotation
        return Rotation3D.rotateAroundAxis(cameraEye, center, axis, angleDeltaRadians);
    }

    private Vector3f rotateDeltaOy(float angleDeltaRadians, Vector3f cameraEye) {
        return Rotation3D.rotateAroundPoint(
                cameraEye,
                getState().getCameraPosition().getCenterPosition(),
                0,
                angleDeltaRadians,
                0
        );
    }

    private CameraState getState() {
        return this.state;
    }
}
