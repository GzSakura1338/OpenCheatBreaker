package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderChicken extends RenderLiving {
    public static ResourceLocation chickenTextures = new ResourceLocation("textures/entity/chicken.png");

    public RenderChicken(ModelBase modelBase, float f) {
        super(modelBase, f);
    }

    public void doRender(EntityChicken entityChicken, double d, double d2, double d3, float f, float f2) {
        super.doRender(entityChicken, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntityChicken entityChicken) {
        return chickenTextures;
    }

    public float handleRotationFloat(EntityChicken entityChicken, float f) {
        float f2 = entityChicken.prevFlapProgress + (entityChicken.flapProgress - entityChicken.prevFlapProgress) * f;
        float f3 = entityChicken.prevMaxWingDeviation + (entityChicken.destPos - entityChicken.prevMaxWingDeviation) * f;
        return (MathHelper.sin(f2) + 1.0F) * f3;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityChicken)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public float handleRotationFloat(EntityLivingBase entityLivingBase, float f) {
        return this.handleRotationFloat((EntityChicken)entityLivingBase, f);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityChicken)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityChicken)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityChicken)entity, d, d2, d3, f, f2);
    }
}
