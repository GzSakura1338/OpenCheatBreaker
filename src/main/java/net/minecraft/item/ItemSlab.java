package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock {
   public BlockSlab singleSlab;
   public BlockSlab doubleSlab;

   public ItemSlab(Block block, BlockSlab singleSlab, BlockSlab doubleSlab) {
      super(block);
      this.singleSlab = singleSlab;
      this.doubleSlab = doubleSlab;
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   @Override
   public int getMetadata(int damage) {
      return damage;
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      return this.singleSlab.getUnlocalizedName(stack.getMetadata());
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (stack.stackSize == 0) {
         return false;
      }

      if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
         return false;
      }

      Object variant = this.singleSlab.getVariant(stack);
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == this.singleSlab) {
         IProperty variantProperty = this.singleSlab.getVariantProperty();
         Comparable value = blockState.getValue(variantProperty);
         EnumBlockHalf value2 = blockState.getValue(BlockSlab.HALF);
         if ((side == EnumFacing.UP && value2 == EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && value2 == EnumBlockHalf.TOP)
            && value == variant) {
            IBlockState withProperty = this.doubleSlab.getDefaultState().withProperty(variantProperty, value);
            if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, withProperty)) && worldIn.setBlockState(pos, withProperty, 3)) {
               worldIn.playSoundEffect(
                  pos.getX() + 0.5F,
                  pos.getY() + 0.5F,
                  pos.getZ() + 0.5F,
                  this.doubleSlab.stepSound.getPlaceSound(),
                  (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.doubleSlab.stepSound.getFrequency() * 0.8F
               );
               stack.stackSize--;
            }

            return true;
         }
      }

      return this.tryPlace(stack, worldIn, pos.offset(side), variant) ? true : super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
      BlockPos pos2 = pos;
      IProperty variantProperty = this.singleSlab.getVariantProperty();
      Object variant = this.singleSlab.getVariant(stack);
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == this.singleSlab) {
         boolean flag = blockState.getValue(BlockSlab.HALF) == EnumBlockHalf.TOP;
         if ((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag) && variant == blockState.getValue(variantProperty)) {
            return true;
         }
      }

      pos = pos.offset(side);
      IBlockState blockState2 = worldIn.getBlockState(pos);
      return blockState2.getBlock() == this.singleSlab && variant == blockState2.getValue(variantProperty) ? true : super.canPlaceBlockOnSide(worldIn, pos2, side, player, stack);
   }

   public boolean tryPlace(ItemStack stack, World worldIn, BlockPos pos, Object variantInStack) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == this.singleSlab) {
         IProperty var6p = this.singleSlab.getVariantProperty();
         Comparable value = blockState.getValue(var6p);
         if (value == variantInStack) {
            IBlockState withProperty = this.doubleSlab.getDefaultState().withProperty(var6p, value);
            if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, withProperty)) && worldIn.setBlockState(pos, withProperty, 3)) {
               worldIn.playSoundEffect(
                  pos.getX() + 0.5F,
                  pos.getY() + 0.5F,
                  pos.getZ() + 0.5F,
                  this.doubleSlab.stepSound.getPlaceSound(),
                  (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                  this.doubleSlab.stepSound.getFrequency() * 0.8F
               );
               stack.stackSize--;
            }

            return true;
         }
      }

      return false;
   }
}
