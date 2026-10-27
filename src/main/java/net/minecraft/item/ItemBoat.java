package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBoat extends Item {
   public ItemBoat() {
      this.maxStackSize = 1;
      this.setCreativeTab(CreativeTabs.tabTransport);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      float f = 1.0F;
      float f2 = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * f;
      float f3 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * f;
      double d = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX) * f;
      double d2 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) * f + playerIn.getEyeHeight();
      double d3 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ) * f;
      Vec3 vec3 = new Vec3(d, d2, d3);
      float cos = MathHelper.cos(-f3 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float sin = MathHelper.sin(-f3 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f4 = -MathHelper.cos(-f2 * (float) (Math.PI / 180.0));
      float sin2 = MathHelper.sin(-f2 * (float) (Math.PI / 180.0));
      float f5 = sin * f4;
      float f6 = cos * f4;
      double d4 = 5.0;
      Vec3 addVector = vec3.addVector(f5 * d4, sin2 * d4, f6 * d4);
      MovingObjectPosition rayTraceBlocks = worldIn.rayTraceBlocks(vec3, addVector, true);
      if (rayTraceBlocks == null) {
         return itemStackIn;
      }

      Vec3 look = playerIn.getLook(f);
      boolean flag = false;
      float f7 = 1.0F;
      List list = worldIn.getEntitiesWithinAABBExcludingEntity(
         playerIn, playerIn.getEntityBoundingBox().addCoord(look.xCoord * d4, look.yCoord * d4, look.zCoord * d4).expand(f7, f7, f7)
      );

      for (int i = 0; i < list.size(); i++) {
         Entity entity = (Entity)list.get(i);
         if (entity.canBeCollidedWith()) {
            float collisionBorderSize = entity.getCollisionBorderSize();
            AxisAlignedBB expand = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
            if (expand.isVecInside(vec3)) {
               flag = true;
            }
         }
      }

      if (flag) {
         return itemStackIn;
      }

      if (rayTraceBlocks.typeOfHit == MovingObjectType.BLOCK) {
         BlockPos blockPos = rayTraceBlocks.getBlockPos();
         if (worldIn.getBlockState(blockPos).getBlock() == Blocks.snow_layer) {
            blockPos = blockPos.down();
         }

         EntityBoat entityBoat = new EntityBoat(worldIn, blockPos.getX() + 0.5F, blockPos.getY() + 1.0F, blockPos.getZ() + 0.5F);
         entityBoat.rotationYaw = ((MathHelper.floor_double(playerIn.rotationYaw * 4.0F / 360.0F + 0.5) & 3) - 1) * 90;
         if (!worldIn.getCollidingBoundingBoxes(entityBoat, entityBoat.getEntityBoundingBox().expand(-0.1, -0.1, -0.1)).isEmpty()) {
            return itemStackIn;
         }

         if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(entityBoat);
         }

         if (!playerIn.capabilities.isCreativeMode) {
            itemStackIn.stackSize--;
         }

         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      }

      return itemStackIn;
   }
}
