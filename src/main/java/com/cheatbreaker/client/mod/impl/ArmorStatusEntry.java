package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.HudAlignment;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.AnchorPointUtils;
import com.cheatbreaker.client.ui.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ArmorStatusEntry {
    public int width;
    public Minecraft mc = Minecraft.getMinecraft();
    public int nameWidth;
    public int height;
    public int damageTextWidth;
    public String nameText = "";
    public String damageText = "";
    public ItemStack itemStack;
    public int iconSize;
    public int minHeight;
    public int padding;
    public boolean armor;

    public void updateLayout() {
        this.height = (boolean)ArmorStatusMod.itemName.getValue()
            ? Math.max(Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 2, this.minHeight)
            : Math.max(this.mc.fontRendererObj.FONT_HEIGHT, this.minHeight);
        if (this.itemStack != null) {
            boolean shouldShowDamage = this.shouldShowDamage();
            if ((this.armor && (Boolean)ArmorStatusMod.showArmorDamage.getValue() || !this.armor && (Boolean)ArmorStatusMod.showItemDamage.getValue()) && shouldShowDamage) {
                int n = this.itemStack.getMaxDurability() + 1;
                int n2 = n - this.itemStack.getCurrentDurability();
                if (((String)ArmorStatusMod.damageDisplayType.getValue()).equalsIgnoreCase("value")) {
                    this.damageText = n2 + ((boolean)ArmorStatusMod.showMaxDamage.getValue() ? "/" + n : "");
                } else if (((String)ArmorStatusMod.damageDisplayType.getValue()).equalsIgnoreCase("percent")) {
                    this.damageText = n2 * 100 / n + "%";
                }
            }

            this.damageTextWidth = this.mc.fontRendererObj.getStringWidth(GuiUtils.stripColorCodes(this.damageText));
            this.width = this.padding + this.iconSize + this.padding + this.damageTextWidth;
            if ((Boolean)ArmorStatusMod.itemName.getValue()) {
                this.nameText = this.itemStack.getDisplayName();
                this.width = this.padding
                    + this.iconSize
                    + this.padding
                    + Math.max(this.mc.fontRendererObj.getStringWidth(GuiUtils.stripColorCodes(this.nameText)), this.damageTextWidth);
            }

            this.nameWidth = this.mc.fontRendererObj.getStringWidth(GuiUtils.stripColorCodes(this.nameText));
        }
    }

    public boolean shouldShowDamage() {
        switch (ArmorStatusMod.showDamageAmount.getStringValue()) {
            case "ON":
                return !this.itemStack.isStackable() && this.itemStack.getMaxDurability() > 0;
            case "If Breakable":
                return this.itemStack.isItemStackDamageable();
            case "If Damaged":
                return this.itemStack.isItemDamaged();
            default:
                return false;
        }
    }

    public ArmorStatusEntry(ItemStack itemStack, int n, int n2, int n3, boolean flag) {
        this.itemStack = itemStack;
        this.iconSize = n;
        this.minHeight = n2;
        this.padding = n3;
        this.armor = flag;
        this.updateLayout();
    }

    public int getHeight() {
        return this.height;
    }

    public void render(float f, float f2) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826);
        RenderHelper.enableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        ArmorStatusMod.renderItem.zLevel = -10.0F;
        AnchorPoint anchorPoint = CheatBreaker.getInstance().getModManager().armorStatusMod.getAnchorPoint();
        boolean flag = AnchorPointUtils.getHorizontalAlignment(anchorPoint) == HudAlignment.RIGHT;
        boolean shouldShowDamage = this.shouldShowDamage();
        int colorValue = ArmorStatusMod.durabilityColor.getColorValue();
        if ((this.armor && (Boolean)ArmorStatusMod.showArmorDamage.getValue() || !this.armor && (Boolean)ArmorStatusMod.showItemDamage.getValue()) && shouldShowDamage) {
            int n = this.itemStack.getMaxDurability() + 1;
            int n2 = n - this.itemStack.getMetadata();
            colorValue = ArmorStatusDamageComparable.getColorForPercent(
                ArmorStatusMod.armorStatusDamageComparableList,
                ((String)ArmorStatusMod.damageThresholdType.getValue()).equalsIgnoreCase("percent") ? n2 * 100 / n : n2
            );
        }

        if (flag) {
            ArmorStatusMod.renderItem
                .renderItemAndEffectIntoGUI(
                    this.mc.fontRendererObj, this.mc.getTextureManager(), this.itemStack, (int)(f - (this.iconSize + this.padding)), (int)f2
                );
            GuiUtils.renderItemOverlay(
                this.mc.fontRendererObj,
                this.itemStack,
                (int)(f - (this.iconSize + this.padding)),
                (int)f2,
                (Boolean)ArmorStatusMod.damageOverlay.getValue(),
                (Boolean)ArmorStatusMod.itemCount.getValue()
            );
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826);
            GlStateManager.disableBlend();
            this.mc
                .fontRendererObj
                .drawString(
                    this.nameText + "§r",
                    f - (this.padding + this.iconSize + this.padding) - this.nameWidth,
                    f2,
                    ArmorStatusMod.nameColor.getColorValue(),
                    ArmorStatusMod.textShadow.getBooleanValue()
                );
            this.mc
                .fontRendererObj
                .drawString(
                    this.damageText + "§r",
                    f - (this.padding + this.iconSize + this.padding) - this.damageTextWidth,
                    f2 + ((boolean)ArmorStatusMod.itemName.getValue() ? this.height / 2 : this.height / 4),
                    colorValue,
                    ArmorStatusMod.textShadow.getBooleanValue()
                );
        } else {
            ArmorStatusMod.renderItem.renderItemAndEffectIntoGUI(this.mc.fontRendererObj, this.mc.getTextureManager(), this.itemStack, (int)f, (int)f2);
            GuiUtils.renderItemOverlay(
                this.mc.fontRendererObj,
                this.itemStack,
                (int)f,
                (int)f2,
                (Boolean)ArmorStatusMod.damageOverlay.getValue(),
                (Boolean)ArmorStatusMod.itemCount.getValue()
            );
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826);
            GlStateManager.disableBlend();
            this.mc
                .fontRendererObj
                .drawString(
                    this.nameText + "§r",
                    f + this.iconSize + this.padding,
                    f2,
                    ArmorStatusMod.nameColor.getColorValue(),
                    ArmorStatusMod.textShadow.getBooleanValue()
                );
            this.mc
                .fontRendererObj
                .drawString(
                    this.damageText + "§r",
                    f + this.iconSize + this.padding,
                    f2 + ((boolean)ArmorStatusMod.itemName.getValue() ? this.height / 2 : this.height / 4),
                    colorValue,
                    ArmorStatusMod.textShadow.getBooleanValue()
                );
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public int getWidth() {
        return this.width;
    }
}
