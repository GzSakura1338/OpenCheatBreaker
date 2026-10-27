package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorldNameable;

public interface IInventory extends IWorldNameable {
   int getSizeInventory();

   ItemStack getStackInSlot(int n);

   ItemStack decrStackSize(int n, int n2);

   ItemStack removeStackFromSlot(int n);

   void setInventorySlotContents(int n, ItemStack itemStack);

   int getInventoryStackLimit();

   void markDirty();

   boolean isUseableByPlayer(EntityPlayer entityPlayer);

   void openInventory(EntityPlayer entityPlayer);

   void closeInventory(EntityPlayer entityPlayer);

   boolean isItemValidForSlot(int n, ItemStack itemStack);

   int getField(int n);

   void setField(int n, int n2);

   int getFieldCount();

   void clear();
}
