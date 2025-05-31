package com.game.app.window.screen.world.surface;

import com.game.app.window.model.GraphicUnit;
import com.game.app.window.screen.world.surface.TriangleTaskParameters.TriangleTaskParametersBuilder;
import com.game.utils.ParallelUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class TrianglesBuilder {

    private static int indexOfFirstVertex(int[] indexesArr, int i, int pointPerVertex3d) {
        return indexesArr[i] * pointPerVertex3d;
    }

    private static Vector3f readVertex(float[] verticesArr, int indexOfFirst) {
        return new Vector3f(
                verticesArr[indexOfFirst],
                verticesArr[++indexOfFirst],
                verticesArr[++indexOfFirst]
        );
    }

    @SneakyThrows
    public List<Triangle> toTriangles(GraphicUnit graphicUnit) {
        var model = graphicUnit.getModel();
        var worldMatrix = graphicUnit.getWorldMatrix();
        var start = System.currentTimeMillis();
        var vertices = model.getVertices();
        var indexes = model.getIndexes();

        int pointPerVertex3d = model.getPointPerVertex3d();
        start = System.currentTimeMillis();
        var parameters = TriangleTaskParameters.builder()
                .unitId(graphicUnit.getId())
                .indexes(indexes)
                .vertices(vertices)
                .start(0)
                .end(indexes.length)
                .worldMatrix(worldMatrix)
                .pointPerVertex3d(pointPerVertex3d)
                .build();
        var task = new TriangleTask(parameters);
        var result = ParallelUtils.run(task);
//        LogUtil.logDebug("toTriangles:" + (System.currentTimeMillis() - start) + "ms");// After thread initialization in pool - 10 ms
        return result;
    }

    private static class TriangleTask extends RecursiveTask<List<Triangle>> {
        private static final int THRESHOLD = 10000;
        private final TriangleTaskParameters parameters;

        TriangleTask(TriangleTaskParameters parameters) {
            this.parameters = parameters;
        }

        @Override
        protected List<Triangle> compute() {
            var vertices = parameters.getVertices();
            var indexes = parameters.getIndexes();
            var pointPerVertex3d = parameters.getPointPerVertex3d();
            if (parameters.getEnd() - parameters.getStart() <= THRESHOLD) {
                var result = new ArrayList<Triangle>();
                for (int i = parameters.getStart(); i < parameters.getEnd(); i++) {
                    var worldMatrix = parameters.getWorldMatrix();
                    var triangle = Triangle.builder()
                            .unitId(parameters.getUnitId())
                            .v1(worldMatrix.transformPosition(readVertex(vertices, indexOfFirstVertex(indexes, i, pointPerVertex3d))))
                            .v2(worldMatrix.transformPosition(readVertex(vertices, indexOfFirstVertex(indexes, ++i, pointPerVertex3d))))
                            .v3(worldMatrix.transformPosition(readVertex(vertices, indexOfFirstVertex(indexes, ++i, pointPerVertex3d))))
                            .build();
                    result.add(triangle);
                }
                return result;
            } else {
                return getTriangles();
            }
        }

        private ArrayList<Triangle> getTriangles() {
            var start = parameters.getStart();
            var end = parameters.getEnd();
            var indexes = parameters.getIndexes();
            var vertices = parameters.getVertices();
            int mid = (start + end) / 2;

            var parameters1 = new TriangleTaskParametersBuilder()
                    .indexes(indexes)
                    .vertices(vertices)
                    .start(start)
                    .end(mid)
                    .worldMatrix(parameters.getWorldMatrix())
                    .pointPerVertex3d(parameters.getPointPerVertex3d())
                    .build();
            var parameters2 = new TriangleTaskParametersBuilder()
                    .indexes(indexes)
                    .vertices(vertices)
                    .start(mid)
                    .end(end)
                    .worldMatrix(parameters.getWorldMatrix())
                    .pointPerVertex3d(parameters.getPointPerVertex3d())
                    .build();
            var leftTask = new TriangleTask(parameters1);
            var rightTask = new TriangleTask(parameters2);

            leftTask.fork();
            rightTask.fork();

            var join1 = leftTask.join();
            var join2 = rightTask.join();
            var result = new ArrayList<Triangle>(join1.size() + join2.size());
            result.addAll(join1);
            result.addAll(join2);
            return result;
        }
    }
}

@Builder
@Getter
class TriangleTaskParameters {
    private final long unitId;
    private final int[] indexes;
    private final float[] vertices;
    private final int start;
    private final int end;
    private final Matrix4f worldMatrix;
    private final int pointPerVertex3d;
}
