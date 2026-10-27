package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemColored {
   public ItemLilyPad(Block block) {
      super(block, false);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      MovingObjectPosition movingObjectPositionFromPlayer = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, true);
      if (movingObjectPositionFromPlayer == null) {
         return itemStackIn;
      }

      if (movingObjectPositionFromPlayer.typeOfHit == MovingObjectType.BLOCK) {
         BlockPos blockPos = movingObjectPositionFromPlayer.getBlockPos();
         if (!worldIn.isBlockModifiable(playerIn, blockPos)) {
            return itemStackIn;
         }

         if (!playerIn.canPlayerEdit(blockPos.offset(movingObjectPositionFromPlayer.sideHit), movingObjectPositionFromPlayer.sideHit, itemStackIn)) {
            return itemStackIn;
         }

         BlockPos up = blockPos.up();
         IBlockState blockState = worldIn.getBlockState(blockPos);
         if (blockState.getBlock().getMaterial() == Material.water && blockState.getValue(BlockLiquid.LEVEL) == 0 && worldIn.isAirBlock(up)) {
            worldIn.setBlockState(up, Blocks.waterlily.getDefaultState());
            if (!playerIn.capabilities.isCreativeMode) {
               itemStackIn.stackSize--;
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         }
      }

      return itemStackIn;
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      return Blocks.waterlily.getRenderColor(Blocks.waterlily.getStateFromMeta(stack.getMetadata()));
   }
}
