package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;

public class LockCode {
   public static LockCode EMPTY_CODE = new LockCode("");
   public String lock;

   public LockCode(String code) {
      this.lock = code;
   }

   public boolean isEmpty() {
      return this.lock == null || this.lock.isEmpty();
   }

   public String getLock() {
      return this.lock;
   }

   public void toNBT(NBTTagCompound nbt) {
      nbt.setString("Lock", this.lock);
   }

   public static LockCode fromNBT(NBTTagCompound nbt) {
      if (nbt.hasKey("Lock", 8)) {
         String string = nbt.getString("Lock");
         return new LockCode(string);
      } else {
         return EMPTY_CODE;
      }
   }
}
