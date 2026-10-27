package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGenLakes extends WorldGenerator {
   public Block block;

   public WorldGenLakes(Block blockIn) {
      this.block = blockIn;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      position = position.add(-8, 0, -8);

      while (position.getY() > 5 && worldIn.isAirBlock(position)) {
         position = position.down();
      }

      if (position.getY() <= 4) {
         return false;
      }

      position = position.down(4);
      boolean[] flags = new boolean[2048];
      int n = rand.nextInt(4) + 4;

      for (int i = 0; i < n; i++) {
         double d = rand.nextDouble() * 6.0 + 3.0;
         double d2 = rand.nextDouble() * 4.0 + 2.0;
         double d3 = rand.nextDouble() * 6.0 + 3.0;
         double d4 = rand.nextDouble() * (16.0 - d - 2.0) + 1.0 + d / 2.0;
         double d5 = rand.nextDouble() * (8.0 - d2 - 4.0) + 2.0 + d2 / 2.0;
         double d6 = rand.nextDouble() * (16.0 - d3 - 2.0) + 1.0 + d3 / 2.0;

         for (int j = 1; j < 15; j++) {
            for (int k = 1; k < 15; k++) {
               for (int m = 1; m < 7; m++) {
                  double d7 = (j - d4) / (d / 2.0);
                  double d8 = (m - d5) / (d2 / 2.0);
                  double d9 = (k - d6) / (d3 / 2.0);
                  double d10 = d7 * d7 + d8 * d8 + d9 * d9;
                  if (d10 < 1.0) {
                     flags[(j * 16 + k) * 8 + m] = true;
                  }
               }
            }
         }
      }

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 8; k++) {
               boolean flag = !flags[(i * 16 + j) * 8 + k]
                  && (
                     i < 15 && flags[((i + 1) * 16 + j) * 8 + k]
                        || i > 0 && flags[((i - 1) * 16 + j) * 8 + k]
                        || j < 15 && flags[(i * 16 + j + 1) * 8 + k]
                        || j > 0 && flags[(i * 16 + (j - 1)) * 8 + k]
                        || k < 7 && flags[(i * 16 + j) * 8 + k + 1]
                        || k > 0 && flags[(i * 16 + j) * 8 + (k - 1)]
                  );
               if (flag) {
                  Material material = worldIn.getBlockState(position.add(i, k, j)).getBlock().getMaterial();
                  if (k >= 4 && material.isLiquid()) {
                     return false;
                  }

                  if (k < 4 && !material.isSolid() && worldIn.getBlockState(position.add(i, k, j)).getBlock() != this.block) {
                     return false;
                  }
               }
            }
         }
      }

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 8; k++) {
               if (flags[(i * 16 + j) * 8 + k]) {
                  worldIn.setBlockState(position.add(i, k, j), k >= 4 ? Blocks.air.getDefaultState() : this.block.getDefaultState(), 2);
               }
            }
         }
      }

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 4; k < 8; k++) {
               if (flags[(i * 16 + j) * 8 + k]) {
                  BlockPos blockPos = position.add(i, k - 1, j);
                  if (worldIn.getBlockState(blockPos).getBlock() == Blocks.dirt && worldIn.getLightFor(EnumSkyBlock.SKY, position.add(i, k, j)) > 0) {
                     BiomeGenBase biomeGenForCoords = worldIn.getBiomeGenForCoords(blockPos);
                     if (biomeGenForCoords.topBlock.getBlock() == Blocks.mycelium) {
                        worldIn.setBlockState(blockPos, Blocks.mycelium.getDefaultState(), 2);
                     } else {
                        worldIn.setBlockState(blockPos, Blocks.grass.getDefaultState(), 2);
                     }
                  }
               }
            }
         }
      }

      if (this.block.getMaterial() == Material.lava) {
         for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
               for (int k = 0; k < 8; k++) {
                  boolean flag = !flags[(i * 16 + j) * 8 + k]
                     && (
                        i < 15 && flags[((i + 1) * 16 + j) * 8 + k]
                           || i > 0 && flags[((i - 1) * 16 + j) * 8 + k]
                           || j < 15 && flags[(i * 16 + j + 1) * 8 + k]
                           || j > 0 && flags[(i * 16 + (j - 1)) * 8 + k]
                           || k < 7 && flags[(i * 16 + j) * 8 + k + 1]
                           || k > 0 && flags[(i * 16 + j) * 8 + (k - 1)]
                     );
                  if (flag
                     && (k < 4 || rand.nextInt(2) != 0)
                     && worldIn.getBlockState(position.add(i, k, j)).getBlock().getMaterial().isSolid()) {
                     worldIn.setBlockState(position.add(i, k, j), Blocks.stone.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      if (this.block.getMaterial() == Material.water) {
         for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
               byte b = 4;
               if (worldIn.canBlockFreezeWater(position.add(i, b, j))) {
                  worldIn.setBlockState(position.add(i, b, j), Blocks.ice.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }
}
