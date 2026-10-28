package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIEatGrass extends EntityAIBase {
    public EntityLiving grassEaterEntity;
    public World entityWorld;
    public int eatingGrassTimer;

    public EntityAIEatGrass(EntityLiving entityLiving) {
        this.grassEaterEntity = entityLiving;
        this.entityWorld = entityLiving.worldObj;
        this.setMutexBits(7);
    }

    @Override
    public boolean shouldExecute() {
        if (this.grassEaterEntity.getRNG().nextInt(this.grassEaterEntity.isChild() ? 50 : 1000) != 0) {
            return false;
        }

        int n = MathHelper.floor_double(this.grassEaterEntity.posX);
        int n2 = MathHelper.floor_double(this.grassEaterEntity.posY);
        int n3 = MathHelper.floor_double(this.grassEaterEntity.posZ);
        return this.entityWorld.getBlock(n, n2, n3) == Blocks.tallgrass && this.entityWorld.getBlockMetadata(n, n2, n3) == 1
            || this.entityWorld.getBlock(n, n2 - 1, n3) == Blocks.grass;
    }

    @Override
    public void startExecuting() {
        this.eatingGrassTimer = 40;
        this.entityWorld.setEntityState(this.grassEaterEntity, (byte)10);
        this.grassEaterEntity.getNavigator().clearPathEntity();
    }

    @Override
    public void resetTask() {
        this.eatingGrassTimer = 0;
    }

    @Override
    public boolean continueExecuting() {
        return this.eatingGrassTimer > 0;
    }

    public int getEatingGrassTimer() {
        return this.eatingGrassTimer;
    }

    @Override
    public void updateTask() {
        this.eatingGrassTimer = Math.max(0, this.eatingGrassTimer - 1);
        if (this.eatingGrassTimer == 4) {
            int n = MathHelper.floor_double(this.grassEaterEntity.posX);
            int n2 = MathHelper.floor_double(this.grassEaterEntity.posY);
            int n3 = MathHelper.floor_double(this.grassEaterEntity.posZ);
            if (this.entityWorld.getBlock(n, n2, n3) == Blocks.tallgrass) {
                if (this.entityWorld.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                    this.entityWorld.breakBlock(n, n2, n3, false);
                }

                this.grassEaterEntity.eatGrassBonus();
            } else if (this.entityWorld.getBlock(n, n2 - 1, n3) == Blocks.grass) {
                if (this.entityWorld.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                    this.entityWorld.playAuxSFX(2001, n, n2 - 1, n3, Block.getIdFromBlock(Blocks.grass));
                    this.entityWorld.setBlock(n, n2 - 1, n3, Blocks.dirt, 0, 2);
                }

                this.grassEaterEntity.eatGrassBonus();
            }
        }
    }
}
