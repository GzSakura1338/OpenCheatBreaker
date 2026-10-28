package net.minecraft.block;

import net.minecraft.dispenser.IBlockSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSourceImpl implements IBlockSource {
    public World worldObj;
    public int xPos;
    public int yPos;
    public int zPos;

    public BlockSourceImpl(World world, int n, int n2, int n3) {
        this.worldObj = world;
        this.xPos = n;
        this.yPos = n2;
        this.zPos = n3;
    }

    @Override
    public World getWorld() {
        return this.worldObj;
    }

    @Override
    public double getX() {
        return this.xPos + 0.5;
    }

    @Override
    public double getY() {
        return this.yPos + 0.5;
    }

    @Override
    public double getZ() {
        return this.zPos + 0.5;
    }

    @Override
    public int getXInt() {
        return this.xPos;
    }

    @Override
    public int getYInt() {
        return this.yPos;
    }

    @Override
    public int getZInt() {
        return this.zPos;
    }

    @Override
    public int getBlockMetadata() {
        return this.worldObj.getBlockMetadata(this.xPos, this.yPos, this.zPos);
    }

    @Override
    public TileEntity getBlockTileEntity() {
        return this.worldObj.getTileEntity(this.xPos, this.yPos, this.zPos);
    }
}
