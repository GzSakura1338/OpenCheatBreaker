package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;

public class ModelMagmaCube extends ModelBase {
   public ModelRenderer[] segments = new ModelRenderer[8];
   public ModelRenderer core;

   public ModelMagmaCube() {
      for (int i = 0; i < this.segments.length; i++) {
         byte b = 0;
         int n = i;
         if (i == 2) {
            b = 24;
            n = 10;
         } else if (i == 3) {
            b = 24;
            n = 19;
         }

         this.segments[i] = new ModelRenderer(this, b, n);
         this.segments[i].addBox(-4.0F, 16 + i, -4.0F, 8, 1, 8);
      }

      this.core = new ModelRenderer(this, 0, 16);
      this.core.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
   }

   @Override
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTickTime) {
      EntityMagmaCube entitylivingbaseIn2 = (EntityMagmaCube)entitylivingbaseIn;
      float f3 = entitylivingbaseIn2.prevSquishFactor + (entitylivingbaseIn2.squishFactor - entitylivingbaseIn2.prevSquishFactor) * partialTickTime;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      for (int i = 0; i < this.segments.length; i++) {
         this.segments[i].rotationPointY = -(4 - i) * f3 * 1.7F;
      }
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);
      this.core.render(scale);

      for (int i = 0; i < this.segments.length; i++) {
         this.segments[i].render(scale);
      }
   }
}
