package com.game.client.window.screen.world.camera;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.event.cursor.CursorPositionEvent;
import com.game.client.window.event.key.KeyEvent;
import com.game.client.window.event.listener.WindowEventListener;
import com.game.client.window.event.mouse.MouseButton;
import com.game.client.window.event.mouse.MouseButtonEvent;
import com.game.client.window.event.scroll.ScrollEvent;
import com.game.client.window.screen.world.camera.rotation.Rotation3D;
import com.game.client.window.screen.world.surface.Intersection;
import org.joml.Vector3f;

import java.util.Optional;

class CameraEventHandler implements WindowEventListener {
    private final CameraState state;
    private final SurfaceIntersectionFinder surfaceIntersectionFinder;
    private boolean isRightMousePressed = false;
    private float cursorPositionX;
    private float cursorPositionY;

    CameraEventHandler(CameraState cameraState, SurfaceIntersectionFinder surfaceIntersectionFinder) {
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

    private void stepX(float delta) {
        var angleXZRadians = getAngleXZ();
        var deltaZ = -delta * (float) Math.sin(angleXZRadians);
        getState().setCenterZ(getState().getCenterZ() + deltaZ);

        var deltaX = delta * (float) Math.cos(angleXZRadians);
        getState().setCenterX(getState().getCenterX() + deltaX);

        var cameraPosition = new Vector3f();
        cameraPosition.x = getState().getEyeX() + deltaX;
        cameraPosition.y = getState().getEyeY();
        cameraPosition.z = getState().getEyeZ() + deltaZ;
        cameraPosition = resolveCameraPositionIfUnderSurface(cameraPosition);
        getState().setEyeX(cameraPosition.x());
        getState().setEyeY(cameraPosition.y());
        getState().setEyeZ(cameraPosition.z());

        LogUtil.logDebug(String.format("Move camera to %s", cameraPosition));
        look();
    }

    private void stepY(float delta) {
        var cameraPosition = new Vector3f();
        cameraPosition.x = getState().getEyeX();
        cameraPosition.y = getState().getEyeY() + delta;
        cameraPosition.z = getState().getEyeZ();
        cameraPosition = resolveCameraPositionIfUnderSurface(cameraPosition);
        getState().setEyeX(cameraPosition.x());
        getState().setEyeY(cameraPosition.y());
        getState().setEyeZ(cameraPosition.z());
        look();
    }

    private void stepZ(float delta) {
        var angleXZRadians = getAngleXZ();
        float deltaZ = delta * (float) Math.cos(angleXZRadians);
        getState().setCenterZ(getState().getCenterZ() + deltaZ);

        float deltaX = delta * (float) Math.sin(angleXZRadians);
        getState().setCenterX(getState().getCenterX() + deltaX);

        var cameraPosition = new Vector3f();
        cameraPosition.x = getState().getEyeX() + deltaX;
        cameraPosition.y = getState().getEyeY();
        cameraPosition.z = getState().getEyeZ() + deltaZ;
        cameraPosition = resolveCameraPositionIfUnderSurface(cameraPosition);
        getState().setEyeX(cameraPosition.x());
        getState().setEyeY(cameraPosition.y());
        getState().setEyeZ(cameraPosition.z());

        LogUtil.logDebug(String.format("Move camera to %s", getState().getCameraPosition()));
        look();
    }

    private void look() {
        state.look();
    }

    private float getAngleXZ() {
        return (float) angleBetweenLineAndZAxis(getState().getCenterPosition(), getState().getCameraPosition());
    }

    @Override
    public void event(KeyEvent keyEvent) {
        var step = getState().getMoveStep();
        switch (keyEvent.getKeyActionType()) {
            case PRESSED:
                switch (keyEvent.getKey()) {
                    case KEY_UP:
                        stepZ(-step);
                        break;
                    case KEY_DOWN:
                        stepZ(step);
                        break;
                    case KEY_LEFT:
                        stepX(-step);
                        break;
                    case KEY_RIGHT:
                        stepX(step);
                        break;
                }
                break;
            case REPEAT:
                switch (keyEvent.getKey()) {
                    case KEY_UP:
                        stepZ(-step);
                        break;
                    case KEY_DOWN:
                        stepZ(step);
                        break;
                    case KEY_RIGHT:
                        stepX(step);
                        break;
                    case KEY_LEFT:
                        stepX(-step);
                        break;
                }
                break;
            case RELEASED:
                break;
        }
    }

    @Override
    public void event(ScrollEvent scrollEvent) {
        stepY((int) scrollEvent.getOffsetY() * (-getState().getMoveStep()));
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
        }
//        LogUtil.log(String.format("%s, X: %s, Y: %s", MouseButtonEvent.class.getSimpleName(), mouseButtonEvent.getX(), mouseButtonEvent.getY()));
    }

