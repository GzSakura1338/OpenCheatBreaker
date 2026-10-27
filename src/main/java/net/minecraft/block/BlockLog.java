package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public abstract class BlockLog extends BlockRotatedPillar {
   public static PropertyEnum<BlockLog.EnumAxis> LOG_AXIS = PropertyEnum.create("axis", BlockLog.EnumAxis.class);

   public BlockLog() {
      super(Material.wood);
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setHardness(2.0F);
      this.setStepSound(soundTypeWood);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      byte b = 4;
      int n = b + 1;
      if (worldIn.isAreaLoaded(pos.add(-n, -n, -n), pos.add(n, n, n))) {
         for (BlockPos blockPos : BlockPos.getAllInBox(pos.add(-b, -b, -b), pos.add(b, b, b))) {
            IBlockState blockState = worldIn.getBlockState(blockPos);
            if (blockState.getBlock().getMaterial() == Material.leaves && !blockState.getValue(BlockLeaves.CHECK_DECAY)) {
               worldIn.setBlockState(blockPos, blockState.withProperty(BlockLeaves.CHECK_DECAY, true), 4);
            }
         }
      }
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
         .withProperty(LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()));
   }

   public enum EnumAxis implements IStringSerializable {
      X("x"),
      Y("y"),
      Z("z"),
      NONE("none");

      public String name;

      EnumAxis(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockLog.EnumAxis fromFacingAxis(Axis axis) {
         switch (axis) {
            case X:
               return X;
            case Y:
               return Y;
            case Z:
               return Z;
            default:
               return NONE;
         }
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
