package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class SimpleEvent22 extends PlayerRenderEventBase {
    public SimpleEvent22(EntityPlayer entityPlayer, RenderPlayer renderPlayer, float f) {
        super(entityPlayer, renderPlayer, f);
    }
}
