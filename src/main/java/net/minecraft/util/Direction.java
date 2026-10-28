package net.minecraft.util;

public class Direction {
    public static int[] offsetX = new int[]{0, -1, 0, 1};
    public static int[] offsetZ = new int[]{1, 0, -1, 0};
    public static String[] directions = new String[]{"SOUTH", "WEST", "NORTH", "EAST"};
    public static int[] directionToFacing = new int[]{3, 4, 2, 5};
    public static int[] facingToDirection = new int[]{-1, -1, 2, 0, 1, 3};
    public static int[] rotateOpposite = new int[]{2, 3, 0, 1};
    public static int[] enderEyeMetaToDirection = new int[]{1, 2, 3, 0};
    public static int[] rotateLeft = new int[]{3, 0, 1, 2};
    public static int[][] bedDirection = new int[][]{{1, 0, 3, 2, 5, 4}, {1, 0, 5, 4, 2, 3}, {1, 0, 2, 3, 4, 5}, {1, 0, 4, 5, 3, 2}};

    public static int getMovementDirection(double d, double d2) {
        return MathHelper.abs((float)d) > MathHelper.abs((float)d2) ? (d > 0.0 ? 1 : 3) : (d2 > 0.0 ? 2 : 0);
    }
}
