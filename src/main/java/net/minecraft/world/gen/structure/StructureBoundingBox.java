package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagIntArray;

public class StructureBoundingBox {
    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;

    public StructureBoundingBox() {
    }

    public StructureBoundingBox(int[] ints) {
        if (ints.length == 6) {
            this.minX = ints[0];
            this.minY = ints[1];
            this.minZ = ints[2];
            this.maxX = ints[3];
            this.maxY = ints[4];
            this.maxZ = ints[5];
        }
    }

    public static StructureBoundingBox getNewBoundingBox() {
        return new StructureBoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public static StructureBoundingBox getComponentToAddBoundingBox(
        int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10
    ) {
        switch (n10) {
            case 0:
                return new StructureBoundingBox(n + n4, n2 + n5, n3 + n6, n + n7 - 1 + n4, n2 + n8 - 1 + n5, n3 + n9 - 1 + n6);
            case 1:
                return new StructureBoundingBox(n - n9 + 1 + n6, n2 + n5, n3 + n4, n + n6, n2 + n8 - 1 + n5, n3 + n7 - 1 + n4);
            case 2:
                return new StructureBoundingBox(n + n4, n2 + n5, n3 - n9 + 1 + n6, n + n7 - 1 + n4, n2 + n8 - 1 + n5, n3 + n6);
            case 3:
                return new StructureBoundingBox(n + n6, n2 + n5, n3 + n4, n + n9 - 1 + n6, n2 + n8 - 1 + n5, n3 + n7 - 1 + n4);
            default:
                return new StructureBoundingBox(n + n4, n2 + n5, n3 + n6, n + n7 - 1 + n4, n2 + n8 - 1 + n5, n3 + n9 - 1 + n6);
        }
    }

    public StructureBoundingBox(StructureBoundingBox structureBoundingBox) {
        this.minX = structureBoundingBox.minX;
        this.minY = structureBoundingBox.minY;
        this.minZ = structureBoundingBox.minZ;
        this.maxX = structureBoundingBox.maxX;
        this.maxY = structureBoundingBox.maxY;
        this.maxZ = structureBoundingBox.maxZ;
    }

    public StructureBoundingBox(int n, int n2, int n3, int n4, int n5, int n6) {
        this.minX = n;
        this.minY = n2;
        this.minZ = n3;
        this.maxX = n4;
        this.maxY = n5;
        this.maxZ = n6;
    }

    public StructureBoundingBox(int n, int n2, int n3, int n4) {
        this.minX = n;
        this.minZ = n2;
        this.maxX = n3;
        this.maxZ = n4;
        this.minY = 1;
        this.maxY = 512;
    }

    public boolean intersectsWith(StructureBoundingBox structureBoundingBox) {
        return this.maxX >= structureBoundingBox.minX
            && this.minX <= structureBoundingBox.maxX
            && this.maxZ >= structureBoundingBox.minZ
            && this.minZ <= structureBoundingBox.maxZ
            && this.maxY >= structureBoundingBox.minY
            && this.minY <= structureBoundingBox.maxY;
    }

    public boolean intersectsWith(int n, int n2, int n3, int n4) {
        return this.maxX >= n && this.minX <= n3 && this.maxZ >= n2 && this.minZ <= n4;
    }

    public void expandTo(StructureBoundingBox structureBoundingBox) {
        this.minX = Math.min(this.minX, structureBoundingBox.minX);
        this.minY = Math.min(this.minY, structureBoundingBox.minY);
        this.minZ = Math.min(this.minZ, structureBoundingBox.minZ);
        this.maxX = Math.max(this.maxX, structureBoundingBox.maxX);
        this.maxY = Math.max(this.maxY, structureBoundingBox.maxY);
        this.maxZ = Math.max(this.maxZ, structureBoundingBox.maxZ);
    }

    public void offset(int n, int n2, int n3) {
        this.minX += n;
        this.minY += n2;
        this.minZ += n3;
        this.maxX += n;
        this.maxY += n2;
        this.maxZ += n3;
    }

    public boolean isVecInside(int n, int n2, int n3) {
        return n >= this.minX && n <= this.maxX && n3 >= this.minZ && n3 <= this.maxZ && n2 >= this.minY && n2 <= this.maxY;
    }

    public int getXSize() {
        return this.maxX - this.minX + 1;
    }

    public int getYSize() {
        return this.maxY - this.minY + 1;
    }

    public int getZSize() {
        return this.maxZ - this.minZ + 1;
    }

    public int getCenterX() {
        return this.minX + (this.maxX - this.minX + 1) / 2;
    }

    public int getCenterY() {
        return this.minY + (this.maxY - this.minY + 1) / 2;
    }

    public int getCenterZ() {
        return this.minZ + (this.maxZ - this.minZ + 1) / 2;
    }

    @Override
    public String toString() {
        return "(" + this.minX + ", " + this.minY + ", " + this.minZ + "; " + this.maxX + ", " + this.maxY + ", " + this.maxZ + ")";
    }

    public NBTTagIntArray toNbt() {
        return new NBTTagIntArray(new int[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
    }
}
