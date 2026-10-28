package net.minecraft.pathfinding;

public class Path {
    public PathPoint[] pathPoints = new PathPoint[1024];
    public int count;

    public PathPoint addPoint(PathPoint point) {
        if (point.index >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }

        if (this.count == this.pathPoints.length) {
            PathPoint[] pathPoints = new PathPoint[this.count << 1];
            System.arraycopy(this.pathPoints, 0, pathPoints, 0, this.count);
            this.pathPoints = pathPoints;
        }

        this.pathPoints[this.count] = point;
        point.index = this.count;
        this.sortBack(this.count++);
        return point;
    }

    public void clearPath() {
        this.count = 0;
    }

    public PathPoint dequeue() {
        PathPoint pathPoint = this.pathPoints[0];
        this.pathPoints[0] = this.pathPoints[--this.count];
        this.pathPoints[this.count] = null;
        if (this.count > 0) {
            this.sortForward(0);
        }

        pathPoint.index = -1;
        return pathPoint;
    }

    public void changeDistance(PathPoint pathPoint, float f) {
        float distanceToTarget = pathPoint.distanceToTarget;
        pathPoint.distanceToTarget = f;
        if (f < distanceToTarget) {
            this.sortBack(pathPoint.index);
        } else {
            this.sortForward(pathPoint.index);
        }
    }

    public void sortBack(int n) {
        PathPoint pathPoint = this.pathPoints[n];
        float distanceToTarget = pathPoint.distanceToTarget;

        while (n > 0) {
            int n2 = n - 1 >> 1;
            PathPoint pathPoint2 = this.pathPoints[n2];
            if (distanceToTarget >= pathPoint2.distanceToTarget) {
                break;
            }

            this.pathPoints[n] = pathPoint2;
            pathPoint2.index = n;
            n = n2;
        }

        this.pathPoints[n] = pathPoint;
        pathPoint.index = n;
    }

    public void sortForward(int n) {
        PathPoint pathPoint = this.pathPoints[n];
        float distanceToTarget = pathPoint.distanceToTarget;

        while (true) {
            int n2 = 1 + (n << 1);
            int n3 = n2 + 1;
            if (n2 >= this.count) {
                break;
            }

            PathPoint pathPoint2 = this.pathPoints[n2];
            float distanceToTarget2 = pathPoint2.distanceToTarget;
            PathPoint pathPoint3;
            float f;
            if (n3 >= this.count) {
                pathPoint3 = null;
                f = Float.POSITIVE_INFINITY;
            } else {
                pathPoint3 = this.pathPoints[n3];
                f = pathPoint3.distanceToTarget;
            }

            if (distanceToTarget2 < f) {
                if (distanceToTarget2 >= distanceToTarget) {
                    break;
                }

                this.pathPoints[n] = pathPoint2;
                pathPoint2.index = n;
                n = n2;
            } else {
                if (f >= distanceToTarget) {
                    break;
                }

                this.pathPoints[n] = pathPoint3;
                pathPoint3.index = n;
                n = n3;
            }
        }

        this.pathPoints[n] = pathPoint;
        pathPoint.index = n;
    }

    public boolean isPathEmpty() {
        return this.count == 0;
    }
}
