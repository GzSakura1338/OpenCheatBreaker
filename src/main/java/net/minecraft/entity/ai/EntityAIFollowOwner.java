package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowOwner extends EntityAIBase {
    public EntityTameable thePet;
    public EntityLivingBase theOwner;
    public World theWorld;
    public double speed;
    public PathNavigate petPathfinder;
    public int updateCountdownTicks;
    public float minDist;
    public float maxDist;
    public boolean flag;

    public EntityAIFollowOwner(EntityTameable entityTameable, double d, float f, float f2) {
        this.thePet = entityTameable;
        this.theWorld = entityTameable.worldObj;
        this.speed = d;
        this.petPathfinder = entityTameable.getNavigator();
        this.minDist = f;
        this.maxDist = f2;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase owner = this.thePet.getOwner();
        if (owner == null) {
            return false;
        }

        if (this.thePet.isSitting()) {
            return false;
        }

        if (this.thePet.getDistanceSqToEntity(owner) < this.minDist * this.minDist) {
            return false;
        }

        this.theOwner = owner;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > this.maxDist * this.maxDist && !this.thePet.isSitting();
    }

    @Override
    public void startExecuting() {
        this.updateCountdownTicks = 0;
        this.flag = this.thePet.getNavigator().getAvoidsWater();
        this.thePet.getNavigator().setAvoidsWater(false);
    }

    @Override
    public void resetTask() {
        this.theOwner = null;
        this.petPathfinder.clearPathEntity();
        this.thePet.getNavigator().setAvoidsWater(this.flag);
    }

    @Override
    public void updateTask() {
        this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, this.thePet.getVerticalFaceSpeed());
        if (!this.thePet.isSitting() && --this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.speed)
                && !this.thePet.getLeashed()
                && this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0) {
                int n = MathHelper.floor_double(this.theOwner.posX) - 2;
                int n2 = MathHelper.floor_double(this.theOwner.posZ) - 2;
                int n3 = MathHelper.floor_double(this.theOwner.boundingBox.minY);

                for (int i = 0; i <= 4; i++) {
                    for (int j = 0; j <= 4; j++) {
                        if ((i < 1 || j < 1 || i > 3 || j > 3)
                            && World.doesBlockHaveSolidTopSurface(this.theWorld, n + i, n3 - 1, n2 + j)
                            && !this.theWorld.getBlock(n + i, n3, n2 + j).isNormalCube()
                            && !this.theWorld.getBlock(n + i, n3 + 1, n2 + j).isNormalCube()) {
                            this.thePet.setLocationAndAngles(n + i + 0.5F, n3, n2 + j + 0.5F, this.thePet.rotationYaw, this.thePet.rotationPitch);
                            this.petPathfinder.clearPathEntity();
                            return;
                        }
                    }
                }
            }
        }
    }
}
