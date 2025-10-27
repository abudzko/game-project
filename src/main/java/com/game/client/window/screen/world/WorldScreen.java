package com.game.client.window.screen.world;

import com.game.client.utils.ParallelUtils;
import com.game.client.utils.log.LogUtil;
import com.game.client.window.event.listener.AbstractWindowEventListener;
import com.game.client.window.event.resize.ResizeWindowEvent;
import com.game.client.window.lwjgl.annotation.LwjglMainThread;
import com.game.client.window.lwjgl.program.BatchDrawProgram;
import com.game.client.window.lwjgl.program.LightingProgram;
import com.game.client.window.lwjgl.program.LwjglUnit;
import com.game.client.window.lwjgl.program.RenderObjects;
import com.game.client.window.model.GraphicUnit;
import com.game.client.window.model.GraphicUnitFactory;
import com.game.client.window.screen.world.camera.Camera;
import com.game.client.window.screen.world.engine.GameEngine;
import com.game.client.window.screen.world.surface.StaticDynamicSurface;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class WorldScreen extends AbstractWindowEventListener {
    private final Queue<GraphicUnit> graphicUnitsQueue = new ConcurrentLinkedQueue<>();
    private final Queue<GraphicUnit> deletedGraphicUnitsQueue = new ConcurrentLinkedQueue<>();
    private final Map<Long, GraphicUnit> graphicUnitMap = new ConcurrentHashMap<>();
    private final Map<Long, LwjglUnit> renderedLwjglUnits = new ConcurrentHashMap<>();
    private final WorldScreenState worldScreenState;
    private final StaticDynamicSurface surface = StaticDynamicSurface.create();
    private final LightingProgram program;
    private final Camera camera;
    private final GameEngine gameEngine;
    private final boolean pressed = false;
    private BatchDrawProgram batchDrawProgram;
    private Matrix4f projectionMatrix;
    private boolean isProjectionMatrixChanged = false;

    public WorldScreen(WorldScreenState worldScreenState) {
        this.worldScreenState = worldScreenState;
        this.program = new LightingProgram();
        this.batchDrawProgram = new BatchDrawProgram();
        this.camera = createCamera();
        this.gameEngine = new GameEngine();
        var gameWorld = gameEngine.getGameWorld();
        gameEngine.start();
        gameWorld.getGameUnitMap().forEach((key, gameUnit) -> {
            gameUnit.getSharedUnitState().updateWorldMatrix();
            addGraphicUnit(GraphicUnitFactory.createGraphicUnit(gameUnit));
        });
        var player = gameWorld.getPlayer();
        Optional.ofNullable(camera.findIntersection(player.getSharedUnitState().getPosition()))
                .ifPresent(intersection -> {
                    player.getSharedUnitState().setPosition(intersection.getPoint());
                    player.getSharedUnitState().updateWorldMatrix();
                });
        updateMatrices();
        var windowEventListener = WorldScreenEventHandler.create(getCamera(), gameEngine);
        addEventChildListener(windowEventListener);
    }

    public void render() {
        var start = System.currentTimeMillis();
        var renderObjects = createRenderObjects();
        getProgram().render(renderObjects);
        var end = System.currentTimeMillis();
        var diff = end - start;
//        LogUtil.logDebug("world screen render " + diff + " ms");
    }

    @LwjglMainThread
    private RenderObjects createRenderObjects() {
        var renderObjects = new RenderObjects();
        while (!graphicUnitsQueue.isEmpty()) {
            var graphicUnit = graphicUnitsQueue.poll();
            long gameUnitId = graphicUnit.getSharedUnitState().getGameUnitId();
            var lwjglUnit = renderedLwjglUnits.get(gameUnitId);
            if (lwjglUnit == null) {
                graphicUnitMap.put(gameUnitId, graphicUnit);
                renderedLwjglUnits.put(gameUnitId, getProgram().createLwjglUnit(graphicUnit));
            }
        }

        if (!deletedGraphicUnitsQueue.isEmpty()) {
            while (!deletedGraphicUnitsQueue.isEmpty()) {
                var graphicUnit = deletedGraphicUnitsQueue.poll();
                long gameUnitId = graphicUnit.getSharedUnitState().getGameUnitId();
                renderedLwjglUnits.remove(gameUnitId);
                graphicUnitMap.remove(gameUnitId);
            }
        }

        renderObjects.setLwjglUnits(renderedLwjglUnits.values());
        var vaoIdLwjglUnitMap = renderedLwjglUnits.values()
                .stream()
                .collect(Collectors.groupingBy(LwjglUnit::getVaoId));
        renderObjects.setVaoIdLwjglUnitMap(vaoIdLwjglUnitMap);
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

    private void addGraphicUnit(GraphicUnit graphicUnit) {
        var lwjglUnit = renderedLwjglUnits.get(graphicUnit.getSharedUnitState().getGameUnitId());
        if (lwjglUnit == null) {
            graphicUnitsQueue.add(graphicUnit);
            if (graphicUnit.isSurface()) {
                if (graphicUnit.getSharedUnitState().isDynamic()) {
                    surface.addDynamicGraphicUnit(graphicUnit);
//                    surface.buildDynamicSurface();
                } else {
                    surface.addStaticGraphicUnit(graphicUnit);
                    surface.buildStaticSurface();
                }
            }
        }
    }

    private void deleteGraphicUnit(GraphicUnit graphicUnit) {
        deletedGraphicUnitsQueue.add(graphicUnit);
    }

//    private LightingProgram getProgram() {
//        if (program == null) {
//            throw new IllegalStateException("Program is not created");
//        }
//        return program;
//    }

    private BatchDrawProgram getProgram() {
        if (batchDrawProgram == null) {
            throw new IllegalStateException("Program is not created");
        }
        return batchDrawProgram;
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
//    @Override
//    public void event(CursorPositionEvent event) {
//        super.event(event);
//        if (pressed) {
//            addGraphicUnit(event.getX(), event.getY());
//        }
//    }
//
//    @Override
//    public void event(MouseButtonEvent mouseButtonEvent) {
//        super.event(mouseButtonEvent);
//        if (MouseButton.LEFT.equals(mouseButtonEvent.getButton())) {
//            if (MouseButtonAction.PRESSED.equals(mouseButtonEvent.getAction())) {
//                addGraphicUnit(mouseButtonEvent.getX(), mouseButtonEvent.getY());
//                pressed = true;
//            } else if (MouseButtonAction.RELEASED.equals(mouseButtonEvent.getAction())) {
//                pressed = false;
//                rebuildSurface();
//            }
//        }
//        if (MouseButton.WHEEL.equals(mouseButtonEvent.getButton())) {
//            if (MouseButtonAction.PRESSED.equals(mouseButtonEvent.getAction())) {
//                changeGraphicUnit(mouseButtonEvent.getX(), mouseButtonEvent.getY());
//            }
//        }

//    }
//    private void addGraphicUnit(double x, double y) {
//        Runnable runnable = () -> {
//            try {
//                Optional.ofNullable(getCamera().findIntersection(x, y))
//                        .ifPresentOrElse(
//                                intersection -> {
//                                    addGraphicUnit(GraphicUnitFactory.INSTANCE.createGraphicUnit(GameUnitDao.createUnit(intersection.getPoint())));
//                                    LogUtil.logDebug("Intersection: id = " + intersection.getUnitId() + " point = " + LogUtil.toStr(intersection.getPoint()));
//                                },
//                                () -> LogUtil.logDebug("No intersection")
//                        );
//            } catch (Exception e) {
//                LogUtil.logError(e.getMessage(), e);
//            }
//        };
//        ParallelUtils.run(runnable);

//    }
//    private void changeGraphicUnit(double x, double y) {
//        Runnable runnable = () -> {
//            try {
//                Optional.ofNullable(getCamera().findIntersection(x, y))
//                        .ifPresentOrElse(
//                                intersection -> {
//                                    var graphicUnit = graphicUnitMap.get(intersection.getUnitId());
//                                    if (graphicUnit != null) {
//                                        addGraphicUnit(GraphicUnitFactory.INSTANCE.createGraphicUnit2(GameUnitDao.createUnit(graphicUnit.getPosition())));
//                                        deleteGraphicUnit(graphicUnit);
//                                    }
//                                },
//                                () -> LogUtil.logDebug("No intersection")
//                        );
//            } catch (Exception e) {
//                LogUtil.logError(e.getMessage(), e);
//            }
//        };
//        ParallelUtils.run(runnable);

//    }

    private void rebuildSurface() {
        Runnable runnable = () -> {
            try {
                surface.buildDynamicSurface();
            } catch (Exception e) {
                LogUtil.logError(e.getMessage(), e);
            }
        };
        ParallelUtils.run(runnable);
    }
}