    @Override
    public void event(CursorPositionEvent event) {
        var previousCursorPositionX = cursorPositionX;
        var previousCursorPositionY = cursorPositionY;
        cursorPositionX = ((float) event.getX());
        cursorPositionY = ((float) event.getY());
        if (isRightMousePressed) {
            var start = System.currentTimeMillis();
            var rotationResult = Optional.<Vector3f>empty();
            if (cursorPositionY != previousCursorPositionY) {
                var rotationDirection = -1f;
                if (previousCursorPositionY > cursorPositionY) {
                    rotationDirection = 1f;
                }
                rotationResult = Optional.of(rotateAroundCustomAxis((float) Math.toRadians(rotationDirection)))
                        /* prevent undesired behavior of camera when exceeding of 90 degrees */
                        .filter(p -> {
                            var centerX = getState().getCenterX();
                            var eyeX = getState().getEyeX();
                            var resultX = centerX > eyeX ? centerX > p.x() : centerX < p.x();
                            if (resultX) {
                                var centerZ = getState().getCenterZ();
                                var eyeZ = getState().getEyeZ();
                                return centerZ > eyeZ ? centerZ > p.z() : centerZ < p.z();
                            }
                            return false;
                        });
            }
            if (previousCursorPositionX != cursorPositionX) {
                var rotationDirection = -1f;
                if (previousCursorPositionX > cursorPositionX) {
                    rotationDirection = 1f;
                }
                rotationResult = Optional.of(
                        rotateDeltaOy(
                                (float) Math.toRadians(rotationDirection),
                                rotationResult.orElse(getState().getCameraPosition())
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

    private void setCameraPosition(Vector3f position) {
        getState().setEyeX(position.x());
        getState().setEyeY(position.y());
        getState().setEyeZ(position.z());

        LogUtil.logDebug(String.format("x = %s y = %s z = %s", position.x(), position.y(), position.z()));
        look();
    }

    private Vector3f resolveCameraPositionIfUnderSurface(Vector3f position) {
        return Optional.ofNullable(surfaceIntersectionFinder.findIntersection(position))
                .map(intersection -> {
                    var intersectionPoint = intersection.getPoint();
//                    LogUtil.logDebug(String.format("resolveCameraPosition intersectionPoint %s, position %s", intersectionPoint, position));
                    intersectionPoint.y = intersectionPoint.y + getState().getMoveStep();
                    return intersectionPoint;
                })
                .filter(intersectionPoint -> intersectionPoint.y() > position.y())
                .orElse(position);
    }

    private Vector3f rotateAroundCustomAxis(float angleDeltaRadians) {
        var center = state.getCenterPosition();
        var cameraEye = getState().getCameraPosition();

        var axis = Rotation3D.calculatePerpendicularAxisZXPlane(center, cameraEye);

        // Perform rotation
        return Rotation3D.rotateAroundAxis(cameraEye, center, axis, angleDeltaRadians);
    }

    private Vector3f rotateDeltaOy(float angleDeltaRadians, Vector3f cameraEye) {
        return Rotation3D.rotateAroundPoint(cameraEye, getState().getCenterPosition(), 0, angleDeltaRadians, 0);
    }

    private CameraState getState() {
        return this.state;
    }
}
