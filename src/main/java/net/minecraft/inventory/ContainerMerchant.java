package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerMerchant extends Container {
   public IMerchant theMerchant;
   public World theWorld;
   public InventoryMerchant merchantInventory;

   public ContainerMerchant(InventoryPlayer playerInventory, IMerchant merchant, World worldIn) {
      this.theMerchant = merchant;
      this.theWorld = worldIn;
      this.merchantInventory = new InventoryMerchant(playerInventory.player, merchant);
      this.addSlotToContainer(new Slot(this.merchantInventory, 0, 36, 53));
      this.addSlotToContainer(new Slot(this.merchantInventory, 1, 62, 53));
      this.addSlotToContainer(new SlotMerchantResult(playerInventory.player, merchant, this.merchantInventory, 2, 120, 53));

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
      }
   }

   public InventoryMerchant getMerchantInventory() {
      return this.merchantInventory;
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
   }

   @Override
   public void detectAndSendChanges() {
      super.detectAndSendChanges();
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.merchantInventory.resetRecipeAndSlots();
      super.onCraftMatrixChanged(inventoryIn);
   }

   public void setCurrentRecipeIndex(int currentRecipeIndex) {
      this.merchantInventory.setCurrentRecipeIndex(currentRecipeIndex);
   }

   @Override
   public void updateProgressBar(int id, int data) {
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.theMerchant.getCustomer() == playerIn;
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
         } else if (index != 0 && index != 1) {
            if (index >= 3 && index < 30) {
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

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      this.theMerchant.setCustomer((EntityPlayer)null);
      super.onContainerClosed(playerIn);
      if (!this.theWorld.isRemote) {
         ItemStack removeStackFromSlot = this.merchantInventory.removeStackFromSlot(0);
         if (removeStackFromSlot != null) {
            playerIn.dropPlayerItemWithRandomChoice(removeStackFromSlot, false);
         }

         removeStackFromSlot = this.merchantInventory.removeStackFromSlot(1);
         if (removeStackFromSlot != null) {
            playerIn.dropPlayerItemWithRandomChoice(removeStackFromSlot, false);
         }
      }
   }
}
