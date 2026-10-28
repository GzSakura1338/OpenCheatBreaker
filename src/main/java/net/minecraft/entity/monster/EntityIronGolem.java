package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityIronGolem extends EntityGolem {
    public int homeCheckTimer;
    public Village villageObj;
    public int attackTimer;
    public int holdRoseTick;

    public EntityIronGolem(World world) {
        super(world);
        this.setSize(1.4F, 2.9F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 1.0, true));
        this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9, 32.0F));
        this.tasks.addTask(3, new EntityAIMoveThroughVillage(this, 0.6, true));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
        this.tasks.addTask(5, new EntityAILookAtVillager(this));
        this.tasks.addTask(6, new EntityAIWander(this, 0.6));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIDefendVillage(this));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, true, IMob.mobSelector));
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)0);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public void updateAITick() {
        if (--this.homeCheckTimer <= 0) {
            this.homeCheckTimer = 70 + this.rand.nextInt(50);
            this.villageObj = this.worldObj
                .villageCollectionObj
                .findNearestVillage(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 32);
            if (this.villageObj == null) {
                this.detachHome();
            } else {
                ChunkCoordinates center = this.villageObj.getCenter();
                this.setHomeArea(center.posX, center.posY, center.posZ, (int)(this.villageObj.getVillageRadius() * 0.6F));
            }
        }

        super.updateAITick();
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(100.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25);
    }

    @Override
    public int decreaseAirSupply(int n) {
        return n;
    }

    @Override
    public void collideWithEntity(Entity entity) {
        if (entity instanceof IMob && this.getRNG().nextInt(20) == 0) {
            this.setAttackTarget((EntityLivingBase)entity);
        }

        super.collideWithEntity(entity);
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        super.onLivingUpdate();
        if (this.attackTimer > 0) {
            this.attackTimer--;
        }

        if (this.holdRoseTick > 0) {
            this.holdRoseTick--;
        }

        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 2.5000003E-7F && this.rand.nextInt(5) == 0) {
            int n = MathHelper.floor_double(this.posX);
            int n2 = MathHelper.floor_double(this.posY - 0.2F - this.yOffset);
            int n3 = MathHelper.floor_double(this.posZ);
            Block block = this.worldObj.getBlock(n, n2, n3);
            if (block.getMaterial() != Material.air) {
                this.worldObj
                    .spawnParticle(
                        "blockcrack_" + Block.getIdFromBlock(block) + "_" + this.worldObj.getBlockMetadata(n, n2, n3),
                        this.posX + (this.rand.nextFloat() - 0.5) * this.width,
                        this.boundingBox.minY + 0.1,
                        this.posZ + (this.rand.nextFloat() - 0.5) * this.width,
                        4.0 * (this.rand.nextFloat() - 0.5),
                        0.5,
                        (this.rand.nextFloat() - 0.5) * 4.0
                    );
            }
        }
    }

    @Override
    public boolean canAttackClass(Class clazz) {
        return (!this.isPlayerCreated() || !EntityPlayer.class.isAssignableFrom(clazz)) && super.canAttackClass(clazz);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("PlayerCreated", this.isPlayerCreated());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setPlayerCreated(tagCompund.getBoolean("PlayerCreated"));
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        this.attackTimer = 10;
        this.worldObj.setEntityState(this, (byte)4);
        boolean attackEntityFrom = entity.attackEntityFrom(DamageSource.causeMobDamage(this), 7 + this.rand.nextInt(15));
        if (attackEntityFrom) {
            entity.motionY += 0.4F;
        }

        this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
        return attackEntityFrom;
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 4) {
            this.attackTimer = 10;
            this.playSound("mob.irongolem.throw", 1.0F, 1.0F);
        } else if (b == 11) {
            this.holdRoseTick = 400;
        } else {
            super.handleHealthUpdate(b);
        }
    }

    public Village getVillage() {
        return this.villageObj;
    }

    public int getAttackTimer() {
        return this.attackTimer;
    }

    public void setHoldingRose(boolean flag) {
        this.holdRoseTick = flag ? 400 : 0;
        this.worldObj.setEntityState(this, (byte)11);
    }

    @Override
    public String getHurtSound() {
        return "mob.irongolem.hit";
    }

    @Override
    public String getDeathSound() {
        return "mob.irongolem.death";
    }

    @Override
    public void playStepSound(int x, int y, int z, Block blockIn) {
        this.playSound("mob.irongolem.walk", 1.0F, 1.0F);
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        int nextInt = this.rand.nextInt(3);

        for (int i = 0; i < nextInt; i++) {
            this.dropItemWithOffset(Item.getItemFromBlock(Blocks.red_flower), 1, 0.0F);
        }

        int n2 = 3 + this.rand.nextInt(3);

        for (int i = 0; i < n2; i++) {
            this.dropItem(Items.iron_ingot, 1);
        }
    }

    public int getHoldRoseTick() {
        return this.holdRoseTick;
    }

    public boolean isPlayerCreated() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setPlayerCreated(boolean flag) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
        if (flag) {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 1));
        } else {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & -2));
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!this.isPlayerCreated() && this.attackingPlayer != null && this.villageObj != null) {
            this.villageObj.setReputationForPlayer(this.attackingPlayer.getCommandSenderName(), -5);
        }

        super.onDeath(damageSource);
    }
}
