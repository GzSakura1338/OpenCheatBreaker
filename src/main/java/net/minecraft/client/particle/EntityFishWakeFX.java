package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityFishWakeFX extends EntityFX {
    public EntityFishWakeFX(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.motionX *= 0.3F;
        this.motionY = (float)Math.random() * 0.2F + 0.1F;
        this.motionZ *= 0.3F;
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.setParticleTextureIndex(19);
        this.setSize(0.01F, 0.01F);
        this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.particleGravity = 0.0F;
        this.motionX = d4;
        this.motionY = d5;
        this.motionZ = d6;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY = this.motionY - this.particleGravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98F;
        this.motionY *= 0.98F;
        this.motionZ *= 0.98F;
        int n = 60 - this.particleMaxAge;
        float f = n * 0.001F;
        this.setSize(f, f);
        this.setParticleTextureIndex(19 + n % 4);
        if (this.particleMaxAge-- <= 0) {
            this.setDead();
        }
    }
}
