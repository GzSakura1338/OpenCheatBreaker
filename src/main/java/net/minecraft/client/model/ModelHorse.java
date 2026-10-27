package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.MathHelper;

public class ModelHorse extends ModelBase {
   public ModelRenderer body;
   public ModelRenderer tailBase;
   public ModelRenderer tailMiddle;
   public ModelRenderer tailTip;
   public ModelRenderer backLeftLeg;
   public ModelRenderer backLeftShin;
   public ModelRenderer backLeftHoof;
   public ModelRenderer backRightLeg;
   public ModelRenderer backRightShin;
   public ModelRenderer backRightHoof;
   public ModelRenderer frontLeftLeg;
   public ModelRenderer frontLeftShin;
   public ModelRenderer frontLeftHoof;
   public ModelRenderer frontRightLeg;
   public ModelRenderer frontRightShin;
   public ModelRenderer frontRightHoof;
   public ModelRenderer head;
   public ModelRenderer modelRenderer;
   public ModelRenderer modelRenderer2;
   public ModelRenderer horseLeftEar;
   public ModelRenderer horseRightEar;
   public ModelRenderer muleLeftEar;
   public ModelRenderer muleRightEar;
   public ModelRenderer neck;
   public ModelRenderer muleLeftChest;
   public ModelRenderer muleRightChest;
   public ModelRenderer horseSaddleBottom;
   public ModelRenderer horseSaddleFront;
   public ModelRenderer horseSaddleBack;
   public ModelRenderer horseLeftSaddleMetal;
   public ModelRenderer horseLeftSaddleRope;
   public ModelRenderer horseRightSaddleMetal;
   public ModelRenderer horseRightSaddleRope;
   public ModelRenderer horseLeftFaceMetal;
   public ModelRenderer horseRightFaceMetal;
   public ModelRenderer horseLeftRein;
   public ModelRenderer horseRightRein;
   public ModelRenderer mane;
   public ModelRenderer horseFaceRopes;

