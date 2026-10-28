package net.minecraft.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryBasic implements IInventory {
    public String inventoryTitle;
    public boolean hasCustomName;
    public int slotsCount;
    public List listeners;
    public ItemStack[] inventoryContents;

    public InventoryBasic(String str, boolean flag, int n) {
        this.inventoryTitle = str;
        this.hasCustomName = flag;
        this.slotsCount = n;
        this.inventoryContents = new ItemStack[n];
    }

    public void addListener(IInvBasic invBasic) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }

        this.listeners.add(invBasic);
    }

    public void removeListener(IInvBasic invBasic) {
        this.listeners.remove(invBasic);
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return slotIn >= 0 && slotIn < this.inventoryContents.length ? this.inventoryContents[slotIn] : null;
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

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
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
    public String getInventoryName() {
        return this.inventoryTitle;
    }

    @Override
    public boolean isCustomInventoryName() {
        return this.hasCustomName;
    }

    public void setName(String str) {
        this.hasCustomName = true;
        this.inventoryTitle = str;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        if (this.listeners != null) {
            for (int i = 0; i < this.listeners.size(); i++) {
                ((IInvBasic)this.listeners.get(i)).onInventoryChanged(this);
            }
        }
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
