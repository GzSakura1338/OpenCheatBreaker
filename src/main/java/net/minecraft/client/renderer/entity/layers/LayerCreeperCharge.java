package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

public class LayerCreeperCharge implements LayerRenderer<EntityCreeper> {
   public static ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   public ModelCreeper creeperModel = new ModelCreeper(2.0F);
   public RenderCreeper creeperRenderer;

   public LayerCreeperCharge(RenderCreeper creeperRendererIn) {
      this.creeperRenderer = creeperRendererIn;
   }

   public void doRenderLayer(EntityCreeper entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      if (entitylivingbaseIn.getPowered()) {
         boolean isInvisible = entitylivingbaseIn.isInvisible();
         GlStateManager.depthMask(!isInvisible);
         this.creeperRenderer.bindTexture(LIGHTNING_TEXTURE);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float f6 = entitylivingbaseIn.ticksExisted + partialTicks;
         GlStateManager.translate(f6 * 0.01F, f6 * 0.01F, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         float f7 = 0.5F;
         GlStateManager.color(f7, f7, f7, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(1, 1);
         this.creeperModel.setModelAttributes(this.creeperRenderer.getMainModel());
         this.creeperModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(isInvisible);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
