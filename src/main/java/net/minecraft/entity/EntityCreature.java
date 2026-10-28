package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityCreature extends EntityLiving {
    public static UUID FLEEING_SPEED_BONUS_ID = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
    public static AttributeModifier FLEEING_SPEED_MODIFIER = new AttributeModifier(FLEEING_SPEED_BONUS_ID, "Fleeing speed bonus", 2.0, 2).setSaved(false);
    public PathEntity pathToEntity;
    public Entity entityToAttack;
    public boolean hasAttacked;
    public int fleeingTick;
    public ChunkCoordinates homePosition = new ChunkCoordinates(0, 0, 0);
    public float maximumHomeDistance = -1.0F;
    public EntityAIBase aiBase = new EntityAIMoveTowardsRestriction(this, 1.0);
    public boolean flag2;

    public EntityCreature(World world) {
        super(world);
    }

    public boolean isMovementCeased() {
        return false;
    }

    @Override
    public void updateEntityActionState() {
        this.worldObj.theProfiler.startSection("ai");
        if (this.fleeingTick > 0 && --this.fleeingTick == 0) {
            IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            entityAttribute.removeModifier(FLEEING_SPEED_MODIFIER);
        }

        this.hasAttacked = this.isMovementCeased();
        float f = 16.0F;
        if (this.entityToAttack == null) {
            this.entityToAttack = this.findPlayerToAttack();
            if (this.entityToAttack != null) {
                this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, f, true, false, false, true);
            }
        } else if (this.entityToAttack.isEntityAlive()) {
            float distanceToEntity = this.entityToAttack.getDistanceToEntity(this);
            if (this.canEntityBeSeen(this.entityToAttack)) {
                this.attackEntity(this.entityToAttack, distanceToEntity);
            }
        } else {
            this.entityToAttack = null;
        }

        if (this.entityToAttack instanceof EntityPlayerMP && ((EntityPlayerMP)this.entityToAttack).theItemInWorldManager.isCreative()) {
            this.entityToAttack = null;
        }

        this.worldObj.theProfiler.endSection();
        if (this.hasAttacked || this.entityToAttack == null || this.pathToEntity != null && this.rand.nextInt(20) != 0) {
            if (!this.hasAttacked
                && (this.pathToEntity == null && this.rand.nextInt(180) == 0 || this.rand.nextInt(120) == 0 || this.fleeingTick > 0)
                && this.entityAge < 100) {
                this.updateWanderPath();
            }
        } else {
            this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, f, true, false, false, true);
        }

        int n = MathHelper.floor_double(this.boundingBox.minY + 0.5);
        boolean isInWater = this.isInWater();
        boolean handleLavaMovement = this.handleLavaMovement();
        this.rotationPitch = 0.0F;
        if (this.pathToEntity != null && this.rand.nextInt(100) != 0) {
            this.worldObj.theProfiler.startSection("followpath");
            Vec3 position = this.pathToEntity.getPosition(this);
            double d = this.width * 2.0F;

            while (position != null && position.squareDistanceTo(this.posX, position.yCoord, this.posZ) < d * d) {
                this.pathToEntity.incrementPathIndex();
                if (this.pathToEntity.isFinished()) {
                    position = null;
                    this.pathToEntity = null;
                } else {
                    position = this.pathToEntity.getPosition(this);
                }
            }

            this.isJumping = false;
            if (position != null) {
                double d2 = position.xCoord - this.posX;
                double d3 = position.zCoord - this.posZ;
                double d4 = position.yCoord - n;
                float f2 = (float)(Math.atan2(d3, d2) * 180.0 / Math.PI) - 90.0F;
                float f3 = MathHelper.wrapAngleTo180_float(f2 - this.rotationYaw);
                this.moveForward = (float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
                if (f3 > 30.0F) {
                    f3 = 30.0F;
                }

                if (f3 < -30.0F) {
                    f3 = -30.0F;
                }

                this.rotationYaw += f3;
                if (this.hasAttacked && this.entityToAttack != null) {
                    double d5 = this.entityToAttack.posX - this.posX;
                    double d6 = this.entityToAttack.posZ - this.posZ;
                    float rotationYaw = this.rotationYaw;
                    this.rotationYaw = (float)(Math.atan2(d6, d5) * 180.0 / Math.PI) - 90.0F;
                    f3 = (rotationYaw - this.rotationYaw + 90.0F) * (float) Math.PI / 180.0F;
                    this.moveStrafing = -MathHelper.sin(f3) * this.moveForward * 1.0F;
                    this.moveForward = MathHelper.cos(f3) * this.moveForward * 1.0F;
                }

                if (d4 > 0.0) {
                    this.isJumping = true;
                }
            }

            if (this.entityToAttack != null) {
                this.faceEntity(this.entityToAttack, 30.0F, 30.0F);
            }

            if (this.isCollidedHorizontally && !this.hasPath()) {
                this.isJumping = true;
            }

            if (this.rand.nextFloat() < 0.8F && (isInWater || handleLavaMovement)) {
                this.isJumping = true;
            }

            this.worldObj.theProfiler.endSection();
        } else {
            super.updateEntityActionState();
            this.pathToEntity = null;
        }
    }

    public void updateWanderPath() {
        this.worldObj.theProfiler.startSection("stroll");
        boolean flag = false;
        int n = -1;
        int n2 = -1;
        int n3 = -1;
        float f = -99999.0F;

        for (int i = 0; i < 10; i++) {
            int n4 = MathHelper.floor_double(this.posX + this.rand.nextInt(13) - 6.0);
            int n5 = MathHelper.floor_double(this.posY + this.rand.nextInt(7) - 3.0);
            int n6 = MathHelper.floor_double(this.posZ + this.rand.nextInt(13) - 6.0);
            float blockPathWeight = this.getBlockPathWeight(n4, n5, n6);
            if (blockPathWeight > f) {
                f = blockPathWeight;
                n = n4;
                n2 = n5;
                n3 = n6;
                flag = true;
            }
        }

        if (flag) {
            this.pathToEntity = this.worldObj.getEntityPathToXYZ(this, n, n2, n3, 10.0F, true, false, false, true);
        }

        this.worldObj.theProfiler.endSection();
    }

    public void attackEntity(Entity entity, float f) {
    }

    public float getBlockPathWeight(int n, int n2, int n3) {
        return 0.0F;
    }

    public Entity findPlayerToAttack() {
        return null;
    }

    @Override
    public boolean getCanSpawnHere() {
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.boundingBox.minY);
        int n3 = MathHelper.floor_double(this.posZ);
        return super.getCanSpawnHere() && this.getBlockPathWeight(n, n2, n3) >= 0.0F;
    }

    public boolean hasPath() {
        return this.pathToEntity != null;
    }

    public void setPathToEntity(PathEntity pathEntity) {
        this.pathToEntity = pathEntity;
    }

    public Entity getEntityToAttack() {
        return this.entityToAttack;
    }

    public void setTarget(Entity entity) {
        this.entityToAttack = entity;
    }

    public boolean isWithinHomeDistanceCurrentPosition() {
        return this.isWithinHomeDistance(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
    }

    public boolean isWithinHomeDistance(int n, int n2, int n3) {
        return this.maximumHomeDistance == -1.0F || this.homePosition.getDistanceSquared(n, n2, n3) < this.maximumHomeDistance * this.maximumHomeDistance;
    }

    public void setHomeArea(int n, int n2, int n3, int n4) {
        this.homePosition.set(n, n2, n3);
        this.maximumHomeDistance = n4;
    }

    public ChunkCoordinates getHomePosition() {
        return this.homePosition;
    }

    public float getMaximumHomeDistance() {
        return this.maximumHomeDistance;
    }

    public void detachHome() {
        this.maximumHomeDistance = -1.0F;
    }

    public boolean hasHome() {
        return this.maximumHomeDistance != -1.0F;
    }

    @Override
    public void updateLeashedState() {
        super.updateLeashedState();
        if (this.getLeashed() && this.getLeashedToEntity() != null && this.getLeashedToEntity().worldObj == this.worldObj) {
            Entity leashedToEntity = this.getLeashedToEntity();
            this.setHomeArea((int)leashedToEntity.posX, (int)leashedToEntity.posY, (int)leashedToEntity.posZ, 5);
            float distanceToEntity = this.getDistanceToEntity(leashedToEntity);
            if (this instanceof EntityTameable && ((EntityTameable)this).isSitting()) {
                if (distanceToEntity > 10.0F) {
                    this.clearLeashed(true, true);
                }

                return;
            }

            if (!this.flag2) {
                this.tasks.addTask(2, this.aiBase);
                this.getNavigator().setAvoidsWater(false);
                this.flag2 = true;
            }

            this.process(distanceToEntity);
            if (distanceToEntity > 4.0F) {
                this.getNavigator().tryMoveToEntityLiving(leashedToEntity, 1.0);
            }

            if (distanceToEntity > 6.0F) {
                double d = (leashedToEntity.posX - this.posX) / distanceToEntity;
                double d2 = (leashedToEntity.posY - this.posY) / distanceToEntity;
                double d3 = (leashedToEntity.posZ - this.posZ) / distanceToEntity;
                this.motionX = this.motionX + d * Math.abs(d) * 0.4;
                this.motionY = this.motionY + d2 * Math.abs(d2) * 0.4;
                this.motionZ = this.motionZ + d3 * Math.abs(d3) * 0.4;
            }

            if (distanceToEntity > 10.0F) {
                this.clearLeashed(true, true);
            }
        } else if (!this.getLeashed() && this.flag2) {
            this.flag2 = false;
            this.tasks.removeTask(this.aiBase);
            this.getNavigator().setAvoidsWater(true);
            this.detachHome();
        }
    }

    public void process(float f) {
    }
}
