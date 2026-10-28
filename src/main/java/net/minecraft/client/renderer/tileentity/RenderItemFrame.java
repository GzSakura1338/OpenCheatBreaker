package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.Config;
import net.optifine.Reflector;
import org.lwjgl.opengl.GL11;
import shadersmod.client.ShadersTex;

public class RenderItemFrame extends Render {
    public static ResourceLocation mapBackgroundTextures = new ResourceLocation("textures/map/map_background.png");
    public RenderBlocks renderBlocks2 = new RenderBlocks();
    public Minecraft minecraft = Minecraft.getMinecraft();
    public IIcon iIcon;

    @Override
    public void updateIcons(IIconRegister iconRegister) {
        this.iIcon = iconRegister.registerIcon("itemframe_background");
    }

    public void doRender(EntityItemFrame entityItemFrame, double d, double d2, double d3, float f, float f2) {
        GL11.glPushMatrix();
        double d4 = entityItemFrame.posX - d - 0.5;
        double d5 = entityItemFrame.posY - d2 - 0.5;
        double d6 = entityItemFrame.posZ - d3 - 0.5;
        int n = entityItemFrame.intValue + Direction.offsetX[entityItemFrame.hangingDirection];
        int intValue2 = entityItemFrame.intValue2;
        int n2 = entityItemFrame.intValue3 + Direction.offsetZ[entityItemFrame.hangingDirection];
        GL11.glTranslated(n - d4, intValue2 - d5, n2 - d6);
        if (entityItemFrame.getDisplayedItem() != null && entityItemFrame.getDisplayedItem().getItem() == Items.filled_map) {
            this.render(entityItemFrame);
        } else {
            this.renderFrameItemAsBlock(entityItemFrame);
        }

        this.render2(entityItemFrame);
        GL11.glPopMatrix();
        this.render(entityItemFrame, d + Direction.offsetX[entityItemFrame.hangingDirection] * 0.3F, d2 - 0.25, d3 + Direction.offsetZ[entityItemFrame.hangingDirection] * 0.3F);
    }

    public ResourceLocation getEntityTexture(EntityItemFrame entityItemFrame) {
        return null;
    }

