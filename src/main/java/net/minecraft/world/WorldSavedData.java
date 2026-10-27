package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavedData {
   public String mapName;
   public boolean dirty;

   public WorldSavedData(String name) {
      this.mapName = name;
   }

   public abstract void readFromNBT(NBTTagCompound nbttagCompound);

   public abstract void writeToNBT(NBTTagCompound nbttagCompound);

   public void markDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean isDirty) {
      this.dirty = isDirty;
   }

   public boolean isDirty() {
      return this.dirty;
   }
}
