package net.minecraft.entity.boss;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityWither extends EntityMob implements IRangedAttackMob, IBossDisplayData {
    public float[] sideHeadPitches = new float[2];
    public float[] sideHeadYaws = new float[2];
    public float[] prevSideHeadPitches = new float[2];
    public float[] prevSideHeadYaws = new float[2];
    public int[] intArray = new int[2];
    public int[] intArray2 = new int[2];
    public int intValue;
    public static IEntitySelector attackEntitySelector = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getCreatureAttribute() != EnumCreatureAttribute.UNDEAD;
        }
    };

    public EntityWither(World world) {
        super(world);
        this.setHealth(this.getMaxHealth());
        this.setSize(0.9F, 4.0F);
        this.isImmuneToFire = true;
        this.getNavigator().setCanSwim(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0, 40, 20.0F));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, false, attackEntitySelector));
        this.experienceValue = 50;
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(17, new Integer(0));
        this.dataWatcher.addObject(18, new Integer(0));
        this.dataWatcher.addObject(19, new Integer(0));
        this.dataWatcher.addObject(20, new Integer(0));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Invul", this.getInvulTime());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setInvulTime(tagCompund.getInteger("Invul"));
    }

    @Override
    public float getShadowSize() {
        return this.height / 8.0F;
    }

    @Override
    public String getLivingSound() {
        return "mob.wither.idle";
    }

    @Override
    public String getHurtSound() {
        return "mob.wither.hurt";
    }

    @Override
    public String getDeathSound() {
        return "mob.wither.death";
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        this.motionY *= 0.6F;
        if (!this.worldObj.isRemote && this.getWatchedTargetId(0) > 0) {
            Entity entityByID = this.worldObj.getEntityByID(this.getWatchedTargetId(0));
            if (entityByID != null) {
                if (this.posY < entityByID.posY || !this.isArmored() && this.posY < entityByID.posY + 5.0) {
                    if (this.motionY < 0.0) {
                        this.motionY = 0.0;
                    }

                    this.motionY = this.motionY + (0.5 - this.motionY) * 0.6F;
                }

                double d = entityByID.posX - this.posX;
                double d2 = entityByID.posZ - this.posZ;
                double d3 = d * d + d2 * d2;
                if (d3 > 9.0) {
                    double d4 = MathHelper.sqrt_double(d3);
                    this.motionX = this.motionX + (d / d4 * 0.5 - this.motionX) * 0.6F;
                    this.motionZ = this.motionZ + (d2 / d4 * 0.5 - this.motionZ) * 0.6F;
                }
            }
        }

        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05F) {
            this.rotationYaw = (float)Math.atan2(this.motionZ, this.motionX) * (180.0F / (float)Math.PI) - 90.0F;
        }

        super.onLivingUpdate();

        for (int i = 0; i < 2; i++) {
            this.prevSideHeadYaws[i] = this.sideHeadYaws[i];
            this.prevSideHeadPitches[i] = this.sideHeadPitches[i];
        }

        for (int i = 0; i < 2; i++) {
            int watchedTargetId = this.getWatchedTargetId(i + 1);
            Entity entity = null;
            if (watchedTargetId > 0) {
                entity = this.worldObj.getEntityByID(watchedTargetId);
            }

            if (entity != null) {
                double headX = this.getHeadX(i + 1);
                double headY = this.getHeadY(i + 1);
                double headZ = this.getHeadZ(i + 1);
                double d = entity.posX - headX;
                double d2 = entity.posY + entity.getEyeHeight() - headY;
                double d3 = entity.posZ - headZ;
                double d4 = MathHelper.sqrt_double(d * d + d3 * d3);
                float f = (float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0F;
                float f2 = (float)(-(Math.atan2(d2, d4) * 180.0 / Math.PI));
                this.sideHeadPitches[i] = this.getNextAngle(this.sideHeadPitches[i], f2, 40.0F);
                this.sideHeadYaws[i] = this.getNextAngle(this.sideHeadYaws[i], f, 10.0F);
            } else {
                this.sideHeadYaws[i] = this.getNextAngle(this.sideHeadYaws[i], this.renderYawOffset, 10.0F);
            }
        }

        boolean isArmored = this.isArmored();

        for (int i = 0; i < 3; i++) {
            double headX = this.getHeadX(i);
            double headY = this.getHeadY(i);
            double headZ = this.getHeadZ(i);
            this.worldObj
                .spawnParticle(
                    "smoke",
                    headX + this.rand.nextGaussian() * 0.3F,
                    headY + this.rand.nextGaussian() * 0.3F,
                    headZ + this.rand.nextGaussian() * 0.3F,
                    0.0,
                    0.0,
                    0.0
                );
            if (isArmored && this.worldObj.rand.nextInt(4) == 0) {
                this.worldObj
                    .spawnParticle(
                        "mobSpell",
                        headX + this.rand.nextGaussian() * 0.3F,
                        headY + this.rand.nextGaussian() * 0.3F,
                        headZ + this.rand.nextGaussian() * 0.3F,
                        0.7F,
                        0.7F,
                        0.5
                    );
            }
        }

        if (this.getInvulTime() > 0) {
            for (int i = 0; i < 3; i++) {
                this.worldObj
                    .spawnParticle(
                        "mobSpell",
                        this.posX + this.rand.nextGaussian() * 1.0,
                        this.posY + this.rand.nextFloat() * 3.3F,
                        this.posZ + this.rand.nextGaussian() * 1.0,
                        0.7F,
                        0.7F,
                        0.9F
                    );
            }
        }
    }

    @Override
    public void updateAITasks() {
        if (this.getInvulTime() > 0) {
            int n = this.getInvulTime() - 1;
            if (n <= 0) {
                this.worldObj
                    .newExplosion(
                        this, this.posX, this.posY + this.getEyeHeight(), this.posZ, 7.0F, false, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")
                    );
                this.worldObj.playBroadcastSound(1013, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            }

            this.setInvulTime(n);
            if (this.ticksExisted % 10 == 0) {
                this.heal(10.0F);
            }
        } else {
            super.updateAITasks();

            for (int i = 1; i < 3; i++) {
                if (this.ticksExisted >= this.intArray[i - 1]) {
                    this.intArray[i - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);
                    if (this.worldObj.difficultySetting == EnumDifficulty.NORMAL || this.worldObj.difficultySetting == EnumDifficulty.HARD) {
                        int n = i - 1;
                        int n2 = this.intArray2[i - 1];
                        this.intArray2[n] = this.intArray2[i - 1] + 1;
                        if (n2 > 15) {
                            float f = 10.0F;
                            float f2 = 5.0F;
                            double randomDoubleInRange = MathHelper.getRandomDoubleInRange(this.rand, this.posX - f, this.posX + f);
                            double randomDoubleInRange2 = MathHelper.getRandomDoubleInRange(this.rand, this.posY - f2, this.posY + f2);
                            double randomDoubleInRange3 = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - f, this.posZ + f);
                            this.launchWitherSkullToCoords(i + 1, randomDoubleInRange, randomDoubleInRange2, randomDoubleInRange3, true);
                            this.intArray2[i - 1] = 0;
                        }
                    }

                    int watchedTargetId = this.getWatchedTargetId(i);
                    if (watchedTargetId > 0) {
                        Entity entityByID = this.worldObj.getEntityByID(watchedTargetId);
                        if (entityByID != null && entityByID.isEntityAlive() && this.getDistanceSqToEntity(entityByID) <= 900.0 && this.canEntityBeSeen(entityByID)) {
                            this.launchWitherSkullToEntity(i + 1, (EntityLivingBase)entityByID);
                            this.intArray[i - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                            this.intArray2[i - 1] = 0;
                        } else {
                            this.setTrackedEntityId(i, 0);
                        }
                    } else {
                        List selectEntitiesWithinAABB = this.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(20.0, 8.0, 20.0), attackEntitySelector);

                        for (int j = 0; j < 10 && !selectEntitiesWithinAABB.isEmpty(); j++) {
                            EntityLivingBase entityLivingBase = (EntityLivingBase)selectEntitiesWithinAABB.get(this.rand.nextInt(selectEntitiesWithinAABB.size()));
                            if (entityLivingBase != this && entityLivingBase.isEntityAlive() && this.canEntityBeSeen(entityLivingBase)) {
                                if (entityLivingBase instanceof EntityPlayer) {
                                    if (!((EntityPlayer)entityLivingBase).capabilities.disableDamage) {
                                        this.setTrackedEntityId(i, entityLivingBase.getEntityId());
                                    }
                                } else {
                                    this.setTrackedEntityId(i, entityLivingBase.getEntityId());
                                }
                                break;
                            }

                            selectEntitiesWithinAABB.remove(entityLivingBase);
                        }
                    }
                }
            }

            if (this.getAttackTarget() != null) {
                this.setTrackedEntityId(0, this.getAttackTarget().getEntityId());
            } else {
                this.setTrackedEntityId(0, 0);
            }

            if (this.intValue > 0) {
                this.intValue--;
                if (this.intValue == 0 && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                    int n = MathHelper.floor_double(this.posY);
                    int n2 = MathHelper.floor_double(this.posX);
                    int n3 = MathHelper.floor_double(this.posZ);
                    boolean flag = false;

                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            for (int k = 0; k <= 3; k++) {
                                int n4 = n2 + i;
                                int n5 = n + k;
                                int n6 = n3 + j;
                                Block block = this.worldObj.getBlock(n4, n5, n6);
                                if (block.getMaterial() != Material.air
                                    && block != Blocks.bedrock
                                    && block != Blocks.end_portal
                                    && block != Blocks.end_portal_frame
                                    && block != Blocks.command_block) {
                                    flag = this.worldObj.breakBlock(n4, n5, n6, true) || flag;
                                }
                            }
                        }
                    }

                    if (flag) {
                        this.worldObj.playAuxSFXAtEntity(null, 1012, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                    }
                }
            }

            if (this.ticksExisted % 20 == 0) {
                this.heal(1.0F);
            }
        }
    }

    public void onSummoned() {
        this.setInvulTime(220);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    @Override
    public void setInWeb() {
    }

    @Override
    public int getTotalArmorValue() {
        return 4;
    }

    public double getHeadX(int n) {
        if (n <= 0) {
            return this.posX;
        }

        float f = (this.renderYawOffset + 180 * (n - 1)) / 180.0F * (float) Math.PI;
        float cos = MathHelper.cos(f);
        return this.posX + cos * 1.3;
    }

    public double getHeadY(int n) {
        return n <= 0 ? this.posY + 3.0 : this.posY + 2.2;
    }

    public double getHeadZ(int n) {
        if (n <= 0) {
            return this.posZ;
        }

        float f = (this.renderYawOffset + 180 * (n - 1)) / 180.0F * (float) Math.PI;
        float sin = MathHelper.sin(f);
        return this.posZ + sin * 1.3;
    }

    public float getNextAngle(float f, float f2, float f3) {
        float f4 = MathHelper.wrapAngleTo180_float(f2 - f);
        if (f4 > f3) {
            f4 = f3;
        }

        if (f4 < -f3) {
            f4 = -f3;
        }

        return f + f4;
    }

    public void launchWitherSkullToEntity(int n, EntityLivingBase entityLivingBase) {
        this.launchWitherSkullToCoords(n, entityLivingBase.posX, entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.5, entityLivingBase.posZ, n == 0 && this.rand.nextFloat() < 0.001F);
    }

    public void launchWitherSkullToCoords(int n, double d, double d2, double d3, boolean flag) {
        this.worldObj.playAuxSFXAtEntity(null, 1014, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
        double headX = this.getHeadX(n);
        double headY = this.getHeadY(n);
        double headZ = this.getHeadZ(n);
        double d4 = d - headX;
        double d5 = d2 - headY;
        double d6 = d3 - headZ;
        EntityWitherSkull entityWitherSkull = new EntityWitherSkull(this.worldObj, this, d4, d5, d6);
        if (flag) {
            entityWitherSkull.setInvulnerable(true);
        }

        entityWitherSkull.posY = headY;
        entityWitherSkull.posX = headX;
        entityWitherSkull.posZ = headZ;
        this.worldObj.spawnEntityInWorld(entityWitherSkull);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entityLivingBase, float f) {
        this.launchWitherSkullToEntity(0, entityLivingBase);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        if (source == DamageSource.drown) {
            return false;
        }

        if (this.getInvulTime() > 0) {
            return false;
        }

        if (this.isArmored()) {
            Entity sourceOfDamage = source.getSourceOfDamage();
            if (sourceOfDamage instanceof EntityArrow) {
                return false;
            }
        }

        Entity entity = source.getEntity();
        if (entity != null
            && !(entity instanceof EntityPlayer)
            && entity instanceof EntityLivingBase
            && ((EntityLivingBase)entity).getCreatureAttribute() == this.getCreatureAttribute()) {
            return false;
        }

        if (this.intValue <= 0) {
            this.intValue = 20;
        }

        for (int i = 0; i < this.intArray2.length; i++) {
            this.intArray2[i] = this.intArray2[i] + 3;
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        this.dropItem(Items.nether_star, 1);
        if (!this.worldObj.isRemote) {
            for (EntityPlayer entityPlayer : (Iterable<EntityPlayer>)this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand(50.0, 100.0, 50.0))) {
                entityPlayer.triggerAchievement(AchievementList.killWither);
            }
        }
    }

    @Override
    public void despawnEntity() {
        this.entityAge = 0;
    }

    @Override
    public int getBrightnessForRender(float f) {
        return 15728880;
    }

    @Override
    public void fall(float distance) {
    }

    @Override
    public void addPotionEffect(PotionEffect potionEffect) {
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(300.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6F);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0);
    }

    public float getHeadYaw(int n) {
        return this.sideHeadYaws[n];
    }

    public float getHeadPitch(int n) {
        return this.sideHeadPitches[n];
    }

    public int getInvulTime() {
        return this.dataWatcher.getWatchableObjectInt(20);
    }

    public void setInvulTime(int n) {
        this.dataWatcher.updateObject(20, n);
    }

    public int getWatchedTargetId(int n) {
        return this.dataWatcher.getWatchableObjectInt(17 + n);
    }

    public void setTrackedEntityId(int n, int n2) {
        this.dataWatcher.updateObject(17 + n, n2);
    }

    public boolean isArmored() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    public void mountEntity(Entity entityIn) {
        this.ridingEntity = null;
    }
}
