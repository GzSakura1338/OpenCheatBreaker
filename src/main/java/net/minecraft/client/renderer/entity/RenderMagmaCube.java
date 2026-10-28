package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderMagmaCube extends RenderLiving {
    public static ResourceLocation magmaCubeTextures = new ResourceLocation("textures/entity/slime/magmacube.png");

    public RenderMagmaCube() {
        super(new ModelMagmaCube(), 0.25F);
    }

    public ResourceLocation getEntityTexture(EntityMagmaCube entityMagmaCube) {
        return magmaCubeTextures;
    }

    public void preRenderCallback(EntityMagmaCube entityMagmaCube, float f) {
        int slimeSize = entityMagmaCube.getSlimeSize();
        float f2 = (entityMagmaCube.prevSquishFactor + (entityMagmaCube.squishFactor - entityMagmaCube.prevSquishFactor) * f) / (slimeSize * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        float f4 = slimeSize;
        GL11.glScalef(f3 * f4, 1.0F / f3 * f4, f3 * f4);
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntityMagmaCube)entityLivingBase, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityMagmaCube)entity);
    }
}
