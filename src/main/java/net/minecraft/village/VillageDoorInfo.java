package net.minecraft.village;

public class VillageDoorInfo {
    public int posX;
    public int posY;
    public int posZ;
    public int insideDirectionX;
    public int insideDirectionZ;
    public int lastActivityTimestamp;
    public boolean isDetachedFromVillageFlag;
    public int doorOpeningRestrictionCounter;

    public VillageDoorInfo(int n, int n2, int n3, int n4, int n5, int n6) {
        this.posX = n;
        this.posY = n2;
        this.posZ = n3;
        this.insideDirectionX = n4;
        this.insideDirectionZ = n5;
        this.lastActivityTimestamp = n6;
    }

    public int getDistanceSquared(int n, int n2, int n3) {
        int n4 = n - this.posX;
        int n5 = n2 - this.posY;
        int n6 = n3 - this.posZ;
        return n4 * n4 + n5 * n5 + n6 * n6;
    }

    public int getInsideDistanceSquare(int n, int n2, int n3) {
        int n4 = n - this.posX - this.insideDirectionX;
        int n5 = n2 - this.posY;
        int n6 = n3 - this.posZ - this.insideDirectionZ;
        return n4 * n4 + n5 * n5 + n6 * n6;
    }

    public int getInsidePosX() {
        return this.posX + this.insideDirectionX;
    }

    public int getInsidePosY() {
        return this.posY;
    }

    public int getInsidePosZ() {
        return this.posZ + this.insideDirectionZ;
    }

    public boolean isInside(int n, int n2) {
        int n3 = n - this.posX;
        int n4 = n2 - this.posZ;
        return n3 * this.insideDirectionX + n4 * this.insideDirectionZ >= 0;
    }

    public void resetDoorOpeningRestrictionCounter() {
        this.doorOpeningRestrictionCounter = 0;
    }

    public void incrementDoorOpeningRestrictionCounter() {
        this.doorOpeningRestrictionCounter++;
    }

    public int getDoorOpeningRestrictionCounter() {
        return this.doorOpeningRestrictionCounter;
    }
}