   public ModelHorse() {
      this.textureWidth = 128;
      this.textureHeight = 128;
      this.body = new ModelRenderer(this, 0, 34);
      this.body.addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24);
      this.body.setRotationPoint(0.0F, 11.0F, 9.0F);
      this.tailBase = new ModelRenderer(this, 44, 0);
      this.tailBase.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3);
      this.tailBase.setRotationPoint(0.0F, 3.0F, 14.0F);
      this.setBoxRotation(this.tailBase, -1.134464F, 0.0F, 0.0F);
      this.tailMiddle = new ModelRenderer(this, 38, 7);
      this.tailMiddle.addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7);
      this.tailMiddle.setRotationPoint(0.0F, 3.0F, 14.0F);
      this.setBoxRotation(this.tailMiddle, -1.134464F, 0.0F, 0.0F);
      this.tailTip = new ModelRenderer(this, 24, 3);
      this.tailTip.addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7);
      this.tailTip.setRotationPoint(0.0F, 3.0F, 14.0F);
      this.setBoxRotation(this.tailTip, -1.40215F, 0.0F, 0.0F);
      this.backLeftLeg = new ModelRenderer(this, 78, 29);
      this.backLeftLeg.addBox(-2.5F, -2.0F, -2.5F, 4, 9, 5);
      this.backLeftLeg.setRotationPoint(4.0F, 9.0F, 11.0F);
      this.backLeftShin = new ModelRenderer(this, 78, 43);
      this.backLeftShin.addBox(-2.0F, 0.0F, -1.5F, 3, 5, 3);
      this.backLeftShin.setRotationPoint(4.0F, 16.0F, 11.0F);
      this.backLeftHoof = new ModelRenderer(this, 78, 51);
      this.backLeftHoof.addBox(-2.5F, 5.1F, -2.0F, 4, 3, 4);
      this.backLeftHoof.setRotationPoint(4.0F, 16.0F, 11.0F);
      this.backRightLeg = new ModelRenderer(this, 96, 29);
      this.backRightLeg.addBox(-1.5F, -2.0F, -2.5F, 4, 9, 5);
      this.backRightLeg.setRotationPoint(-4.0F, 9.0F, 11.0F);
      this.backRightShin = new ModelRenderer(this, 96, 43);
      this.backRightShin.addBox(-1.0F, 0.0F, -1.5F, 3, 5, 3);
      this.backRightShin.setRotationPoint(-4.0F, 16.0F, 11.0F);
      this.backRightHoof = new ModelRenderer(this, 96, 51);
      this.backRightHoof.addBox(-1.5F, 5.1F, -2.0F, 4, 3, 4);
      this.backRightHoof.setRotationPoint(-4.0F, 16.0F, 11.0F);
      this.frontLeftLeg = new ModelRenderer(this, 44, 29);
      this.frontLeftLeg.addBox(-1.9F, -1.0F, -2.1F, 3, 8, 4);
      this.frontLeftLeg.setRotationPoint(4.0F, 9.0F, -8.0F);
      this.frontLeftShin = new ModelRenderer(this, 44, 41);
      this.frontLeftShin.addBox(-1.9F, 0.0F, -1.6F, 3, 5, 3);
      this.frontLeftShin.setRotationPoint(4.0F, 16.0F, -8.0F);
      this.frontLeftHoof = new ModelRenderer(this, 44, 51);
      this.frontLeftHoof.addBox(-2.4F, 5.1F, -2.1F, 4, 3, 4);
      this.frontLeftHoof.setRotationPoint(4.0F, 16.0F, -8.0F);
      this.frontRightLeg = new ModelRenderer(this, 60, 29);
      this.frontRightLeg.addBox(-1.1F, -1.0F, -2.1F, 3, 8, 4);
      this.frontRightLeg.setRotationPoint(-4.0F, 9.0F, -8.0F);
      this.frontRightShin = new ModelRenderer(this, 60, 41);
      this.frontRightShin.addBox(-1.1F, 0.0F, -1.6F, 3, 5, 3);
      this.frontRightShin.setRotationPoint(-4.0F, 16.0F, -8.0F);
      this.frontRightHoof = new ModelRenderer(this, 60, 51);
      this.frontRightHoof.addBox(-1.6F, 5.1F, -2.1F, 4, 3, 4);
      this.frontRightHoof.setRotationPoint(-4.0F, 16.0F, -8.0F);
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7);
      this.head.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.head, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.modelRenderer = new ModelRenderer(this, 24, 18);
      this.modelRenderer.addBox(-2.0F, -10.0F, -7.0F, 4, 3, 6);
      this.modelRenderer.setRotationPoint(0.0F, 3.95F, -10.0F);
      this.setBoxRotation(this.modelRenderer, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.modelRenderer2 = new ModelRenderer(this, 24, 27);
      this.modelRenderer2.addBox(-2.0F, -7.0F, -6.5F, 4, 2, 5);
      this.modelRenderer2.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.modelRenderer2, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.head.addChild(this.modelRenderer);
      this.head.addChild(this.modelRenderer2);
      this.horseLeftEar = new ModelRenderer(this, 0, 0);
      this.horseLeftEar.addBox(0.45F, -12.0F, 4.0F, 2, 3, 1);
      this.horseLeftEar.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.horseLeftEar, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.horseRightEar = new ModelRenderer(this, 0, 0);
      this.horseRightEar.addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1);
      this.horseRightEar.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.horseRightEar, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.muleLeftEar = new ModelRenderer(this, 0, 12);
      this.muleLeftEar.addBox(-2.0F, -16.0F, 4.0F, 2, 7, 1);
      this.muleLeftEar.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.muleLeftEar, (float) (Math.PI / 6), 0.0F, (float) (Math.PI / 12));
      this.muleRightEar = new ModelRenderer(this, 0, 12);
      this.muleRightEar.addBox(0.0F, -16.0F, 4.0F, 2, 7, 1);
      this.muleRightEar.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.muleRightEar, (float) (Math.PI / 6), 0.0F, (float) (-Math.PI / 12));
      this.neck = new ModelRenderer(this, 0, 12);
      this.neck.addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8);
      this.neck.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.neck, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.muleLeftChest = new ModelRenderer(this, 0, 34);
      this.muleLeftChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
      this.muleLeftChest.setRotationPoint(-7.5F, 3.0F, 10.0F);
      this.setBoxRotation(this.muleLeftChest, 0.0F, (float) (Math.PI / 2), 0.0F);
      this.muleRightChest = new ModelRenderer(this, 0, 47);
      this.muleRightChest.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3);
      this.muleRightChest.setRotationPoint(4.5F, 3.0F, 10.0F);
      this.setBoxRotation(this.muleRightChest, 0.0F, (float) (Math.PI / 2), 0.0F);
      this.horseSaddleBottom = new ModelRenderer(this, 80, 0);
      this.horseSaddleBottom.addBox(-5.0F, 0.0F, -3.0F, 10, 1, 8);
      this.horseSaddleBottom.setRotationPoint(0.0F, 2.0F, 2.0F);
      this.horseSaddleFront = new ModelRenderer(this, 106, 9);
      this.horseSaddleFront.addBox(-1.5F, -1.0F, -3.0F, 3, 1, 2);
      this.horseSaddleFront.setRotationPoint(0.0F, 2.0F, 2.0F);
      this.horseSaddleBack = new ModelRenderer(this, 80, 9);
      this.horseSaddleBack.addBox(-4.0F, -1.0F, 3.0F, 8, 1, 2);
      this.horseSaddleBack.setRotationPoint(0.0F, 2.0F, 2.0F);
      this.horseLeftSaddleMetal = new ModelRenderer(this, 74, 0);
      this.horseLeftSaddleMetal.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
      this.horseLeftSaddleMetal.setRotationPoint(5.0F, 3.0F, 2.0F);
      this.horseLeftSaddleRope = new ModelRenderer(this, 70, 0);
      this.horseLeftSaddleRope.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
      this.horseLeftSaddleRope.setRotationPoint(5.0F, 3.0F, 2.0F);
      this.horseRightSaddleMetal = new ModelRenderer(this, 74, 4);
      this.horseRightSaddleMetal.addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2);
      this.horseRightSaddleMetal.setRotationPoint(-5.0F, 3.0F, 2.0F);
      this.horseRightSaddleRope = new ModelRenderer(this, 80, 0);
      this.horseRightSaddleRope.addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1);
      this.horseRightSaddleRope.setRotationPoint(-5.0F, 3.0F, 2.0F);
      this.horseLeftFaceMetal = new ModelRenderer(this, 74, 13);
      this.horseLeftFaceMetal.addBox(1.5F, -8.0F, -4.0F, 1, 2, 2);
      this.horseLeftFaceMetal.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.horseLeftFaceMetal, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.horseRightFaceMetal = new ModelRenderer(this, 74, 13);
      this.horseRightFaceMetal.addBox(-2.5F, -8.0F, -4.0F, 1, 2, 2);
      this.horseRightFaceMetal.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.horseRightFaceMetal, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.horseLeftRein = new ModelRenderer(this, 44, 10);
      this.horseLeftRein.addBox(2.6F, -6.0F, -6.0F, 0, 3, 16);
      this.horseLeftRein.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.horseRightRein = new ModelRenderer(this, 44, 5);
      this.horseRightRein.addBox(-2.6F, -6.0F, -6.0F, 0, 3, 16);
      this.horseRightRein.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.mane = new ModelRenderer(this, 58, 0);
      this.mane.addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4);
      this.mane.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.mane, (float) (Math.PI / 6), 0.0F, 0.0F);
      this.horseFaceRopes = new ModelRenderer(this, 80, 12);
      this.horseFaceRopes.addBox(-2.5F, -10.1F, -7.0F, 5, 5, 12, 0.2F);
      this.horseFaceRopes.setRotationPoint(0.0F, 4.0F, -10.0F);
      this.setBoxRotation(this.horseFaceRopes, (float) (Math.PI / 6), 0.0F, 0.0F);
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      EntityHorse entityIn2 = (EntityHorse)entityIn;
      int horseType = entityIn2.getHorseType();
      float grassEatingAmount = entityIn2.getGrassEatingAmount(0.0F);
      boolean isAdultHorse = entityIn2.isAdultHorse();
      boolean flag = isAdultHorse && entityIn2.isHorseSaddled();
      boolean flag2 = isAdultHorse && entityIn2.isChested();
      boolean flag3 = horseType == 1 || horseType == 2;
      float horseSize = entityIn2.getHorseSize();
      boolean flag4 = entityIn2.riddenByEntity != null;
      if (flag) {
         this.horseFaceRopes.render(scale);
         this.horseSaddleBottom.render(scale);
         this.horseSaddleFront.render(scale);
         this.horseSaddleBack.render(scale);
         this.horseLeftSaddleRope.render(scale);
         this.horseLeftSaddleMetal.render(scale);
         this.horseRightSaddleRope.render(scale);
         this.horseRightSaddleMetal.render(scale);
         this.horseLeftFaceMetal.render(scale);
         this.horseRightFaceMetal.render(scale);
         if (flag4) {
            this.horseLeftRein.render(scale);
            this.horseRightRein.render(scale);
         }
      }

      if (!isAdultHorse) {
         GlStateManager.pushMatrix();
         GlStateManager.scale(horseSize, 0.5F + horseSize * 0.5F, horseSize);
         GlStateManager.translate(0.0F, 0.95F * (1.0F - horseSize), 0.0F);
      }

      this.backLeftLeg.render(scale);
      this.backLeftShin.render(scale);
      this.backLeftHoof.render(scale);
      this.backRightLeg.render(scale);
      this.backRightShin.render(scale);
      this.backRightHoof.render(scale);
      this.frontLeftLeg.render(scale);
      this.frontLeftShin.render(scale);
      this.frontLeftHoof.render(scale);
      this.frontRightLeg.render(scale);
      this.frontRightShin.render(scale);
      this.frontRightHoof.render(scale);
      if (!isAdultHorse) {
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(horseSize, horseSize, horseSize);
         GlStateManager.translate(0.0F, 1.35F * (1.0F - horseSize), 0.0F);
      }

      this.body.render(scale);
      this.tailBase.render(scale);
      this.tailMiddle.render(scale);
      this.tailTip.render(scale);
      this.neck.render(scale);
      this.mane.render(scale);
      if (!isAdultHorse) {
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         float f6 = 0.5F + horseSize * horseSize * 0.5F;
         GlStateManager.scale(f6, f6, f6);
         if (grassEatingAmount <= 0.0F) {
            GlStateManager.translate(0.0F, 1.35F * (1.0F - horseSize), 0.0F);
         } else {
            GlStateManager.translate(0.0F, 0.9F * (1.0F - horseSize) * grassEatingAmount + 1.35F * (1.0F - horseSize) * (1.0F - grassEatingAmount), 0.15F * (1.0F - horseSize) * grassEatingAmount);
         }
      }

      if (flag3) {
         this.muleLeftEar.render(scale);
         this.muleRightEar.render(scale);
      } else {
         this.horseLeftEar.render(scale);
         this.horseRightEar.render(scale);
      }

      this.head.render(scale);
      if (!isAdultHorse) {
         GlStateManager.popMatrix();
      }

      if (flag2) {
         this.muleLeftChest.render(scale);
         this.muleRightChest.render(scale);
      }
   }

   public void setBoxRotation(ModelRenderer modelRenderer, float f, float f2, float f3) {
      modelRenderer.rotateAngleX = f;
      modelRenderer.rotateAngleY = f2;
      modelRenderer.rotateAngleZ = f3;
   }

   public float updateHorseRotation(float f, float f2, float f3) {
      float f4 = f2 - f;

      while (f4 < -180.0F) {
         f4 += 360.0F;
      }

      while (f4 >= 180.0F) {
         f4 -= 360.0F;
      }

      return f + f3 * f4;
   }

   @Override
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTickTime) {
      super.setLivingAnimations(entitylivingbaseIn, f, f2, partialTickTime);
      float updateHorseRotation = this.updateHorseRotation(entitylivingbaseIn.prevRenderYawOffset, entitylivingbaseIn.renderYawOffset, partialTickTime);
      float updateHorseRotation2 = this.updateHorseRotation(entitylivingbaseIn.prevRotationYawHead, entitylivingbaseIn.rotationYawHead, partialTickTime);
      float f3 = entitylivingbaseIn.prevRotationPitch + (entitylivingbaseIn.rotationPitch - entitylivingbaseIn.prevRotationPitch) * partialTickTime;
      float f4 = updateHorseRotation2 - updateHorseRotation;
      float f5 = f3 / (180.0F / (float)Math.PI);
      if (f4 > 20.0F) {
         f4 = 20.0F;
      }

      if (f4 < -20.0F) {
         f4 = -20.0F;
      }

      if (f2 > 0.2F) {
         f5 += MathHelper.cos(f * 0.4F) * 0.15F * f2;
      }

      EntityHorse entitylivingbaseIn2 = (EntityHorse)entitylivingbaseIn;
      float grassEatingAmount = entitylivingbaseIn2.getGrassEatingAmount(partialTickTime);
      float rearingAmount = entitylivingbaseIn2.getRearingAmount(partialTickTime);
      float f6 = 1.0F - rearingAmount;
      float mouthOpennessAngle = entitylivingbaseIn2.getMouthOpennessAngle(partialTickTime);
      boolean flag = entitylivingbaseIn2.intValue3 != 0;
      boolean isHorseSaddled = entitylivingbaseIn2.isHorseSaddled();
      boolean flag2 = entitylivingbaseIn2.riddenByEntity != null;
      float f7 = entitylivingbaseIn.ticksExisted + partialTickTime;
      float cos = MathHelper.cos(f * 0.6662F + (float) Math.PI);
      float f8 = cos * 0.8F * f2;
      this.head.rotationPointY = 4.0F;
      this.head.rotationPointZ = -10.0F;
      this.tailBase.rotationPointY = 3.0F;
      this.tailMiddle.rotationPointZ = 14.0F;
      this.muleRightChest.rotationPointY = 3.0F;
      this.muleRightChest.rotationPointZ = 10.0F;
      this.body.rotateAngleX = 0.0F;
      this.head.rotateAngleX = (float) (Math.PI / 6) + f5;
      this.head.rotateAngleY = f4 / (180.0F / (float)Math.PI);
      this.head.rotateAngleX = rearingAmount * ((float) (Math.PI / 12) + f5) + grassEatingAmount * 2.18166F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.head.rotateAngleX;
      this.head.rotateAngleY = rearingAmount * f4 / (180.0F / (float)Math.PI) + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.head.rotateAngleY;
      this.head.rotationPointY = rearingAmount * -6.0F + grassEatingAmount * 11.0F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.head.rotationPointY;
      this.head.rotationPointZ = rearingAmount * -1.0F + grassEatingAmount * -10.0F + (1.0F - Math.max(rearingAmount, grassEatingAmount)) * this.head.rotationPointZ;
      this.tailBase.rotationPointY = rearingAmount * 9.0F + f6 * this.tailBase.rotationPointY;
      this.tailMiddle.rotationPointZ = rearingAmount * 18.0F + f6 * this.tailMiddle.rotationPointZ;
      this.muleRightChest.rotationPointY = rearingAmount * 5.5F + f6 * this.muleRightChest.rotationPointY;
      this.muleRightChest.rotationPointZ = rearingAmount * 15.0F + f6 * this.muleRightChest.rotationPointZ;
      this.body.rotateAngleX = rearingAmount * -45.0F / (180.0F / (float)Math.PI) + f6 * this.body.rotateAngleX;
      this.horseLeftEar.rotationPointY = this.head.rotationPointY;
      this.horseRightEar.rotationPointY = this.head.rotationPointY;
      this.muleLeftEar.rotationPointY = this.head.rotationPointY;
      this.muleRightEar.rotationPointY = this.head.rotationPointY;
      this.neck.rotationPointY = this.head.rotationPointY;
      this.modelRenderer.rotationPointY = 0.02F;
      this.modelRenderer2.rotationPointY = 0.0F;
      this.mane.rotationPointY = this.head.rotationPointY;
      this.horseLeftEar.rotationPointZ = this.head.rotationPointZ;
      this.horseRightEar.rotationPointZ = this.head.rotationPointZ;
      this.muleLeftEar.rotationPointZ = this.head.rotationPointZ;
      this.muleRightEar.rotationPointZ = this.head.rotationPointZ;
      this.neck.rotationPointZ = this.head.rotationPointZ;
      this.modelRenderer.rotationPointZ = 0.02F - mouthOpennessAngle * 1.0F;
      this.modelRenderer2.rotationPointZ = 0.0F + mouthOpennessAngle * 1.0F;
      this.mane.rotationPointZ = this.head.rotationPointZ;
      this.horseLeftEar.rotateAngleX = this.head.rotateAngleX;
      this.horseRightEar.rotateAngleX = this.head.rotateAngleX;
      this.muleLeftEar.rotateAngleX = this.head.rotateAngleX;
      this.muleRightEar.rotateAngleX = this.head.rotateAngleX;
      this.neck.rotateAngleX = this.head.rotateAngleX;
      this.modelRenderer.rotateAngleX = 0.0F - 0.09424778F * mouthOpennessAngle;
      this.modelRenderer2.rotateAngleX = 0.0F + (float) (Math.PI / 20) * mouthOpennessAngle;
      this.mane.rotateAngleX = this.head.rotateAngleX;
      this.horseLeftEar.rotateAngleY = this.head.rotateAngleY;
      this.horseRightEar.rotateAngleY = this.head.rotateAngleY;
      this.muleLeftEar.rotateAngleY = this.head.rotateAngleY;
      this.muleRightEar.rotateAngleY = this.head.rotateAngleY;
      this.neck.rotateAngleY = this.head.rotateAngleY;
      this.modelRenderer.rotateAngleY = 0.0F;
      this.modelRenderer2.rotateAngleY = 0.0F;
      this.mane.rotateAngleY = this.head.rotateAngleY;
      this.muleLeftChest.rotateAngleX = f8 / 5.0F;
      this.muleRightChest.rotateAngleX = -f8 / 5.0F;
      float f9 = (float) (Math.PI / 2);
      float f10 = (float) (Math.PI * 3.0 / 2.0);
      float f11 = (float) (-Math.PI / 3);
      float f12 = (float) (Math.PI / 12) * rearingAmount;
      float cos2 = MathHelper.cos(f7 * 0.6F + (float) Math.PI);
      this.frontLeftLeg.rotationPointY = -2.0F * rearingAmount + 9.0F * f6;
      this.frontLeftLeg.rotationPointZ = -2.0F * rearingAmount + -8.0F * f6;
      this.frontRightLeg.rotationPointY = this.frontLeftLeg.rotationPointY;
      this.frontRightLeg.rotationPointZ = this.frontLeftLeg.rotationPointZ;
      this.backLeftShin.rotationPointY = this.backLeftLeg.rotationPointY + MathHelper.sin((float) (Math.PI / 2) + f12 + f6 * -cos * 0.5F * f2) * 7.0F;
      this.backLeftShin.rotationPointZ = this.backLeftLeg.rotationPointZ
         + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + f12 + f6 * -cos * 0.5F * f2) * 7.0F;
      this.backRightShin.rotationPointY = this.backRightLeg.rotationPointY + MathHelper.sin((float) (Math.PI / 2) + f12 + f6 * cos * 0.5F * f2) * 7.0F;
      this.backRightShin.rotationPointZ = this.backRightLeg.rotationPointZ
         + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + f12 + f6 * cos * 0.5F * f2) * 7.0F;
      float f13 = ((float) (-Math.PI / 3) + cos2) * rearingAmount + f8 * f6;
      float f14 = ((float) (-Math.PI / 3) + -cos2) * rearingAmount + -f8 * f6;
      this.frontLeftShin.rotationPointY = this.frontLeftLeg.rotationPointY + MathHelper.sin((float) (Math.PI / 2) + f13) * 7.0F;
      this.frontLeftShin.rotationPointZ = this.frontLeftLeg.rotationPointZ + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + f13) * 7.0F;
      this.frontRightShin.rotationPointY = this.frontRightLeg.rotationPointY + MathHelper.sin((float) (Math.PI / 2) + f14) * 7.0F;
      this.frontRightShin.rotationPointZ = this.frontRightLeg.rotationPointZ + MathHelper.cos((float) (Math.PI * 3.0 / 2.0) + f14) * 7.0F;
      this.backLeftLeg.rotateAngleX = f12 + -cos * 0.5F * f2 * f6;
      this.backLeftShin.rotateAngleX = -0.08726646F * rearingAmount + (-cos * 0.5F * f2 - Math.max(0.0F, cos * 0.5F * f2)) * f6;
      this.backLeftHoof.rotateAngleX = this.backLeftShin.rotateAngleX;
      this.backRightLeg.rotateAngleX = f12 + cos * 0.5F * f2 * f6;
      this.backRightShin.rotateAngleX = -0.08726646F * rearingAmount + (cos * 0.5F * f2 - Math.max(0.0F, -cos * 0.5F * f2)) * f6;
      this.backRightHoof.rotateAngleX = this.backRightShin.rotateAngleX;
      this.frontLeftLeg.rotateAngleX = f13;
      this.frontLeftShin.rotateAngleX = (this.frontLeftLeg.rotateAngleX + (float) Math.PI * Math.max(0.0F, 0.2F + cos2 * 0.2F)) * rearingAmount
         + (f8 + Math.max(0.0F, cos * 0.5F * f2)) * f6;
      this.frontLeftHoof.rotateAngleX = this.frontLeftShin.rotateAngleX;
      this.frontRightLeg.rotateAngleX = f14;
      this.frontRightShin.rotateAngleX = (this.frontRightLeg.rotateAngleX + (float) Math.PI * Math.max(0.0F, 0.2F - cos2 * 0.2F)) * rearingAmount
         + (-f8 + Math.max(0.0F, -cos * 0.5F * f2)) * f6;
      this.frontRightHoof.rotateAngleX = this.frontRightShin.rotateAngleX;
      this.backLeftHoof.rotationPointY = this.backLeftShin.rotationPointY;
      this.backLeftHoof.rotationPointZ = this.backLeftShin.rotationPointZ;
      this.backRightHoof.rotationPointY = this.backRightShin.rotationPointY;
      this.backRightHoof.rotationPointZ = this.backRightShin.rotationPointZ;
      this.frontLeftHoof.rotationPointY = this.frontLeftShin.rotationPointY;
      this.frontLeftHoof.rotationPointZ = this.frontLeftShin.rotationPointZ;
      this.frontRightHoof.rotationPointY = this.frontRightShin.rotationPointY;
      this.frontRightHoof.rotationPointZ = this.frontRightShin.rotationPointZ;
      if (isHorseSaddled) {
         this.horseSaddleBottom.rotationPointY = rearingAmount * 0.5F + f6 * 2.0F;
         this.horseSaddleBottom.rotationPointZ = rearingAmount * 11.0F + f6 * 2.0F;
         this.horseSaddleFront.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.horseSaddleBack.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.horseLeftSaddleRope.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.horseRightSaddleRope.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.horseLeftSaddleMetal.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.horseRightSaddleMetal.rotationPointY = this.horseSaddleBottom.rotationPointY;
         this.muleLeftChest.rotationPointY = this.muleRightChest.rotationPointY;
         this.horseSaddleFront.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.horseSaddleBack.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.horseLeftSaddleRope.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.horseRightSaddleRope.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.horseLeftSaddleMetal.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.horseRightSaddleMetal.rotationPointZ = this.horseSaddleBottom.rotationPointZ;
         this.muleLeftChest.rotationPointZ = this.muleRightChest.rotationPointZ;
         this.horseSaddleBottom.rotateAngleX = this.body.rotateAngleX;
         this.horseSaddleFront.rotateAngleX = this.body.rotateAngleX;
         this.horseSaddleBack.rotateAngleX = this.body.rotateAngleX;
         this.horseLeftRein.rotationPointY = this.head.rotationPointY;
         this.horseRightRein.rotationPointY = this.head.rotationPointY;
         this.horseFaceRopes.rotationPointY = this.head.rotationPointY;
         this.horseLeftFaceMetal.rotationPointY = this.head.rotationPointY;
         this.horseRightFaceMetal.rotationPointY = this.head.rotationPointY;
         this.horseLeftRein.rotationPointZ = this.head.rotationPointZ;
         this.horseRightRein.rotationPointZ = this.head.rotationPointZ;
         this.horseFaceRopes.rotationPointZ = this.head.rotationPointZ;
         this.horseLeftFaceMetal.rotationPointZ = this.head.rotationPointZ;
         this.horseRightFaceMetal.rotationPointZ = this.head.rotationPointZ;
         this.horseLeftRein.rotateAngleX = f5;
         this.horseRightRein.rotateAngleX = f5;
         this.horseFaceRopes.rotateAngleX = this.head.rotateAngleX;
         this.horseLeftFaceMetal.rotateAngleX = this.head.rotateAngleX;
         this.horseRightFaceMetal.rotateAngleX = this.head.rotateAngleX;
         this.horseFaceRopes.rotateAngleY = this.head.rotateAngleY;
         this.horseLeftFaceMetal.rotateAngleY = this.head.rotateAngleY;
         this.horseLeftRein.rotateAngleY = this.head.rotateAngleY;
         this.horseRightFaceMetal.rotateAngleY = this.head.rotateAngleY;
         this.horseRightRein.rotateAngleY = this.head.rotateAngleY;
         if (flag2) {
            this.horseLeftSaddleRope.rotateAngleX = (float) (-Math.PI / 3);
            this.horseLeftSaddleMetal.rotateAngleX = (float) (-Math.PI / 3);
            this.horseRightSaddleRope.rotateAngleX = (float) (-Math.PI / 3);
            this.horseRightSaddleMetal.rotateAngleX = (float) (-Math.PI / 3);
            this.horseLeftSaddleRope.rotateAngleZ = 0.0F;
            this.horseLeftSaddleMetal.rotateAngleZ = 0.0F;
            this.horseRightSaddleRope.rotateAngleZ = 0.0F;
            this.horseRightSaddleMetal.rotateAngleZ = 0.0F;
         } else {
            this.horseLeftSaddleRope.rotateAngleX = f8 / 3.0F;
            this.horseLeftSaddleMetal.rotateAngleX = f8 / 3.0F;
            this.horseRightSaddleRope.rotateAngleX = f8 / 3.0F;
            this.horseRightSaddleMetal.rotateAngleX = f8 / 3.0F;
            this.horseLeftSaddleRope.rotateAngleZ = f8 / 5.0F;
            this.horseLeftSaddleMetal.rotateAngleZ = f8 / 5.0F;
            this.horseRightSaddleRope.rotateAngleZ = -f8 / 5.0F;
            this.horseRightSaddleMetal.rotateAngleZ = -f8 / 5.0F;
         }
      }

      f9 = -1.3089F + f2 * 1.5F;
      if (f9 > 0.0F) {
         f9 = 0.0F;
      }

      if (flag) {
         this.tailBase.rotateAngleY = MathHelper.cos(f7 * 0.7F);
         f9 = 0.0F;
      } else {
         this.tailBase.rotateAngleY = 0.0F;
      }

      this.tailMiddle.rotateAngleY = this.tailBase.rotateAngleY;
      this.tailTip.rotateAngleY = this.tailBase.rotateAngleY;
      this.tailMiddle.rotationPointY = this.tailBase.rotationPointY;
      this.tailTip.rotationPointY = this.tailBase.rotationPointY;
      this.tailMiddle.rotationPointZ = this.tailBase.rotationPointZ;
      this.tailTip.rotationPointZ = this.tailBase.rotationPointZ;
      this.tailBase.rotateAngleX = f9;
      this.tailMiddle.rotateAngleX = f9;
      this.tailTip.rotateAngleX = -0.2618F + f9;
   }
}
