package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class Slot {
    public IInventory inventory;
    public int slotIndex;
    public int slotNumber;
    public int xDisplayPosition;
    public int yDisplayPosition;

    public Slot(IInventory inventory, int n, int n2, int n3) {
        this.inventory = inventory;
        this.slotIndex = n;
        this.xDisplayPosition = n2;
        this.yDisplayPosition = n3;
    }

    public void onSlotChange(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack != null && itemStack2 != null && itemStack.getItem() == itemStack2.getItem()) {
            int n = itemStack2.stackSize - itemStack.stackSize;
            if (n > 0) {
                this.onCrafting(itemStack, n);
            }
        }
    }

    public void onCrafting(ItemStack itemStack, int n) {
    }

    public void onCrafting(ItemStack itemStack) {
    }

    public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack itemStack) {
        this.onSlotChanged();
    }

    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    public ItemStack getStack() {
        return this.inventory.getStackInSlot(this.slotIndex);
    }

    public boolean getHasStack() {
        return this.getStack() != null;
    }

    public void putStack(ItemStack itemStack) {
        this.inventory.setInventorySlotContents(this.slotIndex, itemStack);
        this.onSlotChanged();
    }

    public void onSlotChanged() {
        this.inventory.markDirty();
    }

    public int getSlotStackLimit() {
        return this.inventory.getInventoryStackLimit();
    }

    public IIcon getBackgroundIconIndex() {
        return null;
    }

    public ItemStack decrStackSize(int n) {
        return this.inventory.decrStackSize(this.slotIndex, n);
    }

    public boolean isHere(IInventory inventory, int n) {
        return inventory == this.inventory && n == this.slotIndex;
    }

    public boolean canTakeStack(EntityPlayer entityPlayer) {
        return true;
    }

    public boolean canBeHovered() {
        return true;
    }
}
