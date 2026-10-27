package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.BlockHugeMushroom.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenBigMushroom extends WorldGenerator {
   public Block mushroomType;

   public WorldGenBigMushroom(Block block) {
      super(true);
      this.mushroomType = block;
   }

   public WorldGenBigMushroom() {
      super(false);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (this.mushroomType == null) {
         this.mushroomType = rand.nextBoolean() ? Blocks.brown_mushroom_block : Blocks.red_mushroom_block;
      }

      int n = rand.nextInt(3) + 4;
      boolean flag = true;
      if (position.getY() >= 1 && position.getY() + n + 1 < 256) {
         for (int i = position.getY(); i <= position.getY() + 1 + n; i++) {
            byte b = 3;
            if (i <= position.getY() + 3) {
               b = 0;
            }

            MutableBlockPos mutableBlockPos = new MutableBlockPos();

            for (int j = position.getX() - b; j <= position.getX() + b && flag; j++) {
               for (int k = position.getZ() - b; k <= position.getZ() + b && flag; k++) {
                  if (i >= 0 && i < 256) {
                     Block block = worldIn.getBlockState(mutableBlockPos.set(j, i, k)).getBlock();
                     if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
                        flag = false;
                     }
                  } else {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         }

         Block block = worldIn.getBlockState(position.down()).getBlock();
         if (block != Blocks.dirt && block != Blocks.grass && block != Blocks.mycelium) {
            return false;
         }

         int n2 = position.getY() + n;
         if (this.mushroomType == Blocks.red_mushroom_block) {
            n2 = position.getY() + n - 3;
         }

         for (int i = n2; i <= position.getY() + n; i++) {
            int n3 = 1;
            if (i < position.getY() + n) {
               n3++;
            }

            if (this.mushroomType == Blocks.brown_mushroom_block) {
               n3 = 3;
            }

            int n4 = position.getX() - n3;
            int n5 = position.getX() + n3;
            int n6 = position.getZ() - n3;
            int n7 = position.getZ() + n3;

            for (int j = n4; j <= n5; j++) {
               for (int k = n6; k <= n7; k++) {
                  int n8 = 5;
                  if (j == n4) {
                     n8--;
                  } else if (j == n5) {
                     n8++;
                  }

                  if (k == n6) {
                     n8 -= 3;
                  } else if (k == n7) {
                     n8 += 3;
                  }

                  EnumType byMetadata = EnumType.byMetadata(n8);
                  if (this.mushroomType == Blocks.brown_mushroom_block || i < position.getY() + n) {
                     if ((j == n4 || j == n5) && (k == n6 || k == n7)) {
                        continue;
                     }

                     if (j == position.getX() - (n3 - 1) && k == n6) {
                        byMetadata = EnumType.NORTH_WEST;
                     }

                     if (j == n4 && k == position.getZ() - (n3 - 1)) {
                        byMetadata = EnumType.NORTH_WEST;
                     }

                     if (j == position.getX() + (n3 - 1) && k == n6) {
                        byMetadata = EnumType.NORTH_EAST;
                     }

                     if (j == n5 && k == position.getZ() - (n3 - 1)) {
                        byMetadata = EnumType.NORTH_EAST;
                     }

                     if (j == position.getX() - (n3 - 1) && k == n7) {
                        byMetadata = EnumType.SOUTH_WEST;
                     }

                     if (j == n4 && k == position.getZ() + (n3 - 1)) {
                        byMetadata = EnumType.SOUTH_WEST;
                     }

                     if (j == position.getX() + (n3 - 1) && k == n7) {
                        byMetadata = EnumType.SOUTH_EAST;
                     }

                     if (j == n5 && k == position.getZ() + (n3 - 1)) {
                        byMetadata = EnumType.SOUTH_EAST;
                     }
                  }

                  if (byMetadata == EnumType.CENTER && i < position.getY() + n) {
                     byMetadata = EnumType.ALL_INSIDE;
                  }

                  if (position.getY() >= position.getY() + n - 1 || byMetadata != EnumType.ALL_INSIDE) {
                     BlockPos blockPos = new BlockPos(j, i, k);
                     if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                        this.setBlockAndNotifyAdequately(worldIn, blockPos, this.mushroomType.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, byMetadata));
                     }
                  }
               }
            }
         }

         for (int i = 0; i < n; i++) {
            Block block2 = worldIn.getBlockState(position.up(i)).getBlock();
            if (!block2.isFullBlock()) {
               this.setBlockAndNotifyAdequately(
                  worldIn, position.up(i), this.mushroomType.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, EnumType.STEM)
               );
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
