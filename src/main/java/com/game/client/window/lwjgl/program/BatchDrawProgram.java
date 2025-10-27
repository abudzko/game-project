package com.game.client.window.lwjgl.program;

import com.game.client.utils.BufferUtils;
import com.game.client.utils.log.LogUtil;
import com.game.client.window.lwjgl.program.shader.Shader;
import com.game.client.window.model.GraphicUnit;
import com.game.client.window.model.LwjglUnitImpl;
import com.game.client.window.model.obj.Model;
import com.game.client.window.model.obj.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static com.game.client.window.lwjgl.program.ProgramDebugger.checkIndividualShaders;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL30.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL30.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL30.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glAttachShader;
import static org.lwjgl.opengl.GL30.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glBufferData;
import static org.lwjgl.opengl.GL30.glClear;
import static org.lwjgl.opengl.GL30.glCreateProgram;
import static org.lwjgl.opengl.GL30.glEnable;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glGenBuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGetAttribLocation;
import static org.lwjgl.opengl.GL30.glGetUniformLocation;
import static org.lwjgl.opengl.GL30.glLinkProgram;
import static org.lwjgl.opengl.GL30.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glUseProgram;
import static org.lwjgl.opengl.GL30.glValidateProgram;
import static org.lwjgl.opengl.GL30.glVertexAttribPointer;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;

public class BatchDrawProgram {
    protected static final String PROJECTION_MATRIX_NAME = "projectionMatrix";
    protected static final String CAMERA_VIEW_MATRIX_NAME = "cameraViewMatrix";
    protected static final String BASE_INSTANCE_NAME = "baseInstance";
    protected static final String USE_SHADING_NAME = "useShading";
    protected static final String POSITION_ATTRIBUTE_NAME = "positionAttribute";
    protected static final String NORMAL_ATTRIBUTE_NAME = "normalAttribute";
    protected static final String TEXTURE_ATTRIBUTE_NAME = "textureAttribute";
    protected static final String CAMERA_POSITION_NAME = "cameraPosition";
    protected static final String SHADER_PATH = "/shaders/batch";

    protected static final String LIGHT_COLOR_NAME = "lightColor";
    protected static final String LIGHT_POSITION_NAME = "lightPosition";
    protected static final String LIGHT_COUNT_NAME = "lightCount";
    private final Shader vertexShader;
    private final Shader fragmentShader;
    private final ConcurrentHashMap<String, Integer> uniformCache = new ConcurrentHashMap<>();
    // For the same models we can reuse vaoId
    private final ConcurrentHashMap<String, Integer> vaoIdCache = new ConcurrentHashMap<>();
    // For the same models we can reuse texture id
    private final ConcurrentHashMap<String, Integer> lwjglTexturesCache = new ConcurrentHashMap<>();
    private int programId;
    private Integer positionAttributeId;
    private Integer textureAttributeId;
    private Integer normalAttributeId;
    private int ssboMatricesId;

    public BatchDrawProgram() {
        this.vertexShader = new Shader(SHADER_PATH + "/vb.vert", GL_VERTEX_SHADER);
        this.fragmentShader = new Shader(SHADER_PATH + "/fb.frag", GL_FRAGMENT_SHADER);
        linkProgram();
        createShaderBuffer();
    }

    private static int loadTexture(Texture texture) {
        // Create a texture
        var id = GL30.glGenTextures();

        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        // Tell opengl how to unpack bytes
        GL30.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 1);

        // Set the texture parameters, can be GL_LINEAR or GL_NEAREST
        GL30.glTexParameterf(GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameterf(GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);

        // Upload texture
        var buffer = BufferUtils.createByteBuffer(texture.getDecodedPng());
        GL30.glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL30.GL_RGBA,
                texture.getTextureWidth(),
                texture.getTextureHeight(),
                0, GL30.GL_RGBA,
                GL30.GL_UNSIGNED_BYTE,
                buffer
        );
        // Free
        BufferUtils.memFree(buffer);
        return id;
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

