package com.game.window.screen.world;

import com.game.dao.GraphicUnitDao;
import com.game.engine.unit.GameUnitDao;
import com.game.lwjgl.annotation.LwjglMainThread;
import com.game.lwjgl.program.LightingProgram;
import com.game.lwjgl.program.RenderObjects;
import com.game.model.DrawableModel;
import com.game.model.GraphicUnit;
import com.game.utils.ParallelUtils;
import com.game.utils.log.LogUtil;
import com.game.window.event.cursor.CursorPositionEvent;
import com.game.window.event.key.KeyEvent;
import com.game.window.event.listener.AbstractWindowEventListener;
import com.game.window.event.mouse.MouseButton;
import com.game.window.event.mouse.MouseButtonAction;
import com.game.window.event.mouse.MouseButtonEvent;
import com.game.window.event.resize.ResizeWindowEvent;
import com.game.window.screen.world.camera.Camera;
import com.game.window.screen.world.surface.Ray;
import com.game.window.screen.world.surface.StaticDynamicSurface;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WorldScreen extends AbstractWindowEventListener {
    private final Queue<GraphicUnit> graphicUnits = new ConcurrentLinkedQueue<>();
    private final Queue<GraphicUnit> deletedGraphicUnits = new ConcurrentLinkedQueue<>();
    private final Map<Long, DrawableModel> drawableModels = new ConcurrentHashMap<>();
    private final WorldScreenState worldScreenState;
    private final StaticDynamicSurface surface = StaticDynamicSurface.create();
    private final LightingProgram program;
    private final Camera camera;
    private Matrix4f projectionMatrix;
    private boolean isProjectionMatrixChanged = false;

    public WorldScreen(WorldScreenState worldScreenState) {
        this.worldScreenState = worldScreenState;
        this.program = new LightingProgram();
        this.camera = createCamera();
//        this.worldScreenEventHandler = WorldScreenEventHandler.create();
//        addEventChildListener(worldScreenEventHandler);
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

    public void addGraphicUnit(GraphicUnit graphicUnit) {
        var drawableModel = drawableModels.get(graphicUnit.getId());
        if (drawableModel == null) {
            graphicUnits.add(graphicUnit);
            if (graphicUnit.isDynamic()) {
                surface.addDynamicGraphicUnit(graphicUnit);
                surface.buildDynamicSurface();
            } else {
                surface.addStaticGraphicUnit(graphicUnit);
                surface.buildStaticSurface();
            }
        }
    }

    public void deleteGraphicUnit(GraphicUnit graphicUnit) {
        deletedGraphicUnits.add(graphicUnit);
    }

    private Ray getRay(double x, double y) {
        var converter = CameraToWorldConverter.builder()
                .mouseX(x)
                .mouseY(y)
                .projectionMatrix(createProjectionMatrix())
                .viewMatrix(getCamera().getCameraViewMatrixCopy())
                .build();
//        LogUtil.logDebug(getCamera().getCameraViewMatrixCopy().toString());
        var directionPoint = converter.directionPoint(worldScreenState);
//        LogUtil.logDebug(String.format("directionPoint: X = %s, Y = %s, Z = %s", directionPoint.x, directionPoint.y, directionPoint.z));
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

    @Override
    public void event(CursorPositionEvent event) {
        super.event(event);
//        runTask(event.getX(), event.getY());
    }

    @Override
    public void event(MouseButtonEvent mouseButtonEvent) {
        super.event(mouseButtonEvent);
        if (MouseButtonAction.PRESSED.equals(mouseButtonEvent.getAction())
                && MouseButton.LEFT.equals(mouseButtonEvent.getButton())) {
            runTask(mouseButtonEvent.getX(), mouseButtonEvent.getY());
        }
    }

    private void runTask(double x, double y) {
        Runnable runnable = () -> {
            try {
                var ray = getRay(x, y);
                Optional.ofNullable(surface.findIntersection(ray))
                        .ifPresentOrElse(
                                intersection -> {
                                    addGraphicUnit(GraphicUnitDao.INSTANCE.createGraphicUnit(GameUnitDao.createUnit(intersection.getPoint())));
                                    LogUtil.logDebug("Intersection: id = " + intersection.getUnitId() + " point = " + LogUtil.toStr(intersection.getPoint()));
                                },
                                () -> LogUtil.logDebug("No intersection")
                        );
            } catch (Exception e) {
                LogUtil.logError(e.getMessage(), e);
            }
        };
        ParallelUtils.run(runnable);
    }
}
