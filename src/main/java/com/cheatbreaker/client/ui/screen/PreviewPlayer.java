package com.cheatbreaker.client.ui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.world.World;
import net.optifine.CapeUtils;

public class PreviewPlayer extends EntityClientPlayerMP {
    @Override
    public ResourceLocation getLocationSkin() {
        return PlayerViewer.resourceLocation == null ? super.getLocationSkin() : PlayerViewer.resourceLocation;
    }

    @Override
    public boolean getHideCape(int n) {
        return false;
    }

    @Override
    public ResourceLocation getLocationCape() {
        if (PlayerViewer.guiButtonPress != null) {
            return PlayerViewer.guiButtonPress;
        } else if (super.getLocationCape() == null) {
            CapeUtils.handleAbstractClientPlayer(this, this.getUniqueID());
            return super.getLocationOfCape();
        } else {
            return super.getLocationCape();
        }
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return false;
    }

    @Override
    public boolean canAttackPlayer(EntityPlayer entityPlayer) {
        return false;
    }

    public PreviewPlayer(Minecraft mc, World world, Session session, NetHandlerPlayClient netHandlerPlayClient, StatFileWriter statFileWriter) {
        super(mc, world, session, netHandlerPlayClient, statFileWriter);
    }

    @Override
    public ItemStack getCurrentEquippedItem() {
        return null;
    }

    @Override
    public ItemStack getCurrentArmor(int n) {
        return null;
    }

    @Override
    public boolean hasCosmetic() {
        return PlayerViewer.guiButtonPress != null;
    }

    @Override
    public boolean hasCape() {
        return PlayerViewer.guiButtonPress != null || this.getLocationCape() != null || this.getLocationOfCape() != null;
    }
}
