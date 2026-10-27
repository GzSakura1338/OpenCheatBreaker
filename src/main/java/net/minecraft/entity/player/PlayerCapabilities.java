package net.minecraft.entity.player;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities {
   public boolean disableDamage;
   public boolean isFlying;
   public boolean allowFlying;
   public boolean isCreativeMode;
   public boolean allowEdit = true;
   public float flySpeed = 0.05F;
   public float walkSpeed = 0.1F;

   public void writeCapabilitiesToNBT(NBTTagCompound tagCompound) {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      nbttagCompound.setBoolean("invulnerable", this.disableDamage);
      nbttagCompound.setBoolean("flying", this.isFlying);
      nbttagCompound.setBoolean("mayfly", this.allowFlying);
      nbttagCompound.setBoolean("instabuild", this.isCreativeMode);
      nbttagCompound.setBoolean("mayBuild", this.allowEdit);
      nbttagCompound.setFloat("flySpeed", this.flySpeed);
      nbttagCompound.setFloat("walkSpeed", this.walkSpeed);
      tagCompound.setTag("abilities", nbttagCompound);
   }

   public void readCapabilitiesFromNBT(NBTTagCompound tagCompound) {
      if (tagCompound.hasKey("abilities", 10)) {
         NBTTagCompound compoundTag = tagCompound.getCompoundTag("abilities");
         this.disableDamage = compoundTag.getBoolean("invulnerable");
         this.isFlying = compoundTag.getBoolean("flying");
         this.allowFlying = compoundTag.getBoolean("mayfly");
         this.isCreativeMode = compoundTag.getBoolean("instabuild");
         if (compoundTag.hasKey("flySpeed", 99)) {
            this.flySpeed = compoundTag.getFloat("flySpeed");
            this.walkSpeed = compoundTag.getFloat("walkSpeed");
         }

         if (compoundTag.hasKey("mayBuild", 1)) {
            this.allowEdit = compoundTag.getBoolean("mayBuild");
         }
      }
   }

   public float getFlySpeed() {
      return this.flySpeed;
   }

   public void setFlySpeed(float speed) {
      this.flySpeed = speed;
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   public void setPlayerWalkSpeed(float speed) {
      this.walkSpeed = speed;
   }
}
