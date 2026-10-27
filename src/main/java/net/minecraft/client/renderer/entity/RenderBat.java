package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderBat extends RenderLiving<EntityBat> {
   public static ResourceLocation batTextures = new ResourceLocation("textures/entity/bat.png");

   public RenderBat(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelBat(), 0.25F);
   }

   public ResourceLocation getEntityTexture(EntityBat entity) {
      return batTextures;
   }

   public void preRenderCallback(EntityBat entitylivingbaseIn, float partialTickTime) {
      GlStateManager.scale(0.35F, 0.35F, 0.35F);
   }

   public void rotateCorpse(EntityBat bat, float f, float f2, float partialTicks) {
      if (!bat.getIsBatHanging()) {
         GlStateManager.translate(0.0F, MathHelper.cos(f * 0.3F) * 0.1F, 0.0F);
      } else {
         GlStateManager.translate(0.0F, -0.1F, 0.0F);
      }

      super.rotateCorpse(bat, f, f2, partialTicks);
   }
}
