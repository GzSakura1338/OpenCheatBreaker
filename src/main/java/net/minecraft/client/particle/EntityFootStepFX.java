package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFootStepFX extends EntityFX {
   public static ResourceLocation FOOTPRINT_TEXTURE = new ResourceLocation("textures/particle/footprint.png");
   public int footstepAge;
   public TextureManager currentFootSteps;
   public int footstepMaxAge;

   public EntityFootStepFX(TextureManager currentFootStepsIn, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
      this.currentFootSteps = currentFootStepsIn;
      this.motionX = this.motionY = this.motionZ = 0.0;
      this.footstepMaxAge = 200;
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float rotationX,
      float rotationZ,
      float rotationYZ,
      float rotationXY,
      float rotationXZ
   ) {
      float f = (this.footstepAge + partialTicks) / this.footstepMaxAge;
      f *= f;
      float f2 = 2.0F - f * 2.0F;
      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      f2 *= 0.2F;
      GlStateManager.disableLighting();
      float f3 = 0.125F;
      float f4 = (float)(this.posX - interpPosX);
      float f5 = (float)(this.posY - interpPosY);
      float f6 = (float)(this.posZ - interpPosZ);
      float lightBrightness = this.worldObj.getLightBrightness(new BlockPos(this));
      this.currentFootSteps.bindTexture(FOOTPRINT_TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      worldRendererIn.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRendererIn.pos(f4 - 0.125F, f5, f6 + 0.125F).tex(0.0, 1.0).color(lightBrightness, lightBrightness, lightBrightness, f2).endVertex();
      worldRendererIn.pos(f4 + 0.125F, f5, f6 + 0.125F).tex(1.0, 1.0).color(lightBrightness, lightBrightness, lightBrightness, f2).endVertex();
      worldRendererIn.pos(f4 + 0.125F, f5, f6 - 0.125F).tex(1.0, 0.0).color(lightBrightness, lightBrightness, lightBrightness, f2).endVertex();
      worldRendererIn.pos(f4 - 0.125F, f5, f6 - 0.125F).tex(0.0, 0.0).color(lightBrightness, lightBrightness, lightBrightness, f2).endVertex();
      Tessellator.getInstance().draw();
      GlStateManager.disableBlend();
      GlStateManager.enableLighting();
   }

   @Override
   public void onUpdate() {
      this.footstepAge++;
      if (this.footstepAge == this.footstepMaxAge) {
         this.setDead();
      }
   }

   @Override
   public int getFXLayer() {
      return 3;
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityFootStepFX(Minecraft.getMinecraft().getTextureManager(), worldIn, xCoordIn, yCoordIn, zCoordIn);
      }
   }
}
