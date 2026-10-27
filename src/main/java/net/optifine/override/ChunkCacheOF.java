package net.optifine.override;

import java.util.Arrays;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.Config;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.util.ArrayCache;

public class ChunkCacheOF implements IBlockAccess {
   public boolean dynamicLights = Config.isDynamicLights();
   public ChunkCache chunkCache;
   public int sizeX;
   public int sizeY;
   public int sizeZ;
   public int sizeXY;
   public int arraySize;
   public int posX;
   public int[] combinedLights;
   public IBlockState[] blockStates;
   public int posY;
   public int posZ;
   public static ArrayCache cacheCombinedLights = new ArrayCache(int.class, 16);
   public static ArrayCache cacheBlockStates = new ArrayCache(IBlockState.class, 16);

   public ChunkCacheOF(ChunkCache chunkCache, BlockPos blockPos, BlockPos blockPos2, int n) {
      this.chunkCache = chunkCache;
      int n2 = blockPos.getX() - n >> 4;
      int n3 = blockPos.getY() - n >> 4;
      int n4 = blockPos.getZ() - n >> 4;
      int n5 = blockPos2.getX() + n >> 4;
      int n6 = blockPos2.getY() + n >> 4;
      int n7 = blockPos2.getZ() + n >> 4;
      this.sizeX = n5 - n2 + 1 << 4;
      this.sizeY = n6 - n3 + 1 << 4;
      this.sizeZ = n7 - n4 + 1 << 4;
      this.sizeXY = this.sizeX * this.sizeY;
      this.arraySize = this.sizeX * this.sizeY * this.sizeZ;
      this.posX = n2 << 4;
      this.posY = n3 << 4;
      this.posZ = n4 << 4;
   }

   public int getPositionIndex(BlockPos blockPos) {
      int n = blockPos.getX() - this.posX;
      if (n >= 0 && n < this.sizeX) {
         int n2 = blockPos.getY() - this.posY;
         if (n2 >= 0 && n2 < this.sizeY) {
            int n3 = blockPos.getZ() - this.posZ;
            return n3 >= 0 && n3 < this.sizeZ ? n3 * this.sizeXY + n2 * this.sizeX + n : -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   @Override
   public int getCombinedLight(BlockPos blockPos, int n) {
      int positionIndex = this.getPositionIndex(blockPos);
      if (positionIndex >= 0 && positionIndex < this.arraySize && this.combinedLights != null) {
         int n2 = this.combinedLights[positionIndex];
         if (n2 == -1) {
            n2 = this.getCombinedLightRaw(blockPos, n);
            this.combinedLights[positionIndex] = n2;
         }

         return n2;
      } else {
         return this.getCombinedLightRaw(blockPos, n);
      }
   }

   public int getCombinedLightRaw(BlockPos blockPos, int n) {
      int combinedLight = this.chunkCache.getCombinedLight(blockPos, n);
      if (this.dynamicLights && !this.getBlockState(blockPos).getBlock().isOpaqueCube()) {
         combinedLight = DynamicLights.getCombinedLight(blockPos, combinedLight);
      }

      return combinedLight;
   }

   @Override
   public IBlockState getBlockState(BlockPos blockPos) {
      int positionIndex = this.getPositionIndex(blockPos);
      if (positionIndex >= 0 && positionIndex < this.arraySize && this.blockStates != null) {
         IBlockState blockState = this.blockStates[positionIndex];
         if (blockState == null) {
            blockState = this.chunkCache.getBlockState(blockPos);
            this.blockStates[positionIndex] = blockState;
         }

         return blockState;
      } else {
         return this.chunkCache.getBlockState(blockPos);
      }
   }

   public void renderStart() {
      if (this.combinedLights == null) {
         this.combinedLights = (int[])cacheCombinedLights.allocate(this.arraySize);
      }

      Arrays.fill(this.combinedLights, -1);
      if (this.blockStates == null) {
         this.blockStates = (IBlockState[])cacheBlockStates.allocate(this.arraySize);
      }

      Arrays.fill(this.blockStates, null);
   }

   public void renderFinish() {
      cacheCombinedLights.free(this.combinedLights);
      this.combinedLights = null;
      cacheBlockStates.free(this.blockStates);
      this.blockStates = null;
   }

   @Override
   public boolean extendedLevelsInChunkCache() {
      return this.chunkCache.extendedLevelsInChunkCache();
   }

   @Override
   public BiomeGenBase getBiomeGenForCoords(BlockPos blockPos) {
      return this.chunkCache.getBiomeGenForCoords(blockPos);
   }

   @Override
   public int getStrongPower(BlockPos blockPos, EnumFacing enumFacing) {
      return this.chunkCache.getStrongPower(blockPos, enumFacing);
   }

   @Override
   public TileEntity getTileEntity(BlockPos blockPos) {
      return this.chunkCache.getTileEntity(blockPos);
   }

   @Override
   public WorldType getWorldType() {
      return this.chunkCache.getWorldType();
   }

   @Override
   public boolean isAirBlock(BlockPos blockPos) {
      return this.chunkCache.isAirBlock(blockPos);
   }

   public boolean isSideSolid(BlockPos blockPos, EnumFacing enumFacing, boolean flag) {
      return Reflector.callBoolean(this.chunkCache, Reflector.ForgeChunkCache_isSideSolid, blockPos, enumFacing, flag);
   }
}
