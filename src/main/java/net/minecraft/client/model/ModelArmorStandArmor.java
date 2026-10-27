package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

public class ModelArmorStandArmor extends ModelBiped {
   public ModelArmorStandArmor() {
      this(0.0F);
   }

   public ModelArmorStandArmor(float modelSize) {
      this(modelSize, 64, 32);
   }

   public ModelArmorStandArmor(float modelSize, int textureWidthIn, int textureHeightIn) {
      super(modelSize, 0.0F, textureWidthIn, textureHeightIn);
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      if (entityIn instanceof EntityArmorStand) {
         EntityArmorStand entityIn2 = (EntityArmorStand)entityIn;
         this.bipedHead.rotateAngleX = (float) (Math.PI / 180.0) * entityIn2.getHeadRotation().getX();
         this.bipedHead.rotateAngleY = (float) (Math.PI / 180.0) * entityIn2.getHeadRotation().getY();
         this.bipedHead.rotateAngleZ = (float) (Math.PI / 180.0) * entityIn2.getHeadRotation().getZ();
         this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
         this.bipedBody.rotateAngleX = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getX();
         this.bipedBody.rotateAngleY = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getY();
         this.bipedBody.rotateAngleZ = (float) (Math.PI / 180.0) * entityIn2.getBodyRotation().getZ();
         this.bipedLeftArm.rotateAngleX = (float) (Math.PI / 180.0) * entityIn2.getLeftArmRotation().getX();
         this.bipedLeftArm.rotateAngleY = (float) (Math.PI / 180.0) * entityIn2.getLeftArmRotation().getY();
         this.bipedLeftArm.rotateAngleZ = (float) (Math.PI / 180.0) * entityIn2.getLeftArmRotation().getZ();
         this.bipedRightArm.rotateAngleX = (float) (Math.PI / 180.0) * entityIn2.getRightArmRotation().getX();
         this.bipedRightArm.rotateAngleY = (float) (Math.PI / 180.0) * entityIn2.getRightArmRotation().getY();
         this.bipedRightArm.rotateAngleZ = (float) (Math.PI / 180.0) * entityIn2.getRightArmRotation().getZ();
         this.bipedLeftLeg.rotateAngleX = (float) (Math.PI / 180.0) * entityIn2.getLeftLegRotation().getX();
         this.bipedLeftLeg.rotateAngleY = (float) (Math.PI / 180.0) * entityIn2.getLeftLegRotation().getY();
         this.bipedLeftLeg.rotateAngleZ = (float) (Math.PI / 180.0) * entityIn2.getLeftLegRotation().getZ();
         this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
         this.bipedRightLeg.rotateAngleX = (float) (Math.PI / 180.0) * entityIn2.getRightLegRotation().getX();
         this.bipedRightLeg.rotateAngleY = (float) (Math.PI / 180.0) * entityIn2.getRightLegRotation().getY();
         this.bipedRightLeg.rotateAngleZ = (float) (Math.PI / 180.0) * entityIn2.getRightLegRotation().getZ();
         this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
         copyModelAngles(this.bipedHead, this.bipedHeadwear);
      }
   }
}
