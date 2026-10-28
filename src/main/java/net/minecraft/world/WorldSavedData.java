package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavedData {
    public String mapName;
    public boolean dirty;

    public WorldSavedData(String str) {
        this.mapName = str;
    }

    public abstract void readFromNBT(NBTTagCompound nbttagCompound);

    public abstract void writeToNBT(NBTTagCompound nbttagCompound);

    public void markDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean flag) {
        this.dirty = flag;
    }

    public boolean isDirty() {
        return this.dirty;
    }
}
