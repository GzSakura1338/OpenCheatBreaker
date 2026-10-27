package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;

public class LayerSaddle implements LayerRenderer<EntityPig> {
   public static ResourceLocation TEXTURE = new ResourceLocation("textures/entity/pig/pig_saddle.png");
   public ModelPig pigModel = new ModelPig(0.5F);
   public RenderPig pigRenderer;

   public LayerSaddle(RenderPig pigRendererIn) {
      this.pigRenderer = pigRendererIn;
   }

   public void doRenderLayer(EntityPig entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      if (entitylivingbaseIn.getSaddled()) {
         this.pigRenderer.bindTexture(TEXTURE);
         this.pigModel.setModelAttributes(this.pigRenderer.getMainModel());
         this.pigModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
