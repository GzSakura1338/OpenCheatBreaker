package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryCraftResult implements IInventory {
    public ItemStack[] stackResult = new ItemStack[1];

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return this.stackResult[0];
    }

    @Override
    public String getInventoryName() {
        return "Result";
    }

    @Override
    public boolean isCustomInventoryName() {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.stackResult[0] != null) {
            ItemStack itemStack = this.stackResult[0];
            this.stackResult[0] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.stackResult[0] != null) {
            ItemStack itemStack = this.stackResult[0];
            this.stackResult[0] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.stackResult[0] = stack;
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
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }
}
