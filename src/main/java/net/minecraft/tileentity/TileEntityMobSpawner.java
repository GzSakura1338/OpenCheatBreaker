package net.minecraft.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity {
    public MobSpawnerBaseLogic behaviorHandler = new MobSpawnerBaseLogic() {
        @Override
        public void addBlockEvent(int n) {
            TileEntityMobSpawner.this.worldObj
                .addBlockEvent(TileEntityMobSpawner.this.xCoord, TileEntityMobSpawner.this.yCoord, TileEntityMobSpawner.this.zCoord, Blocks.mob_spawner, n, 0);
        }

        @Override
        public World getSpawnerWorld() {
            return TileEntityMobSpawner.this.worldObj;
        }

        @Override
        public int getSpawnerX() {
            return TileEntityMobSpawner.this.xCoord;
        }

        @Override
        public int getSpawnerY() {
            return TileEntityMobSpawner.this.yCoord;
        }

        @Override
        public int getSpawnerZ() {
            return TileEntityMobSpawner.this.zCoord;
        }

        @Override
        public void setRandomEntity(WeightedRandomMinecart weightedRandomMinecart) {
            super.setRandomEntity(weightedRandomMinecart);
            if (this.getSpawnerWorld() != null) {
                this.getSpawnerWorld().markBlockForUpdate(TileEntityMobSpawner.this.xCoord, TileEntityMobSpawner.this.yCoord, TileEntityMobSpawner.this.zCoord);
            }
        }
    };

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.behaviorHandler.readFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        this.behaviorHandler.writeToNBT(compound);
    }

    @Override
    public void updateEntity() {
        this.behaviorHandler.updateSpawner();
        super.updateEntity();
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        this.writeToNBT(nbttagCompound);
        nbttagCompound.removeTag("SpawnPotentials");
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbttagCompound);
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        return this.behaviorHandler.setDelayToMin(id) || super.receiveClientEvent(id, type);
    }

    public MobSpawnerBaseLogic getLogic() {
        return this.behaviorHandler;
    }
}
