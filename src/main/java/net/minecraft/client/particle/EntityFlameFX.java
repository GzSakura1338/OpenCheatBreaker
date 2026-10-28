package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntityFlameFX extends EntityFX {
    public float flameScale;

    public EntityFlameFX(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.motionX = this.motionX * 0.01F + d4;
        this.motionY = this.motionY * 0.01F + d5;
        this.motionZ = this.motionZ * 0.01F + d6;
        double d7 = d + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
        d7 = d2 + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
        d7 = d3 + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
        this.flameScale = this.particleScale;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
        this.noClip = true;
        this.setParticleTextureIndex(48);
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = (this.particleAge + f) / this.particleMaxAge;
        this.particleScale = this.flameScale * (1.0F - f7 * f7 * 0.5F);
        super.renderParticle(tessellator, f, f2, f3, f4, f5, f6);
    }

    @Override
    public int getBrightnessForRender(float f) {
        float f2 = (this.particleAge + f) / this.particleMaxAge;
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        int brightnessForRender = super.getBrightnessForRender(f);
        int n = brightnessForRender & 0xFF;
        int n2 = brightnessForRender >> 16 & 0xFF;
        n += (int)(f2 * 15.0F * 16.0F);
        if (n > 240) {
            n = 240;
        }

        return n | n2 << 16;
    }

    @Override
    public float getBrightness(float f) {
        float f2 = (this.particleAge + f) / this.particleMaxAge;
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        float brightness = super.getBrightness(f);
        return brightness * f2 + (1.0F - f2);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.96F;
        this.motionY *= 0.96F;
        this.motionZ *= 0.96F;
        if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
        }
    }
}
