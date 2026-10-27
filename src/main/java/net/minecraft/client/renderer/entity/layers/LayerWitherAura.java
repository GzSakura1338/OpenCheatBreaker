package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelWither;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderWither;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class LayerWitherAura implements LayerRenderer<EntityWither> {
   public static ResourceLocation WITHER_ARMOR = new ResourceLocation("textures/entity/wither/wither_armor.png");
   public ModelWither witherModel = new ModelWither(0.5F);
   public RenderWither witherRenderer;

   public LayerWitherAura(RenderWither witherRendererIn) {
      this.witherRenderer = witherRendererIn;
   }

   public void doRenderLayer(EntityWither entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      if (entitylivingbaseIn.isArmored()) {
         GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
         this.witherRenderer.bindTexture(WITHER_ARMOR);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float f6 = entitylivingbaseIn.ticksExisted + partialTicks;
         float f7 = MathHelper.cos(f6 * 0.02F) * 3.0F;
         float f8 = f6 * 0.01F;
         GlStateManager.translate(f7, f8, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         float f9 = 0.5F;
         GlStateManager.color(f9, f9, f9, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(1, 1);
         this.witherModel.setLivingAnimations(entitylivingbaseIn, f, f2, partialTicks);
         this.witherModel.setModelAttributes(this.witherRenderer.getMainModel());
         this.witherModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
