package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderOcelot extends RenderLiving {
    public static ResourceLocation blackOcelotTextures = new ResourceLocation("textures/entity/cat/black.png");
    public static ResourceLocation ocelotTextures = new ResourceLocation("textures/entity/cat/ocelot.png");
    public static ResourceLocation redOcelotTextures = new ResourceLocation("textures/entity/cat/red.png");
    public static ResourceLocation siameseOcelotTextures = new ResourceLocation("textures/entity/cat/siamese.png");

    public RenderOcelot(ModelBase modelBase, float f) {
        super(modelBase, f);
    }

    public void doRender(EntityOcelot entityOcelot, double d, double d2, double d3, float f, float f2) {
        super.doRender(entityOcelot, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntityOcelot entityOcelot) {
        switch (entityOcelot.getTameSkin()) {
            case 0:
            default:
                return ocelotTextures;
            case 1:
                return blackOcelotTextures;
            case 2:
                return redOcelotTextures;
            case 3:
                return siameseOcelotTextures;
        }
    }

    public void preRenderCallback(EntityOcelot entityOcelot, float f) {
        super.preRenderCallback(entityOcelot, f);
        if (entityOcelot.isTamed()) {
            GL11.glScalef(0.8F, 0.8F, 0.8F);
        }
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityOcelot)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntityOcelot)entityLivingBase, f);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityOcelot)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityOcelot)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityOcelot)entity, d, d2, d3, f, f2);
    }
}
