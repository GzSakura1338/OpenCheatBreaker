package net.minecraft.tileentity;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityFlowerPot extends TileEntity {
   public Item flowerPotItem;
   public int flowerPotData;

   public TileEntityFlowerPot() {
   }

   public TileEntityFlowerPot(Item potItem, int potData) {
      this.flowerPotItem = potItem;
      this.flowerPotData = potData;
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      ResourceLocation nameForObject = Item.itemRegistry.getNameForObject(this.flowerPotItem);
      compound.setString("Item", nameForObject == null ? "" : nameForObject.toString());
      compound.setInteger("Data", this.flowerPotData);
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      if (compound.hasKey("Item", 8)) {
         this.flowerPotItem = Item.getByNameOrId(compound.getString("Item"));
      } else {
         this.flowerPotItem = Item.getItemById(compound.getInteger("Item"));
      }

      this.flowerPotData = compound.getInteger("Data");
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      this.writeToNBT(nbttagCompound);
      nbttagCompound.removeTag("Item");
      nbttagCompound.setInteger("Item", Item.getIdFromItem(this.flowerPotItem));
      return new S35PacketUpdateTileEntity(this.pos, 5, nbttagCompound);
   }

   public void setFlowerPotData(Item potItem, int potData) {
      this.flowerPotItem = potItem;
      this.flowerPotData = potData;
   }

   public Item getFlowerPotItem() {
      return this.flowerPotItem;
   }

   public int getFlowerPotData() {
      return this.flowerPotData;
   }
}
