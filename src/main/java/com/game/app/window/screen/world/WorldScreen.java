package com.game.app.window.screen.world;

import com.game.app.window.event.cursor.CursorPositionEvent;
import com.game.app.window.event.listener.AbstractWindowEventListener;
import com.game.app.window.event.mouse.MouseButton;
import com.game.app.window.event.mouse.MouseButtonAction;
import com.game.app.window.event.mouse.MouseButtonEvent;
import com.game.app.window.event.resize.ResizeWindowEvent;
import com.game.app.window.lwjgl.annotation.LwjglMainThread;
import com.game.app.window.lwjgl.program.LightingProgram;
import com.game.app.window.lwjgl.program.RenderObjects;
import com.game.app.window.model.GraphicUnit;
import com.game.app.window.model.GraphicUnitFactory;
import com.game.app.window.model.LwjglUnit;
import com.game.app.window.screen.world.camera.Camera;
import com.game.app.window.screen.world.surface.StaticDynamicSurface;
import com.game.engine.unit.GameUnitDao;
import com.game.utils.ParallelUtils;
import com.game.utils.log.LogUtil;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WorldScreen extends AbstractWindowEventListener {
    private final Queue<GraphicUnit> graphicUnits = new ConcurrentLinkedQueue<>();
    private final Queue<GraphicUnit> deletedGraphicUnits = new ConcurrentLinkedQueue<>();
    private final Map<Long, LwjglUnit> drawableModels = new ConcurrentHashMap<>();
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
                drawableModels.put(gameUnit.getId(), getProgram().createLwjglUnit(gameUnit));
            }
        }

        if (!deletedGraphicUnits.isEmpty()) {
            while (!deletedGraphicUnits.isEmpty()) {
                var gameUnit = deletedGraphicUnits.poll();
                drawableModels.remove(gameUnit.getId());
            }
        }

        renderObjects.setLwjglUnits(drawableModels.values());
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
        var camera = Camera.createCamera(surface, worldScreenState.getWidth(), worldScreenState.getHeight());
        addEventChildListener(camera);
        return camera;
    }

    private void updateProjectionMatrix() {
        projectionMatrix = createProjectionMatrix();
        isProjectionMatrixChanged = true;
    }

    private Matrix4f createProjectionMatrix() {
        return getCamera().createProjectionMatrix();
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
            if (graphicUnit.isSurface()) {
                if (graphicUnit.isDynamic()) {
                    surface.addDynamicGraphicUnit(graphicUnit);
                    surface.buildDynamicSurface();
                } else {
                    surface.addStaticGraphicUnit(graphicUnit);
                    surface.buildStaticSurface();
                }
            }
        }
    }

    public void deleteGraphicUnit(GraphicUnit graphicUnit) {
        deletedGraphicUnits.add(graphicUnit);
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
                Optional.ofNullable(getCamera().findIntersection(x, y))
                        .ifPresentOrElse(
                                intersection -> {
                                    addGraphicUnit(GraphicUnitFactory.INSTANCE.createGraphicUnit(GameUnitDao.createUnit(intersection.getPoint())));
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
