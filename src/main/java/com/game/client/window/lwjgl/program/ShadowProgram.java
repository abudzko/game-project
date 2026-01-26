package com.game.client.window.lwjgl.program;

import com.game.client.utils.BufferUtils;
import com.game.client.utils.debug.ShadowDebug;
import com.game.client.utils.log.LogUtil;
import com.game.client.window.lwjgl.program.shader.Shader;
import com.game.client.window.model.GraphicUnit;
import com.game.client.window.model.LwjglUnitImpl;
import com.game.client.window.model.obj.Model;
import com.game.client.window.model.obj.ObjectModels;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.game.client.window.lwjgl.program.ProgramDebugger.checkIndividualShaders;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_BORDER_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterfv;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL30.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glAttachShader;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glClear;
import static org.lwjgl.opengl.GL30.glCreateProgram;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGetUniformLocation;
import static org.lwjgl.opengl.GL30.glLinkProgram;
import static org.lwjgl.opengl.GL30.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glUseProgram;
import static org.lwjgl.opengl.GL30.glValidateProgram;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;

/**
 * Draws shadow map in frame buffer as texture image
 * Should be invoked before main render method as shadow map should be ready before it
 */
public class ShadowProgram {
    protected static final String BASE_INSTANCE_NAME = "baseInstance";
    protected static final String SHADER_PATH = "/shaders/shadow";

    protected static final String POSITION_ATTRIBUTE_NAME = "positionAttribute";
    protected static final String LIGHT_SPACE_MATRIX_NAME = "lightSpaceMatrix";
    private final Shader vertexShader;
    private final Shader fragmentShader;
    private final ConcurrentHashMap<String, Integer> uniformCache = new ConcurrentHashMap<>();
    // For the same models we can reuse vaoId
    private final ConcurrentHashMap<String, Integer> vaoIdCache = new ConcurrentHashMap<>();
    private int programId;
    // Frame Buffer Object for shadows
    private int shadowFboId;
    // Texture with depth map
    @Getter
    private int depthMapTextureId;

    private int ssboMatricesId;
    private Integer positionAttributeId;
    private boolean saved;

    public ShadowProgram() {
        this.vertexShader = new Shader(SHADER_PATH + "/shadow.vert", GL_VERTEX_SHADER);
        this.fragmentShader = new Shader(SHADER_PATH + "/shadow.frag", GL_FRAGMENT_SHADER);
        linkProgram();
        shadowMap();
        createShaderBuffer();
    }

    private static Matrix4f calculateLightMatrix(RenderObjects renderObjects) {
        var zoneConfig = ObjectModels.getZoneConfig();
        var light = renderObjects.getVaoIdLwjglUnitMap()
                .values()
                .stream()
                .map(unitList -> unitList.get(0))
                .filter(LwjglUnit::isLight).findFirst().orElseThrow();
        int sceneRadius = (int) (Math.max(zoneConfig.mapRowCount, zoneConfig.mapColumnCount) * zoneConfig.zoneSize * zoneConfig.xzScale) / 2;
        var sceneCenter = new Vector3f(sceneRadius, 0, -sceneRadius);
        var lightPosition = new Vector3f(light.getLight().getLightPosition());

        var lightDirection = new Vector3f(sceneCenter)
                .sub(lightPosition)
                .normalize();
        var lightPos = new Vector3f(sceneCenter)
                .sub(lightDirection.x * sceneRadius,
                        lightDirection.y * sceneRadius,
                        lightDirection.z * sceneRadius);

        // Like camera matrix but from position of Sun
        var lightView = new Matrix4f().lookAt(
                lightPos,
                sceneCenter,
                new Vector3f(0.0f, 1.0f, 0.0f) // UP = +Y
        );

        // Orthographic matrix to view whole scene - converts 3D coordinates to 2D space -> image
        var orthoSize = sceneRadius * 4f;
        var lightProjection = new Matrix4f().ortho(
                -orthoSize, orthoSize,
                -orthoSize, orthoSize,
                0.1f, sceneRadius * 6.0f
        );

        return lightProjection.mul(lightView, new Matrix4f());
    }

    private void linkProgram() {
        programId = glCreateProgram();

        glAttachShader(programId, vertexShader.getId());
        glAttachShader(programId, fragmentShader.getId());

        glLinkProgram(programId);
        glValidateProgram(programId);

        releaseResources();

        checkIndividualShaders(programId);
    }

