package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIFleeSun extends EntityAIBase {
    public EntityCreature theCreature;
    public double shelterX;
    public double shelterY;
    public double shelterZ;
    public double movementSpeed;
    public World theWorld;

    public EntityAIFleeSun(EntityCreature entityCreature, double d) {
        this.theCreature = entityCreature;
        this.movementSpeed = d;
        this.theWorld = entityCreature.worldObj;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.theWorld.isDaytime()) {
            return false;
        }

        if (!this.theCreature.isBurning()) {
            return false;
        }

        if (!this.theWorld
            .canBlockSeeTheSky(
                MathHelper.floor_double(this.theCreature.posX), (int)this.theCreature.boundingBox.minY, MathHelper.floor_double(this.theCreature.posZ)
            )) {
            return false;
        }

        Vec3 findPossibleShelter = this.findPossibleShelter();
        if (findPossibleShelter == null) {
            return false;
        }

        this.shelterX = findPossibleShelter.xCoord;
        this.shelterY = findPossibleShelter.yCoord;
        this.shelterZ = findPossibleShelter.zCoord;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return !this.theCreature.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.theCreature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    public Vec3 findPossibleShelter() {
        Random rNG = this.theCreature.getRNG();

        for (int i = 0; i < 10; i++) {
            int n = MathHelper.floor_double(this.theCreature.posX + rNG.nextInt(20) - 10.0);
            int n2 = MathHelper.floor_double(this.theCreature.boundingBox.minY + rNG.nextInt(6) - 3.0);
            int n3 = MathHelper.floor_double(this.theCreature.posZ + rNG.nextInt(20) - 10.0);
            if (!this.theWorld.canBlockSeeTheSky(n, n2, n3) && this.theCreature.getBlockPathWeight(n, n2, n3) < 0.0F) {
                return Vec3.createVectorHelper(n, n2, n3);
            }
        }

        return null;
    }
}
