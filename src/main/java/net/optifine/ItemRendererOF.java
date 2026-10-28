package net.optifine;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.impl.EnchantmentGlintMod;
import java.lang.reflect.Field;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class ItemRendererOF extends ItemRenderer {
    public Minecraft mc2 = null;
    public RenderBlocks renderBlocksIr = null;
    public static ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static Field ItemRenderer_renderBlockInstance = Reflector.getField(ItemRenderer.class, RenderBlocks.class);

    public ItemRendererOF(Minecraft mc) {
        super(mc);
        this.mc2 = mc;
        if (ItemRenderer_renderBlockInstance == null) {
            Config.error("ItemRenderOF not initialized");
        }

        try {
            this.renderBlocksIr = (RenderBlocks)ItemRenderer_renderBlockInstance.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void renderItem(EntityLivingBase entityLivingBase, ItemStack itemStack, int n) {
        GL11.glPushMatrix();
        TextureManager textureManager = this.mc2.getTextureManager();
        Item item = itemStack.getItem();
        Block blockFromItem = Block.getBlockFromItem(item);
        if (itemStack != null && blockFromItem != null && blockFromItem.getRenderBlockPass() != 0) {
            GL11.glEnable(3042);
            GL11.glEnable(2884);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        Object obj = null;
        Object obj2 = null;
        if (Reflector.MinecraftForgeClient_getItemRenderer.exists()) {
            obj = Reflector.getFieldValue(Reflector.ItemRenderType_EQUIPPED);
            obj2 = Reflector.call(Reflector.MinecraftForgeClient_getItemRenderer, itemStack, obj);
        }

        if (obj2 != null) {
            textureManager.bindTexture(textureManager.getResourceLocation(itemStack.getItemSpriteNumber()));
            Reflector.callVoid(Reflector.ForgeHooksClient_renderEquippedItem, obj, obj2, this.renderBlocksIr, entityLivingBase, itemStack);
        } else if (itemStack.getItemSpriteNumber() == 0 && item instanceof ItemBlock && RenderBlocks.renderItemIn3d(blockFromItem.getRenderType())) {
            textureManager.bindTexture(textureManager.getResourceLocation(0));
            if (itemStack != null && blockFromItem != null && blockFromItem.getRenderBlockPass() != 0) {
                if (!Config.isShaders() || !Shaders.renderItemKeepDepthMask) {
                    GL11.glDepthMask(false);
                }

                this.renderBlocksIr.renderBlockAsItem(blockFromItem, itemStack.getMetadata(), 1.0F);
                GL11.glDepthMask(true);
            } else {
                this.renderBlocksIr.renderBlockAsItem(blockFromItem, itemStack.getMetadata(), 1.0F);
            }
        } else {
            IIcon itemIcon = entityLivingBase.getItemIcon(itemStack, n);
            if (itemIcon == null) {
                GL11.glPopMatrix();
                return;
            }

            textureManager.bindTexture(textureManager.getResourceLocation(itemStack.getItemSpriteNumber()));
            TextureUtil.saveAndSetTextureScaling(false, false, 1.0F);
            Tessellator instance = Tessellator.instance;
            float minU = itemIcon.getMinU();
            float maxU = itemIcon.getMaxU();
            float minV = itemIcon.getMinV();
            float maxV = itemIcon.getMaxV();
            float f = 0.0F;
            float f2 = 0.3F;
            GL11.glEnable(32826);
            GL11.glTranslatef(-f, -f2, 0.0F);
            float f3 = 1.5F;
            GL11.glScalef(f3, f3, f3);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
            renderItemIn2D(instance, maxU, minV, minU, maxV, itemIcon.getIconWidth(), itemIcon.getIconHeight(), 0.0625F);
            boolean flag = false;
            if (Reflector.ForgeItemStack_hasEffect.exists()) {
                flag = Reflector.callBoolean(itemStack, Reflector.ForgeItemStack_hasEffect, n);
            } else {
                flag = itemStack.hasEffect() && n == 0;
            }

            EnchantmentGlintMod enchantmentGlintMod = CheatBreaker.getInstance().getModManager().enchantmentGlintMod;
            if (flag && enchantmentGlintMod.isEnabled() && (Boolean)enchantmentGlintMod.held.glint.getValue()) {
                GL11.glDepthFunc(514);
                GL11.glDisable(2896);
                textureManager.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(3042);
                OpenGlHelper.glBlendFunc(768, 1, 1, 0);
                int colorValue = enchantmentGlintMod.held.glintColor.getColorValue();
                float f4 = (colorValue >> 24 & 0xFF) / 255.0F;
                float f5 = (colorValue >> 16 & 0xFF) / 255.0F;
                float f6 = (colorValue >> 8 & 0xFF) / 255.0F;
                float f7 = (colorValue & 0xFF) / 255.0F;
                float f8 = f4 * 0.76F;
                GL11.glColor4f(f5 * f8, f6 * f8, f7 * f8, 1.0F);
                GL11.glMatrixMode(5890);
                GL11.glPushMatrix();
                float f9 = 0.125F;
                GL11.glScalef(f9, f9, f9);
                float f10 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f10, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(instance, 0.0F, 0.0F, 1.0F, 1.0F, 16, 16, 0.0625F);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f9, f9, f9);
                f10 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f10, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(instance, 0.0F, 0.0F, 1.0F, 1.0F, 16, 16, 0.0625F);
                GL11.glPopMatrix();
                GL11.glMatrixMode(5888);
                GL11.glDisable(3042);
                GL11.glEnable(2896);
                GL11.glDepthFunc(515);
            }

            GL11.glDisable(32826);
            textureManager.bindTexture(textureManager.getResourceLocation(itemStack.getItemSpriteNumber()));
            TextureUtil.restoreTextureScaling();
        }

        if (itemStack != null && blockFromItem != null && blockFromItem.getRenderBlockPass() != 0) {
            GL11.glDisable(3042);
        }

        GL11.glPopMatrix();
    }

    @Override
    public void renderItemInFirstPerson(float f) {
        this.mc2.theWorld.renderItemInFirstPerson = true;
        super.renderItemInFirstPerson(f);
        this.mc2.theWorld.renderItemInFirstPerson = false;
    }

    @Override
    public void updateEquippedItem() {
        super.updateEquippedItem();
        if (Config.isShaders()) {
            Shaders.setItemToRenderMain((ItemStack)Reflector.getFieldValue(this, Reflector.ItemRenderer_itemToRender));
        }
    }
}
