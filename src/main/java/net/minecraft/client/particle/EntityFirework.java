package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFirework {
   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         EntityFirework.SparkFX sparkFX = new EntityFirework.SparkFX(
            worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Minecraft.getMinecraft().effectRenderer
         );
         sparkFX.setAlphaF(0.99F);
         return sparkFX;
      }
   }

   public static class OverlayFX extends EntityFX {
      public OverlayFX(World world, double d, double d2, double d3) {
         super(world, d, d2, d3);
         this.particleMaxAge = 4;
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
         float f = 0.25F;
         float f2 = 0.5F;
         float f3 = 0.125F;
         float f4 = 0.375F;
         float f5 = 7.1F * MathHelper.sin((this.particleAge + partialTicks - 1.0F) * 0.25F * (float) Math.PI);
         this.particleAlpha = 0.6F - (this.particleAge + partialTicks - 1.0F) * 0.25F * 0.5F;
         float f6 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
         float f7 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
         float f8 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
         int brightnessForRender = this.getBrightnessForRender(partialTicks);
         int n = brightnessForRender >> 16 & 65535;
         int n2 = brightnessForRender & 65535;
         worldRendererIn.pos(f6 - rotationX * f5 - rotationXY * f5, f7 - rotationZ * f5, f8 - rotationYZ * f5 - rotationXZ * f5)
            .tex(0.5, 0.375)
            .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(n, n2)
            .endVertex();
         worldRendererIn.pos(f6 - rotationX * f5 + rotationXY * f5, f7 + rotationZ * f5, f8 - rotationYZ * f5 + rotationXZ * f5)
            .tex(0.5, 0.125)
            .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(n, n2)
            .endVertex();
         worldRendererIn.pos(f6 + rotationX * f5 + rotationXY * f5, f7 + rotationZ * f5, f8 + rotationYZ * f5 + rotationXZ * f5)
            .tex(0.25, 0.125)
            .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(n, n2)
            .endVertex();
         worldRendererIn.pos(f6 + rotationX * f5 - rotationXY * f5, f7 - rotationZ * f5, f8 + rotationYZ * f5 - rotationXZ * f5)
            .tex(0.25, 0.375)
            .color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
            .lightmap(n, n2)
            .endVertex();
      }
   }

   public static class SparkFX extends EntityFX {
      public int baseTextureIndex = 160;
      public boolean trail;
      public boolean twinkle;
      public EffectRenderer manager;
      public float fadeColourRed;
      public float fadeColourGreen;
      public float fadeColourBlue;
      public boolean hasFadeColour;

      public SparkFX(World world, double d, double d2, double d3, double d4, double d5, double d6, EffectRenderer effectRenderer) {
         super(world, d, d2, d3);
         this.motionX = d4;
         this.motionY = d5;
         this.motionZ = d6;
         this.manager = effectRenderer;
         this.particleScale *= 0.75F;
         this.particleMaxAge = 48 + this.rand.nextInt(12);
         this.noClip = false;
      }

      public void setTrail(boolean trailIn) {
         this.trail = trailIn;
      }

      public void setTwinkle(boolean twinkleIn) {
         this.twinkle = twinkleIn;
      }

      public void setColour(int colour) {
         float f = ((colour & 0xFF0000) >> 16) / 255.0F;
         float f2 = ((colour & 0xFF00) >> 8) / 255.0F;
         float f3 = ((colour & 0xFF) >> 0) / 255.0F;
         float f4 = 1.0F;
         this.setRBGColorF(f * f4, f2 * f4, f3 * f4);
      }

      public void setFadeColour(int faceColour) {
         this.fadeColourRed = ((faceColour & 0xFF0000) >> 16) / 255.0F;
         this.fadeColourGreen = ((faceColour & 0xFF00) >> 8) / 255.0F;
         this.fadeColourBlue = ((faceColour & 0xFF) >> 0) / 255.0F;
         this.hasFadeColour = true;
      }

      @Override
      public AxisAlignedBB getCollisionBoundingBox() {
         return null;
      }

      @Override
      public boolean canBePushed() {
         return false;
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
         if (!this.twinkle || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0) {
            super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
         }
      }

      @Override
      public void onUpdate() {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
         }

         if (this.particleAge > this.particleMaxAge / 2) {
            this.setAlphaF(1.0F - ((float)this.particleAge - this.particleMaxAge / 2) / this.particleMaxAge);
            if (this.hasFadeColour) {
               this.particleRed = this.particleRed + (this.fadeColourRed - this.particleRed) * 0.2F;
               this.particleGreen = this.particleGreen + (this.fadeColourGreen - this.particleGreen) * 0.2F;
               this.particleBlue = this.particleBlue + (this.fadeColourBlue - this.particleBlue) * 0.2F;
            }
         }

         this.setParticleTextureIndex(this.baseTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
         this.motionY -= 0.004;
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.91F;
         this.motionY *= 0.91F;
         this.motionZ *= 0.91F;
         if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
         }

         if (this.trail && this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0) {
            EntityFirework.SparkFX sparkFX = new EntityFirework.SparkFX(this.worldObj, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0, this.manager);
            sparkFX.setAlphaF(0.99F);
            sparkFX.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
            sparkFX.particleAge = sparkFX.particleMaxAge / 2;
            if (this.hasFadeColour) {
               sparkFX.hasFadeColour = true;
               sparkFX.fadeColourRed = this.fadeColourRed;
               sparkFX.fadeColourGreen = this.fadeColourGreen;
               sparkFX.fadeColourBlue = this.fadeColourBlue;
            }

            sparkFX.twinkle = this.twinkle;
            this.manager.addEffect(sparkFX);
         }
      }

      @Override
      public int getBrightnessForRender(float partialTicks) {
         return 15728880;
      }

      @Override
      public float getBrightness(float partialTicks) {
         return 1.0F;
      }
   }

   public static class StarterFX extends EntityFX {
      public int fireworkAge;
      public EffectRenderer theEffectRenderer;
      public NBTTagList fireworkExplosions;
      public boolean twinkle;

      public StarterFX(World world, double d, double d2, double d3, double d4, double d5, double d6, EffectRenderer effectRenderer, NBTTagCompound nbttagCompound) {
         super(world, d, d2, d3, 0.0, 0.0, 0.0);
         this.motionX = d4;
         this.motionY = d5;
         this.motionZ = d6;
         this.theEffectRenderer = effectRenderer;
         this.particleMaxAge = 8;
         if (nbttagCompound != null) {
            this.fireworkExplosions = nbttagCompound.getTagList("Explosions", 10);
            if (this.fireworkExplosions.tagCount() == 0) {
               this.fireworkExplosions = null;
            } else {
               this.particleMaxAge = this.fireworkExplosions.tagCount() * 2 - 1;

               for (int i = 0; i < this.fireworkExplosions.tagCount(); i++) {
                  NBTTagCompound compoundTagAt = this.fireworkExplosions.getCompoundTagAt(i);
                  if (compoundTagAt.getBoolean("Flicker")) {
                     this.twinkle = true;
                     this.particleMaxAge += 15;
                     break;
                  }
               }
            }
         }
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
      }

      @Override
      public void onUpdate() {
         if (this.fireworkAge == 0 && this.fireworkExplosions != null) {
            boolean isFar = this.isFar();
            boolean flag = false;
            if (this.fireworkExplosions.tagCount() >= 3) {
               flag = true;
            } else {
               for (int i = 0; i < this.fireworkExplosions.tagCount(); i++) {
                  NBTTagCompound compoundTagAt = this.fireworkExplosions.getCompoundTagAt(i);
                  if (compoundTagAt.getByte("Type") == 1) {
                     flag = true;
                     break;
                  }
               }
            }

            String str = "fireworks." + (flag ? "largeBlast" : "blast") + (isFar ? "_far" : "");
            this.worldObj.playSound(this.posX, this.posY, this.posZ, str, 20.0F, 0.95F + this.rand.nextFloat() * 0.1F, true);
         }

         if (this.fireworkAge % 2 == 0 && this.fireworkExplosions != null && this.fireworkAge / 2 < this.fireworkExplosions.tagCount()) {
            int n = this.fireworkAge / 2;
            NBTTagCompound compoundTagAt = this.fireworkExplosions.getCompoundTagAt(n);
            byte byte2 = compoundTagAt.getByte("Type");
            boolean boolean2 = compoundTagAt.getBoolean("Trail");
            boolean boolean3 = compoundTagAt.getBoolean("Flicker");
            int[] intArray = compoundTagAt.getIntArray("Colors");
            int[] intArray2 = compoundTagAt.getIntArray("FadeColors");
            if (intArray.length == 0) {
               intArray = new int[]{ItemDye.dyeColors[0]};
            }

            if (byte2 == 1) {
               this.createBall(0.5, 4, intArray, intArray2, boolean2, boolean3);
            } else if (byte2 == 2) {
               this.createShaped(
                  0.5,
                  new double[][]{
                     {0.0, 1.0},
                     {0.3455, 0.309},
                     {0.9511, 0.309},
                     {0.3795918367346939, -0.12653061224489795},
                     {0.6122448979591837, -0.8040816326530612},
                     {0.0, -0.35918367346938773}
                  },
                  intArray,
                  intArray2,
                  boolean2,
                  boolean3,
                  false
               );
            } else if (byte2 == 3) {
               this.createShaped(
                  0.5,
                  new double[][]{
                     {0.0, 0.2},
                     {0.2, 0.2},
                     {0.2, 0.6},
                     {0.6, 0.6},
                     {0.6, 0.2},
                     {0.2, 0.2},
                     {0.2, 0.0},
                     {0.4, 0.0},
                     {0.4, -0.6},
                     {0.2, -0.6},
                     {0.2, -0.4},
                     {0.0, -0.4}
                  },
                  intArray,
                  intArray2,
                  boolean2,
                  boolean3,
                  true
               );
            } else if (byte2 == 4) {
               this.createBurst(intArray, intArray2, boolean2, boolean3);
            } else {
               this.createBall(0.25, 2, intArray, intArray2, boolean2, boolean3);
            }

            int n2 = intArray[0];
            float f = ((n2 & 0xFF0000) >> 16) / 255.0F;
            float f2 = ((n2 & 0xFF00) >> 8) / 255.0F;
            float f3 = ((n2 & 0xFF) >> 0) / 255.0F;
            EntityFirework.OverlayFX overlayFX = new EntityFirework.OverlayFX(this.worldObj, this.posX, this.posY, this.posZ);
            overlayFX.setRBGColorF(f, f2, f3);
            this.theEffectRenderer.addEffect(overlayFX);
         }

         this.fireworkAge++;
         if (this.fireworkAge > this.particleMaxAge) {
            if (this.twinkle) {
               boolean isFar = this.isFar();
               String str = "fireworks." + (isFar ? "twinkle_far" : "twinkle");
               this.worldObj.playSound(this.posX, this.posY, this.posZ, str, 20.0F, 0.9F + this.rand.nextFloat() * 0.15F, true);
            }

            this.setDead();
         }
      }

      public boolean isFar() {
         Minecraft minecraft = Minecraft.getMinecraft();
         return minecraft == null || minecraft.getRenderViewEntity() == null || minecraft.getRenderViewEntity().getDistanceSq(this.posX, this.posY, this.posZ) >= 256.0;
      }

      public void createParticle(
         double d, double d2, double d3, double d4, double d5, double d6, int[] ints, int[] ints2, boolean flag, boolean flag2
      ) {
         EntityFirework.SparkFX sparkFX = new EntityFirework.SparkFX(this.worldObj, d, d2, d3, d4, d5, d6, this.theEffectRenderer);
         sparkFX.setAlphaF(0.99F);
         sparkFX.setTrail(flag);
         sparkFX.setTwinkle(flag2);
         int nextInt = this.rand.nextInt(ints.length);
         sparkFX.setColour(ints[nextInt]);
         if (ints2 != null && ints2.length > 0) {
            sparkFX.setFadeColour(ints2[this.rand.nextInt(ints2.length)]);
         }

         this.theEffectRenderer.addEffect(sparkFX);
      }

      public void createBall(double speed, int size, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
         double posX = this.posX;
         double posY = this.posY;
         double posZ = this.posZ;

         for (int i = -size; i <= size; i++) {
            for (int j = -size; j <= size; j++) {
               for (int k = -size; k <= size; k++) {
                  double d = j + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                  double d2 = i + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                  double d3 = k + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5;
                  double d4 = MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3) / speed + this.rand.nextGaussian() * 0.05;
                  this.createParticle(posX, posY, posZ, d / d4, d2 / d4, d3 / d4, colours, fadeColours, trail, twinkleIn);
                  if (i != -size && i != size && j != -size && j != size) {
                     k += size * 2 - 1;
                  }
               }
            }
         }
      }

      public void createShaped(double speed, double[][] shape, int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn, boolean flag) {
         double d = shape[0][0];
         double d2 = shape[0][1];
         this.createParticle(this.posX, this.posY, this.posZ, d * speed, d2 * speed, 0.0, colours, fadeColours, trail, twinkleIn);
         float f = this.rand.nextFloat() * (float) Math.PI;
         double d3 = flag ? 0.034 : 0.34;

         for (int i = 0; i < 3; i++) {
            double d4 = f + i * (float) Math.PI * d3;
            double d5 = d;
            double d6 = d2;

            for (int j = 1; j < shape.length; j++) {
               double d7 = shape[j][0];
               double d8 = shape[j][1];

               for (double d9 = 0.25; d9 <= 1.0; d9 += 0.25) {
                  double d10 = (d5 + (d7 - d5) * d9) * speed;
                  double d11 = (d6 + (d8 - d6) * d9) * speed;
                  double d12 = d10 * Math.sin(d4);
                  d10 *= Math.cos(d4);

                  for (double d13 = -1.0; d13 <= 1.0; d13 += 2.0) {
                     this.createParticle(this.posX, this.posY, this.posZ, d10 * d13, d11, d12 * d13, colours, fadeColours, trail, twinkleIn);
                  }
               }

               d5 = d7;
               d6 = d8;
            }
         }
      }

      public void createBurst(int[] colours, int[] fadeColours, boolean trail, boolean twinkleIn) {
         double d = this.rand.nextGaussian() * 0.05;
         double d2 = this.rand.nextGaussian() * 0.05;

         for (int i = 0; i < 70; i++) {
            double d3 = this.motionX * 0.5 + this.rand.nextGaussian() * 0.15 + d;
            double d4 = this.motionZ * 0.5 + this.rand.nextGaussian() * 0.15 + d2;
            double d5 = this.motionY * 0.5 + this.rand.nextDouble() * 0.5;
            this.createParticle(this.posX, this.posY, this.posZ, d3, d5, d4, colours, fadeColours, trail, twinkleIn);
         }
      }

      @Override
      public int getFXLayer() {
         return 0;
      }
   }
}
