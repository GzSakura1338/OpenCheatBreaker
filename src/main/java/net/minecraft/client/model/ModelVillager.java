package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelVillager extends ModelBase {
   public ModelRenderer villagerHead;
   public ModelRenderer villagerNose;
   public ModelRenderer villagerBody;
   public ModelRenderer villagerArms;
   public ModelRenderer rightVillagerLeg;
   public ModelRenderer leftVillagerLeg;

   public ModelVillager(float f) {
      this(f, 0.0F, 64, 64);
   }

   public ModelVillager(float f, float f2, int n, int n2) {
      this.villagerHead = new ModelRenderer(this).setTextureSize(n, n2);
      this.villagerHead.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
      this.villagerHead.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
      this.villagerNose = new ModelRenderer(this).setTextureSize(n, n2);
      this.villagerNose.setRotationPoint(0.0F, f2 - 2.0F, 0.0F);
      this.villagerNose.setTextureOffset(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, f);
      this.villagerHead.addChild(this.villagerNose);
      this.villagerBody = new ModelRenderer(this).setTextureSize(n, n2);
      this.villagerBody.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
      this.villagerBody.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, f);
      this.villagerBody.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, f + 0.5F);
      this.villagerArms = new ModelRenderer(this).setTextureSize(n, n2);
      this.villagerArms.setRotationPoint(0.0F, 0.0F + f2 + 2.0F, 0.0F);
      this.villagerArms.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, f);
      this.villagerArms.setTextureOffset(44, 22).addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, f);
      this.villagerArms.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, f);
      this.rightVillagerLeg = new ModelRenderer(this, 0, 22).setTextureSize(n, n2);
      this.rightVillagerLeg.setRotationPoint(-2.0F, 12.0F + f2, 0.0F);
      this.rightVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
      this.leftVillagerLeg = new ModelRenderer(this, 0, 22).setTextureSize(n, n2);
      this.leftVillagerLeg.mirror = true;
      this.leftVillagerLeg.setRotationPoint(2.0F, 12.0F + f2, 0.0F);
      this.leftVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);
      this.villagerHead.render(scale);
      this.villagerBody.render(scale);
      this.rightVillagerLeg.render(scale);
      this.leftVillagerLeg.render(scale);
      this.villagerArms.render(scale);
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      this.villagerHead.rotateAngleY = netHeadYaw / (180.0F / (float)Math.PI);
      this.villagerHead.rotateAngleX = headPitch / (180.0F / (float)Math.PI);
      this.villagerArms.rotationPointY = 3.0F;
      this.villagerArms.rotationPointZ = -1.0F;
      this.villagerArms.rotateAngleX = -0.75F;
      this.rightVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
      this.leftVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
      this.rightVillagerLeg.rotateAngleY = 0.0F;
      this.leftVillagerLeg.rotateAngleY = 0.0F;
   }
}
