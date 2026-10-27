package net.minecraft.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public interface IInteractionObject extends IWorldNameable {
   Container createContainer(InventoryPlayer inventoryPlayer, EntityPlayer entityPlayer);

   String getGuiID();
}
