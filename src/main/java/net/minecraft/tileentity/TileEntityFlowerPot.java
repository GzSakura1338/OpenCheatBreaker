package net.minecraft.tileentity;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityFlowerPot extends TileEntity {
    public Item flowerPotItem;
    public int flowerPotData;

    public TileEntityFlowerPot() {
    }

    public TileEntityFlowerPot(Item item, int n) {
        this.flowerPotItem = item;
        this.flowerPotData = n;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Item", Item.getIdFromItem(this.flowerPotItem));
        compound.setInteger("Data", this.flowerPotData);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.flowerPotItem = Item.getItemById(compound.getInteger("Item"));
        this.flowerPotData = compound.getInteger("Data");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        this.writeToNBT(nbttagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 5, nbttagCompound);
    }

    public void setFlower(Item item, int n) {
        this.flowerPotItem = item;
        this.flowerPotData = n;
    }

    public Item getFlowerPotItem() {
        return this.flowerPotItem;
    }

    public int getFlowerPotData() {
        return this.flowerPotData;
    }
}
