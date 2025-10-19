package com.game.client.window.screen.world.surface;

import org.joml.Vector3f;

import java.util.List;

public class BVHNode {
    private final List<Triangle> triangles;
    private final AABB boundingBox;
    private BVHNode left;
    private BVHNode right;

    public BVHNode(List<Triangle> triangles) {
        this.triangles = triangles;
        this.boundingBox = calculateBoundingBox(triangles);
        if (triangles.size() > 10) { // Порог для разделения
            var splitTriangles = splitTriangles(triangles);
            this.left = new BVHNode(splitTriangles.get(0));
            this.right = new BVHNode(splitTriangles.get(1));
        }
    }

    private AABB calculateBoundingBox(List<Triangle> triangles) {
        // Вычисление ограничивающего объема для списка треугольников
        if (triangles.isEmpty()) {
            throw new IllegalArgumentException("Triangles list can't be empty");
        }

        // Инициализация минимальных и максимальных значений
        var min = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        var max = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

        // Обход всех треугольников
        for (Triangle triangle : triangles) {
            var v1 = triangle.getV1();
            var v2 = triangle.getV2();
            var v3 = triangle.getV3();

            // Обновляем минимальные и максимальные значения
            min.min(v1).min(v2).min(v3);
            max.max(v1).max(v2).max(v3);
        }

        return new AABB(min, max);

    }

    private List<List<Triangle>> splitTriangles(List<Triangle> triangles) {
        // Разделение треугольников на две группы: left/right
        return List.of(
                triangles.subList(0, triangles.size() / 2),
                triangles.subList(triangles.size() / 2, triangles.size())
        );
    }

    public Intersection findIntersection(Ray ray) {
        if (!boundingBox.intersects(ray)) {
            return null;
        }

        if (left == null && right == null) {
            var intersectionPoint = new Vector3f();
            for (var triangle : triangles) {
                if (triangle.intersects(ray, intersectionPoint)) {
                    return Intersection.builder().unitId(triangle.getUnitId()).point(intersectionPoint).build();
                }
            }
            return null;
        }

        var leftIntersection = left == null ? null : left.findIntersection(ray);
           if (leftIntersection == null) {
               return right.findIntersection(ray);
           } else {
               var rightIntersection = right.findIntersection(ray);
               if (rightIntersection == null) {
                   return leftIntersection;
               } else {
                   return ray.getStartPoint().distance(leftIntersection.getPoint()) < ray.getStartPoint().distance(rightIntersection.getPoint())
                           ? leftIntersection
                           : rightIntersection;
               }
           }
    }
}
