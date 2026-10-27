package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStem extends BlockBush implements IGrowable {
   public static PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
   public static PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
      public boolean apply(EnumFacing enumFacing) {
         return enumFacing != EnumFacing.DOWN;
      }
   });
   public Block crop;

   public BlockStem(Block crop) {
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(FACING, EnumFacing.UP));
      this.crop = crop;
      this.setTickRandomly(true);
      float f = 0.125F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
      this.setCreativeTab((CreativeTabs)null);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      state = state.withProperty(FACING, EnumFacing.UP);

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         if (worldIn.getBlockState(pos.offset(enumFacing)).getBlock() == this.crop) {
            state = state.withProperty(FACING, enumFacing);
            break;
         }
      }

      return state;
   }

   @Override
   public boolean canPlaceBlockOn(Block ground) {
      return ground == Blocks.farmland;
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      super.updateTick(worldIn, pos, state, rand);
      if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
         float growthChance = BlockCrops.getGrowthChance(this, worldIn, pos);
         if (rand.nextInt((int)(25.0F / growthChance) + 1) == 0) {
            int value = state.getValue(AGE);
            if (value < 7) {
               state = state.withProperty(AGE, value + 1);
               worldIn.setBlockState(pos, state, 2);
            } else {
               for (EnumFacing enumFacing : Plane.HORIZONTAL) {
                  if (worldIn.getBlockState(pos.offset(enumFacing)).getBlock() == this.crop) {
                     return;
                  }
               }

               pos = pos.offset(Plane.HORIZONTAL.random(rand));
               Block block = worldIn.getBlockState(pos.down()).getBlock();
               if (worldIn.getBlockState(pos).getBlock().blockMaterial == Material.air
                  && (block == Blocks.farmland || block == Blocks.dirt || block == Blocks.grass)) {
                  worldIn.setBlockState(pos, this.crop.getDefaultState());
               }
            }
         }
      }
   }

   public void growStem(World worldIn, BlockPos pos, IBlockState state) {
      int n = state.getValue(AGE) + MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);
      worldIn.setBlockState(pos, state.withProperty(AGE, Math.min(7, n)), 2);
   }

   @Override
   public int getRenderColor(IBlockState state) {
      if (state.getBlock() != this) {
         return super.getRenderColor(state);
      }

      int value = state.getValue(AGE);
      int n = value * 32;
      int n2 = 255 - value * 8;
      int n3 = value * 4;
      return n << 16 | n2 << 8 | n3;
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return this.getRenderColor(worldIn.getBlockState(pos));
   }

   @Override
   public void setBlockBoundsForItemRender() {
      float f = 0.125F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.maxY = (worldIn.getBlockState(pos).getValue(AGE) * 2 + 2) / 16.0F;
      float f = 0.125F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (float)this.maxY, 0.5F + f);
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
      if (!worldIn.isRemote) {
         Item seedItem = this.getSeedItem();
         if (seedItem != null) {
            int value = state.getValue(AGE);

            for (int i = 0; i < 3; i++) {
               if (worldIn.rand.nextInt(15) <= value) {
                  spawnAsEntity(worldIn, pos, new ItemStack(seedItem));
               }
            }
         }
      }
   }

   public Item getSeedItem() {
      return this.crop == Blocks.pumpkin ? Items.pumpkin_seeds : (this.crop == Blocks.melon_block ? Items.melon_seeds : null);
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      Item seedItem = this.getSeedItem();
      return seedItem != null ? seedItem : null;
   }

   @Override
   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return state.getValue(AGE) != 7;
   }

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      this.growStem(worldIn, pos, state);
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
      return new BlockState(this, AGE, FACING);
   }
}
