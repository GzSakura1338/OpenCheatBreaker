package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerBeacon extends Container {
   public IInventory tileBeacon;
   public ContainerBeacon.BeaconSlot beaconSlot;

   public ContainerBeacon(IInventory playerInventory, IInventory tileBeaconIn) {
      this.tileBeacon = tileBeaconIn;
      this.addSlotToContainer(this.beaconSlot = new ContainerBeacon.BeaconSlot(tileBeaconIn, 0, 136, 110));
      byte b = 36;
      short s = 137;

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, b + j * 18, s + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlotToContainer(new Slot(playerInventory, i, b + i * 18, 58 + s));
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendAllWindowProperties(this, this.tileBeacon);
   }

   @Override
   public void updateProgressBar(int id, int data) {
      this.tileBeacon.setField(id, data);
   }

   public IInventory getPaymentInventory() {
      return this.tileBeacon;
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (playerIn != null && !playerIn.worldObj.isRemote) {
         ItemStack decrStackSize = this.beaconSlot.decrStackSize(this.beaconSlot.getSlotStackLimit());
         if (decrStackSize != null) {
            playerIn.dropPlayerItemWithRandomChoice(decrStackSize, false);
         }
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.tileBeacon.isUseableByPlayer(playerIn);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         itemStack = stack.copy();
         if (index == 0) {
            if (!this.mergeItemStack(stack, 1, 37, true)) {
               return null;
            }

            slot.onSlotChange(stack, itemStack);
         } else if (!this.beaconSlot.getHasStack() && this.beaconSlot.isItemValid(stack) && stack.stackSize == 1) {
            if (!this.mergeItemStack(stack, 0, 1, false)) {
               return null;
            }
         } else if (index >= 1 && index < 28) {
            if (!this.mergeItemStack(stack, 28, 37, false)) {
               return null;
            }
         } else if (index >= 28 && index < 37) {
            if (!this.mergeItemStack(stack, 1, 28, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(stack, 1, 37, false)) {
            return null;
         }

         if (stack.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }

         if (stack.stackSize == itemStack.stackSize) {
            return null;
         }

         slot.onPickupFromSlot(playerIn, stack);
      }

      return itemStack;
   }

   class BeaconSlot extends Slot {
      public BeaconSlot(IInventory inventory, int n, int n2, int n3) {
         super(inventory, n, n2, n3);
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
         return stack == null
            ? false
            : stack.getItem() == Items.emerald
               || stack.getItem() == Items.diamond
               || stack.getItem() == Items.gold_ingot
               || stack.getItem() == Items.iron_ingot;
      }

      @Override
      public int getSlotStackLimit() {
         return 1;
      }
   }
}
