package net.minecraft.util;

public class AxisAlignedBB {
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;

    public static AxisAlignedBB getBoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.minX = x1;
        this.minY = y1;
        this.minZ = z1;
        this.maxX = x2;
        this.maxY = y2;
        this.maxZ = z2;
    }

    public AxisAlignedBB setBounds(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.minX = x1;
        this.minY = y1;
        this.minZ = z1;
        this.maxX = x2;
        this.maxY = y2;
        this.maxZ = z2;
        return this;
    }

    public AxisAlignedBB addCoord(double x, double y, double z) {
        double minX = this.minX;
        double minY = this.minY;
        double minZ = this.minZ;
        double maxX = this.maxX;
        double maxY = this.maxY;
        double maxZ = this.maxZ;
        if (x < 0.0) {
            minX += x;
        }

        if (x > 0.0) {
            maxX += x;
        }

        if (y < 0.0) {
            minY += y;
        }

        if (y > 0.0) {
            maxY += y;
        }

        if (z < 0.0) {
            minZ += z;
        }

        if (z > 0.0) {
            maxZ += z;
        }

        return getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public AxisAlignedBB expand(double x, double y, double z) {
        double d = this.minX - x;
        double d2 = this.minY - y;
        double d3 = this.minZ - z;
        double d4 = this.maxX + x;
        double d5 = this.maxY + y;
        double d6 = this.maxZ + z;
        return getBoundingBox(d, d2, d3, d4, d5, d6);
    }

    public AxisAlignedBB union(AxisAlignedBB other) {
        double min = Math.min(this.minX, other.minX);
        double min2 = Math.min(this.minY, other.minY);
        double min3 = Math.min(this.minZ, other.minZ);
        double max = Math.max(this.maxX, other.maxX);
        double max2 = Math.max(this.maxY, other.maxY);
        double max3 = Math.max(this.maxZ, other.maxZ);
        return getBoundingBox(min, min2, min3, max, max2, max3);
    }

    public AxisAlignedBB getOffsetBoundingBox(double x, double y, double z) {
        return getBoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public double calculateXOffset(AxisAlignedBB other, double d) {
        if (!(other.maxY > this.minY) || !(other.minY < this.maxY)) {
            return d;
        }

        if (other.maxZ > this.minZ && other.minZ < this.maxZ) {
            if (d > 0.0 && other.maxX <= this.minX) {
                double d2 = this.minX - other.maxX;
                if (d2 < d) {
                    d = d2;
                }
            }

            if (d < 0.0 && other.minX >= this.maxX) {
                double d2 = this.maxX - other.minX;
                if (d2 > d) {
                    d = d2;
                }
            }

            return d;
        } else {
            return d;
        }
    }

    public double calculateYOffset(AxisAlignedBB other, double d) {
        if (!(other.maxX > this.minX) || !(other.minX < this.maxX)) {
            return d;
        }

        if (other.maxZ > this.minZ && other.minZ < this.maxZ) {
            if (d > 0.0 && other.maxY <= this.minY) {
                double d2 = this.minY - other.maxY;
                if (d2 < d) {
                    d = d2;
                }
            }

            if (d < 0.0 && other.minY >= this.maxY) {
                double d2 = this.maxY - other.minY;
                if (d2 > d) {
                    d = d2;
                }
            }

            return d;
        } else {
            return d;
        }
    }

    public double calculateZOffset(AxisAlignedBB other, double d) {
        if (!(other.maxX > this.minX) || !(other.minX < this.maxX)) {
            return d;
        }

        if (other.maxY > this.minY && other.minY < this.maxY) {
            if (d > 0.0 && other.maxZ <= this.minZ) {
                double d2 = this.minZ - other.maxZ;
                if (d2 < d) {
                    d = d2;
                }
            }

            if (d < 0.0 && other.minZ >= this.maxZ) {
                double d2 = this.maxZ - other.minZ;
                if (d2 > d) {
                    d = d2;
                }
            }

            return d;
        } else {
            return d;
        }
    }

    public boolean intersectsWith(AxisAlignedBB other) {
        return other.maxX > this.minX
            && other.minX < this.maxX
            && other.maxY > this.minY
            && other.minY < this.maxY
            && other.maxZ > this.minZ
            && other.minZ < this.maxZ;
    }

    public AxisAlignedBB offset(double x, double y, double z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;
        return this;
    }

    public boolean isVecInside(Vec3 vec) {
        return vec.xCoord > this.minX
            && vec.xCoord < this.maxX
            && vec.yCoord > this.minY
            && vec.yCoord < this.maxY
            && vec.zCoord > this.minZ
            && vec.zCoord < this.maxZ;
    }

    public double getAverageEdgeLength() {
        double d = this.maxX - this.minX;
        double d2 = this.maxY - this.minY;
        double d3 = this.maxZ - this.minZ;
        return (d + d2 + d3) / 3.0;
    }

    public AxisAlignedBB contract(double x, double y, double z) {
        double d = this.minX + x;
        double d2 = this.minY + y;
        double d3 = this.minZ + z;
        double d4 = this.maxX - x;
        double d5 = this.maxY - y;
        double d6 = this.maxZ - z;
        return getBoundingBox(d, d2, d3, d4, d5, d6);
    }

    public AxisAlignedBB copy() {
        return getBoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public MovingObjectPosition calculateIntercept(Vec3 vec3, Vec3 vec32) {
        Vec3 intermediateWithXValue = vec3.getIntermediateWithXValue(vec32, this.minX);
        Vec3 intermediateWithXValue2 = vec3.getIntermediateWithXValue(vec32, this.maxX);
        Vec3 intermediateWithYValue = vec3.getIntermediateWithYValue(vec32, this.minY);
        Vec3 intermediateWithYValue2 = vec3.getIntermediateWithYValue(vec32, this.maxY);
        Vec3 intermediateWithZValue = vec3.getIntermediateWithZValue(vec32, this.minZ);
        Vec3 intermediateWithZValue2 = vec3.getIntermediateWithZValue(vec32, this.maxZ);
        if (!this.isVecInYZ(intermediateWithXValue)) {
            intermediateWithXValue = null;
        }

        if (!this.isVecInYZ(intermediateWithXValue2)) {
            intermediateWithXValue2 = null;
        }

        if (!this.isVecInXZ(intermediateWithYValue)) {
            intermediateWithYValue = null;
        }

        if (!this.isVecInXZ(intermediateWithYValue2)) {
            intermediateWithYValue2 = null;
        }

        if (!this.isVecInXY(intermediateWithZValue)) {
            intermediateWithZValue = null;
        }

        if (!this.isVecInXY(intermediateWithZValue2)) {
            intermediateWithZValue2 = null;
        }

        Vec3 vec33 = null;
        if (intermediateWithXValue != null && (vec33 == null || vec3.squareDistanceTo(intermediateWithXValue) < vec3.squareDistanceTo(vec33))) {
            vec33 = intermediateWithXValue;
        }

        if (intermediateWithXValue2 != null && (vec33 == null || vec3.squareDistanceTo(intermediateWithXValue2) < vec3.squareDistanceTo(vec33))) {
            vec33 = intermediateWithXValue2;
        }

        if (intermediateWithYValue != null && (vec33 == null || vec3.squareDistanceTo(intermediateWithYValue) < vec3.squareDistanceTo(vec33))) {
            vec33 = intermediateWithYValue;
        }

        if (intermediateWithYValue2 != null && (vec33 == null || vec3.squareDistanceTo(intermediateWithYValue2) < vec3.squareDistanceTo(vec33))) {
            vec33 = intermediateWithYValue2;
        }

        if (intermediateWithZValue != null && (vec33 == null || vec3.squareDistanceTo(intermediateWithZValue) < vec3.squareDistanceTo(vec33))) {
            vec33 = intermediateWithZValue;
        }

        if (intermediateWithZValue2 != null && (vec33 == null || vec3.squareDistanceTo(intermediateWithZValue2) < vec3.squareDistanceTo(vec33))) {
            vec33 = intermediateWithZValue2;
        }

        if (vec33 == null) {
            return null;
        }

        byte b = -1;
        if (vec33 == intermediateWithXValue) {
            b = 4;
        }

        if (vec33 == intermediateWithXValue2) {
            b = 5;
        }

        if (vec33 == intermediateWithYValue) {
            b = 0;
        }

        if (vec33 == intermediateWithYValue2) {
            b = 1;
        }

        if (vec33 == intermediateWithZValue) {
            b = 2;
        }

        if (vec33 == intermediateWithZValue2) {
            b = 3;
        }

        return new MovingObjectPosition(0, 0, 0, b, vec33);
    }

    public boolean isVecInYZ(Vec3 vec) {
        return vec != null && vec.yCoord >= this.minY && vec.yCoord <= this.maxY && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    public boolean isVecInXZ(Vec3 vec) {
        return vec != null && vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    public boolean isVecInXY(Vec3 vec) {
        return vec != null && vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.yCoord >= this.minY && vec.yCoord <= this.maxY;
    }

    public void setBB(AxisAlignedBB other) {
        this.minX = other.minX;
        this.minY = other.minY;
        this.minZ = other.minZ;
        this.maxX = other.maxX;
        this.maxY = other.maxY;
        this.maxZ = other.maxZ;
    }

    @Override
    public String toString() {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }
}
