package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSlime extends RenderLiving {
    public static ResourceLocation slimeTextures = new ResourceLocation("textures/entity/slime/slime.png");
    public ModelBase scaleAmount;

    public RenderSlime(ModelBase modelBase, ModelBase modelBase2, float f) {
        super(modelBase, f);
        this.scaleAmount = modelBase2;
    }

    public int shouldRenderPass(EntitySlime entitySlime, int n, float f) {
        if (entitySlime.isInvisible()) {
            return 0;
        }

        if (n == 0) {
            this.setRenderPassModel(this.scaleAmount);
            GL11.glEnable(2977);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            return 1;
        }

        if (n == 1) {
            GL11.glDisable(3042);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        return -1;
    }

    public void preRenderCallback(EntitySlime entitySlime, float f) {
        float slimeSize = entitySlime.getSlimeSize();
        float f2 = (entitySlime.prevSquishFactor + (entitySlime.squishFactor - entitySlime.prevSquishFactor) * f) / (slimeSize * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        GL11.glScalef(f3 * slimeSize, 1.0F / f3 * slimeSize, f3 * slimeSize);
    }

    public ResourceLocation getEntityTexture(EntitySlime entitySlime) {
        return slimeTextures;
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntitySlime)entityLivingBase, f);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntitySlime)entityLivingBase, n, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntitySlime)entity);
    }
}
