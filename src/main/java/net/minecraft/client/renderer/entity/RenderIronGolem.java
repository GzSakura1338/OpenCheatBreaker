package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderIronGolem extends RenderLiving {
    public static ResourceLocation ironGolemTextures = new ResourceLocation("textures/entity/iron_golem.png");
    public ModelIronGolem ironGolemModel = (ModelIronGolem)this.mainModel;

    public RenderIronGolem() {
        super(new ModelIronGolem(), 0.5F);
    }

    public void doRender(EntityIronGolem entityIronGolem, double d, double d2, double d3, float f, float f2) {
        super.doRender(entityIronGolem, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntityIronGolem entityIronGolem) {
        return ironGolemTextures;
    }

    public void rotateCorpse(EntityIronGolem entityIronGolem, float f, float f2, float f3) {
        super.rotateCorpse(entityIronGolem, f, f2, f3);
        if (entityIronGolem.limbSwingAmount >= 0.01) {
            float f4 = 13.0F;
            float f5 = entityIronGolem.limbSwing - entityIronGolem.limbSwingAmount * (1.0F - f3) + 6.0F;
            float f6 = (Math.abs(f5 % f4 - f4 * 0.5F) - f4 * 0.25F) / (f4 * 0.25F);
            GL11.glRotatef(6.5F * f6, 0.0F, 0.0F, 1.0F);
        }
    }

    public void renderEquippedItems(EntityIronGolem entityIronGolem, float f) {
        super.renderEquippedItems(entityIronGolem, f);
        if (entityIronGolem.getHoldRoseTick() != 0) {
            GL11.glEnable(32826);
            GL11.glPushMatrix();
            GL11.glRotatef(5.0F + 180.0F * this.ironGolemModel.ironGolemRightArm.rotateAngleX / (float) Math.PI, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(-0.6875F, 1.25F, -0.9375F);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            float f2 = 0.8F;
            GL11.glScalef(f2, -f2, f2);
            int brightnessForRender = entityIronGolem.getBrightnessForRender(f);
            int n = brightnessForRender % 65536;
            int n2 = brightnessForRender / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindTexture(TextureMap.locationBlocksTexture);
            this.renderBlocks.renderBlockAsItem(Blocks.red_flower, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glDisable(32826);
        }
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityIronGolem)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
        this.renderEquippedItems((EntityIronGolem)entityLivingBase, f);
    }

    @Override
    public void rotateCorpse(EntityLivingBase entityLivingBase, float f, float f2, float f3) {
        this.rotateCorpse((EntityIronGolem)entityLivingBase, f, f2, f3);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityIronGolem)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityIronGolem)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityIronGolem)entity, d, d2, d3, f, f2);
    }
}
