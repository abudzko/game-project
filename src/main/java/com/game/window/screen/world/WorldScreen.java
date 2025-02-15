package com.game.window.screen.world;

import com.game.lwjgl.annotation.LwjglMainThread;
import com.game.lwjgl.program.LightingProgram;
import com.game.lwjgl.program.RenderObjects;
import com.game.model.DrawableModel;
import com.game.model.GraphicUnit;
import com.game.utils.log.LogUtil;
import com.game.window.camera.Camera;
import com.game.window.camera.world.CameraToWorldConverter;
import com.game.window.camera.world.GroundIntersection;
import com.game.window.camera.world.surface.Ray;
import com.game.window.event.listener.AbstractWindowEventListener;
import com.game.window.event.mouse.MouseButtonEvent;
import com.game.window.event.resize.ResizeWindowEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WorldScreen extends AbstractWindowEventListener {
    private final Queue<GraphicUnit> graphicUnits = new ConcurrentLinkedQueue<>();
    private final Queue<GraphicUnit> deletedGraphicUnits = new ConcurrentLinkedQueue<>();
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
        while (!graphicUnits.isEmpty()) {
            var gameUnit = graphicUnits.poll();
            var drawableModel = drawableModels.get(gameUnit.getId());
            if (drawableModel == null) {
                drawableModels.put(gameUnit.getId(), getProgram().createDrawableModel(gameUnit));
            }
        }

        if (!deletedGraphicUnits.isEmpty()) {
            var deletedModels = new ArrayList<DrawableModel>();
            while (!deletedGraphicUnits.isEmpty()) {
                var gameUnit = deletedGraphicUnits.poll();
                var drawableModel = drawableModels.remove(gameUnit.getId());
                if (drawableModel != null) {
                    deletedModels.add(drawableModel);
                }
                renderObjects.setDeletedModels(deletedModels);
            }
        }

        renderObjects.setModels(drawableModels.values());
        getCamera().getCameraViewMatrixCopyIfChanged().ifPresent(matrix4f -> {
            getCamera().setCameraViewMatrixChanged(false);
            renderObjects.setCameraViewMatrix(matrix4f);
        });
        if (isProjectionMatrixChanged) {
            isProjectionMatrixChanged = false;
            renderObjects.setProjectionMatrix(projectionMatrix);
        }

        renderObjects.setCameraPosition(camera.getCameraPosition());

        return renderObjects;
    }

    private Camera createCamera() {
        var camera = Camera.createCamera();
        addEventChildListener(camera);
        return camera;
    }

    private void updateProjectionMatrix() {
        projectionMatrix = createProjectionMatrix();
        isProjectionMatrixChanged = true;
    }

    private Matrix4f createProjectionMatrix() {
        var aspectRatio = worldScreenState.getWidth() / worldScreenState.getHeight();
        return getCamera().createProjectionMatrix(aspectRatio);
    }

    public Camera getCamera() {
        if (camera == null) {
            throw new IllegalStateException("Camera is not created");
        }
        return camera;
    }

    public void addGameUnit(GraphicUnit graphicUnit) {
        var drawableModel = drawableModels.get(graphicUnit.getId());
        if (drawableModel == null) {
            graphicUnits.add(graphicUnit);
        }
    }

    public void updateGameUnit(GraphicUnit graphicUnit) {
        var drawableModel = drawableModels.get(graphicUnit.getId());
        if (drawableModel != null) {
            drawableModel.updateWorldMatrix();
        }
    }

    public void deleteGameUnit(GraphicUnit graphicUnit) {
        deletedGraphicUnits.add(graphicUnit);
    }

    // TODO ??
    public Vector3f getWorldCoordinates(MouseButtonEvent mouseButtonEvent) {
        var converter = new CameraToWorldConverter(mouseButtonEvent, createProjectionMatrix(), getCamera().getCameraViewMatrixCopy());
        var rayVector = converter.directionPoint(worldScreenState);
        var wordCoordinates = new GroundIntersection(getCamera().getCameraPosition()).findPoint(rayVector);
        LogUtil.log(String.format("wordCoordinates: X = %s, Y = %s, Z = %s", wordCoordinates.x, wordCoordinates.y, wordCoordinates.z));
        return wordCoordinates;
    }

    public Ray getRay(MouseButtonEvent mouseButtonEvent) {
        var converter = new CameraToWorldConverter(mouseButtonEvent, createProjectionMatrix(), getCamera().getCameraViewMatrixCopy());
        LogUtil.log(getCamera().getCameraViewMatrixCopy().toString());
        var directionPoint = converter.directionPoint(worldScreenState);
        LogUtil.log(String.format("directionPoint: X = %s, Y = %s, Z = %s", directionPoint.x, directionPoint.y, directionPoint.z));
        return new Ray(getCamera().getCameraPosition(), directionPoint);
    }


    private LightingProgram getProgram() {
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
