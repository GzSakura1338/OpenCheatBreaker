package net.minecraft.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityWitherSkull extends EntityFireball {
    public EntityWitherSkull(World world) {
        super(world);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityWitherSkull(World world, EntityLivingBase entityLivingBase, double d, double d2, double d3) {
        super(world, entityLivingBase, d, d2, d3);
        this.setSize(0.3125F, 0.3125F);
    }

    @Override
    public float getMotionFactor() {
        return this.isInvulnerable() ? 0.73F : super.getMotionFactor();
    }

    public EntityWitherSkull(World world, double d, double d2, double d3, double d4, double d5, double d6) {
        super(world, d, d2, d3, d4, d5, d6);
        this.setSize(0.3125F, 0.3125F);
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public float getExplosionResistance(Explosion explosionIn, World worldIn, int x, int y, int z, Block blockIn) {
        float explosionResistance = super.getExplosionResistance(explosionIn, worldIn, x, y, z, blockIn);
        if (this.isInvulnerable()
            && blockIn != Blocks.bedrock
            && blockIn != Blocks.end_portal
            && blockIn != Blocks.end_portal_frame
            && blockIn != Blocks.command_block) {
            explosionResistance = Math.min(0.8F, explosionResistance);
        }

        return explosionResistance;
    }

    @Override
    public void onImpact(MovingObjectPosition movingObjectPosition) {
        if (!this.worldObj.isRemote) {
            if (movingObjectPosition.entityHit != null) {
                if (this.shootingEntity != null) {
                    if (movingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 8.0F) && !movingObjectPosition.entityHit.isEntityAlive()) {
                        this.shootingEntity.heal(5.0F);
                    }
                } else {
                    movingObjectPosition.entityHit.attackEntityFrom(DamageSource.magic, 5.0F);
                }

                if (movingObjectPosition.entityHit instanceof EntityLivingBase) {
                    byte b = 0;
                    if (this.worldObj.difficultySetting == EnumDifficulty.NORMAL) {
                        b = 10;
                    } else if (this.worldObj.difficultySetting == EnumDifficulty.HARD) {
                        b = 40;
                    }

                    if (b > 0) {
                        ((EntityLivingBase)movingObjectPosition.entityHit).addPotionEffect(new PotionEffect(Potion.wither.id, 20 * b, 1));
                    }
                }
            }

            this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, 1.0F, false, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
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

    @Override
    public void entityInit() {
        this.dataWatcher.addObject(10, (byte)0);
    }

    public boolean isInvulnerable() {
        try {
            return this.dataWatcher.getWatchableObjectByte(10) == 1;
        } catch (Exception e) {
            return true;
        }
    }

    public void setInvulnerable(boolean flag) {
        this.dataWatcher.updateObject(10, (byte)(flag ? 1 : 0));
    }
}
