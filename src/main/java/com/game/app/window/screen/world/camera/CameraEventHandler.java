package com.game.app.window.screen.world.camera;

import com.game.app.window.event.cursor.CursorPositionEvent;
import com.game.app.window.event.key.KeyEvent;
import com.game.app.window.event.listener.WindowEventListener;
import com.game.app.window.event.mouse.MouseButton;
import com.game.app.window.event.mouse.MouseButtonEvent;
import com.game.app.window.event.scroll.ScrollEvent;
import com.game.app.window.screen.world.camera.rotation.Rotation3D;
import com.game.utils.log.LogUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Optional;

class CameraEventHandler implements WindowEventListener {
    private final CameraState state;
    private final SurfaceIntersectionFinder surfaceIntersectionFinder;

    CameraEventHandler(CameraState cameraState, SurfaceIntersectionFinder surfaceIntersectionFinder) {
        this.state = cameraState;
        this.surfaceIntersectionFinder = surfaceIntersectionFinder;
        look();
    }

    public static double angleBetweenLineAndZAxis(Vector3f center, Vector3f eye) {
        // Direction vector of the line
        double dx = eye.x() - center.x();
        double dz = eye.z() - center.z();
        return Math.atan2(dx, dz);
    }

    /**
     * TODO fix case when camera is under surface
     */
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
        cameraPosition = resolveCameraPosition(cameraPosition);
        getState().setEyeX(cameraPosition.x());
        getState().setEyeY(cameraPosition.y());
        getState().setEyeZ(cameraPosition.z());

        LogUtil.logDebug(String.format("Move camera to %s", cameraPosition));
        look();
    }

    private void stepY(float delta) {
        var cameraPosition = new Vector3f();
        cameraPosition.x = getState().getEyeX();
        cameraPosition.y = getState().getEyeY()+ delta;
        cameraPosition.z = getState().getEyeZ();
        cameraPosition = resolveCameraPosition(cameraPosition);
        getState().setEyeX(cameraPosition.x());
        getState().setEyeY(cameraPosition.y());
        getState().setEyeZ(cameraPosition.z());
        look();
    }

    private void stepZ(float delta) {
        var angleXZRadians = getAngleXZ();
        float deltaZ = delta * (float) Math.cos(angleXZRadians);
        getState().setCenterZ(getState().getCenterZ() + deltaZ);
//        getState().setEyeZ(getState().getEyeZ() + deltaZ);

        float deltaX = delta * (float) Math.sin(angleXZRadians);
        getState().setCenterX(getState().getCenterX() + deltaX);
//        getState().setEyeX(getState().getEyeX() + deltaX);

        var cameraPosition = new Vector3f();
        cameraPosition.x = getState().getEyeX() + deltaX;
        cameraPosition.y = getState().getEyeY();
        cameraPosition.z = getState().getEyeZ() + deltaZ;
        cameraPosition = resolveCameraPosition(cameraPosition);
        getState().setEyeX(cameraPosition.x());
        getState().setEyeY(cameraPosition.y());
        getState().setEyeZ(cameraPosition.z());

        LogUtil.logDebug(String.format("Move camera to %s", getState().getCameraPosition()));
        look();
    }

    private float getAngleXZ() {
        return (float) angleBetweenLineAndZAxis(getState().getCenterPosition(), getState().getCameraPosition());
    }

    private void look() {
        var m = new Matrix4f();
        m.lookAt(eye(), center(), up());
        getState().setCameraViewMatrix(m);
    }

    private Vector3f eye() {
        return new Vector3f(getState().getEyeX(), getState().getEyeY(), getState().getEyeZ());
    }

    private Vector3f center() {
        return new Vector3f(getState().getCenterX(), getState().getCenterY(), getState().getCenterZ());
    }

    private Vector3f up() {
        return new Vector3f(getState().getUpX(), getState().getUpY(), getState().getUpZ());
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
                    getState().setRightMousePressed(true);
                    getState().setRightMouseReleased(true);
                    break;
                case RELEASED:
                    getState().setRightMouseReleased(true);
                    getState().setRightMousePressed(false);
                    break;
            }
        }
//        LogUtil.log(String.format("%s, X: %s, Y: %s", MouseButtonEvent.class.getSimpleName(), mouseButtonEvent.getX(), mouseButtonEvent.getY()));
    }

    @Override
    public void event(CursorPositionEvent event) {
        getState().setPreviousCursorPositionX(getState().getCursorPositionX());
        getState().setPreviousCursorPositionY(getState().getCursorPositionY());
        getState().setCursorPositionX((float) event.getX());
        getState().setCursorPositionY((float) event.getY());
        if (getState().isRightMousePressed()) {
            var start = System.currentTimeMillis();
            var rotationResult = Optional.<Vector3f>empty();
            if (getState().getCursorPositionY() != getState().getPreviousCursorPositionY()) {
                var rotationDirection = -1f;
                if (getState().getPreviousCursorPositionY() > getState().getCursorPositionY()) {
                    rotationDirection = 1f;
                }
                boolean down = rotationDirection > 0;
                rotationResult = Optional.of(rotateAroundCustomAxis((float) Math.toRadians(rotationDirection * 1)))
                        /* prevent undesired behavior of camera when exceeding of 90 degrees */
                        .filter(p -> {
                            var diff = p.z() - getState().getCenterZ();
                            LogUtil.logDebug(String.format("Diff %s %s", diff, down));
                            return down || Math.abs(diff) > .1f;
                        });
            }
            if (getState().getPreviousCursorPositionX() != getState().getCursorPositionX()) {
                var rotationDirection = -1f;
                if (getState().getPreviousCursorPositionX() > getState().getCursorPositionX()) {
                    rotationDirection = 1f;
                }
                rotationResult = Optional.of(
                        rotateDeltaOy(
                                (float) Math.toRadians(rotationDirection * 1),
                                rotationResult.orElse(getState().getCameraPosition())
                        )
                );

            }
            rotationResult.ifPresent(this::setCameraPosition);
//            LogUtil.logDebug(String.format("CursorPositionEvent %sms", System.currentTimeMillis() - start));
        }
    }

    private void setCameraPosition(Vector3f position) {
        var point = resolveCameraPosition(position);

        getState().setEyeX(point.x());
        getState().setEyeY(point.y());
        getState().setEyeZ(point.z());

        LogUtil.logDebug(String.format("x = %s y = %s z = %s", point.x(), point.y(), point.z()));
        look();
    }

    private Vector3f resolveCameraPosition(Vector3f position) {
        var delta = 0.1f;
        return Optional.ofNullable(surfaceIntersectionFinder.findIntersection(position))
                .map(intersection -> {
                    var intersectionPoint = intersection.getPoint();
                    LogUtil.logDebug(String.format("resolveCameraPosition intersectionPoint %s, position %s", intersectionPoint, position));
                    intersectionPoint.y = intersectionPoint.y + delta;
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
