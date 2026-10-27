package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCrops extends BlockBush implements IGrowable {
   public static PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

   public BlockCrops() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
      this.setTickRandomly(true);
      float f = 0.5F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
      this.setCreativeTab((CreativeTabs)null);
      this.setHardness(0.0F);
      this.setStepSound(soundTypeGrass);
      this.disableStats();
   }

   @Override
   public boolean canPlaceBlockOn(Block ground) {
      return ground == Blocks.farmland;
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      super.updateTick(worldIn, pos, state, rand);
      if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
         int value = state.getValue(AGE);
         if (value < 7) {
            float growthChance = getGrowthChance(this, worldIn, pos);
            if (rand.nextInt((int)(25.0F / growthChance) + 1) == 0) {
               worldIn.setBlockState(pos, state.withProperty(AGE, value + 1), 2);
            }
         }
      }
   }

   public void grow(World worldIn, BlockPos pos, IBlockState state) {
      int n = state.getValue(AGE) + MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);
      if (n > 7) {
         n = 7;
      }

      worldIn.setBlockState(pos, state.withProperty(AGE, n), 2);
   }

   public static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos) {
      float f = 1.0F;
      BlockPos down = pos.down();

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            float f2 = 0.0F;
            IBlockState blockState = worldIn.getBlockState(down.add(i, 0, j));
            if (blockState.getBlock() == Blocks.farmland) {
               f2 = 1.0F;
               if (blockState.getValue(BlockFarmland.MOISTURE) > 0) {
                  f2 = 3.0F;
               }
            }

            if (i != 0 || j != 0) {
               f2 /= 4.0F;
            }

            f += f2;
         }
      }

      BlockPos north = pos.north();
      BlockPos south = pos.south();
      BlockPos west = pos.west();
      BlockPos east = pos.east();
      boolean flag = blockIn == worldIn.getBlockState(west).getBlock() || blockIn == worldIn.getBlockState(east).getBlock();
      boolean flag2 = blockIn == worldIn.getBlockState(north).getBlock() || blockIn == worldIn.getBlockState(south).getBlock();
      if (flag && flag2) {
         f /= 2.0F;
      } else {
         boolean flag3 = blockIn == worldIn.getBlockState(west.north()).getBlock()
            || blockIn == worldIn.getBlockState(east.north()).getBlock()
            || blockIn == worldIn.getBlockState(east.south()).getBlock()
            || blockIn == worldIn.getBlockState(west.south()).getBlock();
         if (flag3) {
            f /= 2.0F;
         }
      }

      return f;
   }

   @Override
   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
   }

   public Item getSeed() {
      return Items.wheat_seeds;
   }

   public Item getCrop() {
      return Items.wheat;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);
      if (!worldIn.isRemote) {
         int value = state.getValue(AGE);
         if (value >= 7) {
            int n = 3 + fortune;

            for (int i = 0; i < n; i++) {
               if (worldIn.rand.nextInt(15) <= value) {
                  spawnAsEntity(worldIn, pos, new ItemStack(this.getSeed(), 1, 0));
               }
            }
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return state.getValue(AGE) == 7 ? this.getCrop() : this.getSeed();
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return this.getSeed();
   }

   @Override
   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return state.getValue(AGE) < 7;
   }

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      this.grow(worldIn, pos, state);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, meta);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(AGE);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AGE);
   }
}
