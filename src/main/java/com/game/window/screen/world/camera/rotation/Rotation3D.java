package com.game.window.screen.world.camera.rotation;

import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class Rotation3D {

    public static Vector3f rotateAroundPoint(
            Vector3f point,
            Vector3f center,
            double angleX,
            double angleY,
            double angleZ
    ) {
        // Translate point to origin
        var translatedX = point.x() - center.x();
        var translatedY = point.y() - center.y();
        var translatedZ = point.z() - center.z();

        // Rotation matrix for X-axis
        var cosX = Math.cos(angleX);
        var sinX = Math.sin(angleX);
        var rotateX = new Matrix3d(
                1, 0, 0,
                0, cosX, sinX,
                0, -sinX, cosX
        );

        // Rotation matrix for Y-axis
        var cosY = Math.cos(angleY);
        var sinY = Math.sin(angleY);
        var rotateY = new Matrix3d(
                cosY, 0, -sinY,
                0, 1, 0,
                sinY, 0, cosY
        );
        // Rotation matrix for Z-axis
        var cosZ = Math.cos(angleZ);
        var sinZ = Math.sin(angleZ);
        var rotateZ = new Matrix3d(
                cosZ, sinZ, 0,
                -sinZ, cosZ, 0,
                0, 0, 1
        );

        // Apply rotation matrices (order matters: Z -> Y -> X)
        var rotated = new Vector3d(translatedX, translatedY, translatedZ).mul(rotateZ).mul(rotateY).mul(rotateX);

        // Translate back to original position
        var finalX = rotated.x() + center.x();
        var finalY = rotated.y() + center.y();
        var finalZ = rotated.z() + center.z();

        return new Vector3f((float) finalX, (float) finalY, (float) finalZ);
    }

    public static Vector3f rotateAroundAxis(Vector3f point, Vector3f center, Vector3f axis, double angleRadians) {
        // Translate point to origin
        var translatedX = point.x() - center.x();
        var translatedY = point.y() - center.y();
        var translatedZ = point.z() - center.z();

        // Normalize the axis vector
        var axisLength = Math.sqrt(axis.x() * axis.x() + axis.y() * axis.y() + axis.z() * axis.z());
        var ux = axis.x() / axisLength;
        var uy = axis.y() / axisLength;
        var uz = axis.z() / axisLength;

        // Convert angleRadians to radians

        var cosTheta = Math.cos(angleRadians);
        var sinTheta = Math.sin(angleRadians);

        // Apply Rodrigues' rotation formula
        var v = (ux * translatedX + uy * translatedY + uz * translatedZ) * (1 - cosTheta);
        var rotatedX = translatedX * cosTheta
                + (uy * translatedZ - uz * translatedY) * sinTheta
                + ux * v;

        var rotatedY = translatedY * cosTheta
                + (uz * translatedX - ux * translatedZ) * sinTheta
                + uy * v;

        var rotatedZ = translatedZ * cosTheta
                + (ux * translatedY - uy * translatedX) * sinTheta
                + uz * v;

        // Translate back to original position
        var finalX = rotatedX + center.x();
        var finalY = rotatedY + center.y();
        var finalZ = rotatedZ + center.z();

//        LogUtil.logDebug(String.format("%s %s %s", finalX, finalY, finalZ));
        return new Vector3f((float) finalX,(float) finalY, (float)finalZ);
    }

    public static Vector3f calculatePerpendicularAxisZXPlane(Vector3f center, Vector3f point) {
        // Vector from center to point
        var vx = point.x() - center.x();
        var vz = point.z() - center.z();

        // Perpendicular vector in the ZX plane (Y-component is 0)
        var px = vz;
        var py = 0; // Ensure Y-component is 0
        var pz = -vx;

        return new Vector3f(px, py, pz);
    }
}
