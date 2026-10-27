package net.minecraft.entity;

import net.minecraft.block.BlockFence;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging {
   public EntityLeashKnot(World worldIn) {
      super(worldIn);
   }

   public EntityLeashKnot(World worldIn, BlockPos hangingPositionIn) {
      super(worldIn, hangingPositionIn);
      this.setPosition(hangingPositionIn.getX() + 0.5, hangingPositionIn.getY() + 0.5, hangingPositionIn.getZ() + 0.5);
      float f = 0.125F;
      float f2 = 0.1875F;
      float f3 = 0.25F;
      this.setEntityBoundingBox(
         new AxisAlignedBB(this.posX - 0.1875, this.posY - 0.25 + 0.125, this.posZ - 0.1875, this.posX + 0.1875, this.posY + 0.25 + 0.125, this.posZ + 0.1875)
      );
   }

   @Override
   public void entityInit() {
      super.entityInit();
   }

   @Override
   public void updateFacingWithBoundingBox(EnumFacing facingDirectionIn) {
   }

   @Override
   public int getWidthPixels() {
      return 9;
   }

   @Override
   public int getHeightPixels() {
      return 9;
   }

   @Override
   public float getEyeHeight() {
      return -0.0625F;
   }

   @Override
   public boolean isInRangeToRenderDist(double distance) {
      return distance < 1024.0;
   }

   @Override
   public void onBroken(Entity brokenEntity) {
   }

   @Override
   public boolean writeToNBTOptional(NBTTagCompound tagCompund) {
      return false;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      ItemStack heldItem = playerIn.getHeldItem();
      boolean flag = false;
      if (heldItem != null && heldItem.getItem() == Items.lead && !this.worldObj.isRemote) {
         double d = 7.0;

         for (EntityLiving entityLiving : this.worldObj
            .getEntitiesWithinAABB(
               EntityLiving.class,
               new AxisAlignedBB(this.posX - d, this.posY - d, this.posZ - d, this.posX + d, this.posY + d, this.posZ + d)
            )) {
            if (entityLiving.getLeashed() && entityLiving.getLeashedToEntity() == playerIn) {
               entityLiving.setLeashedToEntity(this, true);
               flag = true;
            }
         }
      }

      if (!this.worldObj.isRemote && !flag) {
         this.setDead();
         if (playerIn.capabilities.isCreativeMode) {
            double d = 7.0;

            for (EntityLiving entityLiving : this.worldObj
               .getEntitiesWithinAABB(
                  EntityLiving.class,
                  new AxisAlignedBB(this.posX - d, this.posY - d, this.posZ - d, this.posX + d, this.posY + d, this.posZ + d)
               )) {
               if (entityLiving.getLeashed() && entityLiving.getLeashedToEntity() == this) {
                  entityLiving.clearLeashed(true, false);
               }
            }
         }
      }

      return true;
   }

   @Override
   public boolean onValidSurface() {
      return this.worldObj.getBlockState(this.hangingPosition).getBlock() instanceof BlockFence;
   }

   public static EntityLeashKnot createKnot(World worldIn, BlockPos fence) {
      EntityLeashKnot entityLeashKnot = new EntityLeashKnot(worldIn, fence);
      entityLeashKnot.forceSpawn = true;
      worldIn.spawnEntityInWorld(entityLeashKnot);
      return entityLeashKnot;
   }

   public static EntityLeashKnot getKnotForPosition(World worldIn, BlockPos pos) {
      int n = pos.getX();
      int n2 = pos.getY();
      int n3 = pos.getZ();

      for (EntityLeashKnot entityLeashKnot : worldIn.getEntitiesWithinAABB(
         EntityLeashKnot.class, new AxisAlignedBB(n - 1.0, n2 - 1.0, n3 - 1.0, n + 1.0, n2 + 1.0, n3 + 1.0)
      )) {
         if (entityLeashKnot.getHangingPosition().equals(pos)) {
            return entityLeashKnot;
         }
      }

      return null;
   }
}
