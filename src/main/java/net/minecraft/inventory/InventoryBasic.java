package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryBasic implements IInventory {
   public String inventoryTitle;
   public boolean hasCustomName;
   public int slotsCount;
   public List<IInvBasic> changeListeners;
   public ItemStack[] inventoryContents;

   public InventoryBasic(String title, boolean customName, int slotCount) {
      this.inventoryTitle = title;
      this.hasCustomName = customName;
      this.slotsCount = slotCount;
      this.inventoryContents = new ItemStack[slotCount];
   }

   public InventoryBasic(IChatComponent title, int slotCount) {
      this(title.getUnformattedText(), true, slotCount);
   }

   public void addInventoryChangeListener(IInvBasic listener) {
      if (this.changeListeners == null) {
         this.changeListeners = Lists.newArrayList();
      }

      this.changeListeners.add(listener);
   }

   public void removeInventoryChangeListener(IInvBasic listener) {
      this.changeListeners.remove(listener);
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.inventoryContents.length ? this.inventoryContents[index] : null;
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.inventoryContents[index] != null) {
         if (this.inventoryContents[index].stackSize <= count) {
            ItemStack itemStack = this.inventoryContents[index];
            this.inventoryContents[index] = null;
            this.markDirty();
            return itemStack;
         }

         ItemStack splitStack = this.inventoryContents[index].splitStack(count);
         if (this.inventoryContents[index].stackSize == 0) {
            this.inventoryContents[index] = null;
         }

         this.markDirty();
         return splitStack;
      } else {
         return null;
      }
   }

   public ItemStack fillInventoryWith(ItemStack stack) {
      ItemStack itemStack = stack.copy();

      for (int i = 0; i < this.slotsCount; i++) {
         ItemStack stackInSlot = this.getStackInSlot(i);
         if (stackInSlot == null) {
            this.setInventorySlotContents(i, itemStack);
            this.markDirty();
            return null;
         }

         if (ItemStack.areItemsEqual(stackInSlot, itemStack)) {
            int min = Math.min(this.getInventoryStackLimit(), stackInSlot.getMaxStackSize());
            int min2 = Math.min(itemStack.stackSize, min - stackInSlot.stackSize);
            if (min2 > 0) {
               stackInSlot.stackSize += min2;
               itemStack.stackSize -= min2;
               if (itemStack.stackSize <= 0) {
                  this.markDirty();
                  return null;
               }
            }
         }
      }

      if (itemStack.stackSize != stack.stackSize) {
         this.markDirty();
      }

      return itemStack;
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.inventoryContents[index] != null) {
         ItemStack itemStack = this.inventoryContents[index];
         this.inventoryContents[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.inventoryContents[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }

      this.markDirty();
   }

   @Override
   public int getSizeInventory() {
      return this.slotsCount;
   }

   @Override
   public String getName() {
      return this.inventoryTitle;
   }

   @Override
   public boolean hasCustomName() {
      return this.hasCustomName;
   }

   public void setCustomName(String inventoryTitleIn) {
      this.hasCustomName = true;
      this.inventoryTitle = inventoryTitleIn;
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public int getInventoryStackLimit() {
      return 64;
   }

   @Override
   public void markDirty() {
      if (this.changeListeners != null) {
         for (int i = 0; i < this.changeListeners.size(); i++) {
            this.changeListeners.get(i).onInventoryChanged(this);
         }
      }
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
      for (int i = 0; i < this.inventoryContents.length; i++) {
         this.inventoryContents[i] = null;
      }
   }
}
