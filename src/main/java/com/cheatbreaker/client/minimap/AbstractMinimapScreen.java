package com.cheatbreaker.client.minimap;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

public class AbstractMinimapScreen extends GuiScreen {
    public int getWidth() {
        return this.width;
    }

    @Override
    public void onGuiClosed() {
        MinimapSettings.instance.save();
    }

    public List getButtonList() {
        return this.buttonList;
    }

    public Minecraft getMc() {
        return this.mc;
    }

    public void drawMinimapBackground() {
        if (!AbstractVoxelMap.instance.getMapOptions().showInScreens) {
            AbstractVoxelMap.instance.getMap().drawMinimap(this.mc);
            GL11.glClear(256);
        }
    }

    public int getHeight() {
        return this.height;
    }

    public FontRenderer getFontRendererObj() {
        return this.fontRendererObj;
    }
}
