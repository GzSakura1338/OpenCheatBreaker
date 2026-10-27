package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelMinecart extends ModelBase {
   public ModelRenderer[] sideModels = new ModelRenderer[7];

   public ModelMinecart() {
      this.sideModels[0] = new ModelRenderer(this, 0, 10);
      this.sideModels[1] = new ModelRenderer(this, 0, 0);
      this.sideModels[2] = new ModelRenderer(this, 0, 0);
      this.sideModels[3] = new ModelRenderer(this, 0, 0);
      this.sideModels[4] = new ModelRenderer(this, 0, 0);
      this.sideModels[5] = new ModelRenderer(this, 44, 10);
      byte b = 20;
      byte b2 = 8;
      byte b3 = 16;
      byte b4 = 4;
      this.sideModels[0].addBox(-b / 2, -b3 / 2, -1.0F, b, b3, 2, 0.0F);
      this.sideModels[0].setRotationPoint(0.0F, b4, 0.0F);
      this.sideModels[5].addBox(-b / 2 + 1, -b3 / 2 + 1, -1.0F, b - 2, b3 - 2, 1, 0.0F);
      this.sideModels[5].setRotationPoint(0.0F, b4, 0.0F);
      this.sideModels[1].addBox(-b / 2 + 2, -b2 - 1, -1.0F, b - 4, b2, 2, 0.0F);
      this.sideModels[1].setRotationPoint(-b / 2 + 1, b4, 0.0F);
      this.sideModels[2].addBox(-b / 2 + 2, -b2 - 1, -1.0F, b - 4, b2, 2, 0.0F);
      this.sideModels[2].setRotationPoint(b / 2 - 1, b4, 0.0F);
      this.sideModels[3].addBox(-b / 2 + 2, -b2 - 1, -1.0F, b - 4, b2, 2, 0.0F);
      this.sideModels[3].setRotationPoint(0.0F, b4, -b3 / 2 + 1);
      this.sideModels[4].addBox(-b / 2 + 2, -b2 - 1, -1.0F, b - 4, b2, 2, 0.0F);
      this.sideModels[4].setRotationPoint(0.0F, b4, b3 / 2 - 1);
      this.sideModels[0].rotateAngleX = (float) (Math.PI / 2);
      this.sideModels[1].rotateAngleY = (float) (Math.PI * 3.0 / 2.0);
      this.sideModels[2].rotateAngleY = (float) (Math.PI / 2);
      this.sideModels[3].rotateAngleY = (float) Math.PI;
      this.sideModels[5].rotateAngleX = (float) (-Math.PI / 2);
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      this.sideModels[5].rotationPointY = 4.0F - f3;

      for (int i = 0; i < 6; i++) {
         this.sideModels[i].render(scale);
      }
   }
}
