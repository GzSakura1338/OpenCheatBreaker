package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

public abstract class EntityAIDoorInteract extends EntityAIBase {
    public EntityLiving theEntity;
    public int entityPosX;
    public int entityPosY;
    public int entityPosZ;
    public BlockDoor doorBlock;
    public boolean hasStoppedDoorInteraction;
    public float entityPositionX;
    public float entityPositionZ;

    public EntityAIDoorInteract(EntityLiving entityLiving) {
        this.theEntity = entityLiving;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.theEntity.isCollidedHorizontally) {
            return false;
        }

        PathNavigate navigator = this.theEntity.getNavigator();
        PathEntity path = navigator.getPath();
        if (path != null && !path.isFinished() && navigator.getCanBreakDoors()) {
            for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); i++) {
                PathPoint pathPointFromIndex = path.getPathPointFromIndex(i);
                this.entityPosX = pathPointFromIndex.xCoord;
                this.entityPosY = pathPointFromIndex.yCoord + 1;
                this.entityPosZ = pathPointFromIndex.zCoord;
                if (this.theEntity.getDistanceSq(this.entityPosX, this.theEntity.posY, this.entityPosZ) <= 2.25) {
                    this.doorBlock = this.getWoodenDoorBlock(this.entityPosX, this.entityPosY, this.entityPosZ);
                    if (this.doorBlock != null) {
                        return true;
                    }
                }
            }

            this.entityPosX = MathHelper.floor_double(this.theEntity.posX);
            this.entityPosY = MathHelper.floor_double(this.theEntity.posY + 1.0);
            this.entityPosZ = MathHelper.floor_double(this.theEntity.posZ);
            this.doorBlock = this.getWoodenDoorBlock(this.entityPosX, this.entityPosY, this.entityPosZ);
            return this.doorBlock != null;
        } else {
            return false;
        }
    }

    @Override
    public boolean continueExecuting() {
        return !this.hasStoppedDoorInteraction;
    }

    @Override
    public void startExecuting() {
        this.hasStoppedDoorInteraction = false;
        this.entityPositionX = (float)(this.entityPosX + 0.5F - this.theEntity.posX);
        this.entityPositionZ = (float)(this.entityPosZ + 0.5F - this.theEntity.posZ);
    }

    @Override
    public void updateTask() {
        float f = (float)(this.entityPosX + 0.5F - this.theEntity.posX);
        float f2 = (float)(this.entityPosZ + 0.5F - this.theEntity.posZ);
        float f3 = this.entityPositionX * f + this.entityPositionZ * f2;
        if (f3 < 0.0F) {
            this.hasStoppedDoorInteraction = true;
        }
    }

    public BlockDoor getWoodenDoorBlock(int n, int n2, int n3) {
        Block block = this.theEntity.worldObj.getBlock(n, n2, n3);
        return block != Blocks.wooden_door ? null : (BlockDoor)block;
    }
}
