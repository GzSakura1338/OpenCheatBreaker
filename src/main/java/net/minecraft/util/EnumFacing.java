package net.minecraft.util;

public enum EnumFacing {
    DOWN(0, 1, 0, -1, 0),
    UP(1, 0, 0, 1, 0),
    NORTH(2, 3, 0, 0, -1),
    SOUTH(3, 2, 0, 0, 1),
    EAST(4, 5, -1, 0, 0),
    WEST(5, 4, 1, 0, 0);

    public int order_a;
    public int order_b;
    public int frontOffsetX;
    public int frontOffsetY;
    public int frontOffsetZ;
    public static EnumFacing[] faceList = new EnumFacing[6];

    EnumFacing(int n, int n2, int n3, int n4, int n5) {
        this.order_a = n;
        this.order_b = n2;
        this.frontOffsetX = n3;
        this.frontOffsetY = n4;
        this.frontOffsetZ = n5;
    }

    public int getFrontOffsetX() {
        return this.frontOffsetX;
    }

    public int getFrontOffsetY() {
        return this.frontOffsetY;
    }

    public int getFrontOffsetZ() {
        return this.frontOffsetZ;
    }

    public static EnumFacing getFront(int n) {
        return faceList[n % faceList.length];
    }

    static {
        for (EnumFacing enumFacing : values()) {
            faceList[enumFacing.order_a] = enumFacing;
        }
    }
}
