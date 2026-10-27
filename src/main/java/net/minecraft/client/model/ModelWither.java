package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;

public class ModelWither extends ModelBase {
   public ModelRenderer[] modelRendererArray;
   public ModelRenderer[] modelRendererArray2;

   public ModelWither(float f) {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.modelRendererArray = new ModelRenderer[3];
      this.modelRendererArray[0] = new ModelRenderer(this, 0, 16);
      this.modelRendererArray[0].addBox(-10.0F, 3.9F, -0.5F, 20, 3, 3, f);
      this.modelRendererArray[1] = new ModelRenderer(this).setTextureSize(this.textureWidth, this.textureHeight);
      this.modelRendererArray[1].setRotationPoint(-2.0F, 6.9F, -0.5F);
      this.modelRendererArray[1].setTextureOffset(0, 22).addBox(0.0F, 0.0F, 0.0F, 3, 10, 3, f);
      this.modelRendererArray[1].setTextureOffset(24, 22).addBox(-4.0F, 1.5F, 0.5F, 11, 2, 2, f);
      this.modelRendererArray[1].setTextureOffset(24, 22).addBox(-4.0F, 4.0F, 0.5F, 11, 2, 2, f);
      this.modelRendererArray[1].setTextureOffset(24, 22).addBox(-4.0F, 6.5F, 0.5F, 11, 2, 2, f);
      this.modelRendererArray[2] = new ModelRenderer(this, 12, 22);
      this.modelRendererArray[2].addBox(0.0F, 0.0F, 0.0F, 3, 6, 3, f);
      this.modelRendererArray2 = new ModelRenderer[3];
      this.modelRendererArray2[0] = new ModelRenderer(this, 0, 0);
      this.modelRendererArray2[0].addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, f);
      this.modelRendererArray2[1] = new ModelRenderer(this, 32, 0);
      this.modelRendererArray2[1].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
      this.modelRendererArray2[1].rotationPointX = -8.0F;
      this.modelRendererArray2[1].rotationPointY = 4.0F;
      this.modelRendererArray2[2] = new ModelRenderer(this, 32, 0);
      this.modelRendererArray2[2].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
      this.modelRendererArray2[2].rotationPointX = 10.0F;
      this.modelRendererArray2[2].rotationPointY = 4.0F;
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);

      for (ModelRenderer modelRenderer : this.modelRendererArray2) {
         modelRenderer.render(scale);
      }

      for (ModelRenderer modelRenderer : this.modelRendererArray) {
         modelRenderer.render(scale);
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      float cos = MathHelper.cos(ageInTicks * 0.1F);
      this.modelRendererArray[1].rotateAngleX = (0.065F + 0.05F * cos) * (float) Math.PI;
      this.modelRendererArray[2]
         .setRotationPoint(
            -2.0F,
            6.9F + MathHelper.cos(this.modelRendererArray[1].rotateAngleX) * 10.0F,
            -0.5F + MathHelper.sin(this.modelRendererArray[1].rotateAngleX) * 10.0F
         );
      this.modelRendererArray[2].rotateAngleX = (0.265F + 0.1F * cos) * (float) Math.PI;
      this.modelRendererArray2[0].rotateAngleY = netHeadYaw / (180.0F / (float)Math.PI);
      this.modelRendererArray2[0].rotateAngleX = headPitch / (180.0F / (float)Math.PI);
   }

   @Override
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTickTime) {
      EntityWither entitylivingbaseIn2 = (EntityWither)entitylivingbaseIn;

      for (int i = 1; i < 3; i++) {
         this.modelRendererArray2[i].rotateAngleY = (entitylivingbaseIn2.getHeadYaw(i - 1) - entitylivingbaseIn.renderYawOffset) / (180.0F / (float)Math.PI);
         this.modelRendererArray2[i].rotateAngleX = entitylivingbaseIn2.getHeadPitch(i - 1) / (180.0F / (float)Math.PI);
      }
   }
}
