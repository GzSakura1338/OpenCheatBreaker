package net.minecraft.client.renderer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent5;
import com.cheatbreaker.client.mod.impl.EnchantmentGlintMod;
import com.cheatbreaker.client.mod.impl.FreeLookHandler;
import com.cheatbreaker.client.mod.impl.PerspectiveMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

public class ItemRenderer {
    public static ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    public static ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
    public RenderBlocks renderBlocksIr = new RenderBlocks();
    public ItemStack itemToRender;
    public float equippedProgress;
    public float prevEquippedProgress;
    public int equippedItemSlot = -1;
    public Minecraft mc;

    public ItemRenderer(Minecraft mc) {
        this.mc = mc;
    }

    public void renderItem(EntityLivingBase entityLivingBase, ItemStack itemStack, int n) {
        GL11.glPushMatrix();
        TextureManager textureManager = this.mc.getTextureManager();
        Item item = itemStack.getItem();
        Block blockFromItem = Block.getBlockFromItem(item);
        if (itemStack != null && blockFromItem != null && blockFromItem.getRenderBlockPass() != 0) {
            GL11.glEnable(3042);
            GL11.glEnable(2884);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        if (itemStack.getItemSpriteNumber() == 0 && item instanceof ItemBlock && RenderBlocks.renderItemIn3d(blockFromItem.getRenderType())) {
            textureManager.bindTexture(textureManager.getResourceLocation(0));
            if (itemStack != null && blockFromItem != null && blockFromItem.getRenderBlockPass() != 0) {
                GL11.glDepthMask(false);
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
            EnchantmentGlintMod enchantmentGlintMod = CheatBreaker.getInstance().getModManager().enchantmentGlintMod;
            if (itemStack.hasEffect() && n == 0 && enchantmentGlintMod.isEnabled() && (Boolean)enchantmentGlintMod.held.glint.getValue()) {
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
                renderItemIn2D(instance, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f9, f9, f9);
                f10 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f10, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                renderItemIn2D(instance, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
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

    public static void renderItemIn2D(Tessellator tessellator, float f, float f2, float f3, float f4, int n, int n2, float f5) {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        tessellator.addVertexWithUV(0.0, 0.0, 0.0, f, f4);
        tessellator.addVertexWithUV(1.0, 0.0, 0.0, f3, f4);
        tessellator.addVertexWithUV(1.0, 1.0, 0.0, f3, f2);
        tessellator.addVertexWithUV(0.0, 1.0, 0.0, f, f2);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        tessellator.addVertexWithUV(0.0, 1.0, 0.0F - f5, f, f2);
        tessellator.addVertexWithUV(1.0, 1.0, 0.0F - f5, f3, f2);
        tessellator.addVertexWithUV(1.0, 0.0, 0.0F - f5, f3, f4);
        tessellator.addVertexWithUV(0.0, 0.0, 0.0F - f5, f, f4);
        tessellator.draw();
        float f6 = 0.5F * (f - f3) / n;
        float f7 = 0.5F * (f4 - f2) / n2;
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);

        for (int i = 0; i < n; i++) {
            float f8 = (float)i / n;
            float f9 = f + (f3 - f) * f8 - f6;
            tessellator.addVertexWithUV(f8, 0.0, 0.0F - f5, f9, f4);
            tessellator.addVertexWithUV(f8, 0.0, 0.0, f9, f4);
            tessellator.addVertexWithUV(f8, 1.0, 0.0, f9, f2);
            tessellator.addVertexWithUV(f8, 1.0, 0.0F - f5, f9, f2);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);

        for (int i = 0; i < n; i++) {
            float f8 = (float)i / n;
            float f9 = f + (f3 - f) * f8 - f6;
            float f10 = f8 + 1.0F / n;
            tessellator.addVertexWithUV(f10, 1.0, 0.0F - f5, f9, f2);
            tessellator.addVertexWithUV(f10, 1.0, 0.0, f9, f2);
            tessellator.addVertexWithUV(f10, 0.0, 0.0, f9, f4);
            tessellator.addVertexWithUV(f10, 0.0, 0.0F - f5, f9, f4);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);

        for (int i = 0; i < n2; i++) {
            float f8 = (float)i / n2;
            float f9 = f4 + (f2 - f4) * f8 - f7;
            float f10 = f8 + 1.0F / n2;
            tessellator.addVertexWithUV(0.0, f10, 0.0, f, f9);
            tessellator.addVertexWithUV(1.0, f10, 0.0, f3, f9);
            tessellator.addVertexWithUV(1.0, f10, 0.0F - f5, f3, f9);
            tessellator.addVertexWithUV(0.0, f10, 0.0F - f5, f, f9);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);

        for (int i = 0; i < n2; i++) {
            float f8 = (float)i / n2;
            float f9 = f4 + (f2 - f4) * f8 - f7;
            tessellator.addVertexWithUV(1.0, f8, 0.0, f3, f9);
            tessellator.addVertexWithUV(0.0, f8, 0.0, f, f9);
            tessellator.addVertexWithUV(0.0, f8, 0.0F - f5, f, f9);
            tessellator.addVertexWithUV(1.0, f8, 0.0F - f5, f3, f9);
        }

        tessellator.draw();
    }

    public void renderItemInFirstPerson(float f) {
        FreeLookHandler freeLookHandler = CheatBreaker.getInstance().getModManager().freeLookHandler;
        PerspectiveMod perspectiveMod = CheatBreaker.getInstance().getModManager().perspectiveMod;
        float f2 = freeLookHandler.active ? this.mc.thePlayer.rotationYaw - freeLookHandler.yaw : 0.0F;
        float f3 = this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * f;
        EntityClientPlayerMP thePlayer = this.mc.thePlayer;
        float f4 = thePlayer.prevRotationPitch + (thePlayer.rotationPitch - thePlayer.prevRotationPitch) * f;
        GL11.glPushMatrix();
        GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(thePlayer.prevRotationYaw + (thePlayer.rotationYaw - thePlayer.prevRotationYaw) * f, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        EntityClientPlayerMP entityClientPlayerMP = thePlayer;
        float f5 = entityClientPlayerMP.prevRenderArmPitch + (entityClientPlayerMP.renderArmPitch - entityClientPlayerMP.prevRenderArmPitch) * f;
        float f6 = entityClientPlayerMP.prevRenderArmYaw + (entityClientPlayerMP.renderArmYaw - entityClientPlayerMP.prevRenderArmYaw) * f + f2 * 10.0F;
        GL11.glRotatef((thePlayer.rotationPitch - f5) * 0.1F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((thePlayer.rotationYaw - f6) * 0.1F, 0.0F, 1.0F, 0.0F);
        ItemStack itemToRender = this.itemToRender;
        if (itemToRender != null && itemToRender.getItem() instanceof ItemCloth) {
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        int lightBrightnessForSkyBlocks = this.mc
            .theWorld
            .getLightBrightnessForSkyBlocks(MathHelper.floor_double(thePlayer.posX), MathHelper.floor_double(thePlayer.posY), MathHelper.floor_double(thePlayer.posZ), 0);
        int n = lightBrightnessForSkyBlocks % 65536;
        int n2 = lightBrightnessForSkyBlocks / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (itemToRender != null) {
            int colorFromItemStack = itemToRender.getItem().getColorFromItemStack(itemToRender, 0);
            float f7 = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
            float f8 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
            float f9 = (colorFromItemStack & 0xFF) / 255.0F;
            GL11.glColor4f(f7, f8, f9, 1.0F);
        } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (itemToRender != null && itemToRender.getItem() == Items.filled_map) {
            GL11.glPushMatrix();
            float f7 = 0.8F;
            float swingProgress = thePlayer.getSwingProgress(f);
            float sin = MathHelper.sin(swingProgress * (float) Math.PI);
            float sin2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
            GL11.glTranslatef(-sin2 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F) * 0.2F, -sin * 0.2F);
            swingProgress = 1.0F - f4 / 45.0F + 0.1F;
            if (swingProgress < 0.0F) {
                swingProgress = 0.0F;
            }

            if (swingProgress > 1.0F) {
                swingProgress = 1.0F;
            }

            swingProgress = -MathHelper.cos(swingProgress * (float) Math.PI) * 0.5F + 0.5F;
            GL11.glTranslatef(0.0F, 0.0F * f7 - (1.0F - f3) * 1.2F - swingProgress * 0.5F + 0.04F, -0.9F * f7);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(swingProgress * -85.0F, 0.0F, 0.0F, 1.0F);
            GL11.glEnable(32826);
            this.mc.getTextureManager().bindTexture(thePlayer.getLocationSkin());

            for (int i = 0; i < 2; i++) {
                int n3 = i * 2 - 1;
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.0F, -0.6F, 1.1F * n3);
                GL11.glRotatef(-45 * n3, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-65 * n3, 0.0F, 1.0F, 0.0F);
                Render entityRenderObject = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
                RenderPlayer renderPlayer = (RenderPlayer)entityRenderObject;
                float f8 = 1.0F;
                GL11.glScalef(f8, f8, f8);
                renderPlayer.renderFirstPersonArm(this.mc.thePlayer);
                GL11.glPopMatrix();
            }

            sin = thePlayer.getSwingProgress(f);
            sin2 = MathHelper.sin(sin * sin * (float) Math.PI);
            float sin3 = MathHelper.sin(MathHelper.sqrt_float(sin) * (float) Math.PI);
            GL11.glRotatef(-sin2 * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-sin3 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-sin3 * 80.0F, 1.0F, 0.0F, 0.0F);
            float f8 = 0.38F;
            GL11.glScalef(f8, f8, f8);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
            float f9 = 0.015625F;
            GL11.glScalef(f9, f9, f9);
            this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
            Tessellator instance = Tessellator.instance;
            GL11.glNormal3f(0.0F, 0.0F, -1.0F);
            instance.startDrawingQuads();
            byte b = 7;
            instance.addVertexWithUV(0 - b, 128 + b, 0.0, 0.0, 1.0);
            instance.addVertexWithUV(128 + b, 128 + b, 0.0, 1.0, 1.0);
            instance.addVertexWithUV(128 + b, 0 - b, 0.0, 1.0, 0.0);
            instance.addVertexWithUV(0 - b, 0 - b, 0.0, 0.0, 0.0);
            instance.draw();
            MapData mapData = Items.filled_map.getMapData(itemToRender, this.mc.theWorld);
            if (mapData != null) {
                this.mc.entityRenderer.getMapItemRenderer().draw(mapData, false);
            }

            GL11.glPopMatrix();
        } else if (itemToRender != null) {
            GL11.glPushMatrix();
            float f7 = 0.8F;
            if (thePlayer.getItemInUseCount() > 0) {
                EnumAction itemUseAction = itemToRender.getItemUseAction();
                if (itemUseAction == EnumAction.eat || itemUseAction == EnumAction.drink) {
                    float f8 = thePlayer.getItemInUseCount() - f + 1.0F;
                    float f9 = 1.0F - f8 / itemToRender.getMaxItemUseDuration();
                    float f10 = 1.0F - f9;
                    f10 = f10 * f10 * f10;
                    f10 = f10 * f10 * f10;
                    f10 = f10 * f10 * f10;
                    float f11 = 1.0F - f10;
                    GL11.glTranslatef(0.0F, MathHelper.abs(MathHelper.cos(f8 / 4.0F * (float) Math.PI) * 0.1F) * (f9 > 0.2 ? 1 : 0), 0.0F);
                    GL11.glTranslatef(f11 * 0.6F, -f11 * 0.5F, 0.0F);
                    GL11.glRotatef(f11 * 90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(f11 * 10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(f11 * 30.0F, 0.0F, 0.0F, 1.0F);
                }
            } else {
                float swingProgress = thePlayer.getSwingProgress(f);
                float sin = MathHelper.sin(swingProgress * (float) Math.PI);
                float sin2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
                perspectiveMod.applyTranslation(-sin2 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F) * 0.2F, -sin * 0.2F);
            }

            GL11.glTranslatef(0.7F * f7, -0.65F * f7 - (1.0F - f3) * 0.6F, -0.9F * f7);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(32826);
            float swingProgress = thePlayer.getSwingProgress(f);
            float sin = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
            float sin2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
            perspectiveMod.applyRotation(-sin * 20.0F);
            GL11.glRotatef(-sin2 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-sin2 * 80.0F, 1.0F, 0.0F, 0.0F);
            float f8 = perspectiveMod.isEnabled() ? perspectiveMod.staticItemScale.getFloatValue() : 0.4F;
            GL11.glScalef(f8, f8, f8);
            if (thePlayer.getItemInUseCount() > 0) {
                EnumAction itemUseAction = itemToRender.getItemUseAction();
                if (itemUseAction == EnumAction.block) {
                    GL11.glTranslatef(-0.5F, 0.2F, 0.0F);
                    GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
                } else if (itemUseAction == EnumAction.bow) {
                    GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
                    float f9 = itemToRender.getMaxItemUseDuration() - (thePlayer.getItemInUseCount() - f + 1.0F);
                    float f10 = f9 / 20.0F;
                    f10 = (f10 * f10 + f10 * 2.0F) / 3.0F;
                    if (f10 > 1.0F) {
                        f10 = 1.0F;
                    }

                    if (f10 > 0.1F) {
                        GL11.glTranslatef(0.0F, MathHelper.sin((f9 - 0.1F) * 1.3F) * 0.01F * (f10 - 0.1F), 0.0F);
                    }

                    GL11.glTranslatef(0.0F, 0.0F, f10 * 0.1F);
                    GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                    float f11 = 1.0F + f10 * 0.2F;
                    GL11.glScalef(1.0F, 1.0F, f11);
                    GL11.glTranslatef(0.0F, -0.5F, 0.0F);
                    GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
                }
            }

            if (itemToRender.getItem().shouldRotateAroundWhenRendering()) {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (itemToRender.getItem().requiresMultipleRenderPasses()) {
                this.renderItem(thePlayer, itemToRender, 0);
                int colorFromItemStack = itemToRender.getItem().getColorFromItemStack(itemToRender, 1);
                float f9 = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
                float f10 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
                float f11 = (colorFromItemStack & 0xFF) / 255.0F;
                GL11.glColor4f(1.0F * f9, 1.0F * f10, 1.0F * f11, 1.0F);
                this.renderItem(thePlayer, itemToRender, 1);
            } else {
                this.renderItem(thePlayer, itemToRender, 0);
            }

            GL11.glPopMatrix();
        } else if (!thePlayer.isInvisible()) {
            GL11.glPushMatrix();
            float f7 = 0.8F;
            float swingProgress = thePlayer.getSwingProgress(f);
            float sin = MathHelper.sin(swingProgress * (float) Math.PI);
            float sin2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
            GL11.glTranslatef(-sin2 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F) * 0.4F, -sin * 0.4F);
            GL11.glTranslatef(0.8F * f7, -0.75F * f7 - (1.0F - f3) * 0.6F, -0.9F * f7);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(32826);
            swingProgress = thePlayer.getSwingProgress(f);
            sin = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
            sin2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
            GL11.glRotatef(sin2 * 70.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-sin * 20.0F, 0.0F, 0.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(thePlayer.getLocationSkin());
            GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
            GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(5.6F, 0.0F, 0.0F);
            Render entityRenderObject = RenderManager.instance.getEntityRenderObject(this.mc.thePlayer);
            RenderPlayer renderPlayer = (RenderPlayer)entityRenderObject;
            float f8 = 1.0F;
            GL11.glScalef(f8, f8, f8);
            renderPlayer.renderFirstPersonArm(this.mc.thePlayer);
            GL11.glPopMatrix();
        }

        if (itemToRender != null && itemToRender.getItem() instanceof ItemCloth) {
            GL11.glDisable(3042);
        }

        GL11.glDisable(32826);
        RenderHelper.disableStandardItemLighting();
    }

    public void renderOverlays(float f) {
        GL11.glDisable(3008);
        if (this.mc.thePlayer.isBurning()) {
            SimpleEvent5 simpleEvent5 = new SimpleEvent5();
            CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent5);
            if (!simpleEvent5.isCancelled()) {
                this.renderFireInFirstPerson(f);
            }
        }

        if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            int n = MathHelper.floor_double(this.mc.thePlayer.posX);
            int n2 = MathHelper.floor_double(this.mc.thePlayer.posY);
            int n3 = MathHelper.floor_double(this.mc.thePlayer.posZ);
            Block block = this.mc.theWorld.getBlock(n, n2, n3);
            if (this.mc.theWorld.getBlock(n, n2, n3).isNormalCube()) {
                this.renderInsideOfBlock(f, block.getBlockTextureFromSide(2));
            } else {
                for (int i = 0; i < 8; i++) {
                    float f2 = ((i >> 0) % 2 - 0.5F) * this.mc.thePlayer.width * 0.9F;
                    float f3 = ((i >> 1) % 2 - 0.5F) * this.mc.thePlayer.height * 0.2F;
                    float f4 = ((i >> 2) % 2 - 0.5F) * this.mc.thePlayer.width * 0.9F;
                    int n4 = MathHelper.floor_float(n + f2);
                    int n5 = MathHelper.floor_float(n2 + f3);
                    int n6 = MathHelper.floor_float(n3 + f4);
                    if (this.mc.theWorld.getBlock(n4, n5, n6).isNormalCube()) {
                        block = this.mc.theWorld.getBlock(n4, n5, n6);
                    }
                }
            }

            if (block.getMaterial() != Material.air) {
                this.renderInsideOfBlock(f, block.getBlockTextureFromSide(2));
            }
        }

        if (this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
            this.renderWaterOverlayTexture(f);
        }

        GL11.glEnable(3008);
    }

    public void renderInsideOfBlock(float f, IIcon icon) {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator instance = Tessellator.instance;
        float f2 = 0.1F;
        GL11.glColor4f(f2, f2, f2, 0.5F);
        GL11.glPushMatrix();
        float f3 = -1.0F;
        float f4 = 1.0F;
        float f5 = -1.0F;
        float f6 = 1.0F;
        float f7 = -0.5F;
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        instance.startDrawingQuads();
        instance.addVertexWithUV(f3, f5, f7, maxU, maxV);
        instance.addVertexWithUV(f4, f5, f7, minU, maxV);
        instance.addVertexWithUV(f4, f6, f7, minU, minV);
        instance.addVertexWithUV(f3, f6, f7, maxU, minV);
        instance.draw();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void renderWaterOverlayTexture(float f) {
        this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
        Tessellator instance = Tessellator.instance;
        float brightness = this.mc.thePlayer.getBrightness(f);
        GL11.glColor4f(brightness, brightness, brightness, 0.5F);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glPushMatrix();
        float f2 = 4.0F;
        float f3 = -1.0F;
        float f4 = 1.0F;
        float f5 = -1.0F;
        float f6 = 1.0F;
        float f7 = -0.5F;
        float f8 = -this.mc.thePlayer.rotationYaw / 64.0F;
        float f9 = this.mc.thePlayer.rotationPitch / 64.0F;
        instance.startDrawingQuads();
        instance.addVertexWithUV(f3, f5, f7, f2 + f8, f2 + f9);
        instance.addVertexWithUV(f4, f5, f7, 0.0F + f8, f2 + f9);
        instance.addVertexWithUV(f4, f6, f7, 0.0F + f8, 0.0F + f9);
        instance.addVertexWithUV(f3, f6, f7, f2 + f8, 0.0F + f9);
        instance.draw();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
    }

    public void renderFireInFirstPerson(float f) {
        Tessellator instance = Tessellator.instance;
        if (CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, (Float)CheatBreaker.getInstance().getModManager().packTweaksMod.fireOpacity.getValue() / 100.0F);
        } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.9F);
        }

        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        float f2 = 1.0F;

        for (int i = 0; i < 2; i++) {
            GL11.glPushMatrix();
            IIcon fireIcon = Blocks.fire.getFireIcon(1);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float minU = fireIcon.getMinU();
            float maxU = fireIcon.getMaxU();
            float minV = fireIcon.getMinV();
            float maxV = fireIcon.getMaxV();
            float f3 = (0.0F - f2) / 2.0F;
            float f4 = f3 + f2;
            float f5 = 0.0F - f2 / 2.0F;
            float f6 = f5 + f2;
            float f7 = -0.5F;
            if (CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled()) {
                GL11.glTranslatef(-(i * 2 - 1) * 0.24F, -1.3F + (Float)CheatBreaker.getInstance().getModManager().packTweaksMod.fireHeight.getValue(), 0.0F);
            } else {
                GL11.glTranslatef(-(i * 2 - 1) * 0.24F, -0.3F, 0.0F);
            }

            GL11.glRotatef((i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            instance.startDrawingQuads();
            instance.addVertexWithUV(f3, f5, f7, maxU, maxV);
            instance.addVertexWithUV(f4, f5, f7, minU, maxV);
            instance.addVertexWithUV(f4, f6, f7, minU, minV);
            instance.addVertexWithUV(f3, f6, f7, maxU, minV);
            instance.draw();
            GL11.glPopMatrix();
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
    }

    public void updateEquippedItem() {
        this.prevEquippedProgress = this.equippedProgress;
        EntityClientPlayerMP thePlayer = this.mc.thePlayer;
        ItemStack currentItem = thePlayer.inventory.getCurrentItem();
        boolean flag = this.equippedItemSlot == thePlayer.inventory.currentItem && currentItem == this.itemToRender;
        if (this.itemToRender == null && currentItem == null) {
            flag = true;
        }

        if (currentItem != null
            && this.itemToRender != null
            && currentItem != this.itemToRender
            && currentItem.getItem() == this.itemToRender.getItem()
            && currentItem.getMetadata() == this.itemToRender.getMetadata()) {
            this.itemToRender = currentItem;
            flag = true;
        }

        float f = 0.4F;
        float f2 = flag ? 1.0F : 0.0F;
        float f3 = f2 - this.equippedProgress;
        if (f3 < -f) {
            f3 = -f;
        }

        if (f3 > f) {
            f3 = f;
        }

        this.equippedProgress += f3;
        if (this.equippedProgress < 0.1F) {
            this.itemToRender = currentItem;
            this.equippedItemSlot = thePlayer.inventory.currentItem;
        }
    }

    public void resetEquippedProgress() {
        this.equippedProgress = 0.0F;
    }

    public void resetEquippedProgress2() {
        this.equippedProgress = 0.0F;
    }
}
