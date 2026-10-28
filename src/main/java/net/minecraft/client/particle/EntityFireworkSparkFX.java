package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityFireworkSparkFX extends EntityFX {
    public final int baseTextureIndex = 160;
    public boolean flag3;
    public boolean flag2;
    public EffectRenderer effectRenderer;
    public float fadeColourRed;
    public float fadeColourGreen;
    public float fadeColourBlue;
    public boolean hasFadeColour;

    public EntityFireworkSparkFX(World world, double d, double d2, double d3, double d4, double d5, double d6, EffectRenderer effectRenderer) {
        super(world, d, d2, d3);
        this.motionX = d4;
        this.motionY = d5;
        this.motionZ = d6;
        this.effectRenderer = effectRenderer;
        this.particleScale *= 0.75F;
        this.particleMaxAge = 48 + this.rand.nextInt(12);
        this.noClip = false;
    }

    public void setTrail(boolean flag) {
        this.flag3 = flag;
    }

    public void setTwinkle(boolean flag) {
        this.flag2 = flag;
    }

    public void setColour(int n) {
        float f = ((n & 0xFF0000) >> 16) / 255.0F;
        float f2 = ((n & 0xFF00) >> 8) / 255.0F;
        float f3 = ((n & 0xFF) >> 0) / 255.0F;
        float f4 = 1.0F;
        this.setRBGColorF(f * f4, f2 * f4, f3 * f4);
    }

    public void setFadeColour(int n) {
        this.fadeColourRed = ((n & 0xFF0000) >> 16) / 255.0F;
        this.fadeColourGreen = ((n & 0xFF00) >> 8) / 255.0F;
        this.fadeColourBlue = ((n & 0xFF) >> 0) / 255.0F;
        this.hasFadeColour = true;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
        if (!this.flag2 || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0) {
            super.renderParticle(tessellator, f, f2, f3, f4, f5, f6);
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

        this.setParticleTextureIndex(160 + (7 - this.particleAge * 8 / this.particleMaxAge));
        this.motionY -= 0.004;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.91F;
        this.motionY *= 0.91F;
        this.motionZ *= 0.91F;
        if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
        }

        if (this.flag3 && this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0) {
            EntityFireworkSparkFX entityFireworkSparkFX = new EntityFireworkSparkFX(this.worldObj, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0, this.effectRenderer);
            entityFireworkSparkFX.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
            entityFireworkSparkFX.particleAge = entityFireworkSparkFX.particleMaxAge / 2;
            if (this.hasFadeColour) {
                entityFireworkSparkFX.hasFadeColour = true;
                entityFireworkSparkFX.fadeColourRed = this.fadeColourRed;
                entityFireworkSparkFX.fadeColourGreen = this.fadeColourGreen;
                entityFireworkSparkFX.fadeColourBlue = this.fadeColourBlue;
            }

            entityFireworkSparkFX.flag2 = this.flag2;
            this.effectRenderer.addEffect(entityFireworkSparkFX);
        }
    }

    @Override
    public int getBrightnessForRender(float f) {
        return 15728880;
    }

    @Override
    public float getBrightness(float f) {
        return 1.0F;
    }
}
