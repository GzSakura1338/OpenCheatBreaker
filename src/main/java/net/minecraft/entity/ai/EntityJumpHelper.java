package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityJumpHelper {
   public EntityLiving entity;
   public boolean isJumping;

   public EntityJumpHelper(EntityLiving entityIn) {
      this.entity = entityIn;
   }

   public void setJumping() {
      this.isJumping = true;
   }

   public void doJump() {
      this.entity.setJumping(this.isJumping);
      this.isJumping = false;
   }
}
