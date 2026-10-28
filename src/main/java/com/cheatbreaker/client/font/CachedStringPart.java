package com.cheatbreaker.client.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CachedStringPart {
    public static TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
    public int displayList;
    public ResourceLocation resourceLocation;

    public CachedStringPart(int n, ResourceLocation resourceLocation) {
        this.displayList = n;
        this.resourceLocation = resourceLocation;
    }

    public void glDeleteLists() {
        GL11.glDeleteLists(this.displayList, 1);
    }

    public void render() {
        if (this.resourceLocation == null) {
            GL11.glDisable(3553);
        } else {
            textureManager.bindTexture(this.resourceLocation);
        }

        GL11.glCallList(this.displayList);
        if (this.resourceLocation == null) {
            GL11.glEnable(3553);
        }
    }
}
