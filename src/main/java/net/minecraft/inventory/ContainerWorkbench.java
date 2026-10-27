package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerWorkbench extends Container {
   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
   public IInventory craftResult = new InventoryCraftResult();
   public World worldObj;
   public BlockPos pos;

   public ContainerWorkbench(InventoryPlayer playerInventory, World worldIn, BlockPos posIn) {
      this.worldObj = worldIn;
      this.pos = posIn;
      this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
         }
      }

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
      }

      this.onCraftMatrixChanged(this.craftMatrix);
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (!this.worldObj.isRemote) {
         for (int i = 0; i < 9; i++) {
            ItemStack removeStackFromSlot = this.craftMatrix.removeStackFromSlot(i);
            if (removeStackFromSlot != null) {
               playerIn.dropPlayerItemWithRandomChoice(removeStackFromSlot, false);
            }
         }
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.worldObj.getBlockState(this.pos).getBlock() != Blocks.crafting_table
         ? false
         : playerIn.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         itemStack = stack.copy();
         if (index == 0) {
            if (!this.mergeItemStack(stack, 10, 46, true)) {
               return null;
            }

            slot.onSlotChange(stack, itemStack);
         } else if (index >= 10 && index < 37) {
            if (!this.mergeItemStack(stack, 37, 46, false)) {
               return null;
            }
         } else if (index >= 37 && index < 46) {
            if (!this.mergeItemStack(stack, 10, 37, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(stack, 10, 46, false)) {
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
