package net.minecraft.entity.passive;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAnimal extends EntityAgeable implements IAnimals {
    public int inLove;
    public int breeding;
    public EntityPlayer playerInLove;

    public EntityAnimal(World world) {
        super(world);
    }

    @Override
    public void updateAITick() {
        if (this.getGrowingAge() != 0) {
            this.inLove = 0;
        }

        super.updateAITick();
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        super.onLivingUpdate();
        if (this.getGrowingAge() != 0) {
            this.inLove = 0;
        }

        if (this.inLove > 0) {
            this.inLove--;
            String str = "heart";
            if (this.inLove % 10 == 0) {
                double d = this.rand.nextGaussian() * 0.02;
                double d2 = this.rand.nextGaussian() * 0.02;
                double d3 = this.rand.nextGaussian() * 0.02;
                this.worldObj
                    .spawnParticle(
                        str,
                        this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.posY + 0.5 + this.rand.nextFloat() * this.height,
                        this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        d,
                        d2,
                        d3
                    );
            }
        } else {
            this.breeding = 0;
        }
    }

    @Override
    public void attackEntity(Entity entity, float f) {
        if (entity instanceof EntityPlayer) {
            if (f < 3.0F) {
                double d = entity.posX - this.posX;
                double d2 = entity.posZ - this.posZ;
                this.rotationYaw = (float)(Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0F;
                this.hasAttacked = true;
            }

            EntityPlayer entityPlayer = (EntityPlayer)entity;
            if (entityPlayer.getCurrentEquippedItem() == null || !this.isBreedingItem(entityPlayer.getCurrentEquippedItem())) {
                this.entityToAttack = null;
            }
        } else if (entity instanceof EntityAnimal) {
            EntityAnimal entityAnimal = (EntityAnimal)entity;
            if (this.getGrowingAge() > 0 && entityAnimal.getGrowingAge() < 0) {
                if (f < 2.5) {
                    this.hasAttacked = true;
                }
            } else if (this.inLove > 0 && entityAnimal.inLove > 0) {
                if (entityAnimal.entityToAttack == null) {
                    entityAnimal.entityToAttack = this;
                }

                if (entityAnimal.entityToAttack == this && f < 3.5) {
                    entityAnimal.inLove++;
                    this.inLove++;
                    this.breeding++;
                    if (this.breeding % 4 == 0) {
                        this.worldObj
                            .spawnParticle(
                                "heart",
                                this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                                this.posY + 0.5 + this.rand.nextFloat() * this.height,
                                this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                                0.0,
                                0.0,
                                0.0
                            );
                    }

                    if (this.breeding == 60) {
                        this.procreate((EntityAnimal)entity);
                    }
                } else {
                    this.breeding = 0;
                }
            } else {
                this.breeding = 0;
                this.entityToAttack = null;
            }
        }
    }

    public void procreate(EntityAnimal entityAnimal) {
        EntityAgeable createChild = this.createChild(entityAnimal);
        if (createChild != null) {
            if (this.playerInLove == null && entityAnimal.getLovingPlayer() != null) {
                this.playerInLove = entityAnimal.getLovingPlayer();
            }

            if (this.playerInLove != null) {
                this.playerInLove.triggerAchievement(StatList.animalsBredStat);
                if (this instanceof EntityCow) {
                    this.playerInLove.triggerAchievement(AchievementList.breedCow);
                }
            }

            this.setGrowingAge(6000);
            entityAnimal.setGrowingAge(6000);
            this.inLove = 0;
            this.breeding = 0;
            this.entityToAttack = null;
            entityAnimal.entityToAttack = null;
            entityAnimal.breeding = 0;
            entityAnimal.inLove = 0;
            createChild.setGrowingAge(-24000);
            createChild.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);

            for (int i = 0; i < 7; i++) {
                double d = this.rand.nextGaussian() * 0.02;
                double d2 = this.rand.nextGaussian() * 0.02;
                double d3 = this.rand.nextGaussian() * 0.02;
                this.worldObj
                    .spawnParticle(
                        "heart",
                        this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.posY + 0.5 + this.rand.nextFloat() * this.height,
                        this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        d,
                        d2,
                        d3
                    );
            }

            this.worldObj.spawnEntityInWorld(createChild);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        this.fleeingTick = 60;
        if (!this.isAIEnabled()) {
            IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            if (entityAttribute.getModifier(FLEEING_SPEED_BONUS_ID) == null) {
                entityAttribute.applyModifier(FLEEING_SPEED_MODIFIER);
            }
        }

        this.entityToAttack = null;
        this.inLove = 0;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public float getBlockPathWeight(int n, int n2, int n3) {
        return this.worldObj.getBlock(n, n2 - 1, n3) == Blocks.grass ? 10.0F : this.worldObj.getLightBrightness(n, n2, n3) - 0.5F;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("InLove", this.inLove);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.inLove = tagCompund.getInteger("InLove");
    }

    @Override
    public Entity findPlayerToAttack() {
        if (this.fleeingTick > 0) {
            return null;
        }

        float f = 8.0F;
        if (this.inLove > 0) {
            List entitiesWithinAABB = this.worldObj.getEntitiesWithinAABB(this.getClass(), this.boundingBox.expand(f, f, f));

            for (int i = 0; i < entitiesWithinAABB.size(); i++) {
                EntityAnimal entityAnimal = (EntityAnimal)entitiesWithinAABB.get(i);
                if (entityAnimal != this && entityAnimal.inLove > 0) {
                    return entityAnimal;
                }
            }
        } else if (this.getGrowingAge() == 0) {
            List entitiesWithinAABB = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand(f, f, f));

            for (int i = 0; i < entitiesWithinAABB.size(); i++) {
                EntityPlayer entityPlayer = (EntityPlayer)entitiesWithinAABB.get(i);
                if (entityPlayer.getCurrentEquippedItem() != null && this.isBreedingItem(entityPlayer.getCurrentEquippedItem())) {
                    return entityPlayer;
                }
            }
        } else if (this.getGrowingAge() > 0) {
            List entitiesWithinAABB = this.worldObj.getEntitiesWithinAABB(this.getClass(), this.boundingBox.expand(f, f, f));

            for (int i = 0; i < entitiesWithinAABB.size(); i++) {
                EntityAnimal entityAnimal = (EntityAnimal)entitiesWithinAABB.get(i);
                if (entityAnimal != this && entityAnimal.getGrowingAge() < 0) {
                    return entityAnimal;
                }
            }
        }

        return null;
    }

    @Override
    public boolean getCanSpawnHere() {
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.boundingBox.minY);
        int n3 = MathHelper.floor_double(this.posZ);
        return this.worldObj.getBlock(n, n2 - 1, n3) == Blocks.grass
            && this.worldObj.getFullBlockLightValue(n, n2, n3) > 8
            && super.getCanSpawnHere();
    }

    @Override
    public int getTalkInterval() {
        return 120;
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public int getExperiencePoints(EntityPlayer entityPlayer) {
        return 1 + this.worldObj.rand.nextInt(3);
    }

    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack.getItem() == Items.wheat;
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) throws java.io.IOException {
        ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
        if (currentItem != null && this.isBreedingItem(currentItem) && this.getGrowingAge() == 0 && this.inLove <= 0) {
            if (!entityPlayer.capabilities.isCreativeMode) {
                currentItem.stackSize--;
                if (currentItem.stackSize <= 0) {
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                }
            }

            this.setInLove(entityPlayer);
            return true;
        } else {
            return super.interact(entityPlayer);
        }
    }

    public void setInLove(EntityPlayer entityPlayer) {
        this.inLove = 600;
        this.playerInLove = entityPlayer;
        this.entityToAttack = null;
        this.worldObj.setEntityState(this, (byte)18);
    }

    public EntityPlayer getLovingPlayer() {
        return this.playerInLove;
    }

    public boolean isInLove() {
        return this.inLove > 0;
    }

    public void resetInLove() {
        this.inLove = 0;
    }

    public boolean canMateWith(EntityAnimal entityAnimal) {
        return entityAnimal != this && entityAnimal.getClass() == this.getClass() && this.isInLove() && entityAnimal.isInLove();
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 18) {
            for (int i = 0; i < 7; i++) {
                double d = this.rand.nextGaussian() * 0.02;
                double d2 = this.rand.nextGaussian() * 0.02;
                double d3 = this.rand.nextGaussian() * 0.02;
                this.worldObj
                    .spawnParticle(
                        "heart",
                        this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.posY + 0.5 + this.rand.nextFloat() * this.height,
                        this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        d,
                        d2,
                        d3
                    );
            }
        } else {
            super.handleHealthUpdate(b);
        }
    }
}