    public void render(RenderObjects renderObjects) {
        var start = System.currentTimeMillis();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        enable();
        int matrixSize = 16;
        var matrices16fBuffer = BufferUtils.createFloatBuffer(renderObjects.getLwjglUnits().size() * matrixSize);
        var lights = new ArrayList<LwjglUnit>();

        // Call before draw method otherwise old data will be used in draw calls
        if (renderObjects.getCameraViewMatrix() != null) {
            setUniformMatrix4f(CAMERA_VIEW_MATRIX_NAME, renderObjects.getCameraViewMatrix());
        }
        if (renderObjects.getProjectionMatrix() != null) {
            setUniformMatrix4f(PROJECTION_MATRIX_NAME, renderObjects.getProjectionMatrix());
        }
        if (renderObjects.getCameraPosition() != null) {
            setUniformVec3(CAMERA_POSITION_NAME, renderObjects.getCameraPosition());
        }

        int position = 0;
        int baseInstance = 0;
        var vaoIdBaseInstanceMap = new HashMap<Integer, Integer>();
        for (var entry : renderObjects.getVaoIdLwjglUnitMap().entrySet()) {
            var vaoId = entry.getKey();
            var lwjglUnits = entry.getValue();
            vaoIdBaseInstanceMap.put(vaoId, baseInstance);
            baseInstance += lwjglUnits.size();
            for (LwjglUnit lwjglUnit : lwjglUnits) {
                lwjglUnit.getWorldMatrix().get(matrices16fBuffer);
                position += matrixSize;
                matrices16fBuffer.position(position);
                if (!lwjglUnit.useShading() && lwjglUnit.isLight()) {
                    lights.add(lwjglUnit);
                }
            }
        }

        matrices16fBuffer.flip();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssboMatricesId);
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, matrices16fBuffer);

        BufferUtils.memFree(matrices16fBuffer);

        renderObjects.getVaoIdLwjglUnitMap().forEach((vaoId, lwjglUnits) -> {
            var lwjglUnit = lwjglUnits.get(0);
            int indexCount = lwjglUnit.getIndexCount();

            var textureId = lwjglUnit.getTextureId();
            setUniformInt(USE_SHADING_NAME, lwjglUnit.useShading() ? 1 : 0);
            setUniformInt(BASE_INSTANCE_NAME, vaoIdBaseInstanceMap.get(vaoId));

            // Bind to the VAO
            glBindVertexArray(vaoId);
            glBindTexture(GL_TEXTURE_2D, textureId);
            glDrawElementsInstanced(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0, lwjglUnits.size());

            glBindTexture(GL_TEXTURE_2D, 0);
            glBindVertexArray(0);
        });

        if (!lights.isEmpty()) {
            for (int i = 0; i < lights.size(); i++) {
                var light = lights.get(i).getLight();
                var lightPosition = light.getLightPosition();
                setUniformVec3(
                        LIGHT_POSITION_NAME + "[" + i + "]",
                        new Vector3f(lightPosition.x, lightPosition.y, lightPosition.z)
                );
                var lightColor = light.getLightColor();
                setUniformVec3(
                        LIGHT_COLOR_NAME + "[" + i + "]",
                        new Vector3f(lightColor.x, lightColor.y, lightColor.z)
                );
            }
            setUniformInt(LIGHT_COUNT_NAME, lights.size());
        }

        var end = System.currentTimeMillis();
        var diff = end - start;
        LogUtil.logDebug("render " + diff + " ms");
        disable();
    }

    public LwjglUnit createLwjglUnit(GraphicUnit graphicUnit) {
        var model = graphicUnit.getModel();
        int vaoId = vaoIdCache.computeIfAbsent(model.modelKey(), key -> loadModel(model));
        return new LwjglUnitImpl(vaoId, loadTexture(model), graphicUnit);
    }

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

        // Textures
        int textureVboId = glGenBuffers();
        var textureVertices = model.textureVertices();
        glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
        glBufferData(GL_ARRAY_BUFFER, textureVertices, GL_STATIC_DRAW);
        glVertexAttribPointer(getTextureAttribute(), 2, GL_FLOAT, false, 0, 0);

        // Free
        BufferUtils.memFree(textureVertices);

        // Normals
        var vertexNormals = model.normalsBuffer();
        if (vertexNormals != null) {
            int normalsVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, normalsVboId);
            glBufferData(GL_ARRAY_BUFFER, vertexNormals, GL_STATIC_DRAW);
            glVertexAttribPointer(getNormalAttribute(), model.getPointPerVertex3d(), GL_FLOAT, false, 0, 0);

            // Free
            BufferUtils.memFree(vertexNormals);
        }

        // Indexes
        var indexes = model.indexesBuffer();
        int indexesVboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexesVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexes, GL_STATIC_DRAW);

        // Free
        BufferUtils.memFree(indexes);

        glEnableVertexAttribArray(getTextureAttribute());
        glEnableVertexAttribArray(getNormalAttribute());
        glEnableVertexAttribArray(getPositionAttribute());

        // Unbind the VAO
        glBindVertexArray(0);
        return vaoId;
    }

    private void createShaderBuffer() {
        ssboMatricesId = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssboMatricesId);
        glBufferData(GL_SHADER_STORAGE_BUFFER, 1200000 * 16 * Float.BYTES, GL_DYNAMIC_DRAW);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssboMatricesId);
    }

    private int loadTexture(Model model) {
        return lwjglTexturesCache.computeIfAbsent(model.modelKey(), key -> loadTexture(model.modelTexture()));
    }

    private int getPositionAttribute() {
        if (positionAttributeId == null) {
            positionAttributeId = glGetAttribLocation(getProgramId(), POSITION_ATTRIBUTE_NAME);
        }
        return positionAttributeId;
    }

    private int getTextureAttribute() {
        if (textureAttributeId == null) {
            textureAttributeId = glGetAttribLocation(getProgramId(), TEXTURE_ATTRIBUTE_NAME);
        }
        return textureAttributeId;
    }

    private int getNormalAttribute() {
        if (normalAttributeId == null) {
            normalAttributeId = glGetAttribLocation(getProgramId(), NORMAL_ATTRIBUTE_NAME);
        }
        return normalAttributeId;
    }

    private void releaseResources() {
        vertexShader.deleteShader();
        fragmentShader.deleteShader();
    }

    private void setUniformVec3(String name, Vector3f vector3f) {
        GL30.glUniform3f(
                getUniformIdBy(name),
                vector3f.x,
                vector3f.y,
                vector3f.z
        );
    }

    private void setUniformInt(String name, int value) {
        GL30.glUniform1i(
                getUniformIdBy(name),
                value
        );
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
