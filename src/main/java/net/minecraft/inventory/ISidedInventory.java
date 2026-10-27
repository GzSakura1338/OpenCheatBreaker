package net.minecraft.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface ISidedInventory extends IInventory {
   int[] getSlotsForFace(EnumFacing enumFacing);

   boolean canInsertItem(int n, ItemStack itemStack, EnumFacing enumFacing);

   boolean canExtractItem(int n, ItemStack itemStack, EnumFacing enumFacing);
}
