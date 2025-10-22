package com.game.client.window.screen.world;

import com.game.client.utils.ParallelUtils;
import com.game.client.utils.log.LogUtil;
import com.game.client.window.event.listener.AbstractWindowEventListener;
import com.game.client.window.event.resize.ResizeWindowEvent;
import com.game.client.window.lwjgl.annotation.LwjglMainThread;
import com.game.client.window.lwjgl.program.LightingProgram;
import com.game.client.window.lwjgl.program.LwjglUnit;
import com.game.client.window.lwjgl.program.RenderObjects;
import com.game.client.window.model.GraphicUnit;
import com.game.client.window.model.GraphicUnitFactory;
import com.game.client.window.screen.world.camera.Camera;
import com.game.client.window.screen.world.engine.GameEngine;
import com.game.client.window.screen.world.engine.unit.GameUnit;
import com.game.client.window.screen.world.surface.StaticDynamicSurface;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class WorldScreen extends AbstractWindowEventListener implements OnGameUnitChangedListener {
    private final Queue<GraphicUnit> graphicUnitsQueue = new ConcurrentLinkedQueue<>();
    private final Queue<GraphicUnit> deletedGraphicUnitsQueue = new ConcurrentLinkedQueue<>();
    private final Map<Long, GraphicUnit> graphicUnitMap = new ConcurrentHashMap<>();
    private final Map<Long, LwjglUnit> renderedLwjglUnits = new ConcurrentHashMap<>();
    private final WorldScreenState worldScreenState;
    private final StaticDynamicSurface surface = StaticDynamicSurface.create();
    private final LightingProgram program;
    private final Camera camera;
    private final GameEngine gameEngine;
    private Matrix4f projectionMatrix;
    private boolean isProjectionMatrixChanged = false;
    private volatile boolean pressed = false;

    public WorldScreen(WorldScreenState worldScreenState) {
        this.worldScreenState = worldScreenState;
        this.program = new LightingProgram();
        this.camera = createCamera();
        this.gameEngine = new GameEngine(this);
        var gameWorld = gameEngine.getGameWorld();
        gameEngine.start();
        Stream.of(
                GraphicUnitFactory.createPlayerGraphicUnit(gameWorld.getPlayer()),
                GraphicUnitFactory.createGroundUnit(gameWorld.getGround()),
                GraphicUnitFactory.createSunUnit(gameWorld.getSun()),
                GraphicUnitFactory.createSkydome(gameWorld.getSkydome())
        ).forEach(graphicUnit -> {
            graphicUnit.updateWorldMatrix();
            addGraphicUnit(graphicUnit);
        });
        updateMatrices();
        var windowEventListener = WorldScreenEventHandler.create(getCamera(), gameEngine);
        addEventChildListener(windowEventListener);
    }

    public void render() {
        var renderObjects = createRenderObjects();
        getProgram().render(renderObjects);
    }

    @LwjglMainThread
    private RenderObjects createRenderObjects() {
        var renderObjects = new RenderObjects();
        while (!graphicUnitsQueue.isEmpty()) {
            var graphicUnit = graphicUnitsQueue.poll();
            var drawableModel = renderedLwjglUnits.get(graphicUnit.getGameUnitId());
            if (drawableModel == null) {
                graphicUnitMap.put(graphicUnit.getGameUnitId(), graphicUnit);
                renderedLwjglUnits.put(graphicUnit.getGameUnitId(), getProgram().createLwjglUnit(graphicUnit));
            }
        }

        if (!deletedGraphicUnitsQueue.isEmpty()) {
            while (!deletedGraphicUnitsQueue.isEmpty()) {
                var graphicUnit = deletedGraphicUnitsQueue.poll();
                renderedLwjglUnits.remove(graphicUnit.getGameUnitId());
                graphicUnitMap.remove(graphicUnit.getGameUnitId());
            }
        }

        renderObjects.setLwjglUnits(renderedLwjglUnits.values());
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
        var lwjglUnit = renderedLwjglUnits.get(graphicUnit.getGameUnitId());
        if (lwjglUnit == null) {
            graphicUnitsQueue.add(graphicUnit);
            if (graphicUnit.isSurface()) {
                if (graphicUnit.isDynamic()) {
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
    public void processChanges(GameUnit gameUnit) {
        Optional.ofNullable(gameUnit.getChangeQueue())
                .filter(q -> !q.isEmpty())
                .ifPresent(changes -> {
                    var graphicUnit = graphicUnitMap.get(gameUnit.getId());
                    while (!changes.isEmpty()) {
                        switch (changes.poll()) {
                            case POSITION:
                                graphicUnit.updateWorldMatrix();
                                break;
                        }
                    }
                });
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
