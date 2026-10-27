package net.minecraft.util;

import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

public class Rotations {
   public float x;
   public float y;
   public float z;

   public Rotations(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Rotations(NBTTagList nbt) {
      this.x = nbt.getFloatAt(0);
      this.y = nbt.getFloatAt(1);
      this.z = nbt.getFloatAt(2);
   }

   public NBTTagList writeToNBT() {
      NBTTagList nbttagList = new NBTTagList();
      nbttagList.appendTag(new NBTTagFloat(this.x));
      nbttagList.appendTag(new NBTTagFloat(this.y));
      nbttagList.appendTag(new NBTTagFloat(this.z));
      return nbttagList;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Rotations)) {
         return false;
      }

      Rotations rotations = (Rotations)obj;
      return this.x == rotations.x && this.y == rotations.y && this.z == rotations.z;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }
}
