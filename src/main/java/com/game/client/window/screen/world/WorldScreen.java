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
    private final WorldScreenConfig worldScreenConfig;
    private final StaticDynamicSurface surface = StaticDynamicSurface.create();
    private final Camera camera;
    private final GameEngine gameEngine;
    private BatchDrawProgram batchDrawProgram;
    private Matrix4f projectionMatrix;
    private boolean isProjectionMatrixChanged = false;

    public WorldScreen(WorldScreenConfig worldScreenConfig) {
        this.worldScreenConfig = worldScreenConfig;
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
        var playerEventHandler = PlayerEventHandler.create(getCamera(), gameEngine);
        addEventChildListener(playerEventHandler);
    }

    public void render() {
        var start = System.currentTimeMillis();
        var renderObjects = createRenderObjects();
        getProgram().render(renderObjects);
        var end = System.currentTimeMillis();
        var diff = end - start;
        LogUtil.logDebug(false, "world screen render " + diff + " ms");
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
        var camera = Camera.createCamera(surface, worldScreenConfig.getWidth(), worldScreenConfig.getHeight());
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

    private Camera getCamera() {
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
        worldScreenConfig.setWidth(event.getNewWidth());
        worldScreenConfig.setHeight(event.getNewHeight());
        updateMatrices();
    }
}
