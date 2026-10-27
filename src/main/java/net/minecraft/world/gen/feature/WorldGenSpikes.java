package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenSpikes extends WorldGenerator {
   public Block baseBlockRequired;

   public WorldGenSpikes(Block block) {
      this.baseBlockRequired = block;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.isAirBlock(position) && worldIn.getBlockState(position.down()).getBlock() == this.baseBlockRequired) {
         int n = rand.nextInt(32) + 6;
         int n2 = rand.nextInt(4) + 1;
         MutableBlockPos mutableBlockPos = new MutableBlockPos();

         for (int i = position.getX() - n2; i <= position.getX() + n2; i++) {
            for (int j = position.getZ() - n2; j <= position.getZ() + n2; j++) {
               int n3 = i - position.getX();
               int n4 = j - position.getZ();
               if (n3 * n3 + n4 * n4 <= n2 * n2 + 1
                  && worldIn.getBlockState(mutableBlockPos.set(i, position.getY() - 1, j)).getBlock() != this.baseBlockRequired) {
                  return false;
               }
            }
         }

         for (int i = position.getY(); i < position.getY() + n && i < 256; i++) {
            for (int j = position.getX() - n2; j <= position.getX() + n2; j++) {
               for (int k = position.getZ() - n2; k <= position.getZ() + n2; k++) {
                  int n3 = j - position.getX();
                  int n4 = k - position.getZ();
                  if (n3 * n3 + n4 * n4 <= n2 * n2 + 1) {
                     worldIn.setBlockState(new BlockPos(j, i, k), Blocks.obsidian.getDefaultState(), 2);
                  }
               }
            }
         }

         EntityEnderCrystal entityEnderCrystal = new EntityEnderCrystal(worldIn);
         entityEnderCrystal.setLocationAndAngles(position.getX() + 0.5F, position.getY() + n, position.getZ() + 0.5F, rand.nextFloat() * 360.0F, 0.0F);
         worldIn.spawnEntityInWorld(entityEnderCrystal);
         worldIn.setBlockState(position.up(n), Blocks.bedrock.getDefaultState(), 2);
         return true;
      } else {
         return false;
      }
   }
}
