package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class CooldownEntry {
    public Minecraft mc = Minecraft.getMinecraft();
    public String name;
    public int itemId;
    public long duration;
    public long startTime;
    public ItemStack itemStack;

    public void setDuration(long l) {
        this.duration = l;
    }

    public void restart() {
        this.startTime = System.currentTimeMillis();
    }

    public CooldownEntry(String str, int n, long l) {
        this.name = str;
        this.itemId = n;
        this.duration = l;
        this.startTime = System.currentTimeMillis();
        this.itemStack = new ItemStack(Item.getItemById(n));
    }

    public String getName() {
        return this.name;
    }

    public long getDuration() {
        return this.duration;
    }

    public int getItemId() {
        return this.itemId;
    }

    public void render(Setting setting, float f, float f2, int n) {
        byte b = 17;
        GlStateManager.pushMatrix();
        float zLevel = ArmorStatusMod.renderItem.zLevel;
        ArmorStatusMod.renderItem.zLevel = -50.0F;
        float f3 = 1.35F;
        GL11.glTranslatef(-0.5F, -1.0F, 0.0F);
        GL11.glScalef(f3, f3, f3);
        RenderHelper.enableStandardItemLighting();
        ArmorStatusMod.renderItem
            .renderItemAndEffectIntoGUI(
                this.mc.fontRendererObj, this.mc.getTextureManager(), this.itemStack, (int)((f + b / 2) / f3), (int)((f2 + b / 2) / f3)
            );
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
        ArmorStatusMod.renderItem.zLevel = zLevel;
        double d = this.duration - (System.currentTimeMillis() - this.startTime);
        if (!(d <= 0.0)) {
            if (((String)setting.getValue()).equalsIgnoreCase("Bright")) {
                GlStateManager.color(0.0F, 0.0F, 0.0F, 0.2F);
                RenderUtils.drawProgressRing(f + b, f2 + b, b, 0.0, (float)this.duration / 3.95F, (int)this.duration, d);
                GlStateManager.color(0.9F, 0.9F, 0.9F, 1.0F);
                RenderUtils.drawProgressRing(f + b, f2 + b, b + 0.1F, b - 2, (float)this.duration / 3.95F, (int)this.duration, this.duration);
                GlStateManager.color(0.35F, 0.35F, 0.35F, 0.6F);
                RenderUtils.drawProgressRing(f + b, f2 + b, b + 0.1F, b - 2, (float)this.duration / 3.95F, (int)this.duration, d);
            } else if (((String)setting.getValue()).equalsIgnoreCase("Dark")) {
                GlStateManager.color(0.0F, 0.0F, 0.0F, 0.2F);
                RenderUtils.drawFilledCircle(f + b, f2 + b, b);
                GlStateManager.color(0.0F, 0.0F, 0.0F, 0.2F);
                RenderUtils.drawProgressRing(f + b, f2 + b, b, 0.0, (float)this.duration / 3.95F, (int)this.duration, d);
                GlStateManager.color(0.0F, 0.9F, 0.0F, 1.0F);
                RenderUtils.drawProgressRing(f + b, f2 + b, b + 0.1F, b - 2, (float)this.duration / 3.95F, (int)this.duration, this.duration);
                GlStateManager.color(0.0F, 0.5F, 0.0F, 1.0F);
                RenderUtils.drawProgressRing(f + b, f2 + b, b + 0.1F, b - 2, (float)this.duration / 3.95F, (int)this.duration, d);
            } else if (((String)setting.getValue()).equalsIgnoreCase("Colored")) {
                float f4 = (n >> 24 & 0xFF) / 255.0F;
                float f5 = (n >> 16 & 0xFF) / 255.0F;
                float f6 = (n >> 8 & 0xFF) / 255.0F;
                float f7 = (n & 0xFF) / 255.0F;
                GlStateManager.color(f5, f6, f7, 0.15F * f4);
                RenderUtils.drawFilledCircle(f + b, f2 + b, b);
                GlStateManager.color(f5, f6, f7, 0.25F * f4);
                RenderUtils.drawProgressRing(f + b, f2 + b, b, 0.0, (float)this.duration / 3.95F, (int)this.duration, d);
                GlStateManager.color(f5, f6, f7, f4);
                RenderUtils.drawProgressRing(f + b, f2 + b, b + 0.1F, b - 2, (float)this.duration / 3.95F, (int)this.duration, this.duration);
                GlStateManager.color(f5, f6, f7, 0.15F * f4);
                RenderUtils.drawProgressRing(f + b, f2 + b, b + 0.1F, b - 2, (float)this.duration / 3.95F, (int)this.duration, d);
            }

            String format = String.format("%." + CheatBreaker.getInstance().getModManager().cooldownsMod.decimals.getValue() + "f", d / 1000.0);
            GlStateManager.pushMatrix();
            GL11.glTranslated(0.0, 0.0, 1.0);
            CheatBreaker.getInstance()
                .getFontManager()
                .ubuntuM16
                .drawStringWithOutlineColor(
                    format, f + b - CheatBreaker.getInstance().getFontManager().ubuntuM16.getStringWidth(format) / 2.0F, f2 + b / 2 + 4.0F, -1, 1862270976
                );
            GlStateManager.popMatrix();
        }
    }

    public boolean isExpired() {
        return this.startTime < System.currentTimeMillis() - this.duration;
    }
}
