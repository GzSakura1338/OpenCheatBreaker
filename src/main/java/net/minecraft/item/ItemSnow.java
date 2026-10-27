package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSnow extends ItemBlock {
   public ItemSnow(Block block) {
      super(block);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (stack.stackSize == 0) {
         return false;
      }

      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      IBlockState blockState = worldIn.getBlockState(pos);
      Block block = blockState.getBlock();
      BlockPos pos2 = pos;
      if ((side != EnumFacing.UP || block != this.block) && !block.isReplaceable(worldIn, pos)) {
         pos2 = pos.offset(side);
         blockState = worldIn.getBlockState(pos2);
         block = blockState.getBlock();
      }

      if (block == this.block) {
         int value = blockState.getValue(BlockSnow.LAYERS);
         if (value <= 7) {
            IBlockState withProperty = blockState.withProperty(BlockSnow.LAYERS, value + 1);
            AxisAlignedBB collisionBoundingBox = this.block.getCollisionBoundingBox(worldIn, pos2, withProperty);
            if (collisionBoundingBox != null && worldIn.checkNoEntityCollision(collisionBoundingBox) && worldIn.setBlockState(pos2, withProperty, 2)) {
               worldIn.playSoundEffect(
                  pos2.getX() + 0.5F,
                  pos2.getY() + 0.5F,
                  pos2.getZ() + 0.5F,
                  this.block.stepSound.getPlaceSound(),
                  (this.block.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.block.stepSound.getFrequency() * 0.8F
               );
               stack.stackSize--;
               return true;
            }
         }
      }

      return super.onItemUse(stack, playerIn, worldIn, pos2, side, hitX, hitY, hitZ);
   }

   @Override
   public int getMetadata(int damage) {
      return damage;
   }
}
