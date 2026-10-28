package com.cheatbreaker.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class WorldLabel {
    public static List<WorldLabel> labels = new ArrayList<>();
    public UUID playerId;
    public double x;
    public String[] lines;
    public double y;
    public double z;

    public String[] getLines() {
        return this.lines;
    }

    public static List<WorldLabel> getLabels() {
        return labels;
    }

    public static void renderAll() {
        FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
        RenderManager instance = RenderManager.instance;

        for (WorldLabel worldLabel : labels) {
            if (worldLabel.getLines() != null && worldLabel.getLines().length > 0) {
                for (int i = worldLabel.getLines().length - 1; i >= 0; i--) {
                    String str = worldLabel.getLines()[worldLabel.getLines().length - i - 1];
                    float f = (float)(worldLabel.getX() - (float)RenderManager.renderPosX);
                    float f2 = (float)(worldLabel.getY() + 1.0 + i * 0.25F - (float)RenderManager.renderPosY);
                    float f3 = (float)(worldLabel.getZ() - (float)RenderManager.renderPosZ);
                    float f4 = 1.6F;
                    float f5 = 0.016666668F * f4;
                    GlStateManager.pushMatrix();
                    GL11.glTranslatef(f, f2, f3);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-instance.playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(instance.playerViewX, 1.0F, 0.0F, 0.0F);
                    GL11.glScalef(-f5, -f5, f5);
                    GL11.glDisable(2896);
                    GlStateManager.depthMask(false);
                    GL11.glDisable(2929);
                    GlStateManager.enableBlend();
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    Tessellator instance2 = Tessellator.instance;
                    byte b = 0;
                    GL11.glDisable(3553);
                    instance2.startDrawingQuads();
                    int n = fontRendererObj.getStringWidth(str) / 2;
                    instance2.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                    instance2.addVertex(-n - 1, -1 + b, 0.0);
                    instance2.addVertex(-n - 1, 8 + b, 0.0);
                    instance2.addVertex(n + 1, 8 + b, 0.0);
                    instance2.addVertex(n + 1, -1 + b, 0.0);
                    instance2.draw();
                    GL11.glEnable(3553);
                    fontRendererObj.drawString(str, -fontRendererObj.getStringWidth(str) / 2, b, 553648127);
                    GL11.glEnable(2929);
                    GlStateManager.depthMask(true);
                    fontRendererObj.drawString(str, -fontRendererObj.getStringWidth(str) / 2, b, -1);
                    GL11.glEnable(2896);
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    public double getY() {
        return this.y;
    }

    public void setLines(String[] strs) {
        this.lines = strs;
    }

    public WorldLabel(UUID uuid, double d, double d2, double d3) {
        this.playerId = uuid;
        this.x = d;
        this.y = d2;
        this.z = d3;
    }

    public double getX() {
        return this.x;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public double getZ() {
        return this.z;
    }
}
