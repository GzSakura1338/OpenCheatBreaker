package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMycelium extends Block {
   public static PropertyBool SNOWY = PropertyBool.create("snowy");

   public BlockMycelium() {
      super(Material.grass, MapColor.purpleColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SNOWY, false));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos.up()).getBlock();
      return state.withProperty(SNOWY, block == Blocks.snow || block == Blocks.snow_layer);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         if (worldIn.getLightFromNeighbors(pos.up()) < 4 && worldIn.getBlockState(pos.up()).getBlock().getLightOpacity() > 2) {
            worldIn.setBlockState(pos, Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
         } else if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
            for (int i = 0; i < 4; i++) {
               BlockPos blockPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
               IBlockState blockState = worldIn.getBlockState(blockPos);
               Block block = worldIn.getBlockState(blockPos.up()).getBlock();
               if (blockState.getBlock() == Blocks.dirt
                  && blockState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT
                  && worldIn.getLightFromNeighbors(blockPos.up()) >= 4
                  && block.getLightOpacity() <= 2) {
                  worldIn.setBlockState(blockPos, this.getDefaultState());
               }
            }
         }
      }
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      super.randomDisplayTick(worldIn, pos, state, rand);
      if (rand.nextInt(10) == 0) {
         worldIn.spawnParticle(EnumParticleTypes.TOWN_AURA, pos.getX() + rand.nextFloat(), pos.getY() + 1.1F, pos.getZ() + rand.nextFloat(), 0.0, 0.0, 0.0);
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Blocks.dirt.getItemDropped(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, SNOWY);
   }
}