    public void render(EntityItemFrame entityItemFrame) {
        GL11.glPushMatrix();
        GL11.glRotatef(entityItemFrame.rotationYaw, 0.0F, 1.0F, 0.0F);
        this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        Block planks = Blocks.planks;
        float f = 0.0625F;
        float f2 = 1.0F;
        float f3 = f2 / 2.0F;
        GL11.glPushMatrix();
        this.renderBlocks2.overrideBlockBounds(0.0, 0.5F - f3 + 0.0625F, 0.5F - f3 + 0.0625F, f, 0.5F + f3 - 0.0625F, 0.5F + f3 - 0.0625F);
        this.renderBlocks2.setOverrideBlockTexture(this.iIcon);
        this.renderBlocks2.renderBlockAsItem(planks, 0, 1.0F);
        this.renderBlocks2.clearOverrideBlockTexture();
        this.renderBlocks2.unlockBlockBounds();
        GL11.glPopMatrix();
        this.renderBlocks2.setOverrideBlockTexture(Blocks.planks.getIcon(1, 2));
        GL11.glPushMatrix();
        this.renderBlocks2.overrideBlockBounds(0.0, 0.5F - f3, 0.5F - f3, f + 1.0E-4F, f + 0.5F - f3, 0.5F + f3);
        this.renderBlocks2.renderBlockAsItem(planks, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks2.overrideBlockBounds(0.0, 0.5F + f3 - f, 0.5F - f3, f + 1.0E-4F, 0.5F + f3, 0.5F + f3);
        this.renderBlocks2.renderBlockAsItem(planks, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks2.overrideBlockBounds(0.0, 0.5F - f3, 0.5F - f3, f, 0.5F + f3, f + 0.5F - f3);
        this.renderBlocks2.renderBlockAsItem(planks, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks2.overrideBlockBounds(0.0, 0.5F - f3, 0.5F + f3 - f, f, 0.5F + f3, 0.5F + f3);
        this.renderBlocks2.renderBlockAsItem(planks, 0, 1.0F);
        GL11.glPopMatrix();
        this.renderBlocks2.unlockBlockBounds();
        this.renderBlocks2.clearOverrideBlockTexture();
        GL11.glPopMatrix();
    }

    public void renderFrameItemAsBlock(EntityItemFrame entityItemFrame) {
        GL11.glPushMatrix();
        GL11.glRotatef(entityItemFrame.rotationYaw, 0.0F, 1.0F, 0.0F);
        this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        Block planks = Blocks.planks;
        float f = 0.0625F;
        float f2 = 0.75F;
        float f3 = f2 / 2.0F;
        GL11.glPushMatrix();
        this.renderBlocks2.overrideBlockBounds(0.0, 0.5F - f3 + 0.0625F, 0.5F - f3 + 0.0625F, f * 0.5F, 0.5F + f3 - 0.0625F, 0.5F + f3 - 0.0625F);
        this.renderBlocks2.setOverrideBlockTexture(this.iIcon);
        this.renderBlocks2.renderBlockAsItem(planks, 0, 1.0F);
        this.renderBlocks2.clearOverrideBlockTexture();
        this.renderBlocks2.unlockBlockBounds();
        GL11.glPopMatrix();
        this.renderBlocks2.setOverrideBlockTexture(Blocks.planks.getIcon(1, 2));
        GL11.glPushMatrix();
        this.renderBlocks2.overrideBlockBounds(0.0, 0.5F - f3, 0.5F - f3, f + 1.0E-4F, f + 0.5F - f3, 0.5F + f3);
        this.renderBlocks2.renderBlockAsItem(planks, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks2.overrideBlockBounds(0.0, 0.5F + f3 - f, 0.5F - f3, f + 1.0E-4F, 0.5F + f3, 0.5F + f3);
        this.renderBlocks2.renderBlockAsItem(planks, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks2.overrideBlockBounds(0.0, 0.5F - f3, 0.5F - f3, f, 0.5F + f3, f + 0.5F - f3);
        this.renderBlocks2.renderBlockAsItem(planks, 0, 1.0F);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.renderBlocks2.overrideBlockBounds(0.0, 0.5F - f3, 0.5F + f3 - f, f, 0.5F + f3, 0.5F + f3);
        this.renderBlocks2.renderBlockAsItem(planks, 0, 1.0F);
        GL11.glPopMatrix();
        this.renderBlocks2.unlockBlockBounds();
        this.renderBlocks2.clearOverrideBlockTexture();
        GL11.glPopMatrix();
    }

    public void render2(EntityItemFrame entityItemFrame) {
        ItemStack displayedItem = entityItemFrame.getDisplayedItem();
        if (displayedItem != null) {
            EntityItem entityItem = new EntityItem(entityItemFrame.worldObj, 0.0, 0.0, 0.0, displayedItem);
            Item item = entityItem.getEntityItem().getItem();
            entityItem.getEntityItem().stackSize = 1;
            entityItem.hoverStart = 0.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.453125F * Direction.offsetX[entityItemFrame.hangingDirection], -0.18F, -0.453125F * Direction.offsetZ[entityItemFrame.hangingDirection]);
            GL11.glRotatef(180.0F + entityItemFrame.rotationYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-90 * entityItemFrame.getRotation(), 0.0F, 0.0F, 1.0F);
            switch (entityItemFrame.getRotation()) {
                case 1:
                    GL11.glTranslatef(-0.16F, -0.16F, 0.0F);
                    break;
                case 2:
                    GL11.glTranslatef(0.0F, -0.32F, 0.0F);
                    break;
                case 3:
                    GL11.glTranslatef(0.16F, -0.16F, 0.0F);
            }

            if (!Reflector.postForgeBusEvent(Reflector.RenderItemInFrameEvent_Constructor, entityItemFrame, this)) {
                if (item == Items.filled_map) {
                    this.renderManager.renderEngine.bindTexture(mapBackgroundTextures);
                    Tessellator instance = Tessellator.instance;
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    float f = 0.0078125F;
                    GL11.glScalef(f, f, f);
                    switch (entityItemFrame.getRotation()) {
                        case 0:
                            GL11.glTranslatef(-64.0F, -87.0F, -1.5F);
                            break;
                        case 1:
                            GL11.glTranslatef(-66.5F, -84.5F, -1.5F);
                            break;
                        case 2:
                            GL11.glTranslatef(-64.0F, -82.0F, -1.5F);
                            break;
                        case 3:
                            GL11.glTranslatef(-61.5F, -84.5F, -1.5F);
                    }

                    GL11.glNormal3f(0.0F, 0.0F, -1.0F);
                    MapData mapData = Items.filled_map.getMapData(entityItem.getEntityItem(), entityItemFrame.worldObj);
                    GL11.glTranslatef(0.0F, 0.0F, -1.0F);
                    if (mapData != null) {
                        this.minecraft.entityRenderer.getMapItemRenderer().draw(mapData, true);
                    }
                } else {
                    if (item == Items.compass) {
                        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
                        if (Config.isShaders()) {
                            ShadersTex.bindTextureMapForUpdateAndRender(Config.getMinecraft().getTextureManager(), TextureMap.locationBlocksTexture);
                        } else {
                            textureManager.bindTexture(TextureMap.locationItemsTexture);
                        }

                        TextureAtlasSprite atlasSprite = ((TextureMap)textureManager.getTexture(TextureMap.locationItemsTexture))
                            .getAtlasSprite(Items.compass.getIconIndex(entityItem.getEntityItem()).getIconName());
                        if (atlasSprite instanceof TextureCompass) {
                            TextureCompass textureCompass = (TextureCompass)atlasSprite;
                            double currentAngle = textureCompass.currentAngle;
                            double angleDelta = textureCompass.angleDelta;
                            textureCompass.currentAngle = 0.0;
                            textureCompass.angleDelta = 0.0;
                            textureCompass.updateCompass(entityItemFrame.worldObj, entityItemFrame.posX, entityItemFrame.posZ, MathHelper.wrapAngleTo180_float(180 + entityItemFrame.hangingDirection * 90), false, true);
                            textureCompass.currentAngle = currentAngle;
                            textureCompass.angleDelta = angleDelta;
                        }
                    }

                    RenderItem.renderInFrame = true;
                    RenderManager.instance.renderEntityWithPosYaw(entityItem, 0.0, 0.0, 0.0, 0.0F, 0.0F);
                    RenderItem.renderInFrame = false;
                    if (item == Items.compass) {
                        TextureAtlasSprite atlasSprite = ((TextureMap)Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationItemsTexture))
                            .getAtlasSprite(Items.compass.getIconIndex(entityItem.getEntityItem()).getIconName());
                        if (atlasSprite.getFrameCount() > 0) {
                            atlasSprite.updateAnimation();
                        }
                    }
                }
            }

            GL11.glPopMatrix();
        }
    }

    public void render(EntityItemFrame entityItemFrame, double d, double d2, double d3) {
        if (Minecraft.isGuiEnabled() && entityItemFrame.getDisplayedItem() != null && entityItemFrame.getDisplayedItem().hasDisplayName() && this.renderManager.entity == entityItemFrame) {
            float f = 1.6F;
            float f2 = 0.016666668F * f;
            double distanceSqToEntity = entityItemFrame.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float f3 = entityItemFrame.isSneaking() ? 32.0F : 64.0F;
            if (distanceSqToEntity < f3 * f3) {
                String displayName = entityItemFrame.getDisplayedItem().getDisplayName();
                if (entityItemFrame.isSneaking()) {
                    FontRenderer fontRendererFromRenderManager = this.getFontRendererFromRenderManager();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)d + 0.0F, (float)d2 + entityItemFrame.height + 0.5F, (float)d3);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GL11.glScalef(-f2, -f2, f2);
                    GL11.glDisable(2896);
                    GL11.glTranslatef(0.0F, 0.25F / f2, 0.0F);
                    GL11.glDepthMask(false);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    Tessellator instance = Tessellator.instance;
                    GL11.glDisable(3553);
                    instance.startDrawingQuads();
                    int n = fontRendererFromRenderManager.getStringWidth(displayName) / 2;
                    instance.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                    instance.addVertex(-n - 1, -1.0, 0.0);
                    instance.addVertex(-n - 1, 8.0, 0.0);
                    instance.addVertex(n + 1, 8.0, 0.0);
                    instance.addVertex(n + 1, -1.0, 0.0);
                    instance.draw();
                    GL11.glEnable(3553);
                    GL11.glDepthMask(true);
                    fontRendererFromRenderManager.drawString(displayName, -fontRendererFromRenderManager.getStringWidth(displayName) / 2, 0, 553648127);
                    GL11.glEnable(2896);
                    GL11.glDisable(3042);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                } else {
                    this.renderLivingLabel(entityItemFrame, displayName, d, d2, d3, 64);
                }
            }
        }
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityItemFrame)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityItemFrame)entity, d, d2, d3, f, f2);
    }
}
