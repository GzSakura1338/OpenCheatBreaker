package net.minecraft.client.gui.inventory;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class CreativeCrafting implements ICrafting {
   public Minecraft mc;

   public CreativeCrafting(Minecraft mc) {
      this.mc = mc;
   }

   @Override
   public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList) {
   }

   @Override
   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      this.mc.playerController.sendSlotPacket(stack, slotInd);
   }

   @Override
   public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue) {
   }

   @Override
   public void sendAllWindowProperties(Container container, IInventory inventory) {
   }
}
