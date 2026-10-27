package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
   public static PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
   public int weight;

   public BlockPressurePlateWeighted(Material material, int n) {
      this(material, n, material.getMaterialMapColor());
   }

   public BlockPressurePlateWeighted(Material material, int n, MapColor mapColor) {
      super(material, mapColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, 0));
      this.weight = n;
   }

   @Override
   public int computeRedstoneStrength(World worldIn, BlockPos pos) {
      int min = Math.min(worldIn.getEntitiesWithinAABB(Entity.class, this.getSensitiveAABB(pos)).size(), this.weight);
      if (min > 0) {
         float f = (float)Math.min(this.weight, min) / this.weight;
         return MathHelper.ceiling_float_int(f * 15.0F);
      } else {
         return 0;
      }
   }

   @Override
   public int getRedstoneStrength(IBlockState state) {
      return state.getValue(POWER);
   }

   @Override
   public IBlockState setRedstoneStrength(IBlockState state, int strength) {
      return state.withProperty(POWER, strength);
   }

   @Override
   public int tickRate(World worldIn) {
      return 10;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(POWER, meta);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(POWER);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, POWER);
   }
}
