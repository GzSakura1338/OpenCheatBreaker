package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

public interface ISidedInventory extends IInventory {
    int[] getSlotsForFace(int n);

    boolean canInsertItem(int n, ItemStack itemStack, int n2);

    boolean canExtractItem(int n, ItemStack itemStack, int n2);
}
