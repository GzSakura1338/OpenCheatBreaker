package net.minecraft.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;

public class ChunkCache implements IBlockAccess {
   public World worldObj;
   public int chunkX;
   public int chunkZ;
   public Chunk[][] chunkArray;
   public boolean hasExtendedLevels;

   public ChunkCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
      this.worldObj = worldIn;
      this.chunkX = posFromIn.getX() - subIn >> 4;
      this.chunkZ = posFromIn.getZ() - subIn >> 4;
      int n = posToIn.getX() + subIn >> 4;
      int n2 = posToIn.getZ() + subIn >> 4;
      this.chunkArray = new Chunk[n - this.chunkX + 1][n2 - this.chunkZ + 1];
      this.hasExtendedLevels = true;

      for (int chunkX = this.chunkX; chunkX <= n; chunkX++) {
         for (int chunkZ = this.chunkZ; chunkZ <= n2; chunkZ++) {
            this.chunkArray[chunkX - this.chunkX][chunkZ - this.chunkZ] = worldIn.getChunkFromChunkCoords(chunkX, chunkZ);
         }
      }

      for (int i = posFromIn.getX() >> 4; i <= posToIn.getX() >> 4; i++) {
         for (int j = posFromIn.getZ() >> 4; j <= posToIn.getZ() >> 4; j++) {
            Chunk chunk = this.chunkArray[i - this.chunkX][j - this.chunkZ];
            if (chunk != null && !chunk.getAreLevelsEmpty(posFromIn.getY(), posToIn.getY())) {
               this.hasExtendedLevels = false;
            }
         }
      }
   }

   @Override
   public boolean extendedLevelsInChunkCache() {
      return this.hasExtendedLevels;
   }

   @Override
   public TileEntity getTileEntity(BlockPos pos) {
      int n = (pos.getX() >> 4) - this.chunkX;
      int n2 = (pos.getZ() >> 4) - this.chunkZ;
      return this.chunkArray[n][n2].getTileEntity(pos, EnumCreateEntityType.IMMEDIATE);
   }

   @Override
   public int getCombinedLight(BlockPos pos, int lightValue) {
      int lightForExt = this.getLightForExt(EnumSkyBlock.SKY, pos);
      int lightForExt2 = this.getLightForExt(EnumSkyBlock.BLOCK, pos);
      if (lightForExt2 < lightValue) {
         lightForExt2 = lightValue;
      }

      return lightForExt << 20 | lightForExt2 << 4;
   }

   @Override
   public IBlockState getBlockState(BlockPos pos) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         int n = (pos.getX() >> 4) - this.chunkX;
         int n2 = (pos.getZ() >> 4) - this.chunkZ;
         if (n >= 0 && n < this.chunkArray.length && n2 >= 0 && n2 < this.chunkArray[n].length) {
            Chunk chunk = this.chunkArray[n][n2];
            if (chunk != null) {
               return chunk.getBlockState(pos);
            }
         }
      }

      return Blocks.air.getDefaultState();
   }

   @Override
   public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
      return this.worldObj.getBiomeGenForCoords(pos);
   }

   public int getLightForExt(EnumSkyBlock enumSkyBlock, BlockPos pos) {
      if (enumSkyBlock == EnumSkyBlock.SKY && this.worldObj.provider.getHasNoSky()) {
         return 0;
      }

      if (pos.getY() >= 0 && pos.getY() < 256) {
         if (this.getBlockState(pos).getBlock().getUseNeighborBrightness()) {
            int n = 0;

            for (EnumFacing enumFacing : EnumFacing.values()) {
               int lightFor = this.getLightFor(enumSkyBlock, pos.offset(enumFacing));
               if (lightFor > n) {
                  n = lightFor;
               }

               if (n >= 15) {
                  return n;
               }
            }

            return n;
         } else {
            int n = (pos.getX() >> 4) - this.chunkX;
            int n2 = (pos.getZ() >> 4) - this.chunkZ;
            return this.chunkArray[n][n2].getLightFor(enumSkyBlock, pos);
         }
      } else {
         return enumSkyBlock.defaultLightValue;
      }
   }

   @Override
   public boolean isAirBlock(BlockPos pos) {
      return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
   }

   public int getLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         int n = (pos.getX() >> 4) - this.chunkX;
         int n2 = (pos.getZ() >> 4) - this.chunkZ;
         return this.chunkArray[n][n2].getLightFor(enumSkyBlock, pos);
      } else {
         return enumSkyBlock.defaultLightValue;
      }
   }

   @Override
   public int getStrongPower(BlockPos pos, EnumFacing direction) {
      IBlockState blockState = this.getBlockState(pos);
      return blockState.getBlock().getStrongPower(this, pos, blockState, direction);
   }

   @Override
   public WorldType getWorldType() {
      return this.worldObj.getWorldType();
   }
}
