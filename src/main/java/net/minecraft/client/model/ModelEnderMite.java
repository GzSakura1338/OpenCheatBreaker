package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelEnderMite extends ModelBase {
   public static int[][] intMatrix = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
   public static int[][] intMatrix2 = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
   public static int intValue = intMatrix.length;
   public ModelRenderer[] modelRendererArray = new ModelRenderer[intValue];

   public ModelEnderMite() {
      float f = -3.5F;

      for (int i = 0; i < this.modelRendererArray.length; i++) {
         this.modelRendererArray[i] = new ModelRenderer(this, intMatrix2[i][0], intMatrix2[i][1]);
         this.modelRendererArray[i]
            .addBox(intMatrix[i][0] * -0.5F, 0.0F, intMatrix[i][2] * -0.5F, intMatrix[i][0], intMatrix[i][1], intMatrix[i][2]);
         this.modelRendererArray[i].setRotationPoint(0.0F, 24 - intMatrix[i][1], f);
         if (i < this.modelRendererArray.length - 1) {
            f += (intMatrix[i][2] + intMatrix[i + 1][2]) * 0.5F;
         }
      }
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);

      for (int i = 0; i < this.modelRendererArray.length; i++) {
         this.modelRendererArray[i].render(scale);
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      for (int i = 0; i < this.modelRendererArray.length; i++) {
         this.modelRendererArray[i].rotateAngleY = MathHelper.cos(ageInTicks * 0.9F + i * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.01F
            * (1 + Math.abs(i - 2));
         this.modelRendererArray[i].rotationPointX = MathHelper.sin(ageInTicks * 0.9F + i * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.1F
            * Math.abs(i - 2);
      }
   }
}
