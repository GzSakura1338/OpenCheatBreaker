package net.minecraft.client.gui.inventory;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;

public class CreativeCrafting implements ICrafting {
    public Minecraft mc;

    public CreativeCrafting(Minecraft mc) {
        this.mc = mc;
    }

    @Override
    public void updateCraftingInventory(Container container, List list) {
    }

    @Override
    public void sendSlotContents(Container container, int n, ItemStack itemStack) {
        this.mc.playerController.sendSlotPacket(itemStack, n);
    }

    @Override
    public void sendProgressBarUpdate(Container container, int n, int n2) {
    }
}
