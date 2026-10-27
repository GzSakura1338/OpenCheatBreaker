package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class Barrier extends EntityFX {
   public Barrier(World worldIn, double d, double d2, double d3, Item item) {
      super(worldIn, d, d2, d3, 0.0, 0.0, 0.0);
      this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(item));
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      this.motionX = this.motionY = this.motionZ = 0.0;
      this.particleGravity = 0.0F;
      this.particleMaxAge = 80;
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
      float minU = this.particleIcon.getMinU();
      float maxU = this.particleIcon.getMaxU();
      float minV = this.particleIcon.getMinV();
      float maxV = this.particleIcon.getMaxV();
      float f = 0.5F;
      float f2 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float f3 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float f4 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      int brightnessForRender = this.getBrightnessForRender(partialTicks);
      int n = brightnessForRender >> 16 & 65535;
      int n2 = brightnessForRender & 65535;
      worldRendererIn.pos(f2 - rotationX * 0.5F - rotationXY * 0.5F, f3 - rotationZ * 0.5F, f4 - rotationYZ * 0.5F - rotationXZ * 0.5F)
         .tex(maxU, maxV)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(n, n2)
         .endVertex();
      worldRendererIn.pos(f2 - rotationX * 0.5F + rotationXY * 0.5F, f3 + rotationZ * 0.5F, f4 - rotationYZ * 0.5F + rotationXZ * 0.5F)
         .tex(maxU, minV)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(n, n2)
         .endVertex();
      worldRendererIn.pos(f2 + rotationX * 0.5F + rotationXY * 0.5F, f3 + rotationZ * 0.5F, f4 + rotationYZ * 0.5F + rotationXZ * 0.5F)
         .tex(minU, minV)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(n, n2)
         .endVertex();
      worldRendererIn.pos(f2 + rotationX * 0.5F - rotationXY * 0.5F, f3 - rotationZ * 0.5F, f4 + rotationYZ * 0.5F - rotationXZ * 0.5F)
         .tex(minU, maxV)
         .color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
         .lightmap(n, n2)
         .endVertex();
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new Barrier(worldIn, xCoordIn, yCoordIn, zCoordIn, Item.getItemFromBlock(Blocks.barrier));
      }
   }
}
