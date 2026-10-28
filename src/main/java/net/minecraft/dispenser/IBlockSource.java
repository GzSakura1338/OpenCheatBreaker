package net.minecraft.dispenser;

import net.minecraft.tileentity.TileEntity;

public interface IBlockSource extends ILocatableSource {
    @Override
    double getX();

    @Override
    double getY();

    @Override
    double getZ();

    int getXInt();

    int getYInt();

    int getZInt();

    int getBlockMetadata();

    TileEntity getBlockTileEntity();
}
