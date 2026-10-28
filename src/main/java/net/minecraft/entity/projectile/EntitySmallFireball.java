package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySmallFireball extends EntityFireball {
    public EntitySmallFireball(World world) {
        super(world);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntitySmallFireball(World world, EntityLivingBase entityLivingBase, double d, double d2, double d3) {
        super(world, entityLivingBase, d, d2, d3);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntitySmallFireball(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.setSize(0.3125F, 0.3125F);
    }

    @Override
    public void onImpact(MovingObjectPosition movingObjectPosition) {
        if (!this.worldObj.isRemote) {
            if (movingObjectPosition.entityHit != null) {
                if (!movingObjectPosition.entityHit.isImmuneToFire() && movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F)) {
                    movingObjectPosition.entityHit.setFire(5);
                }
            } else {
                int blockX = movingObjectPosition.blockX;
                int blockY = movingObjectPosition.blockY;
                int blockZ = movingObjectPosition.blockZ;
                switch (movingObjectPosition.sideHit) {
                    case 0:
                        blockY--;
                        break;
                    case 1:
                        blockY++;
                        break;
                    case 2:
                        blockZ--;
                        break;
                    case 3:
                        blockZ++;
                        break;
                    case 4:
                        blockX--;
                        break;
                    case 5:
                        blockX++;
                }

                if (this.worldObj.isAirBlock(blockX, blockY, blockZ)) {
                    this.worldObj.setBlock(blockX, blockY, blockZ, Blocks.fire);
                }
            }

            this.setDead();
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }
}
