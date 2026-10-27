package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelQuadruped extends ModelBase {
   public ModelRenderer head = new ModelRenderer(this, 0, 0);
   public float childYOffset = 8.0F;
   public float childZOffset = 4.0F;
   public ModelRenderer body;
   public ModelRenderer leg1;
   public ModelRenderer leg2;
   public ModelRenderer leg3;
   public ModelRenderer leg4;

   public ModelQuadruped(int n, float f) {
      this.head.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, f);
      this.head.setRotationPoint(0.0F, 18 - n, -6.0F);
      this.body = new ModelRenderer(this, 28, 8);
      this.body.addBox(-5.0F, -10.0F, -7.0F, 10, 16, 8, f);
      this.body.setRotationPoint(0.0F, 17 - n, 2.0F);
      this.leg1 = new ModelRenderer(this, 0, 16);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, n, 4, f);
      this.leg1.setRotationPoint(-3.0F, 24 - n, 7.0F);
      this.leg2 = new ModelRenderer(this, 0, 16);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, n, 4, f);
      this.leg2.setRotationPoint(3.0F, 24 - n, 7.0F);
      this.leg3 = new ModelRenderer(this, 0, 16);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, n, 4, f);
      this.leg3.setRotationPoint(-3.0F, 24 - n, -5.0F);
      this.leg4 = new ModelRenderer(this, 0, 16);
      this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, n, 4, f);
      this.leg4.setRotationPoint(3.0F, 24 - n, -5.0F);
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);
      if (this.isChild) {
         float f6 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, this.childYOffset * scale, this.childZOffset * scale);
         this.head.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         this.body.render(scale);
         this.leg1.render(scale);
         this.leg2.render(scale);
         this.leg3.render(scale);
         this.leg4.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.head.render(scale);
         this.body.render(scale);
         this.leg1.render(scale);
         this.leg2.render(scale);
         this.leg3.render(scale);
         this.leg4.render(scale);
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      float f = 180.0F / (float)Math.PI;
      this.head.rotateAngleX = headPitch / (180.0F / (float)Math.PI);
      this.head.rotateAngleY = netHeadYaw / (180.0F / (float)Math.PI);
      this.body.rotateAngleX = (float) (Math.PI / 2);
      this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
      this.leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
      this.leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
   }
}
