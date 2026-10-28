package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderMinecart extends Render {
    public static ResourceLocation minecartTextures = new ResourceLocation("textures/entity/minecart.png");
    public ModelBase modelMinecart = new ModelMinecart();
    public RenderBlocks renderBlocks2;

    public RenderMinecart() {
        this.shadowSize = 0.5F;
        this.renderBlocks2 = new RenderBlocks();
    }

    public void doRender(EntityMinecart entityMinecart, double d, double d2, double d3, float f, float f2) {
        GL11.glPushMatrix();
        this.bindEntityTexture(entityMinecart);
        long l = entityMinecart.getEntityId() * 493286711L;
        l = l * l * 4392167121L + l * 98761L;
        float f3 = (((float)(l >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f4 = (((float)(l >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float f5 = (((float)(l >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(f3, f4, f5);
        double d4 = entityMinecart.lastTickPosX + (entityMinecart.posX - entityMinecart.lastTickPosX) * f2;
        double d5 = entityMinecart.lastTickPosY + (entityMinecart.posY - entityMinecart.lastTickPosY) * f2;
        double d6 = entityMinecart.lastTickPosZ + (entityMinecart.posZ - entityMinecart.lastTickPosZ) * f2;
        double d7 = 0.3F;
        Vec3 snapPositionToRail = entityMinecart.snapPositionToRail(d4, d5, d6);
        float f6 = entityMinecart.prevRotationPitch + (entityMinecart.rotationPitch - entityMinecart.prevRotationPitch) * f2;
        if (snapPositionToRail != null) {
            Vec3 snapPositionToRailWithOffset = entityMinecart.snapPositionToRailWithOffset(d4, d5, d6, d7);
            Vec3 snapPositionToRailWithOffset2 = entityMinecart.snapPositionToRailWithOffset(d4, d5, d6, -d7);
            if (snapPositionToRailWithOffset == null) {
                snapPositionToRailWithOffset = snapPositionToRail;
            }

            if (snapPositionToRailWithOffset2 == null) {
                snapPositionToRailWithOffset2 = snapPositionToRail;
            }

            d += snapPositionToRail.xCoord - d4;
            d2 += (snapPositionToRailWithOffset.yCoord + snapPositionToRailWithOffset2.yCoord) / 2.0 - d5;
            d3 += snapPositionToRail.zCoord - d6;
            Vec3 addVector = snapPositionToRailWithOffset2.addVector(-snapPositionToRailWithOffset.xCoord, -snapPositionToRailWithOffset.yCoord, -snapPositionToRailWithOffset.zCoord);
            if (addVector.lengthVector() != 0.0) {
                addVector = addVector.normalize();
                f = (float)(Math.atan2(addVector.zCoord, addVector.xCoord) * 180.0 / Math.PI);
                f6 = (float)(Math.atan(addVector.yCoord) * 73.0);
            }
        }

        GL11.glTranslatef((float)d, (float)d2, (float)d3);
        GL11.glRotatef(180.0F - f, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-f6, 0.0F, 0.0F, 1.0F);
        float f7 = entityMinecart.getRollingAmplitude() - f2;
        float f8 = entityMinecart.getDamage() - f2;
        if (f8 < 0.0F) {
            f8 = 0.0F;
        }

        if (f7 > 0.0F) {
            GL11.glRotatef(MathHelper.sin(f7) * f7 * f8 / 10.0F * entityMinecart.getRollingDirection(), 1.0F, 0.0F, 0.0F);
        }

        int displayTileOffset = entityMinecart.getDisplayTileOffset();
        Block displayTile = entityMinecart.getDisplayTile();
        int displayTileData = entityMinecart.getDisplayTileData();
        if (displayTile.getRenderType() != -1) {
            GL11.glPushMatrix();
            this.bindTexture(TextureMap.locationBlocksTexture);
            float f9 = 0.75F;
            GL11.glScalef(f9, f9, f9);
            GL11.glTranslatef(0.0F, displayTileOffset / 16.0F, 0.0F);
            this.render(entityMinecart, f2, displayTile, displayTileData);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindEntityTexture(entityMinecart);
        }

        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.modelMinecart.render(entityMinecart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    public ResourceLocation getEntityTexture(EntityMinecart entityMinecart) {
        return minecartTextures;
    }

    public void render(EntityMinecart entityMinecart, float f, Block block, int n) {
        float brightness = entityMinecart.getBrightness(f);
        GL11.glPushMatrix();
        this.renderBlocks2.renderBlockAsItem(block, n, brightness);
        GL11.glPopMatrix();
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityMinecart)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityMinecart)entity, d, d2, d3, f, f2);
    }
}
