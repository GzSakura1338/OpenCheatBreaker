package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerIronGolemFlower;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;

public class RenderIronGolem extends RenderLiving<EntityIronGolem> {
   public static ResourceLocation ironGolemTextures = new ResourceLocation("textures/entity/iron_golem.png");

   public RenderIronGolem(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelIronGolem(), 0.5F);
      this.addLayer(new LayerIronGolemFlower(this));
   }

   public ResourceLocation getEntityTexture(EntityIronGolem entity) {
      return ironGolemTextures;
   }

   public void rotateCorpse(EntityIronGolem bat, float f, float f2, float partialTicks) {
      super.rotateCorpse(bat, f, f2, partialTicks);
      if (bat.limbSwingAmount >= 0.01) {
         float f3 = 13.0F;
         float f4 = bat.limbSwing - bat.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
         float f5 = (Math.abs(f4 % f3 - f3 * 0.5F) - f3 * 0.25F) / (f3 * 0.25F);
         GlStateManager.rotate(6.5F * f5, 0.0F, 0.0F, 1.0F);
      }
   }
}
