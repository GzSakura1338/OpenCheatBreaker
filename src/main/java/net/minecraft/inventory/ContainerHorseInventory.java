package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container {
   public IInventory horseInventory;
   public EntityHorse theHorse;

   public ContainerHorseInventory(IInventory playerInventory, IInventory horseInventoryIn, final EntityHorse horse, EntityPlayer player) {
      this.horseInventory = horseInventoryIn;
      this.theHorse = horse;
      byte b = 3;
      horseInventoryIn.openInventory(player);
      int n = (b - 4) * 18;
      this.addSlotToContainer(new Slot(horseInventoryIn, 0, 8, 18) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return super.isItemValid(stack) && stack.getItem() == Items.saddle && !this.getHasStack();
         }
      });
      this.addSlotToContainer(new Slot(horseInventoryIn, 1, 8, 36) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return super.isItemValid(stack) && horse.canWearArmor() && EntityHorse.isArmorItem(stack.getItem());
         }

         @Override
         public boolean canBeHovered() {
            return horse.canWearArmor();
         }
      });
      if (horse.isChested()) {
         for (int i = 0; i < b; i++) {
            for (int j = 0; j < 5; j++) {
               this.addSlotToContainer(new Slot(horseInventoryIn, 2 + j + i * 5, 80 + j * 18, 18 + i * 18));
            }
         }
      }

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18 + n));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 160 + n));
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.horseInventory.isUseableByPlayer(playerIn) && this.theHorse.isEntityAlive() && this.theHorse.getDistanceToEntity(playerIn) < 8.0F;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         itemStack = stack.copy();
         if (index < this.horseInventory.getSizeInventory()) {
            if (!this.mergeItemStack(stack, this.horseInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
               return null;
            }
         } else if (this.getSlot(1).isItemValid(stack) && !this.getSlot(1).getHasStack()) {
            if (!this.mergeItemStack(stack, 1, 2, false)) {
               return null;
            }
         } else if (this.getSlot(0).isItemValid(stack)) {
            if (!this.mergeItemStack(stack, 0, 1, false)) {
               return null;
            }
         } else if (this.horseInventory.getSizeInventory() <= 2 || !this.mergeItemStack(stack, 2, this.horseInventory.getSizeInventory(), false)) {
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
      this.horseInventory.closeInventory(playerIn);
   }
}
