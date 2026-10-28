package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderGiantZombie extends RenderLiving {
    public static ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
    public float scale;

    public RenderGiantZombie(ModelBase modelBase, float f, float f2) {
        super(modelBase, f * f2);
        this.scale = f2;
    }

    public void preRenderCallback(EntityGiantZombie entityGiantZombie, float f) {
        GL11.glScalef(this.scale, this.scale, this.scale);
    }

    public ResourceLocation getEntityTexture(EntityGiantZombie entityGiantZombie) {
        return zombieTextures;
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntityGiantZombie)entityLivingBase, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityGiantZombie)entity);
    }
}
