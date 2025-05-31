package com.game.app.window.screen.world.surface;

import org.joml.Vector3f;

/**
 * Axis-Aligned Bounding Box(AABB)
 */
public class AABB {
    private final Vector3f min;
    private final Vector3f max;

    public AABB(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Check whether ray intersects AAB box
     */
    public boolean intersects(Ray ray) {
        var startPoint = new Vector3f(ray.getStartPoint());
        var directionPoint = new Vector3f(ray.getDirectionPoint());

        // Обратное направление для SIMD-оптимизации
        var invDirection = new Vector3f(
                1.0f / directionPoint.x,
                1.0f / directionPoint.y,
                1.0f / directionPoint.z
        );

        // Вычисляем точки пересечения для каждой оси
        var t1 = new Vector3f(min).sub(startPoint).mul(invDirection);
        var t2 = new Vector3f(max).sub(startPoint).mul(invDirection);

        // Находим минимальные и максимальные значения для каждой оси
        var tmin = new Vector3f(t1).min(t2);
        var tmax = new Vector3f(t1).max(t2);

        // Находим общие tmin и tmax
        var tenter = Math.max(Math.max(tmin.x, tmin.y), tmin.z);
        var texit = Math.min(Math.min(tmax.x, tmax.y), tmax.z);

        // Проверяем пересечение
        return !(texit < 0) && !(tenter > texit);
    }
}
