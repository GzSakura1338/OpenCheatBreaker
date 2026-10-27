package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

public class ModelArmorStand extends ModelArmorStandArmor {
   public ModelRenderer standRightSide;
   public ModelRenderer standLeftSide;
   public ModelRenderer standWaist;
   public ModelRenderer standBase;

   public ModelArmorStand() {
      this(0.0F);
   }

   public ModelArmorStand(float f) {
      super(f, 64, 64);
      this.bipedHead = new ModelRenderer(this, 0, 0);
      this.bipedHead.addBox(-1.0F, -7.0F, -1.0F, 2, 7, 2, f);
      this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.bipedBody = new ModelRenderer(this, 0, 26);
      this.bipedBody.addBox(-6.0F, 0.0F, -1.5F, 12, 3, 3, f);
      this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.bipedRightArm = new ModelRenderer(this, 24, 0);
      this.bipedRightArm.addBox(-2.0F, -2.0F, -1.0F, 2, 12, 2, f);
      this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
      this.bipedLeftArm = new ModelRenderer(this, 32, 16);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.addBox(0.0F, -2.0F, -1.0F, 2, 12, 2, f);
      this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 8, 0);
      this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, f);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
      this.bipedLeftLeg = new ModelRenderer(this, 40, 16);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, f);
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
      this.standRightSide = new ModelRenderer(this, 16, 0);
      this.standRightSide.addBox(-3.0F, 3.0F, -1.0F, 2, 7, 2, f);
      this.standRightSide.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.standRightSide.showModel = true;
      this.standLeftSide = new ModelRenderer(this, 48, 16);
      this.standLeftSide.addBox(1.0F, 3.0F, -1.0F, 2, 7, 2, f);
      this.standLeftSide.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.standWaist = new ModelRenderer(this, 0, 48);
      this.standWaist.addBox(-4.0F, 10.0F, -1.0F, 8, 2, 2, f);
      this.standWaist.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.standBase = new ModelRenderer(this, 0, 32);
      this.standBase.addBox(-6.0F, 11.0F, -6.0F, 12, 1, 12, f);
      this.standBase.setRotationPoint(0.0F, 12.0F, 0.0F);
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      if (entityIn instanceof EntityArmorStand) {
         EntityArmorStand entityIn2 = (EntityArmorStand)entityIn;
         this.bipedLeftArm.showModel = entityIn2.getShowArms();
         this.bipedRightArm.showModel = entityIn2.getShowArms();
         this.standBase.showModel = !entityIn2.hasNoBasePlate();
         this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
         this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
         this.standRightSide.rotateAngleX = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getX();
         this.standRightSide.rotateAngleY = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getY();
         this.standRightSide.rotateAngleZ = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getZ();
         this.standLeftSide.rotateAngleX = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getX();
         this.standLeftSide.rotateAngleY = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getY();
         this.standLeftSide.rotateAngleZ = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getZ();
         this.standWaist.rotateAngleX = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getX();
         this.standWaist.rotateAngleY = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getY();
         this.standWaist.rotateAngleZ = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getZ();
         float f = (entityIn2.getLeftLegRotation().getX() + entityIn2.getRightLegRotation().getX()) / 2.0F;
         float f2 = (entityIn2.getLeftLegRotation().getY() + entityIn2.getRightLegRotation().getY()) / 2.0F;
         float f3 = (entityIn2.getLeftLegRotation().getZ() + entityIn2.getRightLegRotation().getZ()) / 2.0F;
         this.standBase.rotateAngleX = 0.0F;
         this.standBase.rotateAngleY = (float) (Math.PI / 180.0) * -entityIn.rotationYaw;
         this.standBase.rotateAngleZ = 0.0F;
      }
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      super.render(entityIn, f, f2, f3, f4, f5, scale);
      GlStateManager.pushMatrix();
      if (this.isChild) {
         float f6 = 2.0F;
         GlStateManager.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         this.standRightSide.render(scale);
         this.standLeftSide.render(scale);
         this.standWaist.render(scale);
         this.standBase.render(scale);
      } else {
         if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         this.standRightSide.render(scale);
         this.standLeftSide.render(scale);
         this.standWaist.render(scale);
         this.standBase.render(scale);
      }

      GlStateManager.popMatrix();
   }

   @Override
   public void postRenderArm(float scale) {
      boolean showModel = this.bipedRightArm.showModel;
      this.bipedRightArm.showModel = true;
      super.postRenderArm(scale);
      this.bipedRightArm.showModel = showModel;
   }
}
