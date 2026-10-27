package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.CustomColors;

public class LayerSheepWool implements LayerRenderer<EntitySheep> {
   public static ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   public ModelSheep1 sheepModel = new ModelSheep1();
   public RenderSheep sheepRenderer;

   public LayerSheepWool(RenderSheep sheepRendererIn) {
      this.sheepRenderer = sheepRendererIn;
   }

   public void doRenderLayer(EntitySheep entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      if (!entitylivingbaseIn.getSheared() && !entitylivingbaseIn.isInvisible()) {
         this.sheepRenderer.bindTexture(TEXTURE);
         if (entitylivingbaseIn.hasCustomName() && "jeb_".equals(entitylivingbaseIn.getCustomNameTag())) {
            byte b = 25;
            int n = entitylivingbaseIn.ticksExisted / 25 + entitylivingbaseIn.getEntityId();
            int length = EnumDyeColor.values().length;
            int n2 = n % length;
            int n3 = (n + 1) % length;
            float f6 = (entitylivingbaseIn.ticksExisted % 25 + partialTicks) / 25.0F;
            float[] dyeRgb = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(n2));
            float[] dyeRgb2 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(n3));
            if (Config.isCustomColors()) {
               dyeRgb = CustomColors.getSheepColors(EnumDyeColor.byMetadata(n2), dyeRgb);
               dyeRgb2 = CustomColors.getSheepColors(EnumDyeColor.byMetadata(n3), dyeRgb2);
            }

            GlStateManager.color(
               dyeRgb[0] * (1.0F - f6) + dyeRgb2[0] * f6, dyeRgb[1] * (1.0F - f6) + dyeRgb2[1] * f6, dyeRgb[2] * (1.0F - f6) + dyeRgb2[2] * f6
            );
         } else {
            float[] dyeRgb = EntitySheep.getDyeRgb(entitylivingbaseIn.getFleeceColor());
            if (Config.isCustomColors()) {
               dyeRgb = CustomColors.getSheepColors(entitylivingbaseIn.getFleeceColor(), dyeRgb);
            }

            GlStateManager.color(dyeRgb[0], dyeRgb[1], dyeRgb[2]);
         }

         this.sheepModel.setModelAttributes(this.sheepRenderer.getMainModel());
         this.sheepModel.setLivingAnimations(entitylivingbaseIn, f, f2, partialTicks);
         this.sheepModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