    /**
     * Creates Frame buffer Object, buffer where shadow map will be stored
     */
    private void shadowMap() {
        // Generate id of frame buffer object
        shadowFboId = glGenFramebuffers();
        // Create frame buffer object
        glBindFramebuffer(GL_FRAMEBUFFER, shadowFboId);

        depthMapTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthMapTextureId);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
                GL_TEXTURE_2D, depthMapTextureId, 0);


        // Create Texture to store shadow map
        int size = TextureUnits.DEPTH_MAP_SIZE;
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT,
                size, size, 0,
                GL_DEPTH_COMPONENT, GL_FLOAT,
                (ByteBuffer) null);

        // Filters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

        float[] borderColor = {1.0f, 1.0f, 1.0f, 1.0f};
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);


        // Disable draw buffer due to only depth is required
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("FBO error: " + status);
        }
    }

    public void render(RenderObjects renderObjects) {
        var start = System.currentTimeMillis();

        glBindFramebuffer(GL_FRAMEBUFFER, shadowFboId);

        var mapSize = TextureUnits.DEPTH_MAP_SIZE;
        glViewport(0, 0, mapSize, mapSize);
        glClearDepth(1.0f);
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        glDepthFunc(GL_LESS);
        // Disable draw buffer due to only depth is required
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        enable();

        var lightSpaceMatrix = calculateLightMatrix(renderObjects);
        renderObjects.setLightSpaceMatrix(lightSpaceMatrix);
        setUniformMatrix4f(LIGHT_SPACE_MATRIX_NAME, lightSpaceMatrix);

        var unitCount = new AtomicInteger();
        var shadowVaoIdLwjglUnitMap = renderObjects.getShadowVaoIdLwjglUnitMap();
        var vaoIds = shadowVaoIdLwjglUnitMap.values().stream()
                .filter(unitList -> unitList.get(0).useShading())
                .map(unitList -> {
                    unitCount.addAndGet(unitList.size());
                    return unitList.get(0);
                })
                .map(LwjglUnit::getVaoId)
                .collect(Collectors.toList());

        int matrixSize = 16;
        var matrices16fBuffer = BufferUtils.createFloatBuffer(unitCount.get() * matrixSize);

        int position = 0;
        int baseInstance = 0;
        var vaoIdBaseInstanceMap = new HashMap<Integer, Integer>();

        for (var vaoId : vaoIds) {
            var lwjglUnits = shadowVaoIdLwjglUnitMap.get(vaoId);

            vaoIdBaseInstanceMap.put(vaoId, baseInstance);
            baseInstance += lwjglUnits.size();
            for (LwjglUnit lwjglUnit : lwjglUnits) {
                new Matrix4f(lwjglUnit.getWorldMatrix()).get(matrices16fBuffer);
                position += matrixSize;
                matrices16fBuffer.position(position);
            }
        }

        matrices16fBuffer.flip();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssboMatricesId);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, matrices16fBuffer);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssboMatricesId);

        BufferUtils.memFree(matrices16fBuffer);

        for (var vaoId : vaoIds) {
            var lwjglUnits = shadowVaoIdLwjglUnitMap.get(vaoId);
            var lwjglUnit = lwjglUnits.get(0);
            int indexCount = lwjglUnit.getIndexCount();

            setUniformInt(BASE_INSTANCE_NAME, vaoIdBaseInstanceMap.get(vaoId));

            // Bind to the VAO
            glBindVertexArray(vaoId);
            glDrawElementsInstanced(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0, lwjglUnits.size());

            glBindVertexArray(0);
        }

        if (!saved) {
            ShadowDebug.saveDepthMap(depthMapTextureId, TextureUnits.DEPTH_MAP_SIZE, "depthmap.png");
            saved = true;
        }
        var end = System.currentTimeMillis();
        var diff = end - start;
        LogUtil.logDebug(false, "render " + diff + " ms");
        disable();
    }

    private void createShaderBuffer() {
        ssboMatricesId = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssboMatricesId);
        glBufferData(GL_SHADER_STORAGE_BUFFER, 1200000 * 16 * Float.BYTES, GL_DYNAMIC_DRAW);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssboMatricesId);
    }

    public LwjglUnit createLwjglUnit(GraphicUnit graphicUnit) {
        var model = graphicUnit.getModel();
        int vaoId = vaoIdCache.computeIfAbsent(model.modelKey(), key -> loadModel(model));
        return new LwjglUnitImpl(vaoId, 0, graphicUnit);
    }

    /**
     * Only vertexes and indexes are used for shadows
     */
    private int loadModel(Model model) {
        // Load in GPU Memory our model
        // Create VAO per model
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Vertices
        var vertices = model.verticesBuffer();
        int verticesVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, verticesVboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(getPositionAttribute(), model.getPointPerVertex3d(), GL_FLOAT, false, 0, 0);

        // Free
        BufferUtils.memFree(vertices);

        // Indexes
        var indexes = model.indexesBuffer();
        int indexesVboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexesVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexes, GL_STATIC_DRAW);

        // Free
        BufferUtils.memFree(indexes);

        glEnableVertexAttribArray(getPositionAttribute());

        // Unbind the VAO
        glBindVertexArray(0);
        return vaoId;
    }

    private int getPositionAttribute() {
        if (positionAttributeId == null) {
            positionAttributeId = glGetAttribLocation(getProgramId(), POSITION_ATTRIBUTE_NAME);
        }
        return positionAttributeId;
    }


    private void releaseResources() {
        vertexShader.deleteShader();
        fragmentShader.deleteShader();
    }

    private void setUniformMatrix4f(String name, Matrix4f matrix4f) {
        var floatBuffer = BufferUtils.toFloatBuffer(matrix4f);
        glUniformMatrix4fv(
                getUniformIdBy(name),
                false,
                floatBuffer
        );
        BufferUtils.memFree(floatBuffer);
    }

    private void setUniformInt(String name, int value) {
        GL30.glUniform1i(
                getUniformIdBy(name),
                value
        );
    }

    private int getUniformIdBy(String uniformName) {
        return uniformCache.computeIfAbsent(uniformName, name -> {
            var uniformId = glGetUniformLocation(programId, uniformName);
            if (uniformId == -1) {
                throw new IllegalArgumentException(String.format(
                        "Could not find uniform location by name: %s",
                        uniformName
                ));
            }
            return uniformId;
        });
    }

    private int getProgramId() {
        return programId;
    }

    private void enable() {
        glUseProgram(getProgramId());
    }

    private void disable() {
        glUseProgram(0);
    }
}
