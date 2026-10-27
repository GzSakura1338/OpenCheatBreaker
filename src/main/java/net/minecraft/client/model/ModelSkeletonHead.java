package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelSkeletonHead extends ModelBase {
   public ModelRenderer skeletonHead;

   public ModelSkeletonHead() {
      this(0, 35, 64, 64);
   }

   public ModelSkeletonHead(int n, int n2, int n3, int n4) {
      this.textureWidth = n3;
      this.textureHeight = n4;
      this.skeletonHead = new ModelRenderer(this, n, n2);
      this.skeletonHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
      this.skeletonHead.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);
      this.skeletonHead.render(scale);
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      this.skeletonHead.rotateAngleY = netHeadYaw / (180.0F / (float)Math.PI);
      this.skeletonHead.rotateAngleX = headPitch / (180.0F / (float)Math.PI);
   }
}
