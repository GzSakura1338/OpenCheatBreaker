package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntityReddustFX extends EntityFX {
    public float reddustParticleScale;

    public EntityReddustFX(World world, double d, double d2, double d3, float f, float f2, float f3) {
        this(world, d, d2, d3, 1.0F, f, f2, f3);
    }

    public EntityReddustFX(World world, double d, double d2, double d3, float f, float f2, float f3, float f4) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.motionX *= 0.1F;
        this.motionY *= 0.1F;
        this.motionZ *= 0.1F;
        if (f2 == 0.0F) {
            f2 = 1.0F;
        }

        float f5 = (float)Math.random() * 0.4F + 0.6F;
        this.particleRed = ((float)(Math.random() * 0.2F) + 0.8F) * f2 * f5;
        this.particleGreen = ((float)(Math.random() * 0.2F) + 0.8F) * f3 * f5;
        this.particleBlue = ((float)(Math.random() * 0.2F) + 0.8F) * f4 * f5;
        this.particleScale *= 0.75F;
        this.particleScale *= f;
        this.reddustParticleScale = this.particleScale;
        this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.particleMaxAge = (int)(this.particleMaxAge * f);
        this.noClip = false;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = (this.particleAge + f) / this.particleMaxAge * 32.0F;
        if (f7 < 0.0F) {
            f7 = 0.0F;
        }

        if (f7 > 1.0F) {
            f7 = 1.0F;
        }

        this.particleScale = this.reddustParticleScale * f7;
        super.renderParticle(tessellator, f, f2, f3, f4, f5, f6);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (this.posY == this.prevPosY) {
            this.motionX *= 1.1;
            this.motionZ *= 1.1;
        }

        this.motionX *= 0.96F;
        this.motionY *= 0.96F;
        this.motionZ *= 0.96F;
        if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
        }
    }
}
