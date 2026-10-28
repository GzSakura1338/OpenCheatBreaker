package com.cheatbreaker.client.event;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class BooleanEvent extends PlayerRenderEventBase {
    public boolean flag = true;
    public boolean flag2 = true;
    public boolean flag3 = true;

    public BooleanEvent(EntityPlayer entityPlayer, RenderPlayer renderPlayer, float f) {
        super(entityPlayer, renderPlayer, f);
    }
}
