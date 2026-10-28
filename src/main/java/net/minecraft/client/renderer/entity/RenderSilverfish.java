package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

public class RenderSilverfish extends RenderLiving {
    public static ResourceLocation silverfishTextures = new ResourceLocation("textures/entity/silverfish.png");

    public RenderSilverfish() {
        super(new ModelSilverfish(), 0.3F);
    }

    public float getDeathMaxRotation(EntitySilverfish entitySilverfish) {
        return 180.0F;
    }

    public void doRender(EntitySilverfish entitySilverfish, double d, double d2, double d3, float f, float f2) {
        super.doRender(entitySilverfish, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntitySilverfish entitySilverfish) {
        return silverfishTextures;
    }

    public int shouldRenderPass(EntitySilverfish entitySilverfish, int n, float f) {
        return -1;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntitySilverfish)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public float getDeathMaxRotation(EntityLivingBase entityLivingBase) {
        return this.getDeathMaxRotation((EntitySilverfish)entityLivingBase);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntitySilverfish)entityLivingBase, n, f);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntitySilverfish)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntitySilverfish)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntitySilverfish)entity, d, d2, d3, f, f2);
    }
}
