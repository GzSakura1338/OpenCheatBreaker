package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerPlayer extends Container {
   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
   public IInventory craftResult = new InventoryCraftResult();
   public boolean isLocalWorld;
   public EntityPlayer thePlayer;

   public ContainerPlayer(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player) {
      this.isLocalWorld = localWorld;
      this.thePlayer = player;
      this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 144, 36));

      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 2; j++) {
            this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 88 + j * 18, 26 + i * 18));
         }
      }

      for (int i = 0; i < 4; i++) {
         final int n = i;
         this.addSlotToContainer(
            new Slot(playerInventory, playerInventory.getSizeInventory() - 1 - i, 8, 8 + i * 18) {
               @Override
               public int getSlotStackLimit() {
                  return 1;
               }

               @Override
               public boolean isItemValid(ItemStack stack) {
                  return stack == null
                     ? false
                     : (
                        stack.getItem() instanceof ItemArmor
                           ? ((ItemArmor)stack.getItem()).armorType == n
                           : (stack.getItem() != Item.getItemFromBlock(Blocks.pumpkin) && stack.getItem() != Items.skull ? false : n == 0)
                     );
               }

               @Override
               public String getSlotTexture() {
                  return ItemArmor.EMPTY_SLOT_NAMES[n];
               }
            }
         );
      }

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
      }

      this.onCraftMatrixChanged(this.craftMatrix);
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);

      for (int i = 0; i < 4; i++) {
         ItemStack removeStackFromSlot = this.craftMatrix.removeStackFromSlot(i);
         if (removeStackFromSlot != null) {
            playerIn.dropPlayerItemWithRandomChoice(removeStackFromSlot, false);
         }
      }

      this.craftResult.setInventorySlotContents(0, (ItemStack)null);
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return true;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         itemStack = stack.copy();
         if (index == 0) {
            if (!this.mergeItemStack(stack, 9, 45, true)) {
               return null;
            }

            slot.onSlotChange(stack, itemStack);
         } else if (index >= 1 && index < 5) {
            if (!this.mergeItemStack(stack, 9, 45, false)) {
               return null;
            }
         } else if (index >= 5 && index < 9) {
            if (!this.mergeItemStack(stack, 9, 45, false)) {
               return null;
            }
         } else if (itemStack.getItem() instanceof ItemArmor && !this.inventorySlots.get(5 + ((ItemArmor)itemStack.getItem()).armorType).getHasStack()) {
            int n = 5 + ((ItemArmor)itemStack.getItem()).armorType;
            if (!this.mergeItemStack(stack, n, n + 1, false)) {
               return null;
            }
         } else if (index >= 9 && index < 36) {
            if (!this.mergeItemStack(stack, 36, 45, false)) {
               return null;
            }
         } else if (index >= 36 && index < 45) {
            if (!this.mergeItemStack(stack, 9, 36, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(stack, 9, 45, false)) {
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
   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
   }
}
