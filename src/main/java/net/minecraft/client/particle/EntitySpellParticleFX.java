package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntitySpellParticleFX extends EntityFX {
    public int baseSpellTextureIndex = 128;

    public EntitySpellParticleFX(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.motionY *= 0.2F;
        if (d4 == 0.0 && d6 == 0.0) {
            this.motionX *= 0.1F;
            this.motionZ *= 0.1F;
        }

        this.particleScale *= 0.75F;
        this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
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

        this.setParticleTextureIndex(this.baseSpellTextureIndex + (7 - this.particleAge * 8 / this.particleMaxAge));
        this.motionY += 0.004;
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

    public void setBaseSpellTextureIndex(int n) {
        this.baseSpellTextureIndex = n;
    }
}
