package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Rotations;
import net.minecraft.world.World;

public class ItemArmorStand extends Item {
   public ItemArmorStand() {
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side == EnumFacing.DOWN) {
         return false;
      }

      boolean isReplaceable = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
      BlockPos blockPos = isReplaceable ? pos : pos.offset(side);
      if (!playerIn.canPlayerEdit(blockPos, side, stack)) {
         return false;
      }

      BlockPos up = blockPos.up();
      boolean flag = !worldIn.isAirBlock(blockPos) && !worldIn.getBlockState(blockPos).getBlock().isReplaceable(worldIn, blockPos);
      flag |= !worldIn.isAirBlock(up) && !worldIn.getBlockState(up).getBlock().isReplaceable(worldIn, up);
      if (flag) {
         return false;
      }

      double d = blockPos.getX();
      double d2 = blockPos.getY();
      double d3 = blockPos.getZ();
      List list = worldIn.getEntitiesWithinAABBExcludingEntity(
         (Entity)null, AxisAlignedBB.fromBounds(d, d2, d3, d + 1.0, d2 + 2.0, d3 + 1.0)
      );
      if (list.size() > 0) {
         return false;
      }

      if (!worldIn.isRemote) {
         worldIn.setBlockToAir(blockPos);
         worldIn.setBlockToAir(up);
         EntityArmorStand entityArmorStand = new EntityArmorStand(worldIn, d + 0.5, d2, d3 + 0.5);
         float f = MathHelper.floor_float((MathHelper.wrapAngleTo180_float(playerIn.rotationYaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;
         entityArmorStand.setLocationAndAngles(d + 0.5, d2, d3 + 0.5, f, 0.0F);
         this.applyRandomRotations(entityArmorStand, worldIn.rand);
         NBTTagCompound tagCompound = stack.getTagCompound();
         if (tagCompound != null && tagCompound.hasKey("EntityTag", 10)) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            entityArmorStand.writeToNBTOptional(nbttagCompound);
            nbttagCompound.merge(tagCompound.getCompoundTag("EntityTag"));
            entityArmorStand.readFromNBT(nbttagCompound);
         }

         worldIn.spawnEntityInWorld(entityArmorStand);
      }

      stack.stackSize--;
      return true;
   }

   public void applyRandomRotations(EntityArmorStand armorStand, Random rand) {
      Rotations headRotation = armorStand.getHeadRotation();
      float f = rand.nextFloat() * 5.0F;
      float f2 = rand.nextFloat() * 20.0F - 10.0F;
      Rotations rotations = new Rotations(headRotation.getX() + f, headRotation.getY() + f2, headRotation.getZ());
      armorStand.setHeadRotation(rotations);
      headRotation = armorStand.getBodyRotation();
      f = rand.nextFloat() * 10.0F - 5.0F;
      rotations = new Rotations(headRotation.getX(), headRotation.getY() + f, headRotation.getZ());
      armorStand.setBodyRotation(rotations);
   }
}
