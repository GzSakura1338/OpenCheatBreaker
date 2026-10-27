package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

public class RenderFireball extends Render<EntityFireball> {
   public float scale;

   public RenderFireball(RenderManager renderManagerIn, float scaleIn) {
      super(renderManagerIn);
      this.scale = scaleIn;
   }

   public void doRender(EntityFireball entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      this.bindEntityTexture(entity);
      GlStateManager.translate((float)x, (float)y, (float)z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(this.scale, this.scale, this.scale);
      TextureAtlasSprite particleIcon = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Items.fire_charge);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      float minU = particleIcon.getMinU();
      float maxU = particleIcon.getMaxU();
      float minV = particleIcon.getMinV();
      float maxV = particleIcon.getMaxV();
      float f = 1.0F;
      float f2 = 0.5F;
      float f3 = 0.25F;
      GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
      worldRenderer.pos(-0.5, -0.25, 0.0).tex(minU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(0.5, -0.25, 0.0).tex(maxU, maxV).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(0.5, 0.75, 0.0).tex(maxU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(-0.5, 0.75, 0.0).tex(minU, minV).normal(0.0F, 1.0F, 0.0F).endVertex();
      instance.draw();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityFireball entity) {
      return TextureMap.locationBlocksTexture;
   }
}
