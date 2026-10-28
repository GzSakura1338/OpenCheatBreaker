package net.minecraft.util;

public class Facing {
    public static int[] oppositeSide = new int[]{1, 0, 3, 2, 5, 4};
    public static int[] offsetsXForSide = new int[]{0, 0, 0, 0, -1, 1};
    public static int[] offsetsYForSide = new int[]{-1, 1, 0, 0, 0, 0};
    public static int[] offsetsZForSide = new int[]{0, 0, -1, 1, 0, 0};
    public static String[] facings = new String[]{"DOWN", "UP", "NORTH", "SOUTH", "WEST", "EAST"};
}
