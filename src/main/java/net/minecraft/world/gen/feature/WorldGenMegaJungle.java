package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaJungle extends WorldGenHugeTrees {
   public WorldGenMegaJungle(boolean flag, int n, int n2, IBlockState blockState, IBlockState blockState2) {
      super(flag, n, n2, blockState, blockState2);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int calculateMaxHeight = this.calculateMaxHeight(rand);
      if (!this.canGenerate(worldIn, rand, position, calculateMaxHeight)) {
         return false;
      }

      this.generateJungleLeaves(worldIn, position.up(calculateMaxHeight), 2);

      for (int i = position.getY() + calculateMaxHeight - 2 - rand.nextInt(4); i > position.getY() + calculateMaxHeight / 2; i -= 2 + rand.nextInt(4)) {
         float f = rand.nextFloat() * (float) Math.PI * 2.0F;
         int n = position.getX() + (int)(0.5F + MathHelper.cos(f) * 4.0F);
         int n2 = position.getZ() + (int)(0.5F + MathHelper.sin(f) * 4.0F);

         for (int j = 0; j < 5; j++) {
            n = position.getX() + (int)(1.5F + MathHelper.cos(f) * j);
            n2 = position.getZ() + (int)(1.5F + MathHelper.sin(f) * j);
            this.setBlockAndNotifyAdequately(worldIn, new BlockPos(n, i - 3 + j / 2, n2), this.woodMetadata);
         }

         int n3 = 1 + rand.nextInt(2);
         int n4 = i;

         for (int j = i - n3; j <= n4; j++) {
            int n5 = j - n4;
            this.generateLeaves(worldIn, new BlockPos(n, j, n2), 1 - n5);
         }
      }

      for (int i = 0; i < calculateMaxHeight; i++) {
         BlockPos up = position.up(i);
         if (this.isBlockReplaceable(worldIn.getBlockState(up).getBlock())) {
            this.setBlockAndNotifyAdequately(worldIn, up, this.woodMetadata);
            if (i > 0) {
               this.generateVines(worldIn, rand, up.west(), BlockVine.EAST);
               this.generateVines(worldIn, rand, up.north(), BlockVine.SOUTH);
            }
         }

         if (i < calculateMaxHeight - 1) {
            BlockPos east = up.east();
            if (this.isBlockReplaceable(worldIn.getBlockState(east).getBlock())) {
               this.setBlockAndNotifyAdequately(worldIn, east, this.woodMetadata);
               if (i > 0) {
                  this.generateVines(worldIn, rand, east.east(), BlockVine.WEST);
                  this.generateVines(worldIn, rand, east.north(), BlockVine.SOUTH);
               }
            }

            BlockPos east2 = up.south().east();
            if (this.isBlockReplaceable(worldIn.getBlockState(east2).getBlock())) {
               this.setBlockAndNotifyAdequately(worldIn, east2, this.woodMetadata);
               if (i > 0) {
                  this.generateVines(worldIn, rand, east2.east(), BlockVine.WEST);
                  this.generateVines(worldIn, rand, east2.south(), BlockVine.NORTH);
               }
            }

            BlockPos south = up.south();
            if (this.isBlockReplaceable(worldIn.getBlockState(south).getBlock())) {
               this.setBlockAndNotifyAdequately(worldIn, south, this.woodMetadata);
               if (i > 0) {
                  this.generateVines(worldIn, rand, south.west(), BlockVine.EAST);
                  this.generateVines(worldIn, rand, south.south(), BlockVine.NORTH);
               }
            }
         }
      }

      return true;
   }

   public void generateVines(World world, Random random, BlockPos blockPos, PropertyBool propertyBool) {
      if (random.nextInt(3) > 0 && world.isAirBlock(blockPos)) {
         this.setBlockAndNotifyAdequately(world, blockPos, Blocks.vine.getDefaultState().withProperty(propertyBool, true));
      }
   }

   public void generateJungleLeaves(World worldIn, BlockPos blockPos, int n) {
      byte b = 2;

      for (int i = -b; i <= 0; i++) {
         this.generateLeavesLimited(worldIn, blockPos.up(i), n + 1 - i);
      }
   }
}
