package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWolf extends RenderLiving {
    public static ResourceLocation wolfTextures = new ResourceLocation("textures/entity/wolf/wolf.png");
    public static ResourceLocation tamedWolfTextures = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
    public static ResourceLocation anrgyWolfTextures = new ResourceLocation("textures/entity/wolf/wolf_angry.png");
    public static ResourceLocation wolfCollarTextures = new ResourceLocation("textures/entity/wolf/wolf_collar.png");

    public RenderWolf(ModelBase modelBase, ModelBase modelBase2, float f) {
        super(modelBase, f);
        this.setRenderPassModel(modelBase2);
    }

    public float handleRotationFloat(EntityWolf entityWolf, float f) {
        return entityWolf.getTailRotation();
    }

    public int shouldRenderPass(EntityWolf entityWolf, int n, float f) {
        if (n == 0 && entityWolf.isWolfWet()) {
            float f2 = entityWolf.getBrightness(f) * entityWolf.getShadingWhileWet(f);
            this.bindTexture(wolfTextures);
            GL11.glColor3f(f2, f2, f2);
            return 1;
        } else if (n == 1 && entityWolf.isTamed()) {
            this.bindTexture(wolfCollarTextures);
            int collarColor = entityWolf.getCollarColor();
            GL11.glColor3f(EntitySheep.fleeceColorTable[collarColor][0], EntitySheep.fleeceColorTable[collarColor][1], EntitySheep.fleeceColorTable[collarColor][2]);
            return 1;
        } else {
            return -1;
        }
    }

    public ResourceLocation getEntityTexture(EntityWolf entityWolf) {
        return entityWolf.isTamed() ? tamedWolfTextures : (entityWolf.isAngry() ? anrgyWolfTextures : wolfTextures);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntityWolf)entityLivingBase, n, f);
    }

    @Override
    public float handleRotationFloat(EntityLivingBase entityLivingBase, float f) {
        return this.handleRotationFloat((EntityWolf)entityLivingBase, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityWolf)entity);
    }
}
