package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerHopper extends Container {
   public IInventory hopperInventory;

   public ContainerHopper(InventoryPlayer playerInventory, IInventory hopperInventoryIn, EntityPlayer player) {
      this.hopperInventory = hopperInventoryIn;
      hopperInventoryIn.openInventory(player);
      byte b = 51;

      for (int i = 0; i < hopperInventoryIn.getSizeInventory(); i++) {
         this.addSlotToContainer(new Slot(hopperInventoryIn, i, 44 + i * 18, 20));
      }

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, i * 18 + b));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 58 + b));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.hopperInventory.isUseableByPlayer(playerIn);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         itemStack = stack.copy();
         if (index < this.hopperInventory.getSizeInventory()) {
            if (!this.mergeItemStack(stack, this.hopperInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (!this.mergeItemStack(stack, 0, this.hopperInventory.getSizeInventory(), false)) {
            return null;
         }

         if (stack.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemStack;
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      this.hopperInventory.closeInventory(playerIn);
   }
}
