package com.game.client.window.screen.world;

import com.game.client.utils.log.LogUtil;
import com.game.client.window.engine.GameEngine;
import com.game.client.window.engine.unit.GameUnit;
import com.game.client.window.event.resize.ResizeWindowEvent;
import com.game.client.window.lwjgl.annotation.LwjglMainThread;
import com.game.client.window.lwjgl.program.BatchDrawProgram;
import com.game.client.window.lwjgl.program.LwjglUnit;
import com.game.client.window.lwjgl.program.RenderObjects;
import com.game.client.window.lwjgl.program.ShadowProgram;
import com.game.client.window.screen.world.camera.Camera;
import com.game.client.window.screen.world.surface.StaticDynamicSurface;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class WorldScreen extends Screen {
    private final Queue<GameUnit> gameUnitsQueue = new ConcurrentLinkedQueue<>();
    private final Queue<GameUnit> deletedGameUnitsQueue = new ConcurrentLinkedQueue<>();
    private final Map<Long, GameUnit> gameUnitMap = new ConcurrentHashMap<>();
    private final Map<Long, LwjglUnit> shadowRenderedLwjglUnits = new ConcurrentHashMap<>();
    private final Map<Long, LwjglUnit> renderedLwjglUnits = new ConcurrentHashMap<>();
    private final WorldScreenConfig worldScreenConfig;
    private final StaticDynamicSurface surface = StaticDynamicSurface.INSTANCE;
    private final Camera camera;
    private final GameEngine gameEngine;
    private BatchDrawProgram batchDrawProgram;
    private ShadowProgram shadowProgram;
    private Matrix4f projectionMatrix;
    private boolean isProjectionMatrixChanged = false;

    public WorldScreen() {
        this.worldScreenConfig = new WorldScreenConfig();
        this.batchDrawProgram = new BatchDrawProgram(worldScreenConfig);
        this.shadowProgram = new ShadowProgram();
        this.camera = createCamera();
        this.gameEngine = new GameEngine();
        var gameWorld = gameEngine.getGameWorld();
        gameEngine.start();
        gameWorld.getGameUnitMap().forEach((key, gameUnit) -> {
            var lwjglUnit = renderedLwjglUnits.get(gameUnit.getSharedUnitState().getGameUnitId());
            if (lwjglUnit == null) {
                gameUnitsQueue.add(gameUnit);
            }
        });
        var player = gameWorld.getPlayer();
        Optional.ofNullable(camera.findIntersection(player.getSharedUnitState().getPosition()))
                .ifPresent(intersection -> {
                    player.getSharedUnitState().updateWorldMatrix(intersection.getPoint());
                });
        updateMatrices();
        var playerEventHandler = PlayerEventHandler.create(getCamera(), gameEngine);
        addEventListener(playerEventHandler);
    }

    public static Screen create() {
        return new WorldScreen();
    }

    public void render() {
        var start = System.currentTimeMillis();
        var renderObjects = createRenderObjects();
        shadowProgram.render(renderObjects);
        getProgram().render(renderObjects);
        var end = System.currentTimeMillis();
        var diff = end - start;
        LogUtil.logDebug(false, "world screen render " + diff + " ms");
    }

    @LwjglMainThread
    private RenderObjects createRenderObjects() {
        var renderObjects = new RenderObjects();
        while (!gameUnitsQueue.isEmpty()) {
            var gameUnit = gameUnitsQueue.poll();
            long gameUnitId = gameUnit.getSharedUnitState().getGameUnitId();
            var lwjglUnit = renderedLwjglUnits.get(gameUnitId);
            if (lwjglUnit == null) {
                gameUnitMap.put(gameUnitId, gameUnit);
                renderedLwjglUnits.put(gameUnitId, getProgram().createLwjglUnit(gameUnit));
                shadowRenderedLwjglUnits.put(gameUnitId, shadowProgram.createLwjglUnit(gameUnit));
            }
        }

        if (!deletedGameUnitsQueue.isEmpty()) {
            while (!deletedGameUnitsQueue.isEmpty()) {
                var gameUnit = deletedGameUnitsQueue.poll();
                long gameUnitId = gameUnit.getSharedUnitState().getGameUnitId();
                renderedLwjglUnits.remove(gameUnitId);
                shadowRenderedLwjglUnits.remove(gameUnitId);
                gameUnitMap.remove(gameUnitId);
            }
        }

        renderObjects.setLwjglUnits(renderedLwjglUnits.values());
        var vaoIdLwjglUnitMap = renderedLwjglUnits.values()
                .stream()
                .collect(Collectors.groupingBy(LwjglUnit::getVaoId));
        renderObjects.setVaoIdLwjglUnitMap(vaoIdLwjglUnitMap);

        var shadowVaoIdLwjglUnitMap = shadowRenderedLwjglUnits.values()
                .stream()
                .collect(Collectors.groupingBy(LwjglUnit::getVaoId));
        renderObjects.setShadowVaoIdLwjglUnitMap(shadowVaoIdLwjglUnitMap);

        renderedLwjglUnits.values().forEach(LwjglUnit::prepareWorldMatrix);
        var playerState = gameEngine.getGameWorld().getPlayer().getSharedUnitState();
        var position = playerState.getLwjglWorldMatrix().getPosition();

        getCamera().follow(position);
        renderObjects.setCameraPosition(position);

        getCamera().getCameraViewMatrixCopyIfChanged().ifPresent(matrix4f -> {
            getCamera().setCameraViewMatrixChanged(false);
            renderObjects.setCameraViewMatrix(matrix4f);
        });
        if (isProjectionMatrixChanged) {
            isProjectionMatrixChanged = false;
            renderObjects.setProjectionMatrix(projectionMatrix);
        }

        renderObjects.setDepthMapTexture(shadowProgram.getDepthMapTextureId());

        return renderObjects;
    }

    private Camera createCamera() {
        var camera = Camera.createCamera(surface, worldScreenConfig.getWidth(), worldScreenConfig.getHeight());
        addEventListener(camera);
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
