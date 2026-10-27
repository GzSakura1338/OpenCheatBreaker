package net.minecraft.item;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSign extends Item {
   public ItemSign() {
      this.maxStackSize = 16;
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side == EnumFacing.DOWN) {
         return false;
      }

      if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid()) {
         return false;
      }

      pos = pos.offset(side);
      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      if (!Blocks.standing_sign.canPlaceBlockAt(worldIn, pos)) {
         return false;
      }

      if (worldIn.isRemote) {
         return true;
      }

      if (side == EnumFacing.UP) {
         int n = MathHelper.floor_double((playerIn.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5) & 15;
         worldIn.setBlockState(pos, Blocks.standing_sign.getDefaultState().withProperty(BlockStandingSign.ROTATION, n), 3);
      } else {
         worldIn.setBlockState(pos, Blocks.wall_sign.getDefaultState().withProperty(BlockWallSign.FACING, side), 3);
      }

      stack.stackSize--;
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntitySign && !ItemBlock.setTileEntityNBT(worldIn, playerIn, pos, stack)) {
         playerIn.openEditSign((TileEntitySign)tileEntity);
      }

      return true;
   }
}
