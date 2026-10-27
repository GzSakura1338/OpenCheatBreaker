package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemReed extends Item {
   public Block block;

   public ItemReed(Block block) {
      this.block = block;
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      IBlockState blockState = worldIn.getBlockState(pos);
      Block block = blockState.getBlock();
      if (block == Blocks.snow_layer && blockState.getValue(BlockSnow.LAYERS) < 1) {
         side = EnumFacing.UP;
      } else if (!block.isReplaceable(worldIn, pos)) {
         pos = pos.offset(side);
      }

      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      if (stack.stackSize == 0) {
         return false;
      }

      if (worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack)) {
         IBlockState onBlockPlaced = this.block.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);
         if (worldIn.setBlockState(pos, onBlockPlaced, 3)) {
            onBlockPlaced = worldIn.getBlockState(pos);
            if (onBlockPlaced.getBlock() == this.block) {
               ItemBlock.setTileEntityNBT(worldIn, playerIn, pos, stack);
               onBlockPlaced.getBlock().onBlockPlacedBy(worldIn, pos, onBlockPlaced, playerIn, stack);
            }

            worldIn.playSoundEffect(
               pos.getX() + 0.5F,
               pos.getY() + 0.5F,
               pos.getZ() + 0.5F,
               this.block.stepSound.getPlaceSound(),
               (this.block.stepSound.getVolume() + 1.0F) / 2.0F,
               this.block.stepSound.getFrequency() * 0.8F
            );
            stack.stackSize--;
            return true;
         }
      }

      return false;
   }
}
