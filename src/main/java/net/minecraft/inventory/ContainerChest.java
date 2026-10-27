package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container {
   public IInventory lowerChestInventory;
   public int numRows;

   public ContainerChest(IInventory playerInventory, IInventory chestInventory, EntityPlayer player) {
      this.lowerChestInventory = chestInventory;
      this.numRows = chestInventory.getSizeInventory() / 9;
      chestInventory.openInventory(player);
      int n = (this.numRows - 4) * 18;

      for (int i = 0; i < this.numRows; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(chestInventory, j + i * 9, 8 + j * 18, 18 + i * 18));
         }
      }

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18 + n));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 161 + n));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.lowerChestInventory.isUseableByPlayer(playerIn);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         itemStack = stack.copy();
         if (index < this.numRows * 9) {
            if (!this.mergeItemStack(stack, this.numRows * 9, this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (!this.mergeItemStack(stack, 0, this.numRows * 9, false)) {
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
      this.lowerChestInventory.closeInventory(playerIn);
   }

   public IInventory getLowerChestInventory() {
      return this.lowerChestInventory;
   }
}
