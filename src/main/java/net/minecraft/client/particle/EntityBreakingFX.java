package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityBreakingFX extends EntityFX {
   public EntityBreakingFX(World worldIn, double posXIn, double posYIn, double posZIn, Item item) {
      this(worldIn, posXIn, posYIn, posZIn, item, 0);
   }

   public EntityBreakingFX(World worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, Item item, int n) {
      this(worldIn, posXIn, posYIn, posZIn, item, n);
      this.motionX *= 0.1F;
      this.motionY *= 0.1F;
      this.motionZ *= 0.1F;
      this.motionX += xSpeedIn;
      this.motionY += ySpeedIn;
      this.motionZ += zSpeedIn;
   }

   public EntityBreakingFX(World worldIn, double posXIn, double posYIn, double posZIn, Item item, int n) {
      super(worldIn, posXIn, posYIn, posZIn, 0.0, 0.0, 0.0);
      this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item, n));
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.particleGravity = Blocks.snow.blockParticleGravity;
      this.particleScale /= 2.0F;
   }

   @Override
   public int getFXLayer() {
      return 1;
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
      float f = (this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
      float f2 = f + 0.015609375F;
      float f3 = (this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
      float f4 = f3 + 0.015609375F;
      float f5 = 0.1F * this.particleScale;
      if (this.particleIcon != null) {
         f = this.particleIcon.getInterpolatedU(this.particleTextureJitterX / 4.0F * 16.0F);
         f2 = this.particleIcon.getInterpolatedU((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F);
         f3 = this.particleIcon.getInterpolatedV(this.particleTextureJitterY / 4.0F * 16.0F);
         f4 = this.particleIcon.getInterpolatedV((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F);
      }

      float f6 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float f7 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float f8 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      int brightnessForRender = this.getBrightnessForRender(partialTicks);
      int n = brightnessForRender >> 16 & 65535;
      int n2 = brightnessForRender & 65535;
      worldRendererIn.pos(f6 - rotationX * f5 - rotationXY * f5, f7 - rotationZ * f5, f8 - rotationYZ * f5 - rotationXZ * f5)
         .tex(f, f4)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(n, n2)
         .endVertex();
      worldRendererIn.pos(f6 - rotationX * f5 + rotationXY * f5, f7 + rotationZ * f5, f8 - rotationYZ * f5 + rotationXZ * f5)
         .tex(f, f3)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(n, n2)
         .endVertex();
      worldRendererIn.pos(f6 + rotationX * f5 + rotationXY * f5, f7 + rotationZ * f5, f8 + rotationYZ * f5 + rotationXZ * f5)
         .tex(f2, f3)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(n, n2)
         .endVertex();
      worldRendererIn.pos(f6 + rotationX * f5 - rotationXY * f5, f7 - rotationZ * f5, f8 + rotationYZ * f5 - rotationXZ * f5)
         .tex(f2, f4)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(n, n2)
         .endVertex();
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         int n = ints.length > 1 ? ints[1] : 0;
         return new EntityBreakingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Item.getItemById(ints[0]), n);
      }
   }

   public static class SlimeFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityBreakingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, Items.slime_ball);
      }
   }

   public static class SnowballFactory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityBreakingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, Items.snowball);
      }
   }
}
