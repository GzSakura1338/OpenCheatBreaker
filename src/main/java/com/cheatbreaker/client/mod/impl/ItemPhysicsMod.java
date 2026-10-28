package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.RenderDroppedItemEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ItemPhysicsMod extends AbstractMod {
    public Random random = new Random();
    public RenderBlocks renderBlocks = new RenderBlocks();
    public long lastTickNanos;
    public Setting rotationSpeed = new Setting(this, "Rotation Speed").changeValue(50.0).setRange(10.0, 100.0);
    public double rotation;

    public void onRenderDroppedItemEvent(RenderDroppedItemEvent renderDroppedItemEvent) {
        renderDroppedItemEvent.setCancelled(true);
        this.rotation = (System.nanoTime() - this.lastTickNanos) / 2500000.0 * (this.rotationSpeed.getDoubleValue() / 1000.0);
        if (!this.mc.inGameHasFocus) {
            this.rotation = 0.0;
        }

        EntityItem entityItem;
        ItemStack itemStack;
        if ((itemStack = (entityItem = (EntityItem)renderDroppedItemEvent.getEntity()).getEntityItem()).getItem() != null) {
            this.mc.renderEngine.bindTexture(this.mc.renderEngine.getResourceLocation(entityItem.getEntityItem().getItemSpriteNumber()));
            TextureUtil.saveAndSetTextureScaling(false, false, 1.0F);
            this.random.setSeed(187L);
            GL11.glPushMatrix();
            byte b = 1;
            if (entityItem.getEntityItem().stackSize > 1) {
                b = 2;
            }

            if (entityItem.getEntityItem().stackSize > 5) {
                b = 3;
            }

            if (entityItem.getEntityItem().stackSize > 20) {
                b = 4;
            }

            if (entityItem.getEntityItem().stackSize > 40) {
                b = 5;
            }

            GL11.glTranslatef((float)renderDroppedItemEvent.getX(), (float)renderDroppedItemEvent.getY(), (float)renderDroppedItemEvent.getZ());
            GL11.glEnable(32826);
            if (itemStack.getItemSpriteNumber() == 0
                && itemStack.getItem() instanceof ItemBlock
                && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemStack.getItem()).getRenderType())) {
                Block blockFromItem = Block.getBlockFromItem(itemStack.getItem());
                if (renderDroppedItemEvent.getEntity().onGround) {
                    GL11.glScalef(1.25F, 1.25F, 1.25F);
                    GL11.glTranslatef(0.0F, 0.05F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.09F, 0.0F);
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                } else {
                    GL11.glRotatef(entityItem.rotationYaw, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(entityItem.rotationPitch, 1.0F, 0.0F, 0.0F);
                }

                float f = 0.25F;
                int renderType = blockFromItem.getRenderType();
                if (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2) {
                    f = 0.5F;
                }

                if (blockFromItem.getRenderBlockPass() > 0) {
                    GL11.glAlphaFunc(516, 0.1F);
                    GL11.glEnable(3042);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                }

                GL11.glScalef(f, f, f);

                for (int i = 0; i < b; i++) {
                    GL11.glPushMatrix();
                    if (i > 0) {
                        float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f;
                        float f3 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f;
                        float f4 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f;
                        GL11.glTranslatef(f2, f3, f4);
                    }

                    if (entityItem.rotationPitch > 360.0F) {
                        entityItem.rotationPitch = 0.0F;
                    }

                    if (entityItem != null && !Double.isNaN(entityItem.posX) && !Double.isNaN(entityItem.posY) && !Double.isNaN(entityItem.posZ) && entityItem.worldObj != null && !entityItem.onGround) {
                        double d = this.rotation * 2.0;
                        entityItem.rotationPitch = (float)(entityItem.rotationPitch + d);
                    }

                    this.renderBlocks.renderBlockAsItem(blockFromItem, itemStack.getMetadata(), 1.0F);
                    GL11.glPopMatrix();
                }

                if (blockFromItem.getRenderBlockPass() > 0) {
                    GL11.glDisable(3042);
                }
            } else if (itemStack.getItem().requiresMultipleRenderPasses()) {
                if (renderDroppedItemEvent.getRenderItem().renderWithColor) {
                    GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                    GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                } else {
                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                }

                for (int i = 0; i <= 1; i++) {
                    if (!renderDroppedItemEvent.getEntity().onGround) {
                        GL11.glRotatef((float)this.rotation, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef((float)this.rotation, 1.0F, 0.0F, 0.0F);
                    }

                    this.random.setSeed(187L);
                    IIcon iconFromDamageForRenderPass = itemStack.getItem().getIconFromDamageForRenderPass(itemStack.getMetadata(), i);
                    if (renderDroppedItemEvent.getRenderItem().renderWithColor) {
                        int colorFromItemStack = itemStack.getItem().getColorFromItemStack(itemStack, i);
                        float f = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
                        float f2 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
                        float f3 = (colorFromItemStack & 0xFF) / 255.0F;
                        GL11.glColor4f(f, f2, f3, 1.0F);
                        renderDroppedItemEvent.getRenderItem().renderDroppedItem(entityItem, iconFromDamageForRenderPass, b, renderDroppedItemEvent.getPartialTicks(), f, f2, f3);
                    } else {
                        renderDroppedItemEvent.getRenderItem().renderDroppedItem(entityItem, iconFromDamageForRenderPass, b, renderDroppedItemEvent.getPartialTicks(), 1.0F, 1.0F, 1.0F);
                    }
                }
            } else {
                if (entityItem != null && !Double.isNaN(entityItem.posX) && !Double.isNaN(entityItem.posY) && !Double.isNaN(entityItem.posZ) && entityItem.worldObj != null && !entityItem.onGround) {
                    if (entityItem.onGround) {
                        entityItem.rotationPitch = 0.0F;
                    } else {
                        double d = this.rotation * 2.0;
                        entityItem.rotationPitch = (float)(entityItem.rotationPitch + d);
                    }
                }

                GL11.glRotatef(entityItem.rotationPitch, 1.0F, 0.0F, 0.0F);
                if (itemStack != null && itemStack.getItem() instanceof ItemCloth) {
                    GL11.glAlphaFunc(516, 0.1F);
                    GL11.glEnable(3042);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                }

                if (RenderItem.renderInFrame) {
                    GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                    GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                } else {
                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                }

                IIcon iconIndex = itemStack.getIconIndex();
                if (renderDroppedItemEvent.getRenderItem().renderWithColor) {
                    int colorFromItemStack = itemStack.getItem().getColorFromItemStack(itemStack, 0);
                    float f = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
                    float f2 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
                    float f3 = (colorFromItemStack & 0xFF) / 255.0F;
                    renderDroppedItemEvent.getRenderItem().renderDroppedItem(entityItem, iconIndex, b, renderDroppedItemEvent.getPartialTicks(), f, f2, f3);
                } else {
                    renderDroppedItemEvent.getRenderItem().renderDroppedItem(entityItem, iconIndex, b, renderDroppedItemEvent.getPartialTicks(), 1.0F, 1.0F, 1.0F);
                }

                if (itemStack != null && itemStack.getItem() instanceof ItemCloth) {
                    GL11.glDisable(3042);
                }
            }

            GL11.glDisable(32826);
            GL11.glPopMatrix();
            this.mc.renderEngine.bindTexture(this.mc.renderEngine.getResourceLocation(entityItem.getEntityItem().getItemSpriteNumber()));
            TextureUtil.restoreTextureScaling();
        }
    }

    public ResourceLocation getResourceLocation2() {
        return TextureMap.locationBlocksTexture;
    }

    public ItemPhysicsMod() {
        super("Item Physics");
        this.setCategories(ModCategory.MISC);
        this.addEventHandler(ClientTickEvent.class, clientTickEvent -> this.lastTickNanos = System.nanoTime());
        this.addEventHandler(RenderDroppedItemEvent.class, this::onRenderDroppedItemEvent);
    }

    public int getRenderedStackCount(ItemStack itemStack) {
        byte b = 1;
        if (itemStack.stackSize > 48) {
            b = 5;
        } else if (itemStack.stackSize > 32) {
            b = 4;
        } else if (itemStack.stackSize > 16) {
            b = 3;
        } else if (itemStack.stackSize > 1) {
            b = 2;
        }

        return b;
    }
}
