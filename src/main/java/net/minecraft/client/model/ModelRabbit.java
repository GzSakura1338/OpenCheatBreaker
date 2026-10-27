package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.util.MathHelper;

public class ModelRabbit extends ModelBase {
   public float floatValue = 0.0F;
   public float floatValue2 = 0.0F;
   public ModelRenderer rabbitLeftFoot;
   public ModelRenderer rabbitRightFoot;
   public ModelRenderer rabbitLeftThigh;
   public ModelRenderer rabbitRightThigh;
   public ModelRenderer rabbitBody;
   public ModelRenderer rabbitLeftArm;
   public ModelRenderer rabbitRightArm;
   public ModelRenderer rabbitHead;
   public ModelRenderer rabbitRightEar;
   public ModelRenderer rabbitLeftEar;
   public ModelRenderer rabbitTail;
   public ModelRenderer rabbitNose;

   public ModelRabbit() {
      this.setTextureOffset("head.main", 0, 0);
      this.setTextureOffset("head.nose", 0, 24);
      this.setTextureOffset("head.ear1", 0, 10);
      this.setTextureOffset("head.ear2", 6, 10);
      this.rabbitLeftFoot = new ModelRenderer(this, 26, 24);
      this.rabbitLeftFoot.addBox(-1.0F, 5.5F, -3.7F, 2, 1, 7);
      this.rabbitLeftFoot.setRotationPoint(3.0F, 17.5F, 3.7F);
      this.rabbitLeftFoot.mirror = true;
      this.setRotationOffset(this.rabbitLeftFoot, 0.0F, 0.0F, 0.0F);
      this.rabbitRightFoot = new ModelRenderer(this, 8, 24);
      this.rabbitRightFoot.addBox(-1.0F, 5.5F, -3.7F, 2, 1, 7);
      this.rabbitRightFoot.setRotationPoint(-3.0F, 17.5F, 3.7F);
      this.rabbitRightFoot.mirror = true;
      this.setRotationOffset(this.rabbitRightFoot, 0.0F, 0.0F, 0.0F);
      this.rabbitLeftThigh = new ModelRenderer(this, 30, 15);
      this.rabbitLeftThigh.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 5);
      this.rabbitLeftThigh.setRotationPoint(3.0F, 17.5F, 3.7F);
      this.rabbitLeftThigh.mirror = true;
      this.setRotationOffset(this.rabbitLeftThigh, (float) (-Math.PI / 9), 0.0F, 0.0F);
      this.rabbitRightThigh = new ModelRenderer(this, 16, 15);
      this.rabbitRightThigh.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 5);
      this.rabbitRightThigh.setRotationPoint(-3.0F, 17.5F, 3.7F);
      this.rabbitRightThigh.mirror = true;
      this.setRotationOffset(this.rabbitRightThigh, (float) (-Math.PI / 9), 0.0F, 0.0F);
      this.rabbitBody = new ModelRenderer(this, 0, 0);
      this.rabbitBody.addBox(-3.0F, -2.0F, -10.0F, 6, 5, 10);
      this.rabbitBody.setRotationPoint(0.0F, 19.0F, 8.0F);
      this.rabbitBody.mirror = true;
      this.setRotationOffset(this.rabbitBody, (float) (-Math.PI / 9), 0.0F, 0.0F);
      this.rabbitLeftArm = new ModelRenderer(this, 8, 15);
      this.rabbitLeftArm.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2);
      this.rabbitLeftArm.setRotationPoint(3.0F, 17.0F, -1.0F);
      this.rabbitLeftArm.mirror = true;
      this.setRotationOffset(this.rabbitLeftArm, (float) (-Math.PI / 18), 0.0F, 0.0F);
      this.rabbitRightArm = new ModelRenderer(this, 0, 15);
      this.rabbitRightArm.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2);
      this.rabbitRightArm.setRotationPoint(-3.0F, 17.0F, -1.0F);
      this.rabbitRightArm.mirror = true;
      this.setRotationOffset(this.rabbitRightArm, (float) (-Math.PI / 18), 0.0F, 0.0F);
      this.rabbitHead = new ModelRenderer(this, 32, 0);
      this.rabbitHead.addBox(-2.5F, -4.0F, -5.0F, 5, 4, 5);
      this.rabbitHead.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitHead.mirror = true;
      this.setRotationOffset(this.rabbitHead, 0.0F, 0.0F, 0.0F);
      this.rabbitRightEar = new ModelRenderer(this, 52, 0);
      this.rabbitRightEar.addBox(-2.5F, -9.0F, -1.0F, 2, 5, 1);
      this.rabbitRightEar.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitRightEar.mirror = true;
      this.setRotationOffset(this.rabbitRightEar, 0.0F, (float) (-Math.PI / 12), 0.0F);
      this.rabbitLeftEar = new ModelRenderer(this, 58, 0);
      this.rabbitLeftEar.addBox(0.5F, -9.0F, -1.0F, 2, 5, 1);
      this.rabbitLeftEar.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitLeftEar.mirror = true;
      this.setRotationOffset(this.rabbitLeftEar, 0.0F, (float) (Math.PI / 12), 0.0F);
      this.rabbitTail = new ModelRenderer(this, 52, 6);
      this.rabbitTail.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 2);
      this.rabbitTail.setRotationPoint(0.0F, 20.0F, 7.0F);
      this.rabbitTail.mirror = true;
      this.setRotationOffset(this.rabbitTail, -0.3490659F, 0.0F, 0.0F);
      this.rabbitNose = new ModelRenderer(this, 32, 9);
      this.rabbitNose.addBox(-0.5F, -2.5F, -5.5F, 1, 1, 1);
      this.rabbitNose.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitNose.mirror = true;
      this.setRotationOffset(this.rabbitNose, 0.0F, 0.0F, 0.0F);
   }

   public void setRotationOffset(ModelRenderer modelRenderer, float f, float f2, float f3) {
      modelRenderer.rotateAngleX = f;
      modelRenderer.rotateAngleY = f2;
      modelRenderer.rotateAngleZ = f3;
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);
      if (this.isChild) {
         float f6 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 5.0F * scale, 2.0F * scale);
         this.rabbitHead.render(scale);
         this.rabbitLeftEar.render(scale);
         this.rabbitRightEar.render(scale);
         this.rabbitNose.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         this.rabbitLeftFoot.render(scale);
         this.rabbitRightFoot.render(scale);
         this.rabbitLeftThigh.render(scale);
         this.rabbitRightThigh.render(scale);
         this.rabbitBody.render(scale);
         this.rabbitLeftArm.render(scale);
         this.rabbitRightArm.render(scale);
         this.rabbitTail.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.rabbitLeftFoot.render(scale);
         this.rabbitRightFoot.render(scale);
         this.rabbitLeftThigh.render(scale);
         this.rabbitRightThigh.render(scale);
         this.rabbitBody.render(scale);
         this.rabbitLeftArm.render(scale);
         this.rabbitRightArm.render(scale);
         this.rabbitHead.render(scale);
         this.rabbitRightEar.render(scale);
         this.rabbitLeftEar.render(scale);
         this.rabbitTail.render(scale);
         this.rabbitNose.render(scale);
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      float f = ageInTicks - entityIn.ticksExisted;
      EntityRabbit entityIn2 = (EntityRabbit)entityIn;
      this.rabbitNose.rotateAngleX = this.rabbitHead.rotateAngleX = this.rabbitRightEar.rotateAngleX = this.rabbitLeftEar.rotateAngleX = headPitch
         * (float) (Math.PI / 180.0);
      this.rabbitNose.rotateAngleY = this.rabbitHead.rotateAngleY = netHeadYaw * (float) (Math.PI / 180.0);
      this.rabbitRightEar.rotateAngleY = this.rabbitNose.rotateAngleY - (float) (Math.PI / 12);
      this.rabbitLeftEar.rotateAngleY = this.rabbitNose.rotateAngleY + (float) (Math.PI / 12);
      this.floatValue = MathHelper.sin(entityIn2.getJumpProgress(f) * (float) Math.PI);
      this.rabbitLeftThigh.rotateAngleX = this.rabbitRightThigh.rotateAngleX = (this.floatValue * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
      this.rabbitLeftFoot.rotateAngleX = this.rabbitRightFoot.rotateAngleX = this.floatValue * 50.0F * (float) (Math.PI / 180.0);
      this.rabbitLeftArm.rotateAngleX = this.rabbitRightArm.rotateAngleX = (this.floatValue * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
   }

   @Override
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTickTime) {
   }
}
