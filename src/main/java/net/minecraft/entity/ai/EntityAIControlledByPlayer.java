package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class EntityAIControlledByPlayer extends EntityAIBase {
   public EntityLiving thisEntity;
   public float maxSpeed;
   public float currentSpeed;
   public boolean speedBoosted;
   public int speedBoostTime;
   public int maxSpeedBoostTime;

   public EntityAIControlledByPlayer(EntityLiving entitylivingIn, float maxspeed) {
      this.thisEntity = entitylivingIn;
      this.maxSpeed = maxspeed;
      this.setMutexBits(7);
   }

   @Override
   public void startExecuting() {
      this.currentSpeed = 0.0F;
   }

   @Override
   public void resetTask() {
      this.speedBoosted = false;
      this.currentSpeed = 0.0F;
   }

   @Override
   public boolean shouldExecute() {
      return this.thisEntity.isEntityAlive()
         && this.thisEntity.riddenByEntity != null
         && this.thisEntity.riddenByEntity instanceof EntityPlayer
         && (this.speedBoosted || this.thisEntity.canBeSteered());
   }

   @Override
   public void updateTask() {
      EntityPlayer riddenByEntity = (EntityPlayer)this.thisEntity.riddenByEntity;
      EntityCreature thisEntity = (EntityCreature)this.thisEntity;
      float f = MathHelper.wrapAngleTo180_float(riddenByEntity.rotationYaw - this.thisEntity.rotationYaw) * 0.5F;
      if (f > 5.0F) {
         f = 5.0F;
      }

      if (f < -5.0F) {
         f = -5.0F;
      }

      this.thisEntity.rotationYaw = MathHelper.wrapAngleTo180_float(this.thisEntity.rotationYaw + f);
      if (this.currentSpeed < this.maxSpeed) {
         this.currentSpeed = this.currentSpeed + (this.maxSpeed - this.currentSpeed) * 0.01F;
      }

      if (this.currentSpeed > this.maxSpeed) {
         this.currentSpeed = this.maxSpeed;
      }

      int n = MathHelper.floor_double(this.thisEntity.posX);
      int n2 = MathHelper.floor_double(this.thisEntity.posY);
      int n3 = MathHelper.floor_double(this.thisEntity.posZ);
      float currentSpeed = this.currentSpeed;
      if (this.speedBoosted) {
         if (this.speedBoostTime++ > this.maxSpeedBoostTime) {
            this.speedBoosted = false;
         }

         currentSpeed += currentSpeed * 1.15F * MathHelper.sin((float)this.speedBoostTime / this.maxSpeedBoostTime * (float) Math.PI);
      }

      float f2 = 0.91F;
      if (this.thisEntity.onGround) {
         f2 = this.thisEntity
               .worldObj
               .getBlockState(new BlockPos(MathHelper.floor_float(n), MathHelper.floor_float(n2) - 1, MathHelper.floor_float(n3)))
               .getBlock()
               .slipperiness
            * 0.91F;
      }

      float f3 = 0.16277136F / (f2 * f2 * f2);
      float sin = MathHelper.sin(thisEntity.rotationYaw * (float) Math.PI / 180.0F);
      float cos = MathHelper.cos(thisEntity.rotationYaw * (float) Math.PI / 180.0F);
      float f4 = thisEntity.getAIMoveSpeed() * f3;
      float max = Math.max(currentSpeed, 1.0F);
      max = f4 / max;
      float f5 = currentSpeed * max;
      float f6 = -(f5 * sin);
      float f7 = f5 * cos;
      if (MathHelper.abs(f6) > MathHelper.abs(f7)) {
         if (f6 < 0.0F) {
            f6 -= this.thisEntity.width / 2.0F;
         }

         if (f6 > 0.0F) {
            f6 += this.thisEntity.width / 2.0F;
         }

         f7 = 0.0F;
      } else {
         f6 = 0.0F;
         if (f7 < 0.0F) {
            f7 -= this.thisEntity.width / 2.0F;
         }

         if (f7 > 0.0F) {
            f7 += this.thisEntity.width / 2.0F;
         }
      }

      int n4 = MathHelper.floor_double(this.thisEntity.posX + f6);
      int n5 = MathHelper.floor_double(this.thisEntity.posZ + f7);
      int n6 = MathHelper.floor_float(this.thisEntity.width + 1.0F);
      int n7 = MathHelper.floor_float(this.thisEntity.height + riddenByEntity.height + 1.0F);
      int n8 = MathHelper.floor_float(this.thisEntity.width + 1.0F);
      if (n != n4 || n3 != n5) {
         Block block = this.thisEntity.worldObj.getBlockState(new BlockPos(n, n2, n3)).getBlock();
         boolean flag = !this.isStairOrSlab(block)
            && (
               block.getMaterial() != Material.air
                  || !this.isStairOrSlab(this.thisEntity.worldObj.getBlockState(new BlockPos(n, n2 - 1, n3)).getBlock())
            );
         if (flag
            && 0 == WalkNodeProcessor.getNodeType(this.thisEntity.worldObj, this.thisEntity, n4, n2, n5, n6, n7, n8, false, false, true)
            && 1 == WalkNodeProcessor.getNodeType(this.thisEntity.worldObj, this.thisEntity, n, n2 + 1, n3, n6, n7, n8, false, false, true)
            && 1 == WalkNodeProcessor.getNodeType(this.thisEntity.worldObj, this.thisEntity, n4, n2 + 1, n5, n6, n7, n8, false, false, true)) {
            thisEntity.getJumpHelper().setJumping();
         }
      }

      if (!riddenByEntity.capabilities.isCreativeMode && this.currentSpeed >= this.maxSpeed * 0.5F && this.thisEntity.getRNG().nextFloat() < 0.006F && !this.speedBoosted
         )
       {
         ItemStack heldItem = riddenByEntity.getHeldItem();
         if (heldItem != null && heldItem.getItem() == Items.carrot_on_a_stick) {
            heldItem.damageItem(1, riddenByEntity);
            if (heldItem.stackSize == 0) {
               ItemStack itemStack = new ItemStack(Items.fishing_rod);
               itemStack.setTagCompound(heldItem.getTagCompound());
               riddenByEntity.inventory.mainInventory[riddenByEntity.inventory.currentItem] = itemStack;
            }
         }
      }

      this.thisEntity.moveEntityWithHeading(0.0F, currentSpeed);
   }

   public boolean isStairOrSlab(Block blockIn) {
      return blockIn instanceof BlockStairs || blockIn instanceof BlockSlab;
   }

   public boolean isSpeedBoosted() {
      return this.speedBoosted;
   }

   public void boostSpeed() {
      this.speedBoosted = true;
      this.speedBoostTime = 0;
      this.maxSpeedBoostTime = this.thisEntity.getRNG().nextInt(841) + 140;
   }

   public boolean isControlledByPlayer() {
      return !this.isSpeedBoosted() && this.currentSpeed > this.maxSpeed * 0.3F;
   }
}
