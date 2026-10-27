package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBlaze extends ModelBase {
   public ModelRenderer[] blazeSticks = new ModelRenderer[12];
   public ModelRenderer blazeHead;

   public ModelBlaze() {
      for (int i = 0; i < this.blazeSticks.length; i++) {
         this.blazeSticks[i] = new ModelRenderer(this, 0, 16);
         this.blazeSticks[i].addBox(0.0F, 0.0F, 0.0F, 2, 8, 2);
      }

      this.blazeHead = new ModelRenderer(this, 0, 0);
      this.blazeHead.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);
      this.blazeHead.render(scale);

      for (int i = 0; i < this.blazeSticks.length; i++) {
         this.blazeSticks[i].render(scale);
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      float f = ageInTicks * (float) Math.PI * -0.1F;

      for (int i = 0; i < 4; i++) {
         this.blazeSticks[i].rotationPointY = -2.0F + MathHelper.cos((i * 2 + ageInTicks) * 0.25F);
         this.blazeSticks[i].rotationPointX = MathHelper.cos(f) * 9.0F;
         this.blazeSticks[i].rotationPointZ = MathHelper.sin(f) * 9.0F;
         f++;
      }

      f = (float) (Math.PI / 4) + ageInTicks * (float) Math.PI * 0.03F;

      for (int i = 4; i < 8; i++) {
         this.blazeSticks[i].rotationPointY = 2.0F + MathHelper.cos((i * 2 + ageInTicks) * 0.25F);
         this.blazeSticks[i].rotationPointX = MathHelper.cos(f) * 7.0F;
         this.blazeSticks[i].rotationPointZ = MathHelper.sin(f) * 7.0F;
         f++;
      }

      f = 0.47123894F + ageInTicks * (float) Math.PI * -0.05F;

      for (int i = 8; i < 12; i++) {
         this.blazeSticks[i].rotationPointY = 11.0F + MathHelper.cos((i * 1.5F + ageInTicks) * 0.5F);
         this.blazeSticks[i].rotationPointX = MathHelper.cos(f) * 5.0F;
         this.blazeSticks[i].rotationPointZ = MathHelper.sin(f) * 5.0F;
         f++;
      }

      this.blazeHead.rotateAngleY = netHeadYaw / (180.0F / (float)Math.PI);
      this.blazeHead.rotateAngleX = headPitch / (180.0F / (float)Math.PI);
   }
}
