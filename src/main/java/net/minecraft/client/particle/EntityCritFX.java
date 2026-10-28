package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntityCritFX extends EntityFX {
    public float initialParticleScale;

    public EntityCritFX(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        this(world, d, d2, d3, d4, d5, d6, 1.0F);
    }

    public EntityCritFX(World world, double d, double d2, double d3, double d4, double d5, double d6, float f) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.motionX *= 0.1F;
        this.motionY *= 0.1F;
        this.motionZ *= 0.1F;
        this.motionX += d4 * 0.4;
        this.motionY += d5 * 0.4;
        this.motionZ += d6 * 0.4;
        this.particleRed = this.particleGreen = this.particleBlue = (float)(Math.random() * 0.3F + 0.6F);
        this.particleScale *= 0.75F;
        this.particleScale *= f;
        this.initialParticleScale = this.particleScale;
        this.particleMaxAge = (int)(6.0 / (Math.random() * 0.8 + 0.6));
        this.particleMaxAge = (int)(this.particleMaxAge * f);
        this.noClip = false;
        this.setParticleTextureIndex(65);
        this.onUpdate();
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

        this.particleScale = this.initialParticleScale * f7;
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

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.particleGreen = (float)(this.particleGreen * 0.96);
        this.particleBlue = (float)(this.particleBlue * 0.9);
        this.motionX *= 0.7F;
        this.motionY *= 0.7F;
        this.motionZ *= 0.7F;
        this.motionY -= 0.02F;
        if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
        }
    }
}
