package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class PathEntity {
    public PathPoint[] points;
    public int currentPathIndex;
    public int pathLength;

    public PathEntity(PathPoint[] pathPoints) {
        this.points = pathPoints;
        this.pathLength = pathPoints.length;
    }

    public void incrementPathIndex() {
        this.currentPathIndex++;
    }

    public boolean isFinished() {
        return this.currentPathIndex >= this.pathLength;
    }

    public PathPoint getFinalPathPoint() {
        return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
    }

    public PathPoint getPathPointFromIndex(int n) {
        return this.points[n];
    }

    public int getCurrentPathLength() {
        return this.pathLength;
    }

    public void setCurrentPathLength(int n) {
        this.pathLength = n;
    }

    public int getCurrentPathIndex() {
        return this.currentPathIndex;
    }

    public void setCurrentPathIndex(int n) {
        this.currentPathIndex = n;
    }

    public Vec3 getVectorFromIndex(Entity entity, int n) {
        double d = this.points[n].xCoord + (int)(entity.width + 1.0F) * 0.5;
        double yCoord = this.points[n].yCoord;
        double d2 = this.points[n].zCoord + (int)(entity.width + 1.0F) * 0.5;
        return Vec3.createVectorHelper(d, yCoord, d2);
    }

    public Vec3 getPosition(Entity entity) {
        return this.getVectorFromIndex(entity, this.currentPathIndex);
    }

    public boolean isSamePath(PathEntity pathEntity) {
        if (pathEntity == null) {
            return false;
        }

        if (pathEntity.points.length != this.points.length) {
            return false;
        }

        for (int i = 0; i < this.points.length; i++) {
            if (this.points[i].xCoord != pathEntity.points[i].xCoord
                || this.points[i].yCoord != pathEntity.points[i].yCoord
                || this.points[i].zCoord != pathEntity.points[i].zCoord) {
                return false;
            }
        }

        return true;
    }

    public boolean isDestinationSame(Vec3 vec3) {
        PathPoint finalPathPoint = this.getFinalPathPoint();
        return finalPathPoint != null && finalPathPoint.xCoord == (int)vec3.xCoord && finalPathPoint.zCoord == (int)vec3.zCoord;
    }
}
