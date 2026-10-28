package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSkeleton extends RenderBiped {
    public static ResourceLocation skeletonTextures = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    public static ResourceLocation witherSkeletonTextures = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

    public RenderSkeleton() {
        super(new ModelSkeleton(), 0.5F);
    }

    public void preRenderCallback(EntitySkeleton entitySkeleton, float f) {
        if (entitySkeleton.getSkeletonType() == 1) {
            GL11.glScalef(1.2F, 1.2F, 1.2F);
        }
    }

    @Override
    public void glTranslatef() {
        GL11.glTranslatef(0.09375F, 0.1875F, 0.0F);
    }

    public ResourceLocation getEntityTexture(EntitySkeleton entitySkeleton) {
        return entitySkeleton.getSkeletonType() == 1 ? witherSkeletonTextures : skeletonTextures;
    }

    @Override
    public ResourceLocation getEntityTexture(EntityLiving entityLiving) {
        return this.getEntityTexture((EntitySkeleton)entityLiving);
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntitySkeleton)entityLivingBase, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntitySkeleton)entity);
    }
}
