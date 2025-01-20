package com.game.window.screen.world;

import com.game.event.window.listener.AbstractWindowEventListener;
import com.game.event.window.mouse.MouseButtonEvent;
import com.game.event.window.resize.ResizeWindowEvent;
import com.game.lwjgl.annotation.LwjglMainThread;
import com.game.lwjgl.program.LightingProgram;
import com.game.lwjgl.program.RenderObjects;
import com.game.model.DrawableModel;
import com.game.model.GameUnit;
import com.game.utils.log.LogUtil;
import com.game.window.camera.Camera;
import com.game.window.camera.CameraEventHandler;
import com.game.window.camera.CameraState;
import com.game.window.camera.world.CameraToWorldConverter;
import com.game.window.camera.world.GroundIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WorldScreen extends AbstractWindowEventListener {
    private final Queue<GameUnit> gameUnits = new ConcurrentLinkedQueue<>();
    private final Queue<GameUnit> deletedGameUnits = new ConcurrentLinkedQueue<>();
    private final Map<Long, DrawableModel> drawableModels = new ConcurrentHashMap<>();
    private final WorldScreenState worldScreenState;
    private final LightingProgram program;
    private final Camera camera;
    private Matrix4f projectionMatrix;
    private boolean isProjectionMatrixChanged = false;

    public WorldScreen(WorldScreenState worldScreenState) {
        this.worldScreenState = worldScreenState;
        this.program = new LightingProgram();
        this.camera = createCamera();
        updateMatrices();
    }

    public void render() {
        var renderObjects = createRenderObjects();
        getProgram().render(renderObjects);
    }

    @LwjglMainThread
    private RenderObjects createRenderObjects() {
        var renderObjects = new RenderObjects();
        while (!gameUnits.isEmpty()) {
            var gameUnit = gameUnits.poll();
            var drawableModel = drawableModels.get(gameUnit.getId());
            if (drawableModel == null) {
                drawableModels.put(gameUnit.getId(), getProgram().createDrawableModel(gameUnit));
            }
        }

        if (!deletedGameUnits.isEmpty()) {
            var deletedModels = new ArrayList<DrawableModel>();
            while (!deletedGameUnits.isEmpty()) {
                var gameUnit = deletedGameUnits.poll();
                var drawableModel = drawableModels.remove(gameUnit.getId());
                if (drawableModel != null) {
                    deletedModels.add(drawableModel);
                }
                renderObjects.setDeletedModels(deletedModels);
            }
        }

        renderObjects.setModels(drawableModels.values());
        if (camera.getCameraState().isCameraViewMatrixChanged()) {
            camera.getCameraState().setCameraViewMatrixChanged(false);
            renderObjects.setCameraViewMatrix(camera.getCameraState().getCameraViewMatrixCopy());
        }
        if (isProjectionMatrixChanged) {
            isProjectionMatrixChanged = false;
            renderObjects.setProjectionMatrix(projectionMatrix);
        }

        var cameraState = camera.getCameraState();
        renderObjects.setCameraPosition(new float[]{cameraState.eyeX, cameraState.eyeY, cameraState.eyeZ});

        return renderObjects;
    }

    private Camera createCamera() {
        var camera = new Camera(new CameraState());
        var cameraEventHandler = new CameraEventHandler(camera.getCameraState());
        subscribeOnWindowEvents(cameraEventHandler);
        return camera;
    }

    private void updateProjectionMatrix() {
        projectionMatrix = createProjectionMatrix();
        isProjectionMatrixChanged = true;
    }

    private Matrix4f createProjectionMatrix() {
        var aspectRatio = worldScreenState.getWidth() / worldScreenState.getHeight();
        var projectionMatrix = new Matrix4f();
        var cameraState = getCamera().getCameraState();
        projectionMatrix.perspective(
                cameraState.getFov(),
                aspectRatio,
                cameraState.getzNear(),
                cameraState.getzFar()
        );
        return projectionMatrix;
    }

    public Camera getCamera() {
        if (camera == null) {
            throw new IllegalStateException("Camera is not created");
        }
        return camera;
    }

    public void addGameUnit(GameUnit gameUnit) {
        var drawableModel = drawableModels.get(gameUnit.getId());
        if (drawableModel == null) {
            gameUnits.add(gameUnit);
        }
    }

    public void updateGameUnit(GameUnit gameUnit) {
        var drawableModel = drawableModels.get(gameUnit.getId());
        if (drawableModel != null) {
            drawableModel.updateWorldMatrix();
        }
    }

    public void deleteGameUnit(GameUnit gameUnit) {
        deletedGameUnits.add(gameUnit);
    }

    // TODO ??
    public Vector3f getWorldCoordinates(MouseButtonEvent mouseButtonEvent) {
        var projectionMatrix = createProjectionMatrix();
        var c = new CameraToWorldConverter(mouseButtonEvent, projectionMatrix, getCamera().getCameraState().getCameraViewMatrixCopy());
        var rayVector = c.rayVector(worldScreenState);
        var cameraState = getCamera().getCameraState();
        var cameraPosition = new Vector3f(cameraState.eyeX, cameraState.eyeY, cameraState.eyeZ);
        var wordCoordinates = new GroundIntersection(cameraPosition).findPoint(rayVector);
        LogUtil.log(String.format("wordCoordinates: X = %s, Y = %s, Z = %s", wordCoordinates.x, wordCoordinates.y, wordCoordinates.z));
        return wordCoordinates;
    }

    public LightingProgram getProgram() {
        if (program == null) {
            throw new IllegalStateException("Program is not created");
        }
        return program;
    }

    private void updateMatrices() {
        updateProjectionMatrix();
    }

    @Override
    public void event(ResizeWindowEvent event) {
        super.event(event);
        worldScreenState.setWidth(event.getNewWidth());
        worldScreenState.setHeight(event.getNewHeight());
        updateMatrices();
    }
}
