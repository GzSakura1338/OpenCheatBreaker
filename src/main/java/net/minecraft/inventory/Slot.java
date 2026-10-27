package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Slot {
   public IInventory inventory;
   public int slotIndex;
   public int slotNumber;
   public int xDisplayPosition;
   public int yDisplayPosition;

   public Slot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      this.inventory = inventoryIn;
      this.slotIndex = index;
      this.xDisplayPosition = xPosition;
      this.yDisplayPosition = yPosition;
   }

   public void onSlotChange(ItemStack itemStack, ItemStack itemStack2) {
      if (itemStack != null && itemStack2 != null && itemStack.getItem() == itemStack2.getItem()) {
         int n = itemStack2.stackSize - itemStack.stackSize;
         if (n > 0) {
            this.onCrafting(itemStack, n);
         }
      }
   }

   public void onCrafting(ItemStack stack, int amount) {
   }

   public void onCrafting(ItemStack stack) {
   }

   public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
      this.onSlotChanged();
   }

   public boolean isItemValid(ItemStack stack) {
      return true;
   }

   public ItemStack getStack() {
      return this.inventory.getStackInSlot(this.slotIndex);
   }

   public boolean getHasStack() {
      return this.getStack() != null;
   }

   public void putStack(ItemStack stack) {
      this.inventory.setInventorySlotContents(this.slotIndex, stack);
      this.onSlotChanged();
   }

   public void onSlotChanged() {
      this.inventory.markDirty();
   }

   public int getSlotStackLimit() {
      return this.inventory.getInventoryStackLimit();
   }

   public int getItemStackLimit(ItemStack stack) {
      return this.getSlotStackLimit();
   }

   public String getSlotTexture() {
      return null;
   }

   public ItemStack decrStackSize(int amount) {
      return this.inventory.decrStackSize(this.slotIndex, amount);
   }

   public boolean isHere(IInventory inv, int slotIn) {
      return inv == this.inventory && slotIn == this.slotIndex;
   }

   public boolean canTakeStack(EntityPlayer playerIn) {
      return true;
   }

   public boolean canBeHovered() {
      return true;
   }
}
