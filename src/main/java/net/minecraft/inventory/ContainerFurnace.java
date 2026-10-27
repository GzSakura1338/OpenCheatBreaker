package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerFurnace extends Container {
   public IInventory tileFurnace;
   public int cookTime;
   public int totalCookTime;
   public int furnaceBurnTime;
   public int currentItemBurnTime;

   public ContainerFurnace(InventoryPlayer playerInventory, IInventory furnaceInventory) {
      this.tileFurnace = furnaceInventory;
      this.addSlotToContainer(new Slot(furnaceInventory, 0, 56, 17));
      this.addSlotToContainer(new SlotFurnaceFuel(furnaceInventory, 1, 56, 53));
      this.addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, furnaceInventory, 2, 116, 35));

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendAllWindowProperties(this, this.tileFurnace);
   }

   @Override
   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for (int i = 0; i < this.crafters.size(); i++) {
         ICrafting crafting = this.crafters.get(i);
         if (this.cookTime != this.tileFurnace.getField(2)) {
            crafting.sendProgressBarUpdate(this, 2, this.tileFurnace.getField(2));
         }

         if (this.furnaceBurnTime != this.tileFurnace.getField(0)) {
            crafting.sendProgressBarUpdate(this, 0, this.tileFurnace.getField(0));
         }

         if (this.currentItemBurnTime != this.tileFurnace.getField(1)) {
            crafting.sendProgressBarUpdate(this, 1, this.tileFurnace.getField(1));
         }

         if (this.totalCookTime != this.tileFurnace.getField(3)) {
            crafting.sendProgressBarUpdate(this, 3, this.tileFurnace.getField(3));
         }
      }

      this.cookTime = this.tileFurnace.getField(2);
      this.furnaceBurnTime = this.tileFurnace.getField(0);
      this.currentItemBurnTime = this.tileFurnace.getField(1);
      this.totalCookTime = this.tileFurnace.getField(3);
   }

   @Override
   public void updateProgressBar(int id, int data) {
      this.tileFurnace.setField(id, data);
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.tileFurnace.isUseableByPlayer(playerIn);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         itemStack = stack.copy();
         if (index == 2) {
            if (!this.mergeItemStack(stack, 3, 39, true)) {
               return null;
            }

            slot.onSlotChange(stack, itemStack);
         } else if (index != 1 && index != 0) {
            if (FurnaceRecipes.instance().getSmeltingResult(stack) != null) {
               if (!this.mergeItemStack(stack, 0, 1, false)) {
                  return null;
               }
            } else if (TileEntityFurnace.isItemFuel(stack)) {
               if (!this.mergeItemStack(stack, 1, 2, false)) {
                  return null;
               }
            } else if (index >= 3 && index < 30) {
               if (!this.mergeItemStack(stack, 30, 39, false)) {
                  return null;
               }
            } else if (index >= 30 && index < 39 && !this.mergeItemStack(stack, 3, 30, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(stack, 3, 39, false)) {
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
}
