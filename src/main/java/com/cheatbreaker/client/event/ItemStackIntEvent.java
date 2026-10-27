package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemStackIntEvent extends EntityPlayerRenderPlayerDoubleFloatEvent {
   public int intValue2 = -1;
   public int intValue;
   public ItemStack itemStack;

   public ItemStackIntEvent(EntityPlayer entityPlayer, RenderPlayer renderPlayer, int n, float f, ItemStack itemStack) {
      super(entityPlayer, renderPlayer, f, 0.0, 0.0, 0.0);
      this.intValue = n;
      this.itemStack = itemStack;
   }
}
