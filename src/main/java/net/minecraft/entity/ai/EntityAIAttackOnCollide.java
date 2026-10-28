package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIAttackOnCollide extends EntityAIBase {
    public EntityCreature attacker;
    public World worldObj;
    public int attackTick;
    public double speedTowardsTarget;
    public boolean longMemory;
    public PathEntity entityPathEntity;
    public Class classTarget;
    public int updateCountdownTicks;
    public double targetX;
    public double targetY;
    public double targetZ;

    public EntityAIAttackOnCollide(EntityCreature entityCreature, Class clazz, double d, boolean flag) {
        this(entityCreature, d, flag);
        this.classTarget = clazz;
    }

    public EntityAIAttackOnCollide(EntityCreature entityCreature, double d, boolean flag) {
        this.attacker = entityCreature;
        this.worldObj = entityCreature.worldObj;
        this.speedTowardsTarget = d;
        this.longMemory = flag;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase attackTarget = this.attacker.getAttackTarget();
        if (attackTarget == null) {
            return false;
        }

        if (!attackTarget.isEntityAlive()) {
            return false;
        }

        if (this.classTarget != null && !this.classTarget.isAssignableFrom(attackTarget.getClass())) {
            return false;
        }

        this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(attackTarget);
        return this.entityPathEntity != null;
    }

    @Override
    public boolean continueExecuting() {
        EntityLivingBase attackTarget = this.attacker.getAttackTarget();
        return attackTarget != null
            && attackTarget.isEntityAlive()
            && (
                !this.longMemory
                    ? !this.attacker.getNavigator().noPath()
                    : this.attacker.isWithinHomeDistance(MathHelper.floor_double(attackTarget.posX), MathHelper.floor_double(attackTarget.posY), MathHelper.floor_double(attackTarget.posZ))
            );
    }

    @Override
    public void startExecuting() {
        this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
        this.updateCountdownTicks = 0;
    }

    @Override
    public void resetTask() {
        this.attacker.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        EntityLivingBase attackTarget = this.attacker.getAttackTarget();
        this.attacker.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);
        double distanceSq = this.attacker.getDistanceSq(attackTarget.posX, attackTarget.boundingBox.minY, attackTarget.posZ);
        double d = this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width;
        this.updateCountdownTicks--;
        if ((this.longMemory || this.attacker.getEntitySenses().canSee(attackTarget))
            && this.updateCountdownTicks <= 0
            && (
                this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
                    || attackTarget.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0
                    || this.attacker.getRNG().nextFloat() < 0.05F
            )) {
            this.targetX = attackTarget.posX;
            this.targetY = attackTarget.boundingBox.minY;
            this.targetZ = attackTarget.posZ;
            this.updateCountdownTicks = 4 + this.attacker.getRNG().nextInt(7);
            if (distanceSq > 1024.0) {
                this.updateCountdownTicks += 10;
            } else if (distanceSq > 256.0) {
                this.updateCountdownTicks += 5;
            }

            if (!this.attacker.getNavigator().tryMoveToEntityLiving(attackTarget, this.speedTowardsTarget)) {
                this.updateCountdownTicks += 15;
            }
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        if (distanceSq <= d && this.attackTick <= 20) {
            this.attackTick = 20;
            if (this.attacker.getHeldItem() != null) {
                this.attacker.swingItem();
            }

            this.attacker.attackEntityAsMob(attackTarget);
        }
    }
}
