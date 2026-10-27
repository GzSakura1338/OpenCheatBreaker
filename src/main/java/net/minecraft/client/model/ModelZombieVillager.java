package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelZombieVillager extends ModelBiped {
   public ModelZombieVillager() {
      this(0.0F, 0.0F, false);
   }

   public ModelZombieVillager(float f, float f2, boolean flag) {
      super(f, 0.0F, 64, flag ? 32 : 64);
      if (flag) {
         this.bipedHead = new ModelRenderer(this, 0, 0);
         this.bipedHead.addBox(-4.0F, -10.0F, -4.0F, 8, 8, 8, f);
         this.bipedHead.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
      } else {
         this.bipedHead = new ModelRenderer(this);
         this.bipedHead.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
         this.bipedHead.setTextureOffset(0, 32).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
         this.bipedHead.setTextureOffset(24, 32).addBox(-1.0F, -3.0F, -6.0F, 2, 4, 2, f);
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      float sin = MathHelper.sin(this.swingProgress * (float) Math.PI);
      float sin2 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightArm.rotateAngleY = -(0.1F - sin * 0.6F);
      this.bipedLeftArm.rotateAngleY = 0.1F - sin * 0.6F;
      this.bipedRightArm.rotateAngleX = (float) (-Math.PI / 2);
      this.bipedLeftArm.rotateAngleX = (float) (-Math.PI / 2);
      this.bipedRightArm.rotateAngleX -= sin * 1.2F - sin2 * 0.4F;
      this.bipedLeftArm.rotateAngleX -= sin * 1.2F - sin2 * 0.4F;
      this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + (MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F);
      this.bipedLeftArm.rotateAngleZ = this.bipedLeftArm.rotateAngleZ - (MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F);
      this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX + MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX - MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
   }
}
