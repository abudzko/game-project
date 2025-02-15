package com.game.window.camera.world;

import org.joml.Vector3f;

//TODO tech dept
public class GroundIntersection {

    private final Vector3f cameraPosition;
    private static final int RECURSION_COUNT = 200;

    public GroundIntersection(Vector3f cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    private Vector3f getPointOnRay(Vector3f ray, float distance) {
        var start = new Vector3f(cameraPosition.x, cameraPosition.y, cameraPosition.z);
        var scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return start.add(scaledRay);
    }

    public Vector3f findPoint(Vector3f ray){
        return binarySearch(0, 0, 600, ray);
    }

    private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
//            Vector3f endPoint = getPointOnRay(ray, half);
//            Terrain terrain = getTerrain(endPoint.getX(), endPoint.getZ());
//            if (terrain != null) {
//                return endPoint;
//            } else {
//                return null;
//            }
            return getPointOnRay(ray, half);
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }


    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
        var startPoint = getPointOnRay(ray, start);
        var endPoint = getPointOnRay(ray, finish);
        if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
            return true;
        } else {
            return false;
        }
    }


    private boolean isUnderGround(Vector3f testPoint) {
//        Terrain terrain = getTerrain(testPoint.getX(), testPoint.getZ());
//        float height = 0;
//        if (terrain != null) {
//            height = terrain.getHeightOfTerrain(testPoint.getX(), testPoint.getZ());
//        }
        return testPoint.y < 0;
    }

}
