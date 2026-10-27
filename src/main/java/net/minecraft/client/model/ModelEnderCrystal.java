package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelEnderCrystal extends ModelBase {
   public ModelRenderer glass = new ModelRenderer(this, "glass");
   public ModelRenderer cube;
   public ModelRenderer base;

   public ModelEnderCrystal(float f, boolean flag) {
      this.glass.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      this.cube = new ModelRenderer(this, "cube");
      this.cube.setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      if (flag) {
         this.base = new ModelRenderer(this, "base");
         this.base.setTextureOffset(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12);
      }
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      GlStateManager.pushMatrix();
      GlStateManager.scale(2.0F, 2.0F, 2.0F);
      GlStateManager.translate(0.0F, -0.5F, 0.0F);
      if (this.base != null) {
         this.base.render(scale);
      }

      GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(0.0F, 0.8F + f3, 0.0F);
      GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
      this.glass.render(scale);
      float f6 = 0.875F;
      GlStateManager.scale(f6, f6, f6);
      GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
      this.glass.render(scale);
      GlStateManager.scale(f6, f6, f6);
      GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
      this.cube.render(scale);
      GlStateManager.popMatrix();
   }
}
