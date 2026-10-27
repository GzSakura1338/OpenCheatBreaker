package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryCrafting implements IInventory {
   public ItemStack[] stackList;
   public Container eventHandler;
   public int inventoryWidth;
   public int inventoryHeight;

   public InventoryCrafting(Container eventHandlerIn, int width, int height) {
      int n = width * height;
      this.stackList = new ItemStack[n];
      this.eventHandler = eventHandlerIn;
      this.inventoryWidth = width;
      this.inventoryHeight = height;
   }

   @Override
   public int getSizeInventory() {
      return this.stackList.length;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return index >= this.getSizeInventory() ? null : this.stackList[index];
   }

   public ItemStack getStackInRowAndColumn(int row, int column) {
      return row >= 0 && row < this.inventoryWidth && column >= 0 && column <= this.inventoryHeight
         ? this.getStackInSlot(row + column * this.inventoryWidth)
         : null;
   }

   @Override
   public String getName() {
      return "container.crafting";
   }

   @Override
   public boolean hasCustomName() {
      return false;
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.stackList[index] != null) {
         ItemStack itemStack = this.stackList[index];
         this.stackList[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.stackList[index] != null) {
         if (this.stackList[index].stackSize <= count) {
            ItemStack itemStack = this.stackList[index];
            this.stackList[index] = null;
            this.eventHandler.onCraftMatrixChanged(this);
            return itemStack;
         }

         ItemStack splitStack = this.stackList[index].splitStack(count);
         if (this.stackList[index].stackSize == 0) {
            this.stackList[index] = null;
         }

         this.eventHandler.onCraftMatrixChanged(this);
         return splitStack;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.stackList[index] = stack;
      this.eventHandler.onCraftMatrixChanged(this);
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public void markDirty() {
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return true;
   }

   @Override
   public void openInventory(EntityPlayer player) {
   }

   @Override
   public void closeInventory(EntityPlayer player) {
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.stackList.length; i++) {
         this.stackList[i] = null;
      }
   }

   public int getHeight() {
      return this.inventoryHeight;
   }

   public int getWidth() {
      return this.inventoryWidth;
   }
}
