package net.minecraft.util;

public class Vec3 {
    public double xCoord;
    public double yCoord;
    public double zCoord;

    public static Vec3 createVectorHelper(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public Vec3(double x, double y, double z) {
        if (x == -0.0) {
            x = 0.0;
        }

        if (y == -0.0) {
            y = 0.0;
        }

        if (z == -0.0) {
            z = 0.0;
        }

        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public Vec3 setComponents(double x, double y, double z) {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        return this;
    }

    public Vec3 subtract(Vec3 vec) {
        return createVectorHelper(vec.xCoord - this.xCoord, vec.yCoord - this.yCoord, vec.zCoord - this.zCoord);
    }

    public Vec3 normalize() {
        double d = MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
        return d < 1.0E-4 ? createVectorHelper(0.0, 0.0, 0.0) : createVectorHelper(this.xCoord / d, this.yCoord / d, this.zCoord / d);
    }

    public double dotProduct(Vec3 vec) {
        return this.xCoord * vec.xCoord + this.yCoord * vec.yCoord + this.zCoord * vec.zCoord;
    }

    public Vec3 crossProduct(Vec3 vec) {
        return createVectorHelper(
            this.yCoord * vec.zCoord - this.zCoord * vec.yCoord,
            this.zCoord * vec.xCoord - this.xCoord * vec.zCoord,
            this.xCoord * vec.yCoord - this.yCoord * vec.xCoord
        );
    }

    public Vec3 addVector(double x, double y, double z) {
        return createVectorHelper(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }

    public double distanceTo(Vec3 vec) {
        double d = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;
        return MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
    }

    public double squareDistanceTo(Vec3 vec) {
        double d = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;
        return d * d + d2 * d2 + d3 * d3;
    }

    public double squareDistanceTo(double x, double y, double z) {
        double d = x - this.xCoord;
        double d2 = y - this.yCoord;
        double d3 = z - this.zCoord;
        return d * d + d2 * d2 + d3 * d3;
    }

    public double lengthVector() {
        return MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
    }

    public Vec3 getIntermediateWithXValue(Vec3 vec, double x) {
        double d = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;
        if (d * d < 1.0E-7F) {
            return null;
        }

        double d4 = (x - this.xCoord) / d;
        return d4 >= 0.0 && d4 <= 1.0 ? createVectorHelper(this.xCoord + d * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
    }

    public Vec3 getIntermediateWithYValue(Vec3 vec, double y) {
        double d = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;
        if (d2 * d2 < 1.0E-7F) {
            return null;
        }

        double d4 = (y - this.yCoord) / d2;
        return d4 >= 0.0 && d4 <= 1.0 ? createVectorHelper(this.xCoord + d * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
    }

    public Vec3 getIntermediateWithZValue(Vec3 vec, double z) {
        double d = vec.xCoord - this.xCoord;
        double d2 = vec.yCoord - this.yCoord;
        double d3 = vec.zCoord - this.zCoord;
        if (d3 * d3 < 1.0E-7F) {
            return null;
        }

        double d4 = (z - this.zCoord) / d3;
        return d4 >= 0.0 && d4 <= 1.0 ? createVectorHelper(this.xCoord + d * d4, this.yCoord + d2 * d4, this.zCoord + d3 * d4) : null;
    }

    @Override
    public String toString() {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }

    public void rotateAroundX(float angle) {
        float cos = MathHelper.cos(angle);
        float sin = MathHelper.sin(angle);
        double xCoord = this.xCoord;
        double d = this.yCoord * cos + this.zCoord * sin;
        double d2 = this.zCoord * cos - this.yCoord * sin;
        this.setComponents(xCoord, d, d2);
    }

    public void rotateAroundY(float angle) {
        float cos = MathHelper.cos(angle);
        float sin = MathHelper.sin(angle);
        double d = this.xCoord * cos + this.zCoord * sin;
        double yCoord = this.yCoord;
        double d2 = this.zCoord * cos - this.xCoord * sin;
        this.setComponents(d, yCoord, d2);
    }

    public void rotateAroundZ(float angle) {
        float cos = MathHelper.cos(angle);
        float sin = MathHelper.sin(angle);
        double d = this.xCoord * cos + this.yCoord * sin;
        double d2 = this.yCoord * cos - this.xCoord * sin;
        double zCoord = this.zCoord;
        this.setComponents(d, d2, zCoord);
    }
}
