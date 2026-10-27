package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.CustomColors;

public class LayerWolfCollar implements LayerRenderer<EntityWolf> {
   public static ResourceLocation WOLF_COLLAR = new ResourceLocation("textures/entity/wolf/wolf_collar.png");
   public RenderWolf wolfRenderer;

   public LayerWolfCollar(RenderWolf wolfRendererIn) {
      this.wolfRenderer = wolfRendererIn;
   }

   public void doRenderLayer(EntityWolf entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
         this.wolfRenderer.bindTexture(WOLF_COLLAR);
         EnumDyeColor byMetadata = EnumDyeColor.byMetadata(entitylivingbaseIn.getCollarColor().getMetadata());
         float[] dyeRgb = EntitySheep.getDyeRgb(byMetadata);
         if (Config.isCustomColors()) {
            dyeRgb = CustomColors.getWolfCollarColors(byMetadata, dyeRgb);
         }

         GlStateManager.color(dyeRgb[0], dyeRgb[1], dyeRgb[2]);
         this.wolfRenderer.getMainModel().render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
