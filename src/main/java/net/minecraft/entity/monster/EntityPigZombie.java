package net.minecraft.entity.monster;

import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityPigZombie extends EntityZombie {
    public static UUID ATTACKING_SPEED_BOOST_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    public static AttributeModifier attributeModifier = new AttributeModifier(ATTACKING_SPEED_BOOST_UUID, "Attacking speed boost", 0.45, 0).setSaved(false);
    public int angerLevel;
    public int randomSoundDelay;
    public Entity entity;

    public EntityPigZombie(World world) {
        super(world);
        this.isImmuneToFire = true;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(REINFORCEMENTS_ATTRIBUTE).setBaseValue(0.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(5.0);
    }

    @Override
    public boolean isAIEnabled() {
        return false;
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        if (this.entity != this.entityToAttack && !this.worldObj.isRemote) {
            IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            entityAttribute.removeModifier(attributeModifier);
            if (this.entityToAttack != null) {
                entityAttribute.applyModifier(attributeModifier);
            }
        }

        this.entity = this.entityToAttack;
        if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
            this.playSound("mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
        }

        super.onUpdate();
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL
            && this.worldObj.checkNoEntityCollision(this.boundingBox)
            && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
            && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setShort("Anger", (short)this.angerLevel);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.angerLevel = tagCompund.getShort("Anger");
    }

    @Override
    public Entity findPlayerToAttack() {
        return this.angerLevel == 0 ? null : super.findPlayerToAttack();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        Entity entity = source.getEntity();
        if (entity instanceof EntityPlayer) {
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(32.0, 32.0, 32.0));

            for (int i = 0; i < list.size(); i++) {
                Entity entity2 = (Entity)list.get(i);
                if (entity2 instanceof EntityPigZombie) {
                    EntityPigZombie entityPigZombie = (EntityPigZombie)entity2;
                    entityPigZombie.becomeAngryAt(entity);
                }
            }

            this.becomeAngryAt(entity);
        }

        return super.attackEntityFrom(source, amount);
    }

    public void becomeAngryAt(Entity entity) {
        this.entityToAttack = entity;
        this.angerLevel = 400 + this.rand.nextInt(400);
        this.randomSoundDelay = this.rand.nextInt(40);
    }

    @Override
    public String getLivingSound() {
        return "mob.zombiepig.zpig";
    }

    @Override
    public String getHurtSound() {
        return "mob.zombiepig.zpighurt";
    }

    @Override
    public String getDeathSound() {
        return "mob.zombiepig.zpigdeath";
    }

    @Override
    public void dropFewItems(boolean flag, int n) {
        int nextInt = this.rand.nextInt(2 + n);

        for (int i = 0; i < nextInt; i++) {
            this.dropItem(Items.rotten_flesh, 1);
        }

        nextInt = this.rand.nextInt(2 + n);

        for (int i = 0; i < nextInt; i++) {
            this.dropItem(Items.gold_nugget, 1);
        }
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) {
        return false;
    }

    @Override
    public void dropRareDrop(int n) {
        this.dropItem(Items.gold_ingot, 1);
    }

    @Override
    public void addRandomArmor() {
        this.setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityLivingData) {
        super.onSpawnWithEgg(entityLivingData);
        this.setVillager(false);
        return entityLivingData;
    }
}
