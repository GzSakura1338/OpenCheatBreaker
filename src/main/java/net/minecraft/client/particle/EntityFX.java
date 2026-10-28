package net.minecraft.client.particle;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityFXDoubleEvent;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFX extends Entity {
    public int particleTextureIndexX;
    public int particleTextureIndexY;
    public float particleAlpha = 1.0F;
    public float particleBlue;
    public float particleGreen;
    public float particleRed;
    public float particleTextureJitterX;
    public float particleGravity;
    public float particleTextureJitterY;
    public float particleScale;
    public int particleMaxAge;
    public int particleAge;
    public IIcon particleIcon;
    public static double interpPosX;
    public static double interpPosY;
    public static double interpPosZ;

    public EntityFX(World world, double d, double d2, double d3) {
        super(world);
        this.setSize(0.2F, 0.2F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(d, d2, d3);
        this.lastTickPosX = d;
        this.lastTickPosY = d2;
        this.lastTickPosZ = d3;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
        this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
        this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.particleMaxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
        this.particleAge = 0;
    }

    public EntityFX(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        this(world, d, d2, d3);
        this.motionX = d4 + (float)(Math.random() * 2.0 - 1.0) * 0.4F;
        this.motionY = d5 + (float)(Math.random() * 2.0 - 1.0) * 0.4F;
        this.motionZ = d6 + (float)(Math.random() * 2.0 - 1.0) * 0.4F;
        float f = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
        float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / f2 * f * 0.4F;
        this.motionY = this.motionY / f2 * f * 0.4F + 0.1F;
        this.motionZ = this.motionZ / f2 * f * 0.4F;
    }

    public EntityFX multiplyVelocity(float f) {
        this.motionX *= f;
        this.motionY = (this.motionY - 0.1F) * f + 0.1F;
        this.motionZ *= f;
        return this;
    }

    public EntityFX multipleParticleScaleBy(float f) {
        this.setSize(0.2F * f, 0.2F * f);
        this.particleScale *= f;
        return this;
    }

    public void setRBGColorF(float f, float f2, float f3) {
        this.particleRed = f;
        this.particleGreen = f2;
        this.particleBlue = f3;
    }

    public void setAlphaF(float f) {
        this.particleAlpha = f;
    }

    public float getRedColorF() {
        return this.particleRed;
    }

    public float getGreenColorF() {
        return this.particleGreen;
    }

    public float getBlueColorF() {
        return this.particleBlue;
    }

    @Override
    public boolean canTriggerWalking() {
        return false;
    }

    @Override
    public void entityInit() {
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.motionY = this.motionY - 0.04 * this.particleGravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98F;
        this.motionY *= 0.98F;
        this.motionZ *= 0.98F;
        if (this.onGround) {
            this.motionX *= 0.7F;
            this.motionZ *= 0.7F;
        }
    }

    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = this.particleTextureIndexX / 16.0F;
        float f8 = f7 + 0.0624375F;
        float f9 = this.particleTextureIndexY / 16.0F;
        float f10 = f9 + 0.0624375F;
        float f11 = 0.1F * this.particleScale;
        if (this.particleIcon != null) {
            f7 = this.particleIcon.getMinU();
            f8 = this.particleIcon.getMaxU();
            f9 = this.particleIcon.getMinV();
            f10 = this.particleIcon.getMaxV();
        }

        float f12 = (float)(this.prevPosX + (this.posX - this.prevPosX) * f - interpPosX);
        float f13 = (float)(this.prevPosY + (this.posY - this.prevPosY) * f - interpPosY);
        float f14 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * f - interpPosZ);
        tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tessellator.addVertexWithUV(f12 - f2 * f11 - f5 * f11, f13 - f3 * f11, f14 - f4 * f11 - f6 * f11, f8, f10);
        tessellator.addVertexWithUV(f12 - f2 * f11 + f5 * f11, f13 + f3 * f11, f14 - f4 * f11 + f6 * f11, f8, f9);
        tessellator.addVertexWithUV(f12 + f2 * f11 + f5 * f11, f13 + f3 * f11, f14 + f4 * f11 + f6 * f11, f7, f9);
        tessellator.addVertexWithUV(f12 + f2 * f11 - f5 * f11, f13 - f3 * f11, f14 + f4 * f11 - f6 * f11, f7, f10);
    }

    public int getFXLayer() {
        return 0;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
    }

    public void setParticleIcon(IIcon icon) {
        if (this.getFXLayer() == 1) {
            this.particleIcon = icon;
        } else {
            if (this.getFXLayer() != 2) {
                throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
            }

            this.particleIcon = icon;
        }
    }

    public void setParticleTextureIndex(int n) {
        if (this.getFXLayer() != 0) {
            throw new RuntimeException("Invalid call to Particle.setMiscTex");
        }

        this.particleTextureIndexX = n % 16;
        this.particleTextureIndexY = n / 16;
    }

    public void nextTextureIndexX() {
        this.particleTextureIndexX++;
    }

    @Override
    public boolean canAttackWithItem() {
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
            + ", Pos ("
            + this.posX
            + ","
            + this.posY
            + ","
            + this.posZ
            + "), RGBA ("
            + this.particleRed
            + ","
            + this.particleGreen
            + ","
            + this.particleBlue
            + ","
            + this.particleAlpha
            + "), Age "
            + this.particleAge;
    }

    @Override
    public void moveEntity(double d, double d2, double d3) {
        EntityFXDoubleEvent entityFXDoubleEvent = new EntityFXDoubleEvent(this, d, d2, d3);
        CheatBreaker.getInstance().getEventBus().handleEvent(entityFXDoubleEvent);
        if (!entityFXDoubleEvent.isCancelled()) {
            d = entityFXDoubleEvent.getDoubleValue1();
            d2 = entityFXDoubleEvent.getDoubleValue2();
            d3 = entityFXDoubleEvent.getDoubleValue3();
            super.moveEntity(d, d2, d3);
        }
    }
}
