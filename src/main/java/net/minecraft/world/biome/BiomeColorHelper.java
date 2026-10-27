package net.minecraft.world.biome;

import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.world.IBlockAccess;

public class BiomeColorHelper {
   public static BiomeColorHelper.ColorResolver GRASS_COLOR = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.getGrassColorAtPos(blockPosition);
      }
   };
   public static BiomeColorHelper.ColorResolver FOLIAGE_COLOR = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.getFoliageColorAtPos(blockPosition);
      }
   };
   public static BiomeColorHelper.ColorResolver WATER_COLOR_MULTIPLIER = new BiomeColorHelper.ColorResolver() {
      @Override
      public int getColorAtPos(BiomeGenBase biome, BlockPos blockPosition) {
         return biome.waterColorMultiplier;
      }
   };

   public static int getColorAtPos(IBlockAccess blockAccess, BlockPos pos, BiomeColorHelper.ColorResolver colorResolver) {
      int n = 0;
      int n2 = 0;
      int n3 = 0;

      for (MutableBlockPos mutableBlockPos : BlockPos.getAllInBoxMutable(pos.add(-1, 0, -1), pos.add(1, 0, 1))) {
         int colorAtPos = colorResolver.getColorAtPos(blockAccess.getBiomeGenForCoords(mutableBlockPos), mutableBlockPos);
         n += (colorAtPos & 0xFF0000) >> 16;
         n2 += (colorAtPos & 0xFF00) >> 8;
         n3 += colorAtPos & 0xFF;
      }

      return (n / 9 & 0xFF) << 16 | (n2 / 9 & 0xFF) << 8 | n3 / 9 & 0xFF;
   }

   public static int getGrassColorAtPos(IBlockAccess blockAccess, BlockPos blockPos) {
      return getColorAtPos(blockAccess, blockPos, GRASS_COLOR);
   }

   public static int getFoliageColorAtPos(IBlockAccess blockAccess, BlockPos blockPos) {
      return getColorAtPos(blockAccess, blockPos, FOLIAGE_COLOR);
   }

   public static int getWaterColorAtPos(IBlockAccess blockAccess, BlockPos blockPos) {
      return getColorAtPos(blockAccess, blockPos, WATER_COLOR_MULTIPLIER);
   }

   interface ColorResolver {
      int getColorAtPos(BiomeGenBase biomeGenBase, BlockPos blockPos);
   }
}
