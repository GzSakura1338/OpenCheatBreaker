package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;

public interface IBlockAccess {
    Block getBlock(int n, int n2, int n3);

    TileEntity getTileEntity(int n, int n2, int n3);

    int getLightBrightnessForSkyBlocks(int n, int n2, int n3, int n4);

    int getBlockMetadata(int n, int n2, int n3);

    boolean isAirBlock(int n, int n2, int n3);

    BiomeGenBase getBiomeGenForCoords(int n, int n2);

    int getHeight();

    boolean extendedLevelsInChunkCache();

    int isBlockProvidingPowerTo(int n, int n2, int n3, int n4);
}
