package net.minecraft.world.chunk;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EmptyChunk extends Chunk {
    public EmptyChunk(World world, int n, int n2) {
        super(world, n, n2);
    }

    @Override
    public boolean isAtLocation(int x, int z) {
        return x == this.xPosition && z == this.zPosition;
    }

    @Override
    public int getHeightValue(int x, int z) {
        return 0;
    }

    @Override
    public void generateHeightMap() {
    }

    @Override
    public void generateSkylightMap() {
    }

    @Override
    public Block getBlock(int n, int n2, int n3) {
        return Blocks.air;
    }

    @Override
    public int getBlockLightOpacity(int n, int n2, int n3) {
        return 255;
    }

    @Override
    public boolean setBlockIDWithMetadata(int n, int n2, int n3, Block block, int n4) {
        return true;
    }

    @Override
    public int getBlockMetadata(int n, int n2, int n3) {
        return 0;
    }

    @Override
    public boolean setBlockMetadata(int n, int n2, int n3, int n4) {
        return false;
    }

    @Override
    public int getSavedLightValue(EnumSkyBlock enumSkyBlock, int n, int n2, int n3) {
        return 0;
    }

    @Override
    public void setLightValue(EnumSkyBlock enumSkyBlock, int n, int n2, int n3, int n4) {
    }

    @Override
    public int getBlockLightValue(int n, int n2, int n3, int n4) {
        return 0;
    }

    @Override
    public void addEntity(Entity entity) {
    }

    @Override
    public void removeEntity(Entity entity) {
    }

    @Override
    public void removeEntityAtIndex(Entity entity, int n) {
    }

    @Override
    public boolean canBlockSeeTheSky(int n, int n2, int n3) {
        return false;
    }

    @Override
    public TileEntity getBlockTileEntityInChunk(int n, int n2, int n3) {
        return null;
    }

    @Override
    public void addTileEntity(TileEntity tileEntity) {
    }

    @Override
    public void setBlockTileEntityInChunk(int n, int n2, int n3, TileEntity tileEntity) {
    }

    @Override
    public void removeTileEntity(int n, int n2, int n3) {
    }

    @Override
    public void onChunkLoad() {
    }

    @Override
    public void onChunkUnload() {
    }

    @Override
    public void setChunkModified() {
    }

    @Override
    public void getEntitiesWithinAABBForEntity(Entity entity, AxisAlignedBB axisAlignedBB, List list, IEntitySelector entitySelector) {
    }

    @Override
    public void getEntitiesOfTypeWithinAAAB(Class clazz, AxisAlignedBB axisAlignedBB, List list, IEntitySelector entitySelector) {
    }

    @Override
    public boolean needsSaving(boolean flag) {
        return false;
    }

    @Override
    public Random getRandomWithSeed(long l) {
        return new Random(
            this.worldObj.getSeed()
                    + this.xPosition * this.xPosition * 4987142
                    + this.xPosition * 5947611
                    + this.zPosition * this.zPosition * 4392871L
                    + this.zPosition * 389711
                ^ l
        );
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean getAreLevelsEmpty(int n, int n2) {
        return true;
    }
}
