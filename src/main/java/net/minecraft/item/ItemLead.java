package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemLead extends Item {
   public ItemLead() {
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      Block block = worldIn.getBlockState(pos).getBlock();
      if (block instanceof BlockFence) {
         if (worldIn.isRemote) {
            return true;
         }

         attachToFence(playerIn, worldIn, pos);
         return true;
      } else {
         return false;
      }
   }

   public static boolean attachToFence(EntityPlayer player, World worldIn, BlockPos fence) {
      EntityLeashKnot knotForPosition = EntityLeashKnot.getKnotForPosition(worldIn, fence);
      boolean flag = false;
      double d = 7.0;
      int n = fence.getX();
      int n2 = fence.getY();
      int n3 = fence.getZ();

      for (EntityLiving entityLiving : worldIn.getEntitiesWithinAABB(
         EntityLiving.class, new AxisAlignedBB(n - d, n2 - d, n3 - d, n + d, n2 + d, n3 + d)
      )) {
         if (entityLiving.getLeashed() && entityLiving.getLeashedToEntity() == player) {
            if (knotForPosition == null) {
               knotForPosition = EntityLeashKnot.createKnot(worldIn, fence);
            }

            entityLiving.setLeashedToEntity(knotForPosition, true);
            flag = true;
         }
      }

      return flag;
   }
}
