package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;

public class RenderPig extends RenderLiving {
    public static ResourceLocation saddledPigTextures = new ResourceLocation("textures/entity/pig/pig_saddle.png");
    public static ResourceLocation pigTextures = new ResourceLocation("textures/entity/pig/pig.png");

    public RenderPig(ModelBase modelBase, ModelBase modelBase2, float f) {
        super(modelBase, f);
        this.setRenderPassModel(modelBase2);
    }

    public int shouldRenderPass(EntityPig entityPig, int n, float f) {
        if (n == 0 && entityPig.getSaddled()) {
            this.bindTexture(saddledPigTextures);
            return 1;
        } else {
            return -1;
        }
    }

    public ResourceLocation getEntityTexture(EntityPig entityPig) {
        return pigTextures;
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntityPig)entityLivingBase, n, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityPig)entity);
    }
}
