package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelSilverfish extends ModelBase {
   public ModelRenderer[] silverfishBodyParts = new ModelRenderer[7];
   public float[] floatArray = new float[7];
   public ModelRenderer[] silverfishWings;
   public static int[][] silverfishBoxLength = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
   public static int[][] silverfishTexturePositions = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

   public ModelSilverfish() {
      float f = -3.5F;

      for (int i = 0; i < this.silverfishBodyParts.length; i++) {
         this.silverfishBodyParts[i] = new ModelRenderer(this, silverfishTexturePositions[i][0], silverfishTexturePositions[i][1]);
         this.silverfishBodyParts[i]
            .addBox(
               silverfishBoxLength[i][0] * -0.5F,
               0.0F,
               silverfishBoxLength[i][2] * -0.5F,
               silverfishBoxLength[i][0],
               silverfishBoxLength[i][1],
               silverfishBoxLength[i][2]
            );
         this.silverfishBodyParts[i].setRotationPoint(0.0F, 24 - silverfishBoxLength[i][1], f);
         this.floatArray[i] = f;
         if (i < this.silverfishBodyParts.length - 1) {
            f += (silverfishBoxLength[i][2] + silverfishBoxLength[i + 1][2]) * 0.5F;
         }
      }

      this.silverfishWings = new ModelRenderer[3];
      this.silverfishWings[0] = new ModelRenderer(this, 20, 0);
      this.silverfishWings[0].addBox(-5.0F, 0.0F, silverfishBoxLength[2][2] * -0.5F, 10, 8, silverfishBoxLength[2][2]);
      this.silverfishWings[0].setRotationPoint(0.0F, 16.0F, this.floatArray[2]);
      this.silverfishWings[1] = new ModelRenderer(this, 20, 11);
      this.silverfishWings[1].addBox(-3.0F, 0.0F, silverfishBoxLength[4][2] * -0.5F, 6, 4, silverfishBoxLength[4][2]);
      this.silverfishWings[1].setRotationPoint(0.0F, 20.0F, this.floatArray[4]);
      this.silverfishWings[2] = new ModelRenderer(this, 20, 18);
      this.silverfishWings[2].addBox(-3.0F, 0.0F, silverfishBoxLength[4][2] * -0.5F, 6, 5, silverfishBoxLength[1][2]);
      this.silverfishWings[2].setRotationPoint(0.0F, 19.0F, this.floatArray[1]);
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);

      for (int i = 0; i < this.silverfishBodyParts.length; i++) {
         this.silverfishBodyParts[i].render(scale);
      }

      for (int i = 0; i < this.silverfishWings.length; i++) {
         this.silverfishWings[i].render(scale);
      }
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      for (int i = 0; i < this.silverfishBodyParts.length; i++) {
         this.silverfishBodyParts[i].rotateAngleY = MathHelper.cos(ageInTicks * 0.9F + i * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.05F
            * (1 + Math.abs(i - 2));
         this.silverfishBodyParts[i].rotationPointX = MathHelper.sin(ageInTicks * 0.9F + i * 0.15F * (float) Math.PI)
            * (float) Math.PI
            * 0.2F
            * Math.abs(i - 2);
      }

      this.silverfishWings[0].rotateAngleY = this.silverfishBodyParts[2].rotateAngleY;
      this.silverfishWings[1].rotateAngleY = this.silverfishBodyParts[4].rotateAngleY;
      this.silverfishWings[1].rotationPointX = this.silverfishBodyParts[4].rotationPointX;
      this.silverfishWings[2].rotateAngleY = this.silverfishBodyParts[1].rotateAngleY;
      this.silverfishWings[2].rotationPointX = this.silverfishBodyParts[1].rotationPointX;
   }
}
