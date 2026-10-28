package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PathNavigate {
    public Vec3 lastPosCheck = Vec3.createVectorHelper(0.0, 0.0, 0.0);
    public boolean canPassOpenWoodenDoors = true;
    public PathEntity currentPath;
    public double speed;
    public EntityLiving theEntity;
    public boolean avoidSun;
    public int totalTicks;
    public int ticksAtLastPos;
    public World worldObj;
    public IAttributeInstance pathSearchRange;
    public boolean canPassClosedWoodenDoors;
    public boolean avoidsWater;
    public boolean canSwim;

    public PathNavigate(EntityLiving entityLiving, World world) {
        this.theEntity = entityLiving;
        this.worldObj = world;
        this.pathSearchRange = entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
    }

    public void setAvoidsWater(boolean flag) {
        this.avoidsWater = flag;
    }

    public boolean getAvoidsWater() {
        return this.avoidsWater;
    }

    public void setBreakDoors(boolean flag) {
        this.canPassClosedWoodenDoors = flag;
    }

    public void setEnterDoors(boolean flag) {
        this.canPassOpenWoodenDoors = flag;
    }

    public boolean getCanBreakDoors() {
        return this.canPassClosedWoodenDoors;
    }

    public void setAvoidSun(boolean flag) {
        this.avoidSun = flag;
    }

    public void setSpeed(double d) {
        this.speed = d;
    }

    public void setCanSwim(boolean flag) {
        this.canSwim = flag;
    }

    public float getPathSearchRange() {
        return (float)this.pathSearchRange.getAttributeValue();
    }

    public PathEntity getPathToXYZ(double d, double d2, double d3) {
        return !this.canNavigate()
            ? null
            : this.worldObj
                .getEntityPathToXYZ(
                    this.theEntity,
                    MathHelper.floor_double(d),
                    (int)d2,
                    MathHelper.floor_double(d3),
                    this.getPathSearchRange(),
                    this.canPassOpenWoodenDoors,
                    this.canPassClosedWoodenDoors,
                    this.avoidsWater,
                    this.canSwim
                );
    }

    public boolean tryMoveToXYZ(double d, double d2, double d3, double d4) {
        PathEntity pathToXYZ = this.getPathToXYZ(MathHelper.floor_double(d), (int)d2, MathHelper.floor_double(d3));
        return this.setPath(pathToXYZ, d4);
    }

    public PathEntity getPathToEntityLiving(Entity entity) {
        return !this.canNavigate()
            ? null
            : this.worldObj
                .getPathEntityToEntity(
                    this.theEntity, entity, this.getPathSearchRange(), this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim
                );
    }

    public boolean tryMoveToEntityLiving(Entity entity, double d) {
        PathEntity pathToEntityLiving = this.getPathToEntityLiving(entity);
        return pathToEntityLiving != null && this.setPath(pathToEntityLiving, d);
    }

    public boolean setPath(PathEntity pathEntity, double d) {
        if (pathEntity == null) {
            this.currentPath = null;
            return false;
        }

        if (!pathEntity.isSamePath(this.currentPath)) {
            this.currentPath = pathEntity;
        }

        if (this.avoidSun) {
            this.removeSunnyPath();
        }

        if (this.currentPath.getCurrentPathLength() == 0) {
            return false;
        }

        this.speed = d;
        Vec3 entityPosition = this.getEntityPosition();
        this.ticksAtLastPos = this.totalTicks;
        this.lastPosCheck.xCoord = entityPosition.xCoord;
        this.lastPosCheck.yCoord = entityPosition.yCoord;
        this.lastPosCheck.zCoord = entityPosition.zCoord;
        return true;
    }

    public PathEntity getPath() {
        return this.currentPath;
    }

    public void onUpdateNavigation() {
        this.totalTicks++;
        if (!this.noPath()) {
            if (this.canNavigate()) {
                this.pathFollow();
            }

            if (!this.noPath()) {
                Vec3 position = this.currentPath.getPosition(this.theEntity);
                if (position != null) {
                    this.theEntity.getMoveHelper().setMoveTo(position.xCoord, position.yCoord, position.zCoord, this.speed);
                }
            }
        }
    }

    public void pathFollow() {
        Vec3 entityPosition = this.getEntityPosition();
        int currentPathLength = this.currentPath.getCurrentPathLength();

        for (int currentPathIndex = this.currentPath.getCurrentPathIndex(); currentPathIndex < this.currentPath.getCurrentPathLength(); currentPathIndex++) {
            if (this.currentPath.getPathPointFromIndex(currentPathIndex).yCoord != (int)entityPosition.yCoord) {
                currentPathLength = currentPathIndex;
                break;
            }
        }

        float f = this.theEntity.width * this.theEntity.width;

        for (int currentPathIndex = this.currentPath.getCurrentPathIndex(); currentPathIndex < currentPathLength; currentPathIndex++) {
            if (entityPosition.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, currentPathIndex)) < f) {
                this.currentPath.setCurrentPathIndex(currentPathIndex + 1);
            }
        }

        int n = MathHelper.ceiling_float_int(this.theEntity.width);
        int n2 = (int)this.theEntity.height + 1;
        int n3 = n;

        for (int i = currentPathLength - 1; i >= this.currentPath.getCurrentPathIndex(); i--) {
            if (this.isDirectPathBetweenPoints(entityPosition, this.currentPath.getVectorFromIndex(this.theEntity, i), n, n2, n3)) {
                this.currentPath.setCurrentPathIndex(i);
                break;
            }
        }

        if (this.totalTicks - this.ticksAtLastPos > 100) {
            if (entityPosition.squareDistanceTo(this.lastPosCheck) < 2.25) {
                this.clearPathEntity();
            }

            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck.xCoord = entityPosition.xCoord;
            this.lastPosCheck.yCoord = entityPosition.yCoord;
            this.lastPosCheck.zCoord = entityPosition.zCoord;
        }
    }

    public boolean noPath() {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    public void clearPathEntity() {
        this.currentPath = null;
    }

    public Vec3 getEntityPosition() {
        return Vec3.createVectorHelper(this.theEntity.posX, this.getPathableYPos(), this.theEntity.posZ);
    }

    public int getPathableYPos() {
        if (this.theEntity.isInWater() && this.canSwim) {
            int minY = (int)this.theEntity.boundingBox.minY;
            Block block = this.worldObj.getBlock(MathHelper.floor_double(this.theEntity.posX), minY, MathHelper.floor_double(this.theEntity.posZ));
            int n = 0;

            while (block == Blocks.flowing_water || block == Blocks.water) {
                block = this.worldObj.getBlock(MathHelper.floor_double(this.theEntity.posX), ++minY, MathHelper.floor_double(this.theEntity.posZ));
                if (++n > 16) {
                    return (int)this.theEntity.boundingBox.minY;
                }
            }

            return minY;
        } else {
            return (int)(this.theEntity.boundingBox.minY + 0.5);
        }
    }

    public boolean canNavigate() {
        return this.theEntity.onGround
            || this.canSwim && this.isInLiquid()
            || this.theEntity.isRiding() && this.theEntity instanceof EntityZombie && this.theEntity.ridingEntity instanceof EntityChicken;
    }

    public boolean isInLiquid() {
        return this.theEntity.isInWater() || this.theEntity.handleLavaMovement();
    }

    public void removeSunnyPath() {
        if (!this.worldObj
            .canBlockSeeTheSky(
                MathHelper.floor_double(this.theEntity.posX), (int)(this.theEntity.boundingBox.minY + 0.5), MathHelper.floor_double(this.theEntity.posZ)
            )) {
            for (int i = 0; i < this.currentPath.getCurrentPathLength(); i++) {
                PathPoint pathPointFromIndex = this.currentPath.getPathPointFromIndex(i);
                if (this.worldObj.canBlockSeeTheSky(pathPointFromIndex.xCoord, pathPointFromIndex.yCoord, pathPointFromIndex.zCoord)) {
                    this.currentPath.setCurrentPathLength(i - 1);
                    return;
                }
            }
        }
    }

    public boolean isDirectPathBetweenPoints(Vec3 vec3, Vec3 vec32, int n, int n2, int n3) {
        int n4 = MathHelper.floor_double(vec3.xCoord);
        int n5 = MathHelper.floor_double(vec3.zCoord);
        double d = vec32.xCoord - vec3.xCoord;
        double d2 = vec32.zCoord - vec3.zCoord;
        double d3 = d * d + d2 * d2;
        if (d3 < 1.0E-8) {
            return false;
        }

        double d4 = 1.0 / Math.sqrt(d3);
        d *= d4;
        d2 *= d4;
        n += 2;
        n3 += 2;
        if (!this.isSafeToStandAt(n4, (int)vec3.yCoord, n5, n, n2, n3, vec3, d, d2)) {
            return false;
        }

        n -= 2;
        n3 -= 2;
        double d5 = 1.0 / Math.abs(d);
        double d6 = 1.0 / Math.abs(d2);
        double d7 = n4 * 1 - vec3.xCoord;
        double d8 = n5 * 1 - vec3.zCoord;
        if (d >= 0.0) {
            d7++;
        }

        if (d2 >= 0.0) {
            d8++;
        }

        d7 /= d;
        d8 /= d2;
        int n6 = d < 0.0 ? -1 : 1;
        int n7 = d2 < 0.0 ? -1 : 1;
        int n8 = MathHelper.floor_double(vec32.xCoord);
        int n9 = MathHelper.floor_double(vec32.zCoord);
        int n10 = n8 - n4;
        int n11 = n9 - n5;

        while (n10 * n6 > 0 || n11 * n7 > 0) {
            if (d7 < d8) {
                d7 += d5;
                n4 += n6;
                n10 = n8 - n4;
            } else {
                d8 += d6;
                n5 += n7;
                n11 = n9 - n5;
            }

            if (!this.isSafeToStandAt(n4, (int)vec3.yCoord, n5, n, n2, n3, vec3, d, d2)) {
                return false;
            }
        }

        return true;
    }

    public boolean isSafeToStandAt(int n, int n2, int n3, int n4, int n5, int n6, Vec3 vec3, double d, double d2) {
        int n7 = n - n4 / 2;
        int n8 = n3 - n6 / 2;
        if (!this.isPositionClear(n7, n2, n8, n4, n5, n6, vec3, d, d2)) {
            return false;
        }

        for (int i = n7; i < n7 + n4; i++) {
            for (int j = n8; j < n8 + n6; j++) {
                double d3 = i + 0.5 - vec3.xCoord;
                double d4 = j + 0.5 - vec3.zCoord;
                if (d3 * d + d4 * d2 >= 0.0) {
                    Block block = this.worldObj.getBlock(i, n2 - 1, j);
                    Material material = block.getMaterial();
                    if (material == Material.air) {
                        return false;
                    }

                    if (material == Material.water && !this.theEntity.isInWater()) {
                        return false;
                    }

                    if (material == Material.lava) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public boolean isPositionClear(int n, int n2, int n3, int n4, int n5, int n6, Vec3 vec3, double d, double d2) {
        for (int i = n; i < n + n4; i++) {
            for (int j = n2; j < n2 + n5; j++) {
                for (int k = n3; k < n3 + n6; k++) {
                    double d3 = i + 0.5 - vec3.xCoord;
                    double d4 = k + 0.5 - vec3.zCoord;
                    if (d3 * d + d4 * d2 >= 0.0) {
                        Block block = this.worldObj.getBlock(i, j, k);
                        if (!block.isPassable(this.worldObj, i, j, k)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
