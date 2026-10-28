package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess {
    public World worldObj;
    public int chunkX;
    public int chunkZ;
    public Chunk[][] chunkArray;
    public boolean hasExtendedLevels;

    public ChunkCache(World world, int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        this.worldObj = world;
        this.chunkX = n - n7 >> 4;
        this.chunkZ = n3 - n7 >> 4;
        int n8 = n4 + n7 >> 4;
        int n9 = n6 + n7 >> 4;
        this.chunkArray = new Chunk[n8 - this.chunkX + 1][n9 - this.chunkZ + 1];
        this.hasExtendedLevels = true;

        for (int chunkX = this.chunkX; chunkX <= n8; chunkX++) {
            for (int chunkZ = this.chunkZ; chunkZ <= n9; chunkZ++) {
                Chunk chunkFromChunkCoords = world.getChunkFromChunkCoords(chunkX, chunkZ);
                if (chunkFromChunkCoords != null) {
                    this.chunkArray[chunkX - this.chunkX][chunkZ - this.chunkZ] = chunkFromChunkCoords;
                }
            }
        }

        for (int i = n >> 4; i <= n4 >> 4; i++) {
            for (int j = n3 >> 4; j <= n6 >> 4; j++) {
                Chunk chunk = this.chunkArray[i - this.chunkX][j - this.chunkZ];
                if (chunk != null && !chunk.getAreLevelsEmpty(n2, n5)) {
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
    public Block getBlock(int n, int n2, int n3) {
        Block air = Blocks.air;
        if (n2 >= 0 && n2 < 256) {
            int n4 = (n >> 4) - this.chunkX;
            int n5 = (n3 >> 4) - this.chunkZ;
            if (n4 >= 0 && n4 < this.chunkArray.length && n5 >= 0 && n5 < this.chunkArray[n4].length) {
                Chunk chunk = this.chunkArray[n4][n5];
                if (chunk != null) {
                    air = chunk.getBlock(n & 15, n2, n3 & 15);
                }
            }
        }

        return air;
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        int n = (x >> 4) - this.chunkX;
        int n2 = (z >> 4) - this.chunkZ;
        return this.chunkArray[n][n2].getBlockTileEntityInChunk(x & 15, y, z & 15);
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int n, int n2, int n3, int n4) {
        int skyBlockTypeBrightness = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, n, n2, n3);
        int skyBlockTypeBrightness2 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, n, n2, n3);
        if (skyBlockTypeBrightness2 < n4) {
            skyBlockTypeBrightness2 = n4;
        }

        return skyBlockTypeBrightness << 20 | skyBlockTypeBrightness2 << 4;
    }

    @Override
    public int getBlockMetadata(int n, int n2, int n3) {
        if (n2 < 0) {
            return 0;
        }

        if (n2 >= 256) {
            return 0;
        }

        int n4 = (n >> 4) - this.chunkX;
        int n5 = (n3 >> 4) - this.chunkZ;
        return this.chunkArray[n4][n5].getBlockMetadata(n & 15, n2, n3 & 15);
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return this.worldObj.getBiomeGenForCoords(x, z);
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return this.getBlock(x, y, z).getMaterial() == Material.air;
    }

    public int getSkyBlockTypeBrightness(EnumSkyBlock enumSkyBlock, int n, int n2, int n3) {
        if (n2 < 0) {
            n2 = 0;
        }

        if (n2 >= 256) {
            n2 = 255;
        }

        if (n2 < 0 || n2 >= 256 || n < -30000000 || n3 < -30000000 || n >= 30000000 || n3 > 30000000) {
            return enumSkyBlock.defaultLightValue;
        }

        if (enumSkyBlock == EnumSkyBlock.Sky && this.worldObj.provider.hasNoSky) {
            return 0;
        }

        if (this.getBlock(n, n2, n3).getUseNeighborBrightness()) {
            int specialBlockBrightness = this.getSpecialBlockBrightness(enumSkyBlock, n, n2 + 1, n3);
            int specialBlockBrightness2 = this.getSpecialBlockBrightness(enumSkyBlock, n + 1, n2, n3);
            int specialBlockBrightness3 = this.getSpecialBlockBrightness(enumSkyBlock, n - 1, n2, n3);
            int specialBlockBrightness4 = this.getSpecialBlockBrightness(enumSkyBlock, n, n2, n3 + 1);
            int specialBlockBrightness5 = this.getSpecialBlockBrightness(enumSkyBlock, n, n2, n3 - 1);
            if (specialBlockBrightness2 > specialBlockBrightness) {
                specialBlockBrightness = specialBlockBrightness2;
            }

            if (specialBlockBrightness3 > specialBlockBrightness) {
                specialBlockBrightness = specialBlockBrightness3;
            }

            if (specialBlockBrightness4 > specialBlockBrightness) {
                specialBlockBrightness = specialBlockBrightness4;
            }

            if (specialBlockBrightness5 > specialBlockBrightness) {
                specialBlockBrightness = specialBlockBrightness5;
            }

            return specialBlockBrightness;
        } else {
            int n4 = (n >> 4) - this.chunkX;
            int n5 = (n3 >> 4) - this.chunkZ;
            return this.chunkArray[n4][n5].getSavedLightValue(enumSkyBlock, n & 15, n2, n3 & 15);
        }
    }

    public int getSpecialBlockBrightness(EnumSkyBlock enumSkyBlock, int n, int n2, int n3) {
        if (n2 < 0) {
            n2 = 0;
        }

        if (n2 >= 256) {
            n2 = 255;
        }

        if (n2 >= 0 && n2 < 256 && n >= -30000000 && n3 >= -30000000 && n < 30000000 && n3 <= 30000000) {
            int n4 = (n >> 4) - this.chunkX;
            int n5 = (n3 >> 4) - this.chunkZ;
            return this.chunkArray[n4][n5].getSavedLightValue(enumSkyBlock, n & 15, n2, n3 & 15);
        } else {
            return enumSkyBlock.defaultLightValue;
        }
    }

    @Override
    public int getHeight() {
        return 256;
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int directionIn) {
        return this.getBlock(x, y, z).isProvidingStrongPower(this, x, y, z, directionIn);
    }
}
