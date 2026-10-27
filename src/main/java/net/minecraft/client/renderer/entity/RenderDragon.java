package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerEnderDragonDeath;
import net.minecraft.client.renderer.entity.layers.LayerEnderDragonEyes;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderDragon extends RenderLiving<EntityDragon> {
   public static ResourceLocation enderDragonCrystalBeamTextures = new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png");
   public static ResourceLocation enderDragonExplodingTextures = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
   public static ResourceLocation enderDragonTextures = new ResourceLocation("textures/entity/enderdragon/dragon.png");
   public ModelDragon modelDragon = (ModelDragon)this.mainModel;

   public RenderDragon(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelDragon(0.0F), 0.5F);
      this.addLayer(new LayerEnderDragonEyes(this));
      this.addLayer(new LayerEnderDragonDeath());
   }

   public void rotateCorpse(EntityDragon bat, float f, float f2, float partialTicks) {
      float f3 = (float)bat.getMovementOffsets(7, partialTicks)[0];
      float f4 = (float)(bat.getMovementOffsets(5, partialTicks)[1] - bat.getMovementOffsets(10, partialTicks)[1]);
      GlStateManager.rotate(-f3, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f4 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(0.0F, 0.0F, 1.0F);
      if (bat.deathTime > 0) {
         float f5 = (bat.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f5 = MathHelper.sqrt_float(f5);
         if (f5 > 1.0F) {
            f5 = 1.0F;
         }

         GlStateManager.rotate(f5 * this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
      }
   }

   public void renderModel(EntityDragon entitylivingbaseIn, float f, float f2, float f3, float f4, float f5, float scaleFactor) {
      if (entitylivingbaseIn.deathTicks > 0) {
         float f6 = entitylivingbaseIn.deathTicks / 200.0F;
         GlStateManager.depthFunc(515);
         GlStateManager.enableAlpha();
         GlStateManager.alphaFunc(516, f6);
         this.bindTexture(enderDragonExplodingTextures);
         this.mainModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scaleFactor);
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.depthFunc(514);
      }

      this.bindEntityTexture(entitylivingbaseIn);
      this.mainModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scaleFactor);
      if (entitylivingbaseIn.hurtTime > 0) {
         GlStateManager.depthFunc(514);
         GlStateManager.disableTexture2D();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 0.0F, 0.0F, 0.5F);
         this.mainModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scaleFactor);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
         GlStateManager.depthFunc(515);
      }
   }

   public void doRender(EntityDragon entity, double x, double y, double z, float entityYaw, float partialTicks) {
      BossStatus.setBossStatus(entity, false);
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      if (entity.healingEnderCrystal != null) {
         this.drawRechargeRay(entity, x, y, z, partialTicks);
      }
   }

   public void drawRechargeRay(EntityDragon dragon, double d, double d2, double d3, float f) {
      float f2 = dragon.healingEnderCrystal.innerRotation + f;
      float f3 = MathHelper.sin(f2 * 0.2F) / 2.0F + 0.5F;
      f3 = (f3 * f3 + f3) * 0.2F;
      float f4 = (float)(dragon.healingEnderCrystal.posX - dragon.posX - (dragon.prevPosX - dragon.posX) * (1.0F - f));
      float f5 = (float)(f3 + dragon.healingEnderCrystal.posY - 1.0 - dragon.posY - (dragon.prevPosY - dragon.posY) * (1.0F - f));
      float f6 = (float)(dragon.healingEnderCrystal.posZ - dragon.posZ - (dragon.prevPosZ - dragon.posZ) * (1.0F - f));
      float f7 = MathHelper.sqrt_float(f4 * f4 + f6 * f6);
      float f8 = MathHelper.sqrt_float(f4 * f4 + f5 * f5 + f6 * f6);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)d, (float)d2 + 2.0F, (float)d3);
      GlStateManager.rotate((float)(-Math.atan2(f6, f4)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((float)(-Math.atan2(f7, f5)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableCull();
      this.bindTexture(enderDragonCrystalBeamTextures);
      GlStateManager.shadeModel(7425);
      float f9 = 0.0F - (dragon.ticksExisted + f) * 0.01F;
      float f10 = MathHelper.sqrt_float(f4 * f4 + f5 * f5 + f6 * f6) / 32.0F - (dragon.ticksExisted + f) * 0.01F;
      worldRenderer.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
      byte b = 8;

      for (int i = 0; i <= 8; i++) {
         float f11 = MathHelper.sin(i % 8 * (float) Math.PI * 2.0F / 8.0F) * 0.75F;
         float f12 = MathHelper.cos(i % 8 * (float) Math.PI * 2.0F / 8.0F) * 0.75F;
         float f13 = i % 8 * 1.0F / 8.0F;
         worldRenderer.pos(f11 * 0.2F, f12 * 0.2F, 0.0).tex(f13, f10).color(0, 0, 0, 255).endVertex();
         worldRenderer.pos(f11, f12, f8).tex(f13, f9).color(255, 255, 255, 255).endVertex();
      }

      instance.draw();
      GlStateManager.enableCull();
      GlStateManager.shadeModel(7424);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   public ResourceLocation getEntityTexture(EntityDragon entity) {
      return enderDragonTextures;
   }
}
