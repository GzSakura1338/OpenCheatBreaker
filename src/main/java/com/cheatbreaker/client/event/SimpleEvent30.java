package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class SimpleEvent30 extends EntityPlayerRenderPlayerDoubleFloatEvent {
    public SimpleEvent30(EntityPlayer entityPlayer, RenderPlayer renderPlayer, float f, double d, double d2, double d3) {
        super(entityPlayer, renderPlayer, f, d, d2, d3);
    }

    public SimpleEvent30(EntityPlayer entityPlayer, RenderPlayer renderPlayer, float f) {
        this(entityPlayer, renderPlayer, f, 0.0, 0.0, 0.0);
    }
}
