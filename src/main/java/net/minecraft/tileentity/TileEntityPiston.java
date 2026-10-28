package net.minecraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;

public class TileEntityPiston extends TileEntity {
    public List pushedObjects = new ArrayList();
    public Block storedBlock;
    public int storedMetadata;
    public int storedOrientation;
    public boolean extending;
    public float progress;
    public float lastProgress;
    public boolean shouldHeadBeRendered;

    public TileEntityPiston() {
    }

    public TileEntityPiston(Block block, int n, int n2, boolean flag, boolean flag2) {
        this.storedBlock = block;
        this.storedMetadata = n;
        this.storedOrientation = n2;
        this.extending = flag;
        this.shouldHeadBeRendered = flag2;
    }

    public Block getStoredBlockID() {
        return this.storedBlock;
    }

    @Override
    public int getBlockMetadata() {
        return this.storedMetadata;
    }

    public boolean isExtending() {
        return this.extending;
    }

    public int getPistonOrientation() {
        return this.storedOrientation;
    }

    public boolean shouldPistonHeadBeRendered() {
        return this.shouldHeadBeRendered;
    }

    public float getAmountExtended(float f) {
        if (f > 1.0F) {
            f = 1.0F;
        }

        return this.lastProgress + (this.progress - this.lastProgress) * f;
    }

    public float computeFloat(float f) {
        return this.extending
            ? (this.getAmountExtended(f) - 1.0F) * Facing.offsetsXForSide[this.storedOrientation]
            : (1.0F - this.getAmountExtended(f)) * Facing.offsetsXForSide[this.storedOrientation];
    }

    public float computeFloat2(float f) {
        return this.extending
            ? (this.getAmountExtended(f) - 1.0F) * Facing.offsetsYForSide[this.storedOrientation]
            : (1.0F - this.getAmountExtended(f)) * Facing.offsetsYForSide[this.storedOrientation];
    }

    public float computeFloat3(float f) {
        return this.extending
            ? (this.getAmountExtended(f) - 1.0F) * Facing.offsetsZForSide[this.storedOrientation]
            : (1.0F - this.getAmountExtended(f)) * Facing.offsetsZForSide[this.storedOrientation];
    }

    public void pushEntities(float f, float f2) {
        if (this.extending) {
            f = 1.0F - f;
        } else {
            f--;
        }

        AxisAlignedBB findAxisAlignedBB = Blocks.piston_extension
            .findAxisAlignedBB(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.storedBlock, f, this.storedOrientation);
        if (findAxisAlignedBB != null) {
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(null, findAxisAlignedBB);
            if (!list.isEmpty()) {
                this.pushedObjects.addAll(list);

                for (Entity entity : (Iterable<Entity>)this.pushedObjects) {
                    entity.moveEntity(
                        f2 * Facing.offsetsXForSide[this.storedOrientation],
                        f2 * Facing.offsetsYForSide[this.storedOrientation],
                        f2 * Facing.offsetsZForSide[this.storedOrientation]
                    );
                }

                this.pushedObjects.clear();
            }
        }
    }

    public void clearPistonTileEntity() {
        if (this.lastProgress < 1.0F && this.worldObj != null) {
            this.lastProgress = this.progress = 1.0F;
            this.worldObj.removeTileEntity(this.xCoord, this.yCoord, this.zCoord);
            this.invalidate();
            if (this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) == Blocks.piston_extension) {
                this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, this.storedBlock, this.storedMetadata, 3);
                this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.storedBlock);
            }
        }
    }

    @Override
    public void updateEntity() {
        this.lastProgress = this.progress;
        if (this.lastProgress >= 1.0F) {
            this.pushEntities(1.0F, 0.25F);
            this.worldObj.removeTileEntity(this.xCoord, this.yCoord, this.zCoord);
            this.invalidate();
            if (this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord) == Blocks.piston_extension) {
                this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, this.storedBlock, this.storedMetadata, 3);
                this.worldObj.notifyBlockOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.storedBlock);
            }
        } else {
            this.progress += 0.5F;
            if (this.progress >= 1.0F) {
                this.progress = 1.0F;
            }

            if (this.extending) {
                this.pushEntities(this.progress, this.progress - this.lastProgress + 0.0625F);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.storedBlock = Block.getBlockById(compound.getInteger("blockId"));
        this.storedMetadata = compound.getInteger("blockData");
        this.storedOrientation = compound.getInteger("facing");
        this.lastProgress = this.progress = compound.getFloat("progress");
        this.extending = compound.getBoolean("extending");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("blockId", Block.getIdFromBlock(this.storedBlock));
        compound.setInteger("blockData", this.storedMetadata);
        compound.setInteger("facing", this.storedOrientation);
        compound.setFloat("progress", this.lastProgress);
        compound.setBoolean("extending", this.extending);
    }
}
