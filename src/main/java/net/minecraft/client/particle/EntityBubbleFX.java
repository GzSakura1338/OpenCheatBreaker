package net.minecraft.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBubbleFX extends EntityFX {
    public EntityBubbleFX(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.setParticleTextureIndex(32);
        this.setSize(0.02F, 0.02F);
        this.particleScale = this.particleScale * (this.rand.nextFloat() * 0.6F + 0.2F);
        this.motionX = d4 * 0.2F + (float)(Math.random() * 2.0 - 1.0) * 0.02F;
        this.motionY = d5 * 0.2F + (float)(Math.random() * 2.0 - 1.0) * 0.02F;
        this.motionZ = d6 * 0.2F + (float)(Math.random() * 2.0 - 1.0) * 0.02F;
        this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY += 0.002;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.85F;
        this.motionY *= 0.85F;
        this.motionZ *= 0.85F;
        if (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial()
            != Material.water) {
            this.setDead();
        }

        if (this.particleMaxAge-- <= 0) {
            this.setDead();
        }
    }
}
