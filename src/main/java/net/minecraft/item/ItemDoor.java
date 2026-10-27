package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDoor extends Item {
   public Block block;

   public ItemDoor(Block block) {
      this.block = block;
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side != EnumFacing.UP) {
         return false;
      }

      IBlockState blockState = worldIn.getBlockState(pos);
      Block block = blockState.getBlock();
      if (!block.isReplaceable(worldIn, pos)) {
         pos = pos.offset(side);
      }

      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      if (!this.block.canPlaceBlockAt(worldIn, pos)) {
         return false;
      }

      placeDoor(worldIn, pos, EnumFacing.fromAngle(playerIn.rotationYaw), this.block);
      stack.stackSize--;
      return true;
   }

   public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door) {
      BlockPos offset = pos.offset(facing.rotateY());
      BlockPos offset2 = pos.offset(facing.rotateYCCW());
      int n = (worldIn.getBlockState(offset2).getBlock().isNormalCube() ? 1 : 0) + (worldIn.getBlockState(offset2.up()).getBlock().isNormalCube() ? 1 : 0);
      int n2 = (worldIn.getBlockState(offset).getBlock().isNormalCube() ? 1 : 0) + (worldIn.getBlockState(offset.up()).getBlock().isNormalCube() ? 1 : 0);
      boolean flag = worldIn.getBlockState(offset2).getBlock() == door || worldIn.getBlockState(offset2.up()).getBlock() == door;
      boolean flag2 = worldIn.getBlockState(offset).getBlock() == door || worldIn.getBlockState(offset.up()).getBlock() == door;
      boolean flag3 = false;
      if (flag && !flag2 || n2 > n) {
         flag3 = true;
      }

      BlockPos up = pos.up();
      IBlockState withProperty = door.getDefaultState()
         .withProperty(BlockDoor.FACING, facing)
         .withProperty(BlockDoor.HINGE, flag3 ? EnumHingePosition.RIGHT : EnumHingePosition.LEFT);
      worldIn.setBlockState(pos, withProperty.withProperty(BlockDoor.HALF, EnumDoorHalf.LOWER), 2);
      worldIn.setBlockState(up, withProperty.withProperty(BlockDoor.HALF, EnumDoorHalf.UPPER), 2);
      worldIn.notifyNeighborsOfStateChange(pos, door);
      worldIn.notifyNeighborsOfStateChange(up, door);
   }
}
