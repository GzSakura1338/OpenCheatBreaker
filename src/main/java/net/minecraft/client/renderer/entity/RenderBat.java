package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBat extends RenderLiving {
    public static ResourceLocation batTextures = new ResourceLocation("textures/entity/bat.png");
    public int renderedBatSize = ((ModelBat)this.mainModel).getBatSize();

    public RenderBat() {
        super(new ModelBat(), 0.25F);
    }

    public void doRender(EntityBat entityBat, double d, double d2, double d3, float f, float f2) {
        int batSize = ((ModelBat)this.mainModel).getBatSize();
        if (batSize != this.renderedBatSize) {
            this.renderedBatSize = batSize;
            this.mainModel = new ModelBat();
        }

        super.doRender(entityBat, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntityBat entityBat) {
        return batTextures;
    }

    public void preRenderCallback(EntityBat entityBat, float f) {
        GL11.glScalef(0.35F, 0.35F, 0.35F);
    }

    public void renderLivingAt(EntityBat entityBat, double d, double d2, double d3) {
        super.renderLivingAt(entityBat, d, d2, d3);
    }

    public void rotateCorpse(EntityBat entityBat, float f, float f2, float f3) {
        if (!entityBat.getIsBatHanging()) {
            GL11.glTranslatef(0.0F, MathHelper.cos(f * 0.3F) * 0.1F, 0.0F);
        } else {
            GL11.glTranslatef(0.0F, -0.1F, 0.0F);
        }

        super.rotateCorpse(entityBat, f, f2, f3);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityBat)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntityBat)entityLivingBase, f);
    }

    @Override
    public void rotateCorpse(EntityLivingBase entityLivingBase, float f, float f2, float f3) {
        this.rotateCorpse((EntityBat)entityLivingBase, f, f2, f3);
    }

    @Override
    public void renderLivingAt(EntityLivingBase entityLivingBase, double d, double d2, double d3) {
        this.renderLivingAt((EntityBat)entityLivingBase, d, d2, d3);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityBat)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityBat)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityBat)entity, d, d2, d3, f, f2);
    }
}
