package net.minecraft.entity.ai;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class EntityAIMate extends EntityAIBase {
    public EntityAnimal theAnimal;
    public World theWorld;
    public EntityAnimal targetMate;
    public int spawnBabyDelay;
    public double moveSpeed;

    public EntityAIMate(EntityAnimal entityAnimal, double d) {
        this.theAnimal = entityAnimal;
        this.theWorld = entityAnimal.worldObj;
        this.moveSpeed = d;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.theAnimal.isInLove()) {
            return false;
        }

        this.targetMate = this.getNearbyMate();
        return this.targetMate != null;
    }

    @Override
    public boolean continueExecuting() {
        return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }

    @Override
    public void resetTask() {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    @Override
    public void updateTask() {
        this.theAnimal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, this.theAnimal.getVerticalFaceSpeed());
        this.theAnimal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
        this.spawnBabyDelay++;
        if (this.spawnBabyDelay >= 60 && this.theAnimal.getDistanceSqToEntity(this.targetMate) < 9.0) {
            this.spawnBaby();
        }
    }

    public EntityAnimal getNearbyMate() {
        float f = 8.0F;
        List entitiesWithinAABB = this.theWorld.getEntitiesWithinAABB(this.theAnimal.getClass(), this.theAnimal.boundingBox.expand(f, f, f));
        double d = Double.MAX_VALUE;
        EntityAnimal entityAnimal = null;

        for (EntityAnimal entityAnimal2 : (Iterable<EntityAnimal>)entitiesWithinAABB) {
            if (this.theAnimal.canMateWith(entityAnimal2) && this.theAnimal.getDistanceSqToEntity(entityAnimal2) < d) {
                entityAnimal = entityAnimal2;
                d = this.theAnimal.getDistanceSqToEntity(entityAnimal2);
            }
        }

        return entityAnimal;
    }

    public void spawnBaby() {
        EntityAgeable createChild = this.theAnimal.createChild(this.targetMate);
        if (createChild != null) {
            EntityPlayer lovingPlayer = this.theAnimal.getLovingPlayer();
            if (lovingPlayer == null && this.targetMate.getLovingPlayer() != null) {
                lovingPlayer = this.targetMate.getLovingPlayer();
            }

            if (lovingPlayer != null) {
                lovingPlayer.triggerAchievement(StatList.animalsBredStat);
                if (this.theAnimal instanceof EntityCow) {
                    lovingPlayer.triggerAchievement(AchievementList.breedCow);
                }
            }

            this.theAnimal.setGrowingAge(6000);
            this.targetMate.setGrowingAge(6000);
            this.theAnimal.resetInLove();
            this.targetMate.resetInLove();
            createChild.setGrowingAge(-24000);
            createChild.setLocationAndAngles(this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ, 0.0F, 0.0F);
            this.theWorld.spawnEntityInWorld(createChild);
            Random rNG = this.theAnimal.getRNG();

            for (int i = 0; i < 7; i++) {
                double d = rNG.nextGaussian() * 0.02;
                double d2 = rNG.nextGaussian() * 0.02;
                double d3 = rNG.nextGaussian() * 0.02;
                this.theWorld
                    .spawnParticle(
                        "heart",
                        this.theAnimal.posX + rNG.nextFloat() * this.theAnimal.width * 2.0F - this.theAnimal.width,
                        this.theAnimal.posY + 0.5 + rNG.nextFloat() * this.theAnimal.height,
                        this.theAnimal.posZ + rNG.nextFloat() * this.theAnimal.width * 2.0F - this.theAnimal.width,
                        d,
                        d2,
                        d3
                    );
            }

            if (this.theWorld.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                this.theWorld
                    .spawnEntityInWorld(new EntityXPOrb(this.theWorld, this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ, rNG.nextInt(7) + 1));
            }
        }
    }
}
