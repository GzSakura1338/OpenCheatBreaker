package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBed extends Item {
   public ItemBed() {
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      if (side != EnumFacing.UP) {
         return false;
      }

      IBlockState blockState = worldIn.getBlockState(pos);
      Block block = blockState.getBlock();
      boolean isReplaceable = block.isReplaceable(worldIn, pos);
      if (!isReplaceable) {
         pos = pos.up();
      }

      int n = MathHelper.floor_double(playerIn.rotationYaw * 4.0F / 360.0F + 0.5) & 3;
      EnumFacing horizontal = EnumFacing.getHorizontal(n);
      BlockPos offset = pos.offset(horizontal);
      if (playerIn.canPlayerEdit(pos, side, stack) && playerIn.canPlayerEdit(offset, side, stack)) {
         boolean isReplaceable2 = worldIn.getBlockState(offset).getBlock().isReplaceable(worldIn, offset);
         boolean flag = isReplaceable || worldIn.isAirBlock(pos);
         boolean flag2 = isReplaceable2 || worldIn.isAirBlock(offset);
         if (flag && flag2 && World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && World.doesBlockHaveSolidTopSurface(worldIn, offset.down())) {
            IBlockState withProperty = Blocks.bed
               .getDefaultState()
               .withProperty(BlockBed.OCCUPIED, false)
               .withProperty(BlockBed.FACING, horizontal)
               .withProperty(BlockBed.PART, EnumPartType.FOOT);
            if (worldIn.setBlockState(pos, withProperty, 3)) {
               IBlockState withProperty2 = withProperty.withProperty(BlockBed.PART, EnumPartType.HEAD);
               worldIn.setBlockState(offset, withProperty2, 3);
            }

            stack.stackSize--;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
