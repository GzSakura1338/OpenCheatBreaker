package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class ContainerBrewingStand extends Container {
   public IInventory tileBrewingStand;
   public Slot theSlot;
   public int brewTime;

   public ContainerBrewingStand(InventoryPlayer playerInventory, IInventory tileBrewingStandIn) {
      this.tileBrewingStand = tileBrewingStandIn;
      this.addSlotToContainer(new ContainerBrewingStand.Potion(playerInventory.player, tileBrewingStandIn, 0, 56, 46));
      this.addSlotToContainer(new ContainerBrewingStand.Potion(playerInventory.player, tileBrewingStandIn, 1, 79, 53));
      this.addSlotToContainer(new ContainerBrewingStand.Potion(playerInventory.player, tileBrewingStandIn, 2, 102, 46));
      this.theSlot = this.addSlotToContainer(new ContainerBrewingStand.Ingredient(tileBrewingStandIn, 3, 79, 17));

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
      listener.sendAllWindowProperties(this, this.tileBrewingStand);
   }

   @Override
   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for (int i = 0; i < this.crafters.size(); i++) {
         ICrafting crafting = this.crafters.get(i);
         if (this.brewTime != this.tileBrewingStand.getField(0)) {
            crafting.sendProgressBarUpdate(this, 0, this.tileBrewingStand.getField(0));
         }
      }

      this.brewTime = this.tileBrewingStand.getField(0);
   }

   @Override
   public void updateProgressBar(int id, int data) {
      this.tileBrewingStand.setField(id, data);
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.tileBrewingStand.isUseableByPlayer(playerIn);
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         itemStack = stack.copy();
         if ((index < 0 || index > 2) && index != 3) {
            if (!this.theSlot.getHasStack() && this.theSlot.isItemValid(stack)) {
               if (!this.mergeItemStack(stack, 3, 4, false)) {
                  return null;
               }
            } else if (ContainerBrewingStand.Potion.canHoldPotion(itemStack)) {
               if (!this.mergeItemStack(stack, 0, 3, false)) {
                  return null;
               }
            } else if (index >= 4 && index < 31) {
               if (!this.mergeItemStack(stack, 31, 40, false)) {
                  return null;
               }
            } else if (index >= 31 && index < 40) {
               if (!this.mergeItemStack(stack, 4, 31, false)) {
                  return null;
               }
            } else if (!this.mergeItemStack(stack, 4, 40, false)) {
               return null;
            }
         } else {
            if (!this.mergeItemStack(stack, 4, 40, true)) {
               return null;
            }

            slot.onSlotChange(stack, itemStack);
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

   class Ingredient extends Slot {
      public Ingredient(IInventory inventoryIn, int index, int xPosition, int yPosition) {
         super(inventoryIn, index, xPosition, yPosition);
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
         return stack != null ? stack.getItem().isPotionIngredient(stack) : false;
      }

      @Override
      public int getSlotStackLimit() {
         return 64;
      }
   }

   static class Potion extends Slot {
      public EntityPlayer player;

      public Potion(EntityPlayer playerIn, IInventory inventoryIn, int index, int xPosition, int yPosition) {
         super(inventoryIn, index, xPosition, yPosition);
         this.player = playerIn;
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
         return canHoldPotion(stack);
      }

      @Override
      public int getSlotStackLimit() {
         return 1;
      }

      @Override
      public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
         if (stack.getItem() == Items.potionitem && stack.getMetadata() > 0) {
            this.player.triggerAchievement(AchievementList.potion);
         }

         super.onPickupFromSlot(playerIn, stack);
      }

      public static boolean canHoldPotion(ItemStack stack) {
         return stack != null && (stack.getItem() == Items.potionitem || stack.getItem() == Items.glass_bottle);
      }
   }
}
