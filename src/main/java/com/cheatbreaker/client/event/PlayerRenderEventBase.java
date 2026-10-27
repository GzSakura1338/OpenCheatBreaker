package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

public abstract class PlayerRenderEventBase extends EntityPlayerRenderPlayerDoubleFloatEvent {
   public PlayerRenderEventBase(EntityPlayer entityPlayer, RenderPlayer renderPlayer, float f) {
      super(entityPlayer, renderPlayer, f, 0.0, 0.0, 0.0);
   }
}
