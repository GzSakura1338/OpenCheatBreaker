package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IInventory {
    int getSizeInventory();

    ItemStack getStackInSlot(int n);

    ItemStack decrStackSize(int n, int n2);

    ItemStack getStackInSlotOnClosing(int n);

    void setInventorySlotContents(int n, ItemStack itemStack);

    String getInventoryName();

    boolean isCustomInventoryName();

    int getInventoryStackLimit();

    void markDirty();

    boolean isUseableByPlayer(EntityPlayer entityPlayer);

    void openChest();

    void closeChest();

    boolean isItemValidForSlot(int n, ItemStack itemStack);
}
