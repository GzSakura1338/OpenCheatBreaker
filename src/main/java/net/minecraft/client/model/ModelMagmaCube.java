package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;

public class ModelMagmaCube extends ModelBase {
    public ModelRenderer[] modelRendererArray = new ModelRenderer[8];
    public ModelRenderer modelRenderer;

    public ModelMagmaCube() {
        for (int i = 0; i < this.modelRendererArray.length; i++) {
            byte b = 0;
            int n = i;
            if (i == 2) {
                b = 24;
                n = 10;
            } else if (i == 3) {
                b = 24;
                n = 19;
            }

            this.modelRendererArray[i] = new ModelRenderer(this, b, n);
            this.modelRendererArray[i].addBox(-4.0F, 16 + i, -4.0F, 8, 1, 8);
        }

        this.modelRenderer = new ModelRenderer(this, 0, 16);
        this.modelRenderer.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entityLivingBase, float f, float f2, float f3) {
        EntityMagmaCube entityMagmaCube = (EntityMagmaCube)entityLivingBase;
        float f4 = entityMagmaCube.prevSquishFactor + (entityMagmaCube.squishFactor - entityMagmaCube.prevSquishFactor) * f3;
        if (f4 < 0.0F) {
            f4 = 0.0F;
        }

        for (int i = 0; i < this.modelRendererArray.length; i++) {
            this.modelRendererArray[i].rotationPointY = -(4 - i) * f4 * 1.7F;
        }
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        this.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        this.modelRenderer.render(f6);

        for (int i = 0; i < this.modelRendererArray.length; i++) {
            this.modelRendererArray[i].render(f6);
        }
    }
}
