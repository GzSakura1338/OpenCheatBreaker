package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.RenderDroppedItemEvent;
import com.cheatbreaker.client.event.RenderItemIntoGuiEvent;
import com.cheatbreaker.client.mod.impl.EnchantmentGlintMod;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderItem extends Render {
    public static ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public RenderBlocks renderBlocksRi = new RenderBlocks();
    public Random random = new Random();
    public boolean renderWithColor = true;
    public float zLevel;
    public static boolean renderInFrame;
    public RenderDroppedItemEvent renderDroppedItemEvent;

    public RenderItem() {
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    public void doRender(EntityItem entityItem, double d, double d2, double d3, float f, float f2) {
        this.renderDroppedItemEvent = new RenderDroppedItemEvent(this, entityItem, d, d2, d3, f, f2);
        CheatBreaker.getInstance().getEventBus().handleEvent(this.renderDroppedItemEvent);
        if (!this.renderDroppedItemEvent.isCancelled()) {
            ItemStack entityItem2 = entityItem.getEntityItem();
            if (entityItem2.getItem() != null) {
                this.bindEntityTexture(entityItem);
                TextureUtil.saveAndSetTextureScaling(false, false, 1.0F);
                this.random.setSeed(187L);
                GL11.glPushMatrix();
                float f3 = MathHelper.sin((entityItem.age + f2) / 10.0F + entityItem.hoverStart) * 0.1F + 0.1F;
                float f4 = ((entityItem.age + f2) / 20.0F + entityItem.hoverStart) * (180.0F / (float)Math.PI);
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

                GL11.glTranslatef((float)d, (float)d2 + f3, (float)d3);
                GL11.glEnable(32826);
                if (entityItem2.getItemSpriteNumber() == 0
                    && entityItem2.getItem() instanceof ItemBlock
                    && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(entityItem2.getItem()).getRenderType())) {
                    Block blockFromItem = Block.getBlockFromItem(entityItem2.getItem());
                    GL11.glRotatef(f4, 0.0F, 1.0F, 0.0F);
                    if (renderInFrame) {
                        GL11.glScalef(1.25F, 1.25F, 1.25F);
                        GL11.glTranslatef(0.0F, 0.05F, 0.0F);
                        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                    }

                    float f5 = 0.25F;
                    int renderType = blockFromItem.getRenderType();
                    if (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2) {
                        f5 = 0.5F;
                    }

                    if (blockFromItem.getRenderBlockPass() > 0) {
                        GL11.glAlphaFunc(516, 0.1F);
                        GL11.glEnable(3042);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    }

                    GL11.glScalef(f5, f5, f5);

                    for (int i = 0; i < b; i++) {
                        GL11.glPushMatrix();
                        if (i > 0) {
                            float f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f5;
                            float f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f5;
                            float f8 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f5;
                            GL11.glTranslatef(f6, f7, f8);
                        }

                        this.renderBlocksRi.renderBlockAsItem(blockFromItem, entityItem2.getMetadata(), 1.0F);
                        GL11.glPopMatrix();
                    }

                    if (blockFromItem.getRenderBlockPass() > 0) {
                        GL11.glDisable(3042);
                    }
                } else if (entityItem2.getItemSpriteNumber() == 1 && entityItem2.getItem().requiresMultipleRenderPasses()) {
                    if (renderInFrame) {
                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                    } else {
                        GL11.glScalef(0.5F, 0.5F, 0.5F);
                    }

                    for (int i = 0; i <= 1; i++) {
                        this.random.setSeed(187L);
                        IIcon iconFromDamageForRenderPass = entityItem2.getItem().getIconFromDamageForRenderPass(entityItem2.getMetadata(), i);
                        if (this.renderWithColor) {
                            int colorFromItemStack = entityItem2.getItem().getColorFromItemStack(entityItem2, i);
                            float f5 = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
                            float f6 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
                            float f7 = (colorFromItemStack & 0xFF) / 255.0F;
                            GL11.glColor4f(f5, f6, f7, 1.0F);
                            this.renderDroppedItem(entityItem, iconFromDamageForRenderPass, b, f2, f5, f6, f7);
                        } else {
                            this.renderDroppedItem(entityItem, iconFromDamageForRenderPass, b, f2, 1.0F, 1.0F, 1.0F);
                        }
                    }
                } else {
                    if (entityItem2 != null && entityItem2.getItem() instanceof ItemCloth) {
                        GL11.glAlphaFunc(516, 0.1F);
                        GL11.glEnable(3042);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    }

                    if (renderInFrame) {
                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                    } else {
                        GL11.glScalef(0.5F, 0.5F, 0.5F);
                    }

                    IIcon iconIndex = entityItem2.getIconIndex();
                    if (this.renderWithColor) {
                        int colorFromItemStack = entityItem2.getItem().getColorFromItemStack(entityItem2, 0);
                        float f5 = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
                        float f6 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
                        float f7 = (colorFromItemStack & 0xFF) / 255.0F;
                        this.renderDroppedItem(entityItem, iconIndex, b, f2, f5, f6, f7);
                    } else {
                        this.renderDroppedItem(entityItem, iconIndex, b, f2, 1.0F, 1.0F, 1.0F);
                    }

                    if (entityItem2 != null && entityItem2.getItem() instanceof ItemCloth) {
                        GL11.glDisable(3042);
                    }
                }

                GL11.glDisable(32826);
                GL11.glPopMatrix();
                this.bindEntityTexture(entityItem);
                TextureUtil.restoreTextureScaling();
            }
        }
    }

    public ResourceLocation getEntityTexture(EntityItem entityItem) {
        return this.renderManager.renderEngine.getResourceLocation(entityItem.getEntityItem().getItemSpriteNumber());
    }

    public void renderDroppedItem(EntityItem entityItem, IIcon icon, int n, float f, float f2, float f3, float f4) {
        Tessellator instance = Tessellator.instance;
        if (icon == null) {
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            ResourceLocation resourceLocation = textureManager.getResourceLocation(entityItem.getEntityItem().getItemSpriteNumber());
            icon = ((TextureMap)textureManager.getTexture(resourceLocation)).getAtlasSprite("missingno");
        }

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        float f5 = 1.0F;
        float f6 = 0.5F;
        float f7 = 0.25F;
        if (this.renderManager.options.fancyGraphics) {
            GL11.glPushMatrix();
            if (renderInFrame) {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            } else if ((Boolean)CheatBreaker.getInstance().getModManager().packTweaksMod.facePlayer.getValue()
                && CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled()) {
                GL11.glRotatef(360.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                if ((Boolean)CheatBreaker.getInstance().getModManager().packTweaksMod.rotateTowardsCamera.getValue()) {
                    GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                }
            } else if (!this.renderDroppedItemEvent.isCancelled()) {
                GL11.glRotatef(((entityItem.age + f) / 20.0F + entityItem.hoverStart) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            float f8 = 0.0625F;
            float f9 = 0.021875F;
            ItemStack entityItem2 = entityItem.getEntityItem();
            int stackSize = entityItem2.stackSize;
            byte b;
            if (stackSize < 2) {
                b = 1;
            } else if (stackSize < 16) {
                b = 2;
            } else if (stackSize < 32) {
                b = 3;
            } else {
                b = 4;
            }

            GL11.glTranslatef(-f6, -f7, -((f8 + f9) * b / 2.0F));

            for (int i = 0; i < b; i++) {
                GL11.glTranslatef(0.0F, 0.0F, f8 + f9);
                if (entityItem2.getItemSpriteNumber() == 0) {
                    this.bindTexture(TextureMap.locationBlocksTexture);
                } else {
                    this.bindTexture(TextureMap.locationItemsTexture);
                }

                GL11.glColor4f(f2, f3, f4, 1.0F);
                ItemRenderer.renderItemIn2D(instance, maxU, minV, minU, maxV, icon.getIconWidth(), icon.getIconHeight(), f8);
                EnchantmentGlintMod enchantmentGlintMod = CheatBreaker.getInstance().getModManager().enchantmentGlintMod;
                if (entityItem2.hasEffect() && enchantmentGlintMod.isEnabled() && (Boolean)enchantmentGlintMod.dropped.glint.getValue()) {
                    GL11.glDepthFunc(514);
                    GL11.glDisable(2896);
                    this.renderManager.renderEngine.bindTexture(RES_ITEM_GLINT);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(768, 1);
                    int colorValue = enchantmentGlintMod.dropped.glintColor.getColorValue();
                    float f10 = (colorValue >> 24 & 0xFF) / 255.0F;
                    float f11 = (colorValue >> 16 & 0xFF) / 255.0F;
                    float f12 = (colorValue >> 8 & 0xFF) / 255.0F;
                    float f13 = (colorValue & 0xFF) / 255.0F;
                    float f14 = f10 * 0.76F;
                    GL11.glColor4f(f11 * f14, f12 * f14, f13 * f14, 1.0F);
                    GL11.glMatrixMode(5890);
                    GL11.glPushMatrix();
                    float f15 = 0.125F;
                    GL11.glScalef(f15, f15, f15);
                    float f16 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                    GL11.glTranslatef(f16, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    ItemRenderer.renderItemIn2D(instance, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f8);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(f15, f15, f15);
                    f16 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                    GL11.glTranslatef(-f16, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    ItemRenderer.renderItemIn2D(instance, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f8);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(5888);
                    GL11.glDisable(3042);
                    GL11.glEnable(2896);
                    GL11.glDepthFunc(515);
                }
            }

            GL11.glPopMatrix();
        } else {
            for (int i = 0; i < n; i++) {
                GL11.glPushMatrix();
                if (i > 0) {
                    float f8 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    GL11.glTranslatef(f8, f9, f10);
                }

                if (!renderInFrame) {
                    GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    if ((Boolean)CheatBreaker.getInstance().getModManager().packTweaksMod.rotateTowardsCamera.getValue()
                        && CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled()) {
                        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    }
                }

                GL11.glColor4f(f2, f3, f4, 1.0F);
                instance.startDrawingQuads();
                instance.setNormal(0.0F, 1.0F, 0.0F);
                instance.addVertexWithUV(0.0F - f6, 0.0F - f7, 0.0, minU, maxV);
                instance.addVertexWithUV(f5 - f6, 0.0F - f7, 0.0, maxU, maxV);
                instance.addVertexWithUV(f5 - f6, 1.0F - f7, 0.0, maxU, minV);
                instance.addVertexWithUV(0.0F - f6, 1.0F - f7, 0.0, minU, minV);
                instance.draw();
                GL11.glPopMatrix();
            }
        }
    }

    public void renderItemIntoGUI(FontRenderer fontRenderer, TextureManager textureManager, ItemStack itemStack, int n, int n2) {
        int metadata = itemStack.getMetadata();
        IIcon iconIndex = itemStack.getIconIndex();
        if (itemStack.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemStack.getItem()).getRenderType())) {
            textureManager.bindTexture(TextureMap.locationBlocksTexture);
            Block blockFromItem = Block.getBlockFromItem(itemStack.getItem());
            GL11.glEnable(3008);
            if (blockFromItem.getRenderBlockPass() != 0) {
                GL11.glAlphaFunc(516, 0.1F);
                GL11.glEnable(3042);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            } else {
                GL11.glAlphaFunc(516, 0.5F);
                GL11.glDisable(3042);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(n - 2, n2 + 3, -3.0F + this.zLevel);
            GL11.glScalef(10.0F, 10.0F, 10.0F);
            GL11.glTranslatef(1.0F, 0.5F, 1.0F);
            GL11.glScalef(1.0F, 1.0F, -1.0F);
            GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            int colorFromItemStack = itemStack.getItem().getColorFromItemStack(itemStack, 0);
            float f = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
            float f2 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
            float f3 = (colorFromItemStack & 0xFF) / 255.0F;
            if (this.renderWithColor) {
                GL11.glColor4f(f, f2, f3, 1.0F);
            }

            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            this.renderBlocksRi.useInventoryTint = this.renderWithColor;
            this.renderBlocksRi.renderBlockAsItem(blockFromItem, metadata, 1.0F);
            this.renderBlocksRi.useInventoryTint = true;
            if (blockFromItem.getRenderBlockPass() == 0) {
                GL11.glAlphaFunc(516, 0.1F);
            }

            GL11.glPopMatrix();
        } else if (itemStack.getItem().requiresMultipleRenderPasses()) {
            GL11.glDisable(2896);
            GL11.glEnable(3008);
            textureManager.bindTexture(TextureMap.locationItemsTexture);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(0, 0, 0, 0);
            GL11.glColorMask(false, false, false, true);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Tessellator instance = Tessellator.instance;
            instance.startDrawingQuads();
            instance.setColorOpaque_I(-1);
            instance.addVertex(n - 2, n2 + 18, this.zLevel);
            instance.addVertex(n + 18, n2 + 18, this.zLevel);
            instance.addVertex(n + 18, n2 - 2, this.zLevel);
            instance.addVertex(n - 2, n2 - 2, this.zLevel);
            instance.draw();
            GL11.glColorMask(true, true, true, true);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);

            for (int i = 0; i <= 1; i++) {
                IIcon iconFromDamageForRenderPass = itemStack.getItem().getIconFromDamageForRenderPass(metadata, i);
                int colorFromItemStack = itemStack.getItem().getColorFromItemStack(itemStack, i);
                float f = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
                float f2 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
                float f3 = (colorFromItemStack & 0xFF) / 255.0F;
                if (this.renderWithColor) {
                    GL11.glColor4f(f, f2, f3, 1.0F);
                }

                this.renderIcon(n, n2, iconFromDamageForRenderPass, 16, 16);
            }

            GL11.glEnable(2896);
        } else {
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            ResourceLocation resourceLocation = textureManager.getResourceLocation(itemStack.getItemSpriteNumber());
            textureManager.bindTexture(resourceLocation);
            if (iconIndex == null) {
                iconIndex = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(resourceLocation)).getAtlasSprite("missingno");
            }

            int colorFromItemStack = itemStack.getItem().getColorFromItemStack(itemStack, 0);
            float f = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
            float f2 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
            float f3 = (colorFromItemStack & 0xFF) / 255.0F;
            if (this.renderWithColor) {
                GL11.glColor4f(f, f2, f3, 1.0F);
            }

            this.renderIcon(n, n2, iconIndex, 16, 16);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
        }

        GL11.glEnable(2884);
    }

    public void renderItemAndEffectIntoGUI(FontRenderer fontRenderer, TextureManager textureManager, final ItemStack itemStack, int n, int n2) {
        if (itemStack != null) {
            this.zLevel += 50.0F;

            try {
                this.renderItemIntoGUI(fontRenderer, textureManager, itemStack, n, n2);
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Rendering item");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Item being rendered");
                makeCategory.addCrashSectionCallable("Item Type", new Callable() {
                    public String call() {
                        return String.valueOf(itemStack.getItem());
                    }
                });
                makeCategory.addCrashSectionCallable("Item Aux", new Callable() {
                    public String call() {
                        return String.valueOf(itemStack.getMetadata());
                    }
                });
                makeCategory.addCrashSectionCallable("Item NBT", new Callable() {
                    public String call() {
                        return String.valueOf(itemStack.getTagCompound());
                    }
                });
                makeCategory.addCrashSectionCallable("Item Foil", new Callable() {
                    public String call() {
                        return String.valueOf(itemStack.hasEffect());
                    }
                });
                throw new ReportedException(makeCrashReport);
            }

            RenderItemIntoGuiEvent renderItemIntoGuiEvent = new RenderItemIntoGuiEvent();
            CheatBreaker.getInstance().getEventBus().handleEvent(renderItemIntoGuiEvent);
            EnchantmentGlintMod enchantmentGlintMod = CheatBreaker.getInstance().getModManager().enchantmentGlintMod;
            if (itemStack.hasEffect() && enchantmentGlintMod.isEnabled() && enchantmentGlintMod.inventory.glint.getBooleanValue()) {
                GL11.glDepthFunc(514);
                GL11.glDisable(2896);
                GL11.glDepthMask(false);
                textureManager.bindTexture(RES_ITEM_GLINT);
                GL11.glEnable(3008);
                GL11.glEnable(3042);
                int colorFromItemStack = itemStack.getItem().getColorFromItemStack(itemStack, 0);
                float f = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
                float f2 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
                float f3 = (colorFromItemStack & 0xFF) / 255.0F;
                int colorValue = enchantmentGlintMod.inventory.glintColor.getColorValue();
                float f4 = (colorValue >> 24 & 0xFF) / 255.0F;
                float f5 = (colorValue >> 16 & 0xFF) / 255.0F;
                float f6 = (colorValue >> 8 & 0xFF) / 255.0F;
                float f7 = (colorValue & 0xFF) / 255.0F;
                if (colorFromItemStack != 16777215 && (Boolean)enchantmentGlintMod.showPotionColor.getValue()) {
                    GL11.glColor4f(f * f4, f2 * f4, f3 * f4, 1.0F);
                } else {
                    GL11.glColor4f(f5 * f4, f6 * f4, f7 * f4, 1.0F);
                }

                if (colorFromItemStack != 16777215 && !(Boolean)enchantmentGlintMod.excludePotionGlint.getValue() || colorFromItemStack == 16777215 && !(Boolean)enchantmentGlintMod.onlyRenderPotionGlint.getValue()) {
                    if (enchantmentGlintMod.shinyPotsBoundary.getBooleanValue()) {
                        this.renderGlint(n * 431278612 + n2 * 32178161, n - 2, n2 - 2, 20, 20);
                    } else {
                        this.renderGlint(n * 431278612 + n2 * 32178161, n, n2, 16, 16);
                    }
                }

                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glDepthMask(true);
                GL11.glEnable(2896);
                GL11.glDepthFunc(515);
            }

            this.zLevel -= 50.0F;
        }
    }

    public void renderGlint(int n, int n2, int n3, int n4, int n5) {
        for (int i = 0; i < 2; i++) {
            OpenGlHelper.glBlendFunc((boolean)CheatBreaker.getInstance().getModManager().enchantmentGlintMod.shinyPots.getValue() ? 773 : 772, 1, 0, 0);
            float f = 0.00390625F;
            float f2 = 0.00390625F;
            float f3 = (float)(Minecraft.getSystemTime() % (3000 + i * 1873)) / (3000.0F + i * 1873) * 256.0F;
            float f4 = 0.0F;
            Tessellator instance = Tessellator.instance;
            float f5 = 4.0F;
            if (i == 1) {
                f5 = -1.0F;
            }

            instance.startDrawingQuads();
            instance.addVertexWithUV(n2 + 0, n3 + n5, this.zLevel, (f3 + n5 * f5) * f, (f4 + n5) * f2);
            instance.addVertexWithUV(n2 + n4, n3 + n5, this.zLevel, (f3 + n4 + n5 * f5) * f, (f4 + n5) * f2);
            instance.addVertexWithUV(n2 + n4, n3 + 0, this.zLevel, (f3 + n4) * f, (f4 + 0.0F) * f2);
            instance.addVertexWithUV(n2 + 0, n3 + 0, this.zLevel, (f3 + 0.0F) * f, (f4 + 0.0F) * f2);
            instance.draw();
        }
    }

    public void renderItemOverlayIntoGUI(FontRenderer fontRenderer, TextureManager textureManager, ItemStack itemStack, int n, int n2) {
        this.renderItemOverlayIntoGUI(fontRenderer, textureManager, itemStack, n, n2, null);
    }

    public void renderItemOverlayIntoGUI(FontRenderer fontRenderer, TextureManager textureManager, ItemStack itemStack, int n, int n2, String str) {
        if (itemStack != null) {
            if (itemStack.stackSize > 1 || str != null) {
                String str2 = str == null ? String.valueOf(itemStack.stackSize) : str;
                GL11.glDisable(2896);
                GL11.glDisable(2929);
                GL11.glDisable(3042);
                fontRenderer.drawShadowedString(str2, n + 19 - 2 - fontRenderer.getStringWidth(str2), n2 + 6 + 3, 16777215);
                GL11.glEnable(2896);
                GL11.glEnable(2929);
            }

            if (itemStack.isItemDamaged()) {
                int round = (int)Math.round(13.0 - itemStack.getCurrentDurability() * 13.0 / itemStack.getMaxDurability());
                int round2 = (int)Math.round(255.0 - itemStack.getCurrentDurability() * 255.0 / itemStack.getMaxDurability());
                GL11.glDisable(2896);
                GL11.glDisable(2929);
                GL11.glDisable(3553);
                GL11.glDisable(3008);
                GL11.glDisable(3042);
                Tessellator instance = Tessellator.instance;
                int n3 = 255 - round2 << 16 | round2 << 8;
                int n4 = (255 - round2) / 4 << 16 | 16128;
                this.renderQuad(instance, n + 2, n2 + 13, 13, 2, 0);
                this.renderQuad(instance, n + 2, n2 + 13, 12, 1, n4);
                this.renderQuad(instance, n + 2, n2 + 13, round, 1, n3);
                GL11.glEnable(3042);
                GL11.glEnable(3008);
                GL11.glEnable(3553);
                GL11.glEnable(2896);
                GL11.glEnable(2929);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    public void renderQuad(Tessellator tessellator, int n, int n2, int n3, int n4, int n5) {
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(n5);
        tessellator.addVertex(n + 0, n2 + 0, 0.0);
        tessellator.addVertex(n + 0, n2 + n4, 0.0);
        tessellator.addVertex(n + n3, n2 + n4, 0.0);
        tessellator.addVertex(n + n3, n2 + 0, 0.0);
        tessellator.draw();
    }

    public void renderIcon(int n, int n2, IIcon icon, int n3, int n4) {
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(n + 0, n2 + n4, this.zLevel, icon.getMinU(), icon.getMaxV());
        instance.addVertexWithUV(n + n3, n2 + n4, this.zLevel, icon.getMaxU(), icon.getMaxV());
        instance.addVertexWithUV(n + n3, n2 + 0, this.zLevel, icon.getMaxU(), icon.getMinV());
        instance.addVertexWithUV(n + 0, n2 + 0, this.zLevel, icon.getMinU(), icon.getMinV());
        instance.draw();
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityItem)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityItem)entity, d, d2, d3, f, f2);
    }
}
