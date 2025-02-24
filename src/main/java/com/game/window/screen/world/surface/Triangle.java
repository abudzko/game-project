package com.game.window.screen.world.surface;

import lombok.Builder;
import lombok.Getter;
import org.joml.Vector3f;


@Getter
@Builder
public class Triangle {
    private final long unitId;
    private final Vector3f v1;
    private final Vector3f v2;
    private final Vector3f v3;

    public boolean intersects(Ray ray, Vector3f intersectionPoint) {
        var startPoint = ray.getStartPoint();
        var directionPoint = ray.getDirectionPoint();

        var e1 = new Vector3f(getV2()).sub(getV1());
        var e2 = new Vector3f(getV3()).sub(getV1());

        var p = new Vector3f(directionPoint).cross(e2);
        float det = e1.dot(p);

        // Луч параллелен плоскости треугольника
        if (Math.abs(det) < 1e-8) {
            return false;
        }

        var invDet = 1.0f / det;
        var t = new Vector3f(startPoint).sub(getV1());

        var u = t.dot(p) * invDet;
        if (u < 0 || u > 1) {
            return false;
        }

        var q = new Vector3f(t).cross(e1);
        var v = directionPoint.dot(q) * invDet;
        if (v < 0 || u + v > 1) {
            return false;
        }

        var tParam = e2.dot(q) * invDet;
        if (tParam < 0) {
            return false;
        }

        // Вычисляем точку пересечения
        intersectionPoint.set(startPoint).add(new Vector3f(directionPoint).mul(tParam));
        return true;
    }
}
