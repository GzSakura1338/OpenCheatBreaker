package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGhast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderGhast extends RenderLiving {
    public static ResourceLocation ghastTextures = new ResourceLocation("textures/entity/ghast/ghast.png");
    public static ResourceLocation ghastShootingTextures = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

    public RenderGhast() {
        super(new ModelGhast(), 0.5F);
    }

    public ResourceLocation getEntityTexture(EntityGhast entityGhast) {
        return entityGhast.isShooting() ? ghastShootingTextures : ghastTextures;
    }

    public void preRenderCallback(EntityGhast entityGhast, float f) {
        float f2 = (entityGhast.prevAttackCounter + (entityGhast.attackCounter - entityGhast.prevAttackCounter) * f) / 20.0F;
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        f2 = 1.0F / (f2 * f2 * f2 * f2 * f2 * 2.0F + 1.0F);
        float f3 = (8.0F + f2) / 2.0F;
        float f4 = (8.0F + 1.0F / f2) / 2.0F;
        GL11.glScalef(f4, f3, f4);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntityGhast)entityLivingBase, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityGhast)entity);
    }
}
