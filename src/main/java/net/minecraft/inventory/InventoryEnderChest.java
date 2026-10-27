package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;

public class InventoryEnderChest extends InventoryBasic {
   public TileEntityEnderChest associatedChest;

   public InventoryEnderChest() {
      super("container.enderchest", false, 27);
   }

   public void setChestTileEntity(TileEntityEnderChest chestTileEntity) {
      this.associatedChest = chestTileEntity;
   }

   public void loadInventoryFromNBT(NBTTagList nbttagList) {
      for (int i = 0; i < this.getSizeInventory(); i++) {
         this.setInventorySlotContents(i, (ItemStack)null);
      }

      for (int i = 0; i < nbttagList.tagCount(); i++) {
         NBTTagCompound compoundTagAt = nbttagList.getCompoundTagAt(i);
         int n = compoundTagAt.getByte("Slot") & 255;
         if (n >= 0 && n < this.getSizeInventory()) {
            this.setInventorySlotContents(n, ItemStack.loadItemStackFromNBT(compoundTagAt));
         }
      }
   }

   public NBTTagList saveInventoryToNBT() {
      NBTTagList nbttagList = new NBTTagList();

      for (int i = 0; i < this.getSizeInventory(); i++) {
         ItemStack stackInSlot = this.getStackInSlot(i);
         if (stackInSlot != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setByte("Slot", (byte)i);
            stackInSlot.writeToNBT(nbttagCompound);
            nbttagList.appendTag(nbttagCompound);
         }
      }

      return nbttagList;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.associatedChest != null && !this.associatedChest.canBeUsed(player) ? false : super.isUseableByPlayer(player);
   }

   @Override
   public void openInventory(EntityPlayer player) {
      if (this.associatedChest != null) {
         this.associatedChest.openChest();
      }

      super.openInventory(player);
   }

   @Override
   public void closeInventory(EntityPlayer player) {
      if (this.associatedChest != null) {
         this.associatedChest.closeChest();
      }

      super.closeInventory(player);
      this.associatedChest = null;
   }
}
