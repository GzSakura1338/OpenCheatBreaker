package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryCrafting implements IInventory {
    public ItemStack[] stackList;
    public Container eventHandler;
    public int inventoryWidth;

    public InventoryCrafting(Container container, int n, int n2) {
        int n3 = n * n2;
        this.stackList = new ItemStack[n3];
        this.eventHandler = container;
        this.inventoryWidth = n;
    }

    @Override
    public int getSizeInventory() {
        return this.stackList.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return slotIn >= this.getSizeInventory() ? null : this.stackList[slotIn];
    }

    public ItemStack getStackInRowAndColumn(int n, int n2) {
        if (n >= 0 && n < this.inventoryWidth) {
            int n3 = n + n2 * this.inventoryWidth;
            return this.getStackInSlot(n3);
        } else {
            return null;
        }
    }

    @Override
    public String getInventoryName() {
        return "container.crafting";
    }

    @Override
    public boolean isCustomInventoryName() {
        return false;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
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
