package net.minecraft.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent29;
import com.cheatbreaker.client.event.SimpleEvent38;
import com.cheatbreaker.client.event.SimpleEvent42;
import com.cheatbreaker.client.event.SimpleEvent44;
import com.cheatbreaker.client.event.UpdatePotionEffectsEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLivingBase extends Entity {
    public static UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    public static AttributeModifier sprintingSpeedBoostModifier = new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.3F, 2)
        .setSaved(false);
    public BaseAttributeMap attributeMap;
    public CombatTracker _combatTracker = new CombatTracker(this);
    public HashMap activePotionsMap = new HashMap();
    public ItemStack[] previousEquipment = new ItemStack[5];
    public boolean isSwingInProgress;
    public int swingProgressInt;
    public int arrowHitTimer;
    public float prevHealth;
    public int hurtTime;
    public int maxHurtTime;
    public float attackedAtYaw;
    public int deathTime;
    public int attackTime;
    public float prevSwingProgress;
    public float swingProgress;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public int maxHurtResistantTime = 20;
    public float prevCameraPitch;
    public float cameraPitch;
    public float jumpMovementFactor = 0.02F;
    public boolean potionsNeedUpdate = true;
    public float renderYawOffset;
    public float prevRenderYawOffset;
    public float randomSmallSeed;
    public float prevRotationYawHead;
    public float randomLargeSeed;
    public EntityPlayer attackingPlayer;
    public int recentlyHit;
    public boolean dead;
    public int entityAge;
    public float prevStepBobbingAmount;
    public float stepBobbingAmount;
    public float distanceTraveled;
    public float prevDistanceTraveled;
    public float floatValue;
    public int scoreValue;
    public float lastDamage;
    public boolean isJumping;
    public float moveStrafing;
    public float moveForward;
    public float randomYawVelocity;
    public int newPosRotationIncrements;
    public double newPosX;
    public double newPosY;
    public double newPosZ;
    public double newRotationYaw;
    public double newRotationPitch;
    public float rotationYawHead;
    public EntityLivingBase entityLivingToAttack;
    public int revengeTimer;
    public EntityLivingBase lastAttacker;
    public int lastAttackerTime;
    public float landMovementFactor;
    public int jumpTicks;
    public float absorptionAmount;

    public EntityLivingBase(World world) {
        super(world);
        this.applyEntityAttributes();
        this.setHealth(this.getMaxHealth());
        this.preventEntitySpawning = true;
        this.randomSmallSeed = (float)(Math.random() + 1.0) * 0.01F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.randomLargeSeed = (float)Math.random() * 12398.0F;
        this.rotationYaw = (float)(Math.random() * Math.PI * 2.0);
        this.rotationYawHead = this.rotationYaw;
        this.stepHeight = 0.5F;
    }

    @Override
    public void entityInit() {
        this.dataWatcher.addObject(7, 0);
        this.dataWatcher.addObject(8, (byte)0);
        this.dataWatcher.addObject(9, (byte)0);
        this.dataWatcher.addObject(6, 1.0F);
    }

    public void applyEntityAttributes() {
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
        if (!this.isAIEnabled()) {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.1F);
        }
    }

    @Override
    public void updateFallState(double distanceFallenThisTick, boolean isOnGround) {
        if (!this.isInWater()) {
            this.handleWaterMovement();
        }

        if (isOnGround && this.fallDistance > 0.0F) {
            int n = MathHelper.floor_double(this.posX);
            int n2 = MathHelper.floor_double(this.posY - 0.2F - this.yOffset);
            int n3 = MathHelper.floor_double(this.posZ);
            Block block = this.worldObj.getBlock(n, n2, n3);
            if (block.getMaterial() == Material.air) {
                int renderType = this.worldObj.getBlock(n, n2 - 1, n3).getRenderType();
                if (renderType == 11 || renderType == 32 || renderType == 21) {
                    block = this.worldObj.getBlock(n, n2 - 1, n3);
                }
            } else if (!this.worldObj.isRemote && this.fallDistance > 3.0F) {
                this.worldObj.playAuxSFX(2006, n, n2, n3, MathHelper.ceiling_float_int(this.fallDistance - 3.0F));
            }

            block.onFallenUpon(this.worldObj, n, n2, n3, this, this.fallDistance);
        }

        super.updateFallState(distanceFallenThisTick, isOnGround);
    }

    public boolean canBreatheUnderwater() {
        return false;
    }

    @Override
    public void onEntityUpdate() throws java.io.IOException {
        this.prevSwingProgress = this.swingProgress;
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("livingEntityBaseTick");
        if (this.isEntityAlive() && this.isEntityInsideOpaqueBlock()) {
            this.attackEntityFrom(DamageSource.inWall, 1.0F);
        }

        if (this.isImmuneToFire() || this.worldObj.isRemote) {
            this.extinguish();
        }

        boolean flag = this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.disableDamage;
        if (this.isEntityAlive() && this.isInsideOfMaterial(Material.water)) {
            if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !flag) {
                this.setAir(this.decreaseAirSupply(this.getAir()));
                if (this.getAir() == -20) {
                    this.setAir(0);

                    for (int i = 0; i < 8; i++) {
                        float f = this.rand.nextFloat() - this.rand.nextFloat();
                        float f2 = this.rand.nextFloat() - this.rand.nextFloat();
                        float f3 = this.rand.nextFloat() - this.rand.nextFloat();
                        this.worldObj.spawnParticle("bubble", this.posX + f, this.posY + f2, this.posZ + f3, this.motionX, this.motionY, this.motionZ);
                    }

                    this.attackEntityFrom(DamageSource.drown, 2.0F);
                }
            }

            if (!this.worldObj.isRemote && this.isRiding() && this.ridingEntity instanceof EntityLivingBase) {
                this.mountEntity(null);
            }
        } else {
            this.setAir(300);
        }

        if (this.isEntityAlive() && this.isWet()) {
            this.extinguish();
        }

        this.prevCameraPitch = this.cameraPitch;
        if (this.attackTime > 0) {
            this.attackTime--;
        }

        if (this.hurtTime > 0) {
            this.hurtTime--;
        }

        if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP)) {
            this.hurtResistantTime--;
        }

        if (this.getHealth() <= 0.0F) {
            this.onDeathUpdate();
        }

        if (this.recentlyHit > 0) {
            this.recentlyHit--;
        } else {
            this.attackingPlayer = null;
        }

        if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive()) {
            this.lastAttacker = null;
        }

        if (this.entityLivingToAttack != null) {
            if (!this.entityLivingToAttack.isEntityAlive()) {
                this.setRevengeTarget(null);
            } else if (this.ticksExisted - this.revengeTimer > 100) {
                this.setRevengeTarget(null);
            }
        }

        this.updatePotionEffects();
        this.prevDistanceTraveled = this.distanceTraveled;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYawHead = this.rotationYawHead;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.worldObj.theProfiler.endSection();
    }

    public boolean isChild() {
        return false;
    }

    public void onDeathUpdate() {
        this.deathTime++;
        if (this.deathTime == 20) {
            if (!this.worldObj.isRemote
                && (this.recentlyHit > 0 || this.isPlayer())
                && this.isChild2()
                && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                int experiencePoints = this.getExperiencePoints(this.attackingPlayer);

                while (experiencePoints > 0) {
                    int xPSplit = EntityXPOrb.getXPSplit(experiencePoints);
                    experiencePoints -= xPSplit;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, xPSplit));
                }
            }

            this.setDead();

            for (int i = 0; i < 20; i++) {
                double d = this.rand.nextGaussian() * 0.02;
                double d2 = this.rand.nextGaussian() * 0.02;
                double d3 = this.rand.nextGaussian() * 0.02;
                this.worldObj
                    .spawnParticle(
                        "explode",
                        this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        this.posY + this.rand.nextFloat() * this.height,
                        this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                        d,
                        d2,
                        d3
                    );
            }
        }
    }

    public boolean isChild2() {
        return !this.isChild();
    }

    public int decreaseAirSupply(int n) {
        int respiration = EnchantmentHelper.getRespiration(this);
        return respiration > 0 && this.rand.nextInt(respiration + 1) > 0 ? n : n - 1;
    }

    public int getExperiencePoints(EntityPlayer entityPlayer) {
        return 0;
    }

    public boolean isPlayer() {
        return false;
    }

    public Random getRNG() {
        return this.rand;
    }

    public EntityLivingBase getAITarget() {
        return this.entityLivingToAttack;
    }

    public int getRevengeTimer() {
        return this.revengeTimer;
    }

    public void setRevengeTarget(EntityLivingBase entityLivingBase) {
        this.entityLivingToAttack = entityLivingBase;
        this.revengeTimer = this.ticksExisted;
    }

    public EntityLivingBase getLastAttacker() {
        return this.lastAttacker;
    }

    public int getLastAttackerTime() {
        return this.lastAttackerTime;
    }

    public void setLastAttacker(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            this.lastAttacker = (EntityLivingBase)entity;
        } else {
            this.lastAttacker = null;
        }

        this.lastAttackerTime = this.ticksExisted;
    }

    public int getAge() {
        return this.entityAge;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setFloat("HealF", this.getHealth());
        tagCompound.setShort("Health", (short)Math.ceil(this.getHealth()));
        tagCompound.setShort("HurtTime", (short)this.hurtTime);
        tagCompound.setShort("DeathTime", (short)this.deathTime);
        tagCompound.setShort("AttackTime", (short)this.attackTime);
        tagCompound.setFloat("AbsorptionAmount", this.getAbsorptionAmount());

        for (ItemStack itemStack : this.getInventory()) {
            if (itemStack != null) {
                this.attributeMap.removeAttributeModifiers(itemStack.getAttributeModifiers());
            }
        }

        tagCompound.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));

        for (ItemStack itemStack : this.getInventory()) {
            if (itemStack != null) {
                this.attributeMap.applyAttributeModifiers(itemStack.getAttributeModifiers());
            }
        }

        if (!this.activePotionsMap.isEmpty()) {
            NBTTagList nbttagList = new NBTTagList();

            for (PotionEffect potionEffect : (Iterable<PotionEffect>)this.activePotionsMap.values()) {
                nbttagList.appendTag(potionEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            tagCompound.setTag("ActiveEffects", nbttagList);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        this.setAbsorptionAmount(tagCompund.getFloat("AbsorptionAmount"));
        if (tagCompund.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isRemote) {
            SharedMonsterAttributes.fromNbt(this.getAttributeMap(), tagCompund.getTagList("Attributes", 10));
        }

        if (tagCompund.hasKey("ActiveEffects", 9)) {
            NBTTagList tagList = tagCompund.getTagList("ActiveEffects", 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                PotionEffect readCustomPotionEffectFromNBT = PotionEffect.readCustomPotionEffectFromNBT(compoundTagAt);
                if (readCustomPotionEffectFromNBT != null) {
                    this.activePotionsMap.put(readCustomPotionEffectFromNBT.getPotionID(), readCustomPotionEffectFromNBT);
                    CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent29());
                }
            }
        }

        if (tagCompund.hasKey("HealF", 99)) {
            this.setHealth(tagCompund.getFloat("HealF"));
        } else {
            NBTBase tag = tagCompund.getTag("Health");
            if (tag == null) {
                this.setHealth(this.getMaxHealth());
            } else if (tag.getId() == 5) {
                this.setHealth(((NBTTagFloat)tag).getFloat());
            } else if (tag.getId() == 2) {
                this.setHealth(((NBTTagShort)tag).getShort());
            }
        }

        this.hurtTime = tagCompund.getShort("HurtTime");
        this.deathTime = tagCompund.getShort("DeathTime");
        this.attackTime = tagCompund.getShort("AttackTime");
    }

    public void updatePotionEffects() {
        CheatBreaker.getInstance().getEventBus().handleEvent(new UpdatePotionEffectsEvent());
        Iterator iterator = this.activePotionsMap.keySet().iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer)iterator.next();
            PotionEffect potionEffect = (PotionEffect)this.activePotionsMap.get(integer);
            if (!potionEffect.onUpdate(this)) {
                if (!this.worldObj.isRemote) {
                    iterator.remove();
                    this.onFinishedPotionEffect(potionEffect);
                }
            } else if (potionEffect.getDuration() % 600 == 0) {
                this.onChangedPotionEffect(potionEffect, false);
            }
        }

        if (this.potionsNeedUpdate) {
            if (!this.worldObj.isRemote) {
                if (this.activePotionsMap.isEmpty()) {
                    this.dataWatcher.updateObject(8, (byte)0);
                    this.dataWatcher.updateObject(7, 0);
                    this.setInvisible(false);
                } else {
                    int calcPotionLiquidColor = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
                    this.dataWatcher.updateObject(8, (byte)(PotionHelper.checkCollection(this.activePotionsMap.values()) ? 1 : 0));
                    this.dataWatcher.updateObject(7, calcPotionLiquidColor);
                    this.setInvisible(this.isPotionActive(Potion.invisibility.id));
                }
            }

            this.potionsNeedUpdate = false;
        }

        int watchableObjectInt = this.dataWatcher.getWatchableObjectInt(7);
        boolean flag = this.dataWatcher.getWatchableObjectByte(8) > 0;
        if (watchableObjectInt > 0) {
            boolean flag2 = false;
            if (!this.isInvisible()) {
                flag2 = this.rand.nextBoolean();
            } else {
                flag2 = this.rand.nextInt(15) == 0;
            }

            if (flag) {
                flag2 &= this.rand.nextInt(5) == 0;
            }

            if (flag2 && watchableObjectInt > 0) {
                double d = (watchableObjectInt >> 16 & 0xFF) / 255.0;
                double d2 = (watchableObjectInt >> 8 & 0xFF) / 255.0;
                double d3 = (watchableObjectInt >> 0 & 0xFF) / 255.0;
                this.worldObj
                    .spawnParticle(
                        flag ? "mobSpellAmbient" : "mobSpell",
                        this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                        this.posY + this.rand.nextDouble() * this.height - this.yOffset,
                        this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                        d,
                        d2,
                        d3
                    );
            }
        }
    }

    public void clearActivePotions() {
        Iterator iterator = this.activePotionsMap.keySet().iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer)iterator.next();
            PotionEffect potionEffect = (PotionEffect)this.activePotionsMap.get(integer);
            if (!this.worldObj.isRemote) {
                iterator.remove();
                this.onFinishedPotionEffect(potionEffect);
            }
        }
    }

    public Collection<PotionEffect> getActivePotionEffects() {
        return this.activePotionsMap.values();
    }

    public boolean isPotionActive(int n) {
        return this.activePotionsMap.containsKey(n);
    }

    public boolean isPotionActive(Potion potion) {
        return this.activePotionsMap.containsKey(potion.id);
    }

    public PotionEffect getActivePotionEffect(Potion potion) {
        return (PotionEffect)this.activePotionsMap.get(potion.id);
    }

    public void addPotionEffect(PotionEffect potionEffect) {
        if (this.isPotionApplicable(potionEffect)) {
            if (this.activePotionsMap.containsKey(potionEffect.getPotionID())) {
                ((PotionEffect)this.activePotionsMap.get(potionEffect.getPotionID())).combine(potionEffect);
                this.onChangedPotionEffect((PotionEffect)this.activePotionsMap.get(potionEffect.getPotionID()), true);
            } else {
                this.activePotionsMap.put(potionEffect.getPotionID(), potionEffect);
                this.onNewPotionEffect(potionEffect);
                CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent42());
            }
        }
    }

    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if (this.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD) {
            return true;
        }

        int potionID = potionEffect.getPotionID();
        return potionID != Potion.regeneration.id && potionID != Potion.poison.id;
    }

    public boolean isEntityUndead() {
        return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    public void removePotionEffectClient(int n) {
        this.activePotionsMap.remove(n);
        CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent44());
    }

    public void removePotionEffect(int n) {
        PotionEffect potionEffect = (PotionEffect)this.activePotionsMap.remove(n);
        if (potionEffect != null) {
            this.onFinishedPotionEffect(potionEffect);
        }

        CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent38());
    }

    public void onNewPotionEffect(PotionEffect potionEffect) {
        this.potionsNeedUpdate = true;
        if (!this.worldObj.isRemote) {
            Potion.potionTypes[potionEffect.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), potionEffect.getAmplifier());
        }
    }

    public void onChangedPotionEffect(PotionEffect potionEffect, boolean flag) {
        this.potionsNeedUpdate = true;
        if (flag && !this.worldObj.isRemote) {
            Potion.potionTypes[potionEffect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), potionEffect.getAmplifier());
            Potion.potionTypes[potionEffect.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), potionEffect.getAmplifier());
        }
    }

    public void onFinishedPotionEffect(PotionEffect potionEffect) {
        this.potionsNeedUpdate = true;
        if (!this.worldObj.isRemote) {
            Potion.potionTypes[potionEffect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), potionEffect.getAmplifier());
        }
    }

    public void heal(float f) {
        float health = this.getHealth();
        if (health > 0.0F) {
            this.setHealth(health + f);
        }
    }

    public float getHealth() {
        return this.dataWatcher.getWatchableObjectFloat(6);
    }

    public void setHealth(float f) {
        this.dataWatcher.updateObject(6, MathHelper.clamp_float(f, 0.0F, this.getMaxHealth()));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        if (this.worldObj.isRemote) {
            return false;
        }

        this.entityAge = 0;
        if (this.getHealth() <= 0.0F) {
            return false;
        }

        if (source.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
            return false;
        }

        if ((source == DamageSource.anvil || source == DamageSource.fallingBlock) && this.getEquipmentInSlot(4) != null) {
            this.getEquipmentInSlot(4).damageItem((int)(amount * 4.0F + this.rand.nextFloat() * amount * 2.0F), this);
            amount *= 0.75F;
        }

        this.limbSwingAmount = 1.5F;
        boolean flag = true;
        if (this.hurtResistantTime > this.maxHurtResistantTime / 2.0F) {
            if (amount <= this.lastDamage) {
                return false;
            }

            this.damageEntity(source, amount - this.lastDamage);
            this.lastDamage = amount;
            flag = false;
        } else {
            this.lastDamage = amount;
            this.prevHealth = this.getHealth();
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(source, amount);
            this.hurtTime = this.maxHurtTime = 10;
        }

        this.attackedAtYaw = 0.0F;
        Entity entity = source.getEntity();
        if (entity != null) {
            if (entity instanceof EntityLivingBase) {
                this.setRevengeTarget((EntityLivingBase)entity);
            }

            if (entity instanceof EntityPlayer) {
                this.recentlyHit = 100;
                this.attackingPlayer = (EntityPlayer)entity;
            } else if (entity instanceof EntityWolf) {
                EntityWolf entityWolf = (EntityWolf)entity;
                if (entityWolf.isTamed()) {
                    this.recentlyHit = 100;
                    this.attackingPlayer = null;
                }
            }
        }

        if (flag) {
            this.worldObj.setEntityState(this, (byte)2);
            if (source != DamageSource.drown) {
                this.setBeenAttacked();
            }

            if (entity != null) {
                double d = entity.posX - this.posX;

                double d2;
                for (d2 = entity.posZ - this.posZ; d * d + d2 * d2 < 1.0E-4; d2 = (Math.random() - Math.random()) * 0.01) {
                    d = (Math.random() - Math.random()) * 0.01;
                }

                this.attackedAtYaw = (float)(Math.atan2(d2, d) * 180.0 / Math.PI) - this.rotationYaw;
                this.knockBack(entity, amount, d, d2);
            } else {
                this.attackedAtYaw = (int)(Math.random() * 2.0) * 180;
            }
        }

        if (this.getHealth() <= 0.0F) {
            String deathSound = this.getDeathSound();
            if (flag && deathSound != null) {
                this.playSound(deathSound, this.getSoundVolume(), this.getSoundPitch());
            }

            this.onDeath(source);
        } else {
            String hurtSound = this.getHurtSound();
            if (flag && hurtSound != null) {
                this.playSound(hurtSound, this.getSoundVolume(), this.getSoundPitch());
            }
        }

        return true;
    }

    public void renderBrokenItemStack(ItemStack itemStack) {
        this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

        for (int i = 0; i < 5; i++) {
            Vec3 createVectorHelper = Vec3.createVectorHelper((this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            createVectorHelper.rotateAroundX(-this.rotationPitch * (float) Math.PI / 180.0F);
            createVectorHelper.rotateAroundY(-this.rotationYaw * (float) Math.PI / 180.0F);
            Vec3 createVectorHelper2 = Vec3.createVectorHelper((this.rand.nextFloat() - 0.5) * 0.3, -this.rand.nextFloat() * 0.6 - 0.3, 0.6);
            createVectorHelper2.rotateAroundX(-this.rotationPitch * (float) Math.PI / 180.0F);
            createVectorHelper2.rotateAroundY(-this.rotationYaw * (float) Math.PI / 180.0F);
            createVectorHelper2 = createVectorHelper2.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
            this.worldObj
                .spawnParticle(
                    "iconcrack_" + Item.getIdFromItem(itemStack.getItem()), createVectorHelper2.xCoord, createVectorHelper2.yCoord, createVectorHelper2.zCoord, createVectorHelper.xCoord, createVectorHelper.yCoord + 0.05, createVectorHelper.zCoord
                );
        }
    }

    public void onDeath(DamageSource damageSource) {
        Entity entity = damageSource.getEntity();
        EntityLivingBase opponent = this.getOpponent();
        if (this.scoreValue >= 0 && opponent != null) {
            opponent.addToPlayerScore(this, this.scoreValue);
        }

        if (entity != null) {
            entity.onKillEntity(this);
        }

        this.dead = true;
        this.getCombatTracker().update();
        if (!this.worldObj.isRemote) {
            int n = 0;
            if (entity instanceof EntityPlayer) {
                n = EnchantmentHelper.getLootingModifier((EntityLivingBase)entity);
            }

            if (this.isChild2() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
                this.dropFewItems(this.recentlyHit > 0, n);
                this.dropEquipment(this.recentlyHit > 0, n);
                if (this.recentlyHit > 0) {
                    int n2 = this.rand.nextInt(200) - n;
                    if (n2 < 5) {
                        this.dropRareDrop(n2 <= 0 ? 1 : 0);
                    }
                }
            }
        }

        this.worldObj.setEntityState(this, (byte)3);
    }

    public void dropEquipment(boolean flag, int n) {
    }

    public void knockBack(Entity entity, float f, double d, double d2) {
        if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
            this.isAirBorne = true;
            float f2 = MathHelper.sqrt_double(d * d + d2 * d2);
            float f3 = 0.4F;
            this.motionX /= 2.0;
            this.motionY /= 2.0;
            this.motionZ /= 2.0;
            this.motionX -= d / f2 * f3;
            this.motionY += f3;
            this.motionZ -= d2 / f2 * f3;
            if (this.motionY > 0.4F) {
                this.motionY = 0.4F;
            }
        }
    }

    public String getHurtSound() {
        return "game.neutral.hurt";
    }

    public String getDeathSound() {
        return "game.neutral.die";
    }

    public void dropRareDrop(int n) {
    }

    public void dropFewItems(boolean flag, int n) {
    }

    public boolean isOnLadder() {
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.boundingBox.minY);
        int n3 = MathHelper.floor_double(this.posZ);
        Block block = this.worldObj.getBlock(n, n2, n3);
        return block == Blocks.ladder || block == Blocks.vine;
    }

    @Override
    public boolean isEntityAlive() {
        return !this.isDead && this.getHealth() > 0.0F;
    }

    @Override
    public void fall(float distance) {
        super.fall(distance);
        PotionEffect activePotionEffect = this.getActivePotionEffect(Potion.jump);
        float f = activePotionEffect != null ? activePotionEffect.getAmplifier() + 1 : 0.0F;
        int n = MathHelper.ceiling_float_int(distance - 3.0F - f);
        if (n > 0) {
            this.playSound(this.formatText(n), 1.0F, 1.0F);
            this.attackEntityFrom(DamageSource.fall, n);
            int n2 = MathHelper.floor_double(this.posX);
            int n3 = MathHelper.floor_double(this.posY - 0.2F - this.yOffset);
            int n4 = MathHelper.floor_double(this.posZ);
            Block block = this.worldObj.getBlock(n2, n3, n4);
            if (block.getMaterial() != Material.air) {
                SoundType stepSound = block.stepSound;
                this.playSound(stepSound.getStepSound(), stepSound.getVolume() * 0.5F, stepSound.getFrequency() * 0.75F);
            }
        }
    }

    public String formatText(int n) {
        return n > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
    }

    @Override
    public void performHurtAnimation() {
        this.hurtTime = this.maxHurtTime = 10;
        this.attackedAtYaw = 0.0F;
    }

    public int getTotalArmorValue() {
        int n = 0;

        for (ItemStack itemStack : this.getInventory()) {
            if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
                int damageReduceAmount = ((ItemArmor)itemStack.getItem()).damageReduceAmount;
                n += damageReduceAmount;
            }
        }

        return n;
    }

    public void damageArmor(float f) {
    }

    public float applyArmorCalculations(DamageSource damageSource, float f) {
        if (!damageSource.isUnblockable()) {
            int n = 25 - this.getTotalArmorValue();
            float f2 = f * n;
            this.damageArmor(f);
            f = f2 / 25.0F;
        }

        return f;
    }

    public float applyPotionDamageCalculations(DamageSource damageSource, float f) {
        if (damageSource.isDamageAbsolute()) {
            return f;
        }

        if (this instanceof EntityZombie) {
            f = f;
        }

        if (this.isPotionActive(Potion.resistance) && damageSource != DamageSource.outOfWorld) {
            int n = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
            int n2 = 25 - n;
            float f2 = f * n2;
            f = f2 / 25.0F;
        }

        if (f <= 0.0F) {
            return 0.0F;
        }

        int enchantmentModifierDamage = EnchantmentHelper.getEnchantmentModifierDamage(this.getInventory(), damageSource);
        if (enchantmentModifierDamage > 20) {
            enchantmentModifierDamage = 20;
        }

        if (enchantmentModifierDamage > 0 && enchantmentModifierDamage <= 20) {
            int n = 25 - enchantmentModifierDamage;
            float f2 = f * n;
            f = f2 / 25.0F;
        }

        return f;
    }

    public void damageEntity(DamageSource damageSource, float f) {
        if (!this.isEntityInvulnerable()) {
            f = this.applyArmorCalculations(damageSource, f);
            f = this.applyPotionDamageCalculations(damageSource, f);
            float f2 = f;
            f = Math.max(f - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (f2 - f));
            if (f != 0.0F) {
                float health = this.getHealth();
                this.setHealth(health - f);
                this.getCombatTracker().onDamage(damageSource, health, f);
                this.setAbsorptionAmount(this.getAbsorptionAmount() - f);
            }
        }
    }

    public CombatTracker getCombatTracker() {
        return this._combatTracker;
    }

    public EntityLivingBase getOpponent() {
        return this._combatTracker.getLastAttacker() != null
            ? this._combatTracker.getLastAttacker()
            : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null));
    }

    public float getMaxHealth() {
        return (float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
    }

    public int getArrowCountInEntity() {
        return this.dataWatcher.getWatchableObjectByte(9);
    }

    public void setArrowCountInEntity(int n) {
        this.dataWatcher.updateObject(9, (byte)n);
    }

    public int getArmSwingAnimationEnd() {
        return this.isPotionActive(Potion.digSpeed)
            ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1
            : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
    }

    public void swingItem() {
        if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
            this.swingProgressInt = -1;
            this.isSwingInProgress = true;
            if (this.worldObj instanceof WorldServer) {
                ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S0BPacketAnimation(this, 0));
            }
        }
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 2) {
            this.limbSwingAmount = 1.5F;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.hurtTime = this.maxHurtTime = 10;
            this.attackedAtYaw = 0.0F;
            this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.attackEntityFrom(DamageSource.generic, 0.0F);
        } else if (b == 3) {
            this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.setHealth(0.0F);
            this.onDeath(DamageSource.generic);
        } else {
            super.handleHealthUpdate(b);
        }
    }

    @Override
    public void kill() {
        this.attackEntityFrom(DamageSource.outOfWorld, 4.0F);
    }

    public void updateArmSwingProgress() {
        int armSwingAnimationEnd = this.getArmSwingAnimationEnd();
        if (this.isSwingInProgress) {
            this.swingProgressInt++;
            if (this.swingProgressInt >= armSwingAnimationEnd) {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        } else {
            this.swingProgressInt = 0;
        }

        this.swingProgress = (float)this.swingProgressInt / armSwingAnimationEnd;
    }

    public IAttributeInstance getEntityAttribute(IAttribute attribute) {
        return this.getAttributeMap().getAttributeInstance(attribute);
    }

    public BaseAttributeMap getAttributeMap() {
        if (this.attributeMap == null) {
            this.attributeMap = new ServersideAttributeMap();
        }

        return this.attributeMap;
    }

    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    public abstract ItemStack getHeldItem();

    public abstract ItemStack getEquipmentInSlot(int n);

    @Override
    public abstract void setCurrentItemOrArmor(int n, ItemStack itemStack);

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (entityAttribute.getModifier(sprintingSpeedBoostModifierUUID) != null) {
            entityAttribute.removeModifier(sprintingSpeedBoostModifier);
        }

        if (sprinting) {
            entityAttribute.applyModifier(sprintingSpeedBoostModifier);
        }
    }

    @Override
    public abstract ItemStack[] getInventory();

    public float getSoundVolume() {
        return 1.0F;
    }

    public float getSoundPitch() {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
    }

    public boolean isMovementBlocked() {
        return this.getHealth() <= 0.0F;
    }

    public void setPositionAndUpdate(double d, double d2, double d3) {
        this.setLocationAndAngles(d, d2, d3, this.rotationYaw, this.rotationPitch);
    }

    public void dismountEntity(Entity entity) {
        double posX = entity.posX;
        double d = entity.boundingBox.minY + entity.height;
        double posZ = entity.posZ;
        byte b = 1;

        for (int i = -b; i <= b; i++) {
            for (int j = -b; j < b; j++) {
                if (i != 0 || j != 0) {
                    int n = (int)(this.posX + i);
                    int n2 = (int)(this.posZ + j);
                    AxisAlignedBB offsetBoundingBox = this.boundingBox.getOffsetBoundingBox(i, 1.0, j);
                    if (this.worldObj.findList(offsetBoundingBox).isEmpty()) {
                        if (World.doesBlockHaveSolidTopSurface(this.worldObj, n, (int)this.posY, n2)) {
                            this.setPositionAndUpdate(this.posX + i, this.posY + 1.0, this.posZ + j);
                            return;
                        }

                        if (World.doesBlockHaveSolidTopSurface(this.worldObj, n, (int)this.posY - 1, n2)
                            || this.worldObj.getBlock(n, (int)this.posY - 1, n2).getMaterial() == Material.water) {
                            posX = this.posX + i;
                            d = this.posY + 1.0;
                            posZ = this.posZ + j;
                        }
                    }
                }
            }
        }

        this.setPositionAndUpdate(posX, d, posZ);
    }

    public boolean getAlwaysRenderNameTagForRender() {
        return false;
    }

    public IIcon getItemIcon(ItemStack itemStackIn, int n) {
        return itemStackIn.getItem().requiresMultipleRenderPasses()
            ? itemStackIn.getItem().getIconFromDamageForRenderPass(itemStackIn.getMetadata(), n)
            : itemStackIn.getIconIndex();
    }

    public void jump() {
        this.motionY = 0.42F;
        if (this.isPotionActive(Potion.jump)) {
            this.motionY = this.motionY + (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
        }

        if (this.isSprinting()) {
            float f = this.rotationYaw * (float) (Math.PI / 180.0);
            this.motionX = this.motionX - MathHelper.sin(f) * 0.2F;
            this.motionZ = this.motionZ + MathHelper.cos(f) * 0.2F;
        }

        this.isAirBorne = true;
    }

    public void moveEntityWithHeading(float f, float f2) {
        if (!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
            if (!this.handleLavaMovement() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
                float f3 = 0.91F;
                if (this.onGround) {
                    f3 = this.worldObj
                            .getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ))
                            .slipperiness
                        * 0.91F;
                }

                float f4 = 0.16277136F / (f3 * f3 * f3);
                float f5;
                if (this.onGround) {
                    f5 = this.getAIMoveSpeed() * f4;
                } else {
                    f5 = this.jumpMovementFactor;
                }

                this.moveFlying(f, f2, f5);
                f3 = 0.91F;
                if (this.onGround) {
                    f3 = this.worldObj
                            .getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ))
                            .slipperiness
                        * 0.91F;
                }

                if (this.isOnLadder()) {
                    float f6 = 0.15F;
                    if (this.motionX < -f6) {
                        this.motionX = -f6;
                    }

                    if (this.motionX > f6) {
                        this.motionX = f6;
                    }

                    if (this.motionZ < -f6) {
                        this.motionZ = -f6;
                    }

                    if (this.motionZ > f6) {
                        this.motionZ = f6;
                    }

                    this.fallDistance = 0.0F;
                    if (this.motionY < -0.15) {
                        this.motionY = -0.15;
                    }

                    boolean flag = this.isSneaking() && this instanceof EntityPlayer;
                    if (flag && this.motionY < 0.0) {
                        this.motionY = 0.0;
                    }
                }

                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                if (this.isCollidedHorizontally && this.isOnLadder()) {
                    this.motionY = 0.2;
                }

                if (this.worldObj.isRemote
                    && (
                        !this.worldObj.blockExists((int)this.posX, 0, (int)this.posZ)
                            || !this.worldObj.getChunkFromBlockCoords((int)this.posX, (int)this.posZ).isChunkLoaded
                    )) {
                    if (this.posY > 0.0) {
                        this.motionY = -0.1;
                    } else {
                        this.motionY = 0.0;
                    }
                } else {
                    this.motionY -= 0.08;
                }

                this.motionY *= 0.98F;
                this.motionX *= f3;
                this.motionZ *= f3;
            } else {
                double posY = this.posY;
                this.moveFlying(f, f2, 0.02F);
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.5;
                this.motionY *= 0.5;
                this.motionZ *= 0.5;
                this.motionY -= 0.02;
                if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + posY, this.motionZ)) {
                    this.motionY = 0.3F;
                }
            }
        } else {
            double posY = this.posY;
            this.moveFlying(f, f2, this.isAIEnabled() ? 0.04F : 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.8F;
            this.motionY *= 0.8F;
            this.motionZ *= 0.8F;
            this.motionY -= 0.02;
            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + posY, this.motionZ)) {
                this.motionY = 0.3F;
            }
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d = this.posX - this.prevPosX;
        double d2 = this.posZ - this.prevPosZ;
        float f3 = MathHelper.sqrt_double(d * d + d2 * d2) * 4.0F;
        if (f3 > 1.0F) {
            f3 = 1.0F;
        }

        this.limbSwingAmount = this.limbSwingAmount + (f3 - this.limbSwingAmount) * 0.4F;
        this.limbSwing = this.limbSwing + this.limbSwingAmount;
    }

    public boolean isAIEnabled() {
        return false;
    }

    public float getAIMoveSpeed() {
        return this.isAIEnabled() ? this.landMovementFactor : 0.1F;
    }

    public void setAIMoveSpeed(float f) {
        this.landMovementFactor = f;
    }

    public boolean attackEntityAsMob(Entity entity) {
        this.setLastAttacker(entity);
        return false;
    }

    public boolean isPlayerSleeping() {
        return false;
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        if (!this.worldObj.isRemote) {
            int arrowCountInEntity = this.getArrowCountInEntity();
            if (arrowCountInEntity > 0) {
                if (this.arrowHitTimer <= 0) {
                    this.arrowHitTimer = 20 * (30 - arrowCountInEntity);
                }

                this.arrowHitTimer--;
                if (this.arrowHitTimer <= 0) {
                    this.setArrowCountInEntity(arrowCountInEntity - 1);
                }
            }

            for (int i = 0; i < 5; i++) {
                ItemStack itemStack = this.previousEquipment[i];
                ItemStack equipmentInSlot = this.getEquipmentInSlot(i);
                if (!ItemStack.areItemStacksEqual(equipmentInSlot, itemStack)) {
                    ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S04PacketEntityEquipment(this.getEntityId(), i, equipmentInSlot));
                    if (itemStack != null) {
                        this.attributeMap.removeAttributeModifiers(itemStack.getAttributeModifiers());
                    }

                    if (equipmentInSlot != null) {
                        this.attributeMap.applyAttributeModifiers(equipmentInSlot.getAttributeModifiers());
                    }

                    this.previousEquipment[i] = equipmentInSlot == null ? null : equipmentInSlot.copy();
                }
            }

            if (this.ticksExisted % 20 == 0) {
                this.getCombatTracker().update();
            }
        }

        this.onLivingUpdate();
        double d = this.posX - this.prevPosX;
        double d2 = this.posZ - this.prevPosZ;
        float f = (float)(d * d + d2 * d2);
        float renderYawOffset = this.renderYawOffset;
        float f2 = 0.0F;
        this.prevStepBobbingAmount = this.stepBobbingAmount;
        float f3 = 0.0F;
        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float)Math.sqrt(f) * 3.0F;
            renderYawOffset = (float)Math.atan2(d2, d) * 180.0F / (float) Math.PI - 90.0F;
        }

        if (this.swingProgress > 0.0F) {
            renderYawOffset = this.rotationYaw;
        }

        if (!this.onGround) {
            f3 = 0.0F;
        }

        this.stepBobbingAmount = this.stepBobbingAmount + (f3 - this.stepBobbingAmount) * 0.3F;
        this.worldObj.theProfiler.startSection("headTurn");
        f2 = this.computeFloat(renderYawOffset, f2);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("rangeChecks");

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
            this.prevRenderYawOffset -= 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
            this.prevRenderYawOffset += 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
            this.prevRotationPitch -= 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead < -180.0F) {
            this.prevRotationYawHead -= 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead >= 180.0F) {
            this.prevRotationYawHead += 360.0F;
        }

        this.worldObj.theProfiler.endSection();
        this.distanceTraveled += f2;
    }

    public float computeFloat(float f, float f2) {
        float f3 = MathHelper.wrapAngleTo180_float(f - this.renderYawOffset);
        this.renderYawOffset += f3 * 0.3F;
        float f4 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
        boolean flag = f4 < -90.0F || f4 >= 90.0F;
        if (f4 < -75.0F) {
            f4 = -75.0F;
        }

        if (f4 >= 75.0F) {
            f4 = 75.0F;
        }

        this.renderYawOffset = this.rotationYaw - f4;
        if (f4 * f4 > 2500.0F) {
            this.renderYawOffset += f4 * 0.2F;
        }

        if (flag) {
            f2 *= -1.0F;
        }

        return f2;
    }

    public void onLivingUpdate() throws java.io.IOException {
        if (this.jumpTicks > 0) {
            this.jumpTicks--;
        }

        if (this.newPosRotationIncrements > 0) {
            double d = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
            double d2 = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
            double d3 = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
            double d4 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
            this.rotationYaw = (float)(this.rotationYaw + d4 / this.newPosRotationIncrements);
            this.rotationPitch = (float)(this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
            this.newPosRotationIncrements--;
            this.setPosition(d, d2, d3);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        } else if (!this.isServerWorld()) {
            this.motionX *= 0.98;
            this.motionY *= 0.98;
            this.motionZ *= 0.98;
        }

        if (Math.abs(this.motionX) < 0.005) {
            this.motionX = 0.0;
        }

        if (Math.abs(this.motionY) < 0.005) {
            this.motionY = 0.0;
        }

        if (Math.abs(this.motionZ) < 0.005) {
            this.motionZ = 0.0;
        }

        this.worldObj.theProfiler.startSection("ai");
        if (this.isMovementBlocked()) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        } else if (this.isServerWorld()) {
            if (this.isAIEnabled()) {
                this.worldObj.theProfiler.startSection("newAi");
                this.updateAITasks();
                this.worldObj.theProfiler.endSection();
            } else {
                this.worldObj.theProfiler.startSection("oldAi");
                this.updateEntityActionState();
                this.worldObj.theProfiler.endSection();
                this.rotationYawHead = this.rotationYaw;
            }
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("jump");
        if (this.isJumping) {
            if (this.isInWater() || this.handleLavaMovement()) {
                this.motionY += 0.04F;
            } else if (this.onGround && this.jumpTicks == 0) {
                this.jump();
                this.jumpTicks = 10;
            }
        } else {
            this.jumpTicks = 0;
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("travel");
        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.randomYawVelocity *= 0.9F;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("push");
        if (!this.worldObj.isRemote) {
            this.collideWithNearbyEntities();
        }

        this.worldObj.theProfiler.endSection();
    }

    public void updateAITasks() {
    }

    public void collideWithNearbyEntities() {
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2F, 0.0, 0.2F));
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                Entity entity = (Entity)list.get(i);
                if (entity.canBePushed()) {
                    this.collideWithEntity(entity);
                }
            }
        }
    }

    public void collideWithEntity(Entity entity) {
        entity.applyEntityCollision(this);
    }

    @Override
    public void updateRidden() throws java.io.IOException {
        super.updateRidden();
        this.prevStepBobbingAmount = this.stepBobbingAmount;
        this.stepBobbingAmount = 0.0F;
        this.fallDistance = 0.0F;
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements) {
        this.yOffset = 0.0F;
        this.newPosX = x;
        this.newPosY = y;
        this.newPosZ = z;
        this.newRotationYaw = yaw;
        this.newRotationPitch = pitch;
        this.newPosRotationIncrements = rotationIncrements;
    }

    public void updateAITick() {
    }

    public void updateEntityActionState() {
        this.entityAge++;
    }

    public void setJumping(boolean flag) {
        this.isJumping = flag;
    }

    public void onItemPickup(Entity entity, int n) {
        if (!entity.isDead && !this.worldObj.isRemote) {
            EntityTracker entityTracker = ((WorldServer)this.worldObj).getEntityTracker();
            if (entity instanceof EntityItem) {
                entityTracker.sendToAllTrackingEntity(entity, new S0DPacketCollectItem(entity.getEntityId(), this.getEntityId()));
            }

            if (entity instanceof EntityArrow) {
                entityTracker.sendToAllTrackingEntity(entity, new S0DPacketCollectItem(entity.getEntityId(), this.getEntityId()));
            }

            if (entity instanceof EntityXPOrb) {
                entityTracker.sendToAllTrackingEntity(entity, new S0DPacketCollectItem(entity.getEntityId(), this.getEntityId()));
            }
        }
    }

    public boolean canEntityBeSeen(Entity entity) {
        return this.worldObj
                .rayTraceBlocks(
                    Vec3.createVectorHelper(this.posX, this.posY + this.getEyeHeight(), this.posZ),
                    Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)
                )
            == null;
    }

    @Override
    public Vec3 getLookVec() {
        return this.getLook(1.0F);
    }

    @Override
    public Vec3 getLook(float f) {
        if (f == 1.0F) {
            float cos = MathHelper.cos(-this.rotationYaw * (float) (Math.PI / 180.0) - (float) Math.PI);
            float sin = MathHelper.sin(-this.rotationYaw * (float) (Math.PI / 180.0) - (float) Math.PI);
            float f2 = -MathHelper.cos(-this.rotationPitch * (float) (Math.PI / 180.0));
            float sin2 = MathHelper.sin(-this.rotationPitch * (float) (Math.PI / 180.0));
            return Vec3.createVectorHelper(sin * f2, sin2, cos * f2);
        } else {
            float f2 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * f;
            float f3 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * f;
            float cos = MathHelper.cos(-f3 * (float) (Math.PI / 180.0) - (float) Math.PI);
            float sin = MathHelper.sin(-f3 * (float) (Math.PI / 180.0) - (float) Math.PI);
            float f4 = -MathHelper.cos(-f2 * (float) (Math.PI / 180.0));
            float sin2 = MathHelper.sin(-f2 * (float) (Math.PI / 180.0));
            return Vec3.createVectorHelper(sin * f4, sin2, cos * f4);
        }
    }

    public float getSwingProgress(float f) {
        float f2 = this.swingProgress - this.prevSwingProgress;
        if (f2 < 0.0F) {
            f2++;
        }

        return this.prevSwingProgress + f2 * f;
    }

    public Vec3 getPosition(float f) {
        if (f == 1.0F) {
            return Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        }

        double d = this.prevPosX + (this.posX - this.prevPosX) * f;
        double d2 = this.prevPosY + (this.posY - this.prevPosY) * f;
        double d3 = this.prevPosZ + (this.posZ - this.prevPosZ) * f;
        return Vec3.createVectorHelper(d, d2, d3);
    }

    public MovingObjectPosition rayTrace(double d, float f) {
        Vec3 position = this.getPosition(f);
        Vec3 look = this.getLook(f);
        Vec3 addVector = position.addVector(look.xCoord * d, look.yCoord * d, look.zCoord * d);
        return this.worldObj.rayTraceBlocks(position, addVector, false, false, true);
    }

    public boolean isServerWorld() {
        return !this.worldObj.isRemote;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public boolean canBePushed() {
        return !this.isDead;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.85F;
    }

    @Override
    public void setBeenAttacked() {
        this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
    }

    @Override
    public float getRotationYawHead() {
        return this.rotationYawHead;
    }

    @Override
    public void setRotationYawHead(float rotation) {
        this.rotationYawHead = rotation;
    }

    public float getAbsorptionAmount() {
        return this.absorptionAmount;
    }

    public void setAbsorptionAmount(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.absorptionAmount = f;
    }

    public Team getTeam() {
        return null;
    }

    public boolean isOnSameTeam(EntityLivingBase entityLivingBase) {
        return this.isOnTeam(entityLivingBase.getTeam());
    }

    public boolean isOnTeam(Team team) {
        return this.getTeam() != null && this.getTeam().isSameTeam(team);
    }

    public void enterCombat() {
    }

    public void endCombat() {
    }
}
