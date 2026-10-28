package net.minecraft.client.renderer;

public class DestroyBlockProgress {
    public int miningPlayerEntId;
    public int partialBlockX;
    public int partialBlockY;
    public int partialBlockZ;
    public int partialBlockProgress;
    public int createdAtCloudUpdateTick;

    public DestroyBlockProgress(int n, int n2, int n3, int n4) {
        this.miningPlayerEntId = n;
        this.partialBlockX = n2;
        this.partialBlockY = n3;
        this.partialBlockZ = n4;
    }

    public int getPartialBlockX() {
        return this.partialBlockX;
    }

    public int getPartialBlockY() {
        return this.partialBlockY;
    }

    public int getPartialBlockZ() {
        return this.partialBlockZ;
    }

    public void setPartialBlockDamage(int damage) {
        if (damage > 10) {
            damage = 10;
        }

        this.partialBlockProgress = damage;
    }

    public int getPartialBlockDamage() {
        return this.partialBlockProgress;
    }

    public void setCloudUpdateTick(int n) {
        this.createdAtCloudUpdateTick = n;
    }

    public int getCreationCloudUpdateTick() {
        return this.createdAtCloudUpdateTick;
    }
}
