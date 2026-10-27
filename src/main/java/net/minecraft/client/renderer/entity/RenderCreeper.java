package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCreeperCharge;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderCreeper extends RenderLiving<EntityCreeper> {
   public static ResourceLocation creeperTextures = new ResourceLocation("textures/entity/creeper/creeper.png");

   public RenderCreeper(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelCreeper(), 0.5F);
      this.addLayer(new LayerCreeperCharge(this));
   }

   public void preRenderCallback(EntityCreeper entitylivingbaseIn, float partialTickTime) {
      float creeperFlashIntensity = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);
      float f = 1.0F + MathHelper.sin(creeperFlashIntensity * 100.0F) * creeperFlashIntensity * 0.01F;
      creeperFlashIntensity = MathHelper.clamp_float(creeperFlashIntensity, 0.0F, 1.0F);
      creeperFlashIntensity *= creeperFlashIntensity;
      creeperFlashIntensity *= creeperFlashIntensity;
      float f2 = (1.0F + creeperFlashIntensity * 0.4F) * f;
      float f3 = (1.0F + creeperFlashIntensity * 0.1F) / f;
      GlStateManager.scale(f2, f3, f2);
   }

   public int getColorMultiplier(EntityCreeper entitylivingbaseIn, float lightBrightness, float partialTickTime) {
      float creeperFlashIntensity = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);
      if ((int)(creeperFlashIntensity * 10.0F) % 2 == 0) {
         return 0;
      }

      int n = (int)(creeperFlashIntensity * 0.2F * 255.0F);
      n = MathHelper.clamp_int(n, 0, 255);
      return n << 24 | 16777215;
   }

   public ResourceLocation getEntityTexture(EntityCreeper entity) {
      return creeperTextures;
   }
}
