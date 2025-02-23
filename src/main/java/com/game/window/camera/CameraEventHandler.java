package com.game.window.camera;

import com.game.window.event.cursor.CursorPositionEvent;
import com.game.window.event.key.KeyEvent;
import com.game.window.event.listener.WindowEventListener;
import com.game.window.event.mouse.MouseButton;
import com.game.window.event.mouse.MouseButtonEvent;
import com.game.window.event.scroll.ScrollEvent;
import com.game.utils.log.LogUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

class CameraEventHandler implements WindowEventListener {
    private final CameraState state;

    CameraEventHandler(CameraState cameraState) {
        this.state = cameraState;
        look();
    }

    private void stepX(float delta) {
        float deltaZ = -delta * (float) Math.sin(state.angle);
        state.centerZ += deltaZ;
        state.eyeZ += deltaZ;

        float deltaX = delta * (float) Math.cos(state.angle);
        state.centerX += deltaX;
        state.eyeX += deltaX;
//        LogUtil.logDebug(String.format("Move camera to %s eyeX position dX = %s, dZ = %s angle = %s ", state.eyeX, deltaX, deltaZ, state.angle * 180 / Math.PI));
        look();
    }

    private void stepY(float delta) {
        float newValueY = state.eyeY + delta;
        if (newValueY > 0.1) {
//            LogUtil.logDebug(String.format("Move camera from %s to %s eyeY position", state.eyeY, newValueY));
            state.eyeY = newValueY;
            look();
        } else {
//            LogUtil.logDebug(String.format("Can't move camera from %s to %s eyeY position", state.eyeY, newValueY));
        }
    }

    private void stepZ(float delta) {
        float deltaZ = delta * (float) Math.cos(state.angle);
        state.centerZ += deltaZ;
        state.eyeZ += deltaZ;

        float deltaX = delta * (float) Math.sin(state.angle);
        state.centerX += deltaX;
        state.eyeX += deltaX;
        LogUtil.logDebug(String.format("Move camera to %s eyeZ position angle = %s deltaZ = %s", state.eyeZ, state.angle * 180 / Math.PI, deltaZ));
        look();
    }

    private void rotateDelta(float angleDelta) {
//        var r = Math.sqrt(Math.pow(this.eyeX - this.centerX, 2) + Math.pow(this.eyeZ - this.centerZ, 2));
        var r = 1f;// Constant as pairs eyeX/centerX and eyeZ/centerZ are changed together
        state.angle += angleDelta;
        state.eyeX = state.centerX + (float) (Math.sin(state.angle) * r);
        state.eyeZ = state.centerZ + (float) (Math.cos(state.angle) * r);
//        LogUtil.logDebug(String.format("r: %s, angle: %s, eyeX: %s, eyeZ: %s", r, state.angle, state.eyeX, state.eyeZ));
        look();
    }

    private void look() {
        Matrix4f m = new Matrix4f();
        m.lookAt(eye(), center(), up());
        state.setCameraViewMatrix(m);
    }

    private Vector3f eye() {
        return new Vector3f(state.eyeX, state.eyeY, state.eyeZ);
    }

    private Vector3f center() {
        return new Vector3f(state.centerX, state.centerY, state.centerZ);
    }

    private Vector3f up() {
        return new Vector3f(state.upX, state.upY, state.upZ);
    }

    @Override
    public void event(KeyEvent keyEvent) {
        var step = state.getMoveStep();
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
        stepY((int) scrollEvent.getOffsetY() * (-state.getMoveStep()));
    }

    @Override
    public void event(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.getButton() == MouseButton.RIGHT) {
            switch (mouseButtonEvent.getAction()) {
                case PRESSED:
                    state.isRightMousePressed = true;
                    state.isRightMouseReleased = true;
                    break;
                case RELEASED:
                    state.isRightMouseReleased = true;
                    state.isRightMousePressed = false;
                    break;
            }
        }
//        LogUtil.log(String.format("%s, X: %s, Y: %s", MouseButtonEvent.class.getSimpleName(), mouseButtonEvent.getX(), mouseButtonEvent.getY()));
    }

    @Override
    public void event(CursorPositionEvent event) {
        state.previousCursorPositionX = state.cursorPositionX;
        state.previousCursorPositionY = state.cursorPositionY;
        state.cursorPositionX = (float) event.getX();
        state.cursorPositionY = (float) event.getY();
        if (state.isRightMousePressed) {
            if (state.previousCursorPositionX != state.cursorPositionX) {
                var rotationDirection = -1f;
                if (state.previousCursorPositionX > state.cursorPositionX) {
                    rotationDirection = 1f;
                }
                // TODO magic number
                rotateDelta((float) (rotationDirection * 0.5 * Math.PI / 180));
            }
            if (state.cursorPositionY != state.previousCursorPositionY) {
                var stepYDirection = 1f;
                if (state.previousCursorPositionY > state.cursorPositionY) {
                    stepYDirection = -1f;
                }
                // TODO magic number
                stepY(stepYDirection * 0.05f);
            }
        }
//        LogUtil.log(String.format("X: %s, Y: %s", cursorPositionEvent.getX(), cursorPositionEvent.getY()));
    }
}
