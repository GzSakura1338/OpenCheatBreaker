package com.cheatbreaker.client.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GuiUtils {
    public static int[] COLOR_PALETTE = new int[]{
        0,
        170,
        43520,
        43690,
        11141120,
        11141290,
        16755200,
        11184810,
        5592405,
        5592575,
        5635925,
        5636095,
        16733525,
        16733695,
        16777045,
        16777215,
        0,
        42,
        10752,
        10794,
        2752512,
        2752554,
        2763264,
        2763306,
        1381653,
        1381695,
        1392405,
        1392447,
        4134165,
        4134207,
        4144917,
        4144959
    };

    public static String stripColorCodes(String str) {
        return str.replaceAll("(?i)§[0-9a-fklmnor]", "");
    }

    public static void renderItemWithOverlay(FontRenderer fontRenderer, ItemStack itemStack, int n, int n2) {
        renderItemOverlay(fontRenderer, itemStack, n, n2, true, true);
    }

    public static int countItemsWithMeta(EntityPlayer entityPlayer, Item item, int n) {
        int n2 = 0;

        for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
            if (entityPlayer.inventory.mainInventory[i] != null
                && item.equals(entityPlayer.inventory.mainInventory[i].getItem())
                && (n == -1 || entityPlayer.inventory.mainInventory[i].getMetadata() == n)) {
                n2 += entityPlayer.inventory.mainInventory[i].stackSize;
            }
        }

        return n2;
    }

    public static void drawRect(Tessellator tessellator, int n, int n2, int n3, int n4, int n5) {
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(n5);
        tessellator.addVertex(n, n2, 0.0);
        tessellator.addVertex(n, n2 + n4, 0.0);
        tessellator.addVertex(n + n3, n2 + n4, 0.0);
        tessellator.addVertex(n + n3, n2, 0.0);
        tessellator.draw();
    }

    public static void drawTexture(ResourceLocation resourceLocation, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, float f) {
        handleResourceLocation(resourceLocation, n, n2, n3, n4, n5, n6, n7, n8, n9, n9, n9, n9, f);
    }

    public static void renderItemOverlay(FontRenderer fontRenderer, ItemStack itemStack, int n, int n2, boolean flag, boolean flag2) {
        if (itemStack != null && (flag || flag2)) {
            if (itemStack.isItemDamaged() && flag) {
                int round = (int)Math.round(13.0 - itemStack.getCurrentDurability() * 13.0 / itemStack.getMaxDurability());
                int round2 = (int)Math.round(255.0 - itemStack.getCurrentDurability() * 255.0 / itemStack.getMaxDurability());
                GL11.glDisable(2896);
                GL11.glDisable(2929);
                GL11.glDisable(3553);
                Tessellator instance = Tessellator.instance;
                int n3 = 255 - round2 << 16 | round2 << 8;
                int n4 = (255 - round2) / 4 << 16 | 16128;
                drawRect(instance, n + 2, n2 + 13, 13, 2, 0);
                drawRect(instance, n + 2, n2 + 13, 12, 1, n4);
                drawRect(instance, n + 2, n2 + 13, round, 1, n3);
                GL11.glEnable(3553);
                GL11.glEnable(2896);
                GL11.glEnable(2929);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (flag2) {
                int n3 = 0;
                if (itemStack.getMaxStackSize() > 1) {
                    n3 = countItemsWithMeta(Minecraft.getMinecraft().thePlayer, itemStack.getItem(), itemStack.getMetadata());
                } else if (itemStack.getItem().equals(Items.bow)) {
                    n3 = countItems(Minecraft.getMinecraft().thePlayer, Items.arrow);
                }

                if (n3 > 1) {
                    String str = "" + n3;
                    GL11.glDisable(2896);
                    GL11.glDisable(2929);
                    fontRenderer.drawShadowedString(str, n + 19 - 2 - fontRenderer.getStringWidth(str), n2 + 6 + 3, 16777215);
                    GL11.glEnable(2896);
                    GL11.glEnable(2929);
                }
            }
        }
    }

    public static int countItems(EntityPlayer entityPlayer, Item item) {
        return countItemsWithMeta(entityPlayer, item, -1);
    }

    public static void drawNineSlice(
        int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10, int n11, int n12, float f
    ) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        int n13 = n7 - n11 - n12;
        int n14 = n8 - n9 - n10;
        int n15 = n5 - n11 - n12;
        int n16 = n6 - n9 - n10;
        int n17 = n15 / n13;
        int n18 = n15 % n13;
        int n19 = n16 / n14;
        int n20 = n16 % n14;
        drawTexturedRect(n, n2, n3, n4, n11, n9, f);
        drawTexturedRect(n + n11 + n15, n2, n3 + n11 + n13, n4, n12, n9, f);
        drawTexturedRect(n, n2 + n9 + n16, n3, n4 + n9 + n14, n11, n10, f);
        drawTexturedRect(n + n11 + n15, n2 + n9 + n16, n3 + n11 + n13, n4 + n9 + n14, n12, n10, f);

        for (int i = 0; i < n17 + (n18 > 0 ? 1 : 0); i++) {
            drawTexturedRect(n + n11 + i * n13, n2, n3 + n11, n4, i == n17 ? n18 : n13, n9, f);
            drawTexturedRect(n + n11 + i * n13, n2 + n9 + n16, n3 + n11, n4 + n9 + n14, i == n17 ? n18 : n13, n10, f);

            for (int j = 0; j < n19 + (n20 > 0 ? 1 : 0); j++) {
                drawTexturedRect(
                    n + n11 + i * n13,
                    n2 + n9 + j * n14,
                    n3 + n11,
                    n4 + n9,
                    i == n17 ? n18 : n13,
                    j == n19 ? n20 : n14,
                    f
                );
            }
        }

        for (int i = 0; i < n19 + (n20 > 0 ? 1 : 0); i++) {
            drawTexturedRect(n, n2 + n9 + i * n14, n3, n4 + n9, n11, i == n19 ? n20 : n14, f);
            drawTexturedRect(n + n11 + n15, n2 + n9 + i * n14, n3 + n11 + n13, n4 + n9, n12, i == n19 ? n20 : n14, f);
        }
    }

    public static int getColorForChar(char c, boolean flag) {
        return COLOR_PALETTE[flag ? "0123456789abcdef".indexOf(c) : "0123456789abcdef".indexOf(c) + 16];
    }

    public static void handleResourceLocation(
        ResourceLocation resourceLocation,
        int n,
        int n2,
        int n3,
        int n4,
        int n5,
        int n6,
        int n7,
        int n8,
        int n9,
        int n10,
        int n11,
        int n12,
        float f
    ) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        drawNineSlice(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, f);
    }

    public static void drawTexturedRect(int n, int n2, int n3, int n4, int n5, int n6, float f) {
        float f2 = 0.00390625F;
        float f3 = 0.00390625F;
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(n, n2 + n6, f, n3 * f2, (n4 + n6) * f3);
        instance.addVertexWithUV(n + n5, n2 + n6, f, (n3 + n5) * f2, (n4 + n6) * f3);
        instance.addVertexWithUV(n + n5, n2, f, (n3 + n5) * f2, n4 * f3);
        instance.addVertexWithUV(n, n2, f, n3 * f2, n4 * f3);
        instance.draw();
    }

    public static void drawTexturedRectScaled(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, float f) {
        drawNineSlice(n, n2, n3, n4, n5, n6, n7, n8, n9, n9, n9, n9, f);
    }
}
