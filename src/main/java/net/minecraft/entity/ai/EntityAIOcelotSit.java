package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public class EntityAIOcelotSit extends EntityAIBase {
    public EntityOcelot ocelot;
    public double doubleValue;
    public int intValue;
    public int intValue2;
    public int intValue3;
    public int intValue4;
    public int intValue5;
    public int intValue6;

    public EntityAIOcelotSit(EntityOcelot entityOcelot, double d) {
        this.ocelot = entityOcelot;
        this.doubleValue = d;
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        return this.ocelot.isTamed() && !this.ocelot.isSitting() && this.ocelot.getRNG().nextDouble() <= 0.0065F && this.check();
    }

    @Override
    public boolean continueExecuting() {
        return this.intValue <= this.intValue3 && this.intValue2 <= 60 && this.checkWorld(this.ocelot.worldObj, this.intValue4, this.intValue5, this.intValue6);
    }

    @Override
    public void startExecuting() {
        this.ocelot.getNavigator().tryMoveToXYZ((float)this.intValue4 + 0.5, this.intValue5 + 1, (float)this.intValue6 + 0.5, this.doubleValue);
        this.intValue = 0;
        this.intValue2 = 0;
        this.intValue3 = this.ocelot.getRNG().nextInt(this.ocelot.getRNG().nextInt(1200) + 1200) + 1200;
        this.ocelot.getAISit().setSitting(false);
    }

    @Override
    public void resetTask() {
        this.ocelot.setSitting(false);
    }

    @Override
    public void updateTask() {
        this.intValue++;
        this.ocelot.getAISit().setSitting(false);
        if (this.ocelot.getDistanceSq(this.intValue4, this.intValue5 + 1, this.intValue6) > 1.0) {
            this.ocelot.setSitting(false);
            this.ocelot.getNavigator().tryMoveToXYZ((float)this.intValue4 + 0.5, this.intValue5 + 1, (float)this.intValue6 + 0.5, this.doubleValue);
            this.intValue2++;
        } else if (!this.ocelot.isSitting()) {
            this.ocelot.setSitting(true);
        } else {
            this.intValue2--;
        }
    }

    public boolean check() {
        int posY = (int)this.ocelot.posY;
        double d = 2.147483647E9;

        for (int i = (int)this.ocelot.posX - 8; i < this.ocelot.posX + 8.0; i++) {
            for (int j = (int)this.ocelot.posZ - 8; j < this.ocelot.posZ + 8.0; j++) {
                if (this.checkWorld(this.ocelot.worldObj, i, posY, j) && this.ocelot.worldObj.isAirBlock(i, posY + 1, j)) {
                    double distanceSq = this.ocelot.getDistanceSq(i, posY, j);
                    if (distanceSq < d) {
                        this.intValue4 = i;
                        this.intValue5 = posY;
                        this.intValue6 = j;
                        d = distanceSq;
                    }
                }
            }
        }

        return d < 2.147483647E9;
    }

    public boolean checkWorld(World world, int n, int n2, int n3) {
        Block block = world.getBlock(n, n2, n3);
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        if (block == Blocks.chest) {
            TileEntityChest tileEntity = (TileEntityChest)world.getTileEntity(n, n2, n3);
            return tileEntity.numPlayersUsing < 1;
        } else {
            return block == Blocks.lit_furnace ? true : block == Blocks.bed && !BlockBed.isBlockHeadOfBed(blockMetadata);
        }
    }
}
