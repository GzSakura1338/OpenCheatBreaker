package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityCrit2FX extends EntityFX {
    public Entity theEntity;
    public int currentLife;
    public int maximumLife;
    public String particleName;
    public float floatValue;

    public EntityCrit2FX(World world, Entity entity) {
        this(world, entity, "crit");
    }

    public EntityCrit2FX(World world, Entity entity, String str) {
        super(world, entity.posX, entity.boundingBox.minY + entity.height / 2.0F, entity.posZ, entity.motionX, entity.motionY, entity.motionZ);
        this.theEntity = entity;
        this.maximumLife = 3;
        this.particleName = str;
        this.floatValue = 1.0F;
        this.onUpdate();
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
    }

    @Override
    public void onUpdate() {
        for (int i = 0; i < 16.0F * this.floatValue; i++) {
            double d = this.rand.nextFloat() * 2.0F - 1.0F;
            double d2 = this.rand.nextFloat() * 2.0F - 1.0F;
            double d3 = this.rand.nextFloat() * 2.0F - 1.0F;
            if (d * d + d2 * d2 + d3 * d3 <= 1.0) {
                double d4 = this.theEntity.posX + d * this.theEntity.width / 4.0;
                double d5 = this.theEntity.boundingBox.minY + this.theEntity.height / 2.0F + d2 * this.theEntity.height / 4.0;
                double d6 = this.theEntity.posZ + d3 * this.theEntity.width / 4.0;
                this.worldObj.spawnParticle(this.particleName, d4, d5, d6, d, d2 + 0.2, d3);
            }
        }

        this.currentLife++;
        if (this.currentLife >= this.maximumLife) {
            this.setDead();
        }
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    public EntityCrit2FX(World world, Entity entity, String str, float f) {
        super(world, entity.posX, entity.boundingBox.minY + entity.height / 2.0F, entity.posZ, entity.motionX, entity.motionY, entity.motionZ);
        this.theEntity = entity;
        this.maximumLife = 3;
        this.particleName = str;
        this.floatValue = f;
        this.onUpdate();
    }
}
