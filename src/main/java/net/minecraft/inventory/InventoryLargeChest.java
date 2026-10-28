package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryLargeChest implements IInventory {
    public String name;
    public IInventory upperChest;
    public IInventory lowerChest;

    public InventoryLargeChest(String str, IInventory inventory, IInventory inventory2) {
        this.name = str;
        if (inventory == null) {
            inventory = inventory2;
        }

        if (inventory2 == null) {
            inventory2 = inventory;
        }

        this.upperChest = inventory;
        this.lowerChest = inventory2;
    }

    @Override
    public int getSizeInventory() {
        return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
    }

    public boolean isPartOfLargeChest(IInventory inventory) {
        return this.upperChest == inventory || this.lowerChest == inventory;
    }

    @Override
    public String getInventoryName() {
        return this.upperChest.isCustomInventoryName()
            ? this.upperChest.getInventoryName()
            : (this.lowerChest.isCustomInventoryName() ? this.lowerChest.getInventoryName() : this.name);
    }

    @Override
    public boolean isCustomInventoryName() {
        return this.upperChest.isCustomInventoryName() || this.lowerChest.isCustomInventoryName();
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return slotIn >= this.upperChest.getSizeInventory()
            ? this.lowerChest.getStackInSlot(slotIn - this.upperChest.getSizeInventory())
            : this.upperChest.getStackInSlot(slotIn);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return index >= this.upperChest.getSizeInventory()
            ? this.lowerChest.decrStackSize(index - this.upperChest.getSizeInventory(), count)
            : this.upperChest.decrStackSize(index, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return index >= this.upperChest.getSizeInventory()
            ? this.lowerChest.getStackInSlotOnClosing(index - this.upperChest.getSizeInventory())
            : this.upperChest.getStackInSlotOnClosing(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index >= this.upperChest.getSizeInventory()) {
            this.lowerChest.setInventorySlotContents(index - this.upperChest.getSizeInventory(), stack);
        } else {
            this.upperChest.setInventorySlotContents(index, stack);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return this.upperChest.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
        this.upperChest.markDirty();
        this.lowerChest.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.upperChest.isUseableByPlayer(player) && this.lowerChest.isUseableByPlayer(player);
    }

    @Override
    public void openChest() {
        this.upperChest.openChest();
        this.lowerChest.openChest();
    }

    @Override
    public void closeChest() {
        this.upperChest.closeChest();
        this.lowerChest.closeChest();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }
}
