package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenAbstractTree extends WorldGenerator {
   public WorldGenAbstractTree(boolean notify) {
      super(notify);
   }

   public boolean isBlockReplaceable(Block block) {
      Material material = block.getMaterial();
      return material == Material.air
         || material == Material.leaves
         || block == Blocks.grass
         || block == Blocks.dirt
         || block == Blocks.log
         || block == Blocks.log2
         || block == Blocks.sapling
         || block == Blocks.vine;
   }

   public void handleWorld(World worldIn, Random random, BlockPos blockPos) {
   }

   public void setDirt(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getBlock() != Blocks.dirt) {
         this.setBlockAndNotifyAdequately(worldIn, pos, Blocks.dirt.getDefaultState());
      }
   }
}
