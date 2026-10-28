package net.minecraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;

public class TileEntityEnderChest extends TileEntity {
    public float animationProgress;
    public float prevLidAngle;
    public int viewerCount;
    public int ticks;

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (++this.ticks % 20 * 4 == 0) {
            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, Blocks.ender_chest, 1, this.viewerCount);
        }

        this.prevLidAngle = this.animationProgress;
        float f = 0.1F;
        if (this.viewerCount > 0 && this.animationProgress == 0.0F) {
            double d = this.xCoord + 0.5;
            double d2 = this.zCoord + 0.5;
            this.worldObj.playSoundEffect(d, this.yCoord + 0.5, d2, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.viewerCount == 0 && this.animationProgress > 0.0F || this.viewerCount > 0 && this.animationProgress < 1.0F) {
            float animationProgress = this.animationProgress;
            if (this.viewerCount > 0) {
                this.animationProgress += f;
            } else {
                this.animationProgress -= f;
            }

            if (this.animationProgress > 1.0F) {
                this.animationProgress = 1.0F;
            }

            float f2 = 0.5F;
            if (this.animationProgress < f2 && animationProgress >= f2) {
                double d = this.xCoord + 0.5;
                double d2 = this.zCoord + 0.5;
                this.worldObj.playSoundEffect(d, this.yCoord + 0.5, d2, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.animationProgress < 0.0F) {
                this.animationProgress = 0.0F;
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.viewerCount = type;
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public void invalidate() {
        this.updateContainingBlockInfo();
        super.invalidate();
    }

    public void onOpen() {
        this.viewerCount++;
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, Blocks.ender_chest, 1, this.viewerCount);
    }

    public void onClose() {
        this.viewerCount--;
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, Blocks.ender_chest, 1, this.viewerCount);
    }

    public boolean canPlayerUse(EntityPlayer entityPlayer) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && entityPlayer.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }
}
