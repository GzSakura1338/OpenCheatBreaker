package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface ICrafting {
   void updateCraftingInventory(Container container, List<ItemStack> list);

   void sendSlotContents(Container container, int n, ItemStack itemStack);

   void sendProgressBarUpdate(Container container, int n, int n2);

   void sendAllWindowProperties(Container container, IInventory inventory);
}
