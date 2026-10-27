package net.minecraft.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent29;
import com.cheatbreaker.client.event.SimpleEvent38;
import com.cheatbreaker.client.event.SimpleEvent42;
import com.cheatbreaker.client.event.SimpleEvent44;
import com.cheatbreaker.client.event.SimpleEvent8;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLivingBase extends Entity {
   public static UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
   public static AttributeModifier sprintingSpeedBoostModifier = new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.3F, 2)
      .setSaved(false);
   public BaseAttributeMap attributeMap;
   public CombatTracker _combatTracker = new CombatTracker(this);
   public Map<Integer, PotionEffect> activePotionsMap = Maps.newHashMap();
   public ItemStack[] previousEquipment = new ItemStack[5];
   public boolean isSwingInProgress;
   public int swingProgressInt;
   public int arrowHitTimer;
   public int hurtTime;
   public int maxHurtTime;
   public float attackedAtYaw;
   public int deathTime;
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
   public float randomUnused1;
   public float prevRotationYawHead;
   public float randomUnused2;
   public EntityPlayer attackingPlayer;
   public int recentlyHit;
   public boolean dead;
   public int entityAge;
   public float prevOnGroundSpeedFactor;
   public float onGroundSpeedFactor;
   public float movedDistance;
   public float prevMovedDistance;
   public float unused180;
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

   @Override
   public void onKillCommand() {
      this.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
   }

   public EntityLivingBase(World worldIn) {
      super(worldIn);
      this.applyEntityAttributes();
      this.setHealth(this.getMaxHealth());
      this.preventEntitySpawning = true;
      this.randomUnused1 = (float)((Math.random() + 1.0) * 0.01F);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.randomUnused2 = (float)Math.random() * 12398.0F;
      this.rotationYaw = (float)(Math.random() * Math.PI * 2.0);
      this.rotationYawHead = this.rotationYaw;
      this.stepHeight = 0.6F;
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
   }

   @Override
   public void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
      if (!this.isInWater()) {
         this.handleWaterMovement();
      }

      if (!this.worldObj.isRemote && this.fallDistance > 3.0F && onGroundIn) {
         IBlockState blockState = this.worldObj.getBlockState(pos);
         Block block = blockState.getBlock();
         float f = MathHelper.ceiling_float_int(this.fallDistance - 3.0F);
         if (block.getMaterial() != Material.air) {
            double min = Math.min(0.2F + f / 15.0F, 10.0F);
            if (min > 2.5) {
               min = 2.5;
            }

            int n = (int)(150.0 * min);
            ((WorldServer)this.worldObj)
               .spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, n, 0.0, 0.0, 0.0, 0.15F, Block.getStateId(blockState));
         }
      }

      super.updateFallState(y, onGroundIn, blockIn, pos);
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   @Override
   public void onEntityUpdate() {
      this.prevSwingProgress = this.swingProgress;
      super.onEntityUpdate();
      this.worldObj.theProfiler.startSection("livingEntityBaseTick");
      boolean flag = this instanceof EntityPlayer;
      if (this.isEntityAlive()) {
         if (this.isEntityInsideOpaqueBlock()) {
            this.attackEntityFrom(DamageSource.inWall, 1.0F);
         } else if (flag && !this.worldObj.getWorldBorder().contains(this.getEntityBoundingBox())) {
            double d = this.worldObj.getWorldBorder().getClosestDistance(this) + this.worldObj.getWorldBorder().getDamageBuffer();
            if (d < 0.0) {
               this.attackEntityFrom(DamageSource.inWall, Math.max(1, MathHelper.floor_double(-d * this.worldObj.getWorldBorder().getDamageAmount())));
            }
         }
      }

      if (this.isImmuneToFire() || this.worldObj.isRemote) {
         this.extinguish();
      }

      boolean flag2 = flag && ((EntityPlayer)this).capabilities.disableDamage;
      if (this.isEntityAlive()) {
         if (this.isInsideOfMaterial(Material.water)) {
            if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !flag2) {
               this.setAir(this.decreaseAirSupply(this.getAir()));
               if (this.getAir() == -20) {
                  this.setAir(0);

                  for (int i = 0; i < 8; i++) {
                     float f = this.rand.nextFloat() - this.rand.nextFloat();
                     float f2 = this.rand.nextFloat() - this.rand.nextFloat();
                     float f3 = this.rand.nextFloat() - this.rand.nextFloat();
                     this.worldObj
                        .spawnParticle(
                           EnumParticleTypes.WATER_BUBBLE, this.posX + f, this.posY + f2, this.posZ + f3, this.motionX, this.motionY, this.motionZ
                        );
                  }

                  this.attackEntityFrom(DamageSource.drown, 2.0F);
               }
            }

            if (!this.worldObj.isRemote && this.isRiding() && this.ridingEntity instanceof EntityLivingBase) {
               this.mountEntity((Entity)null);
            }
         } else {
            this.setAir(300);
         }
      }

      if (this.isEntityAlive() && this.isWet()) {
         this.extinguish();
      }

      this.prevCameraPitch = this.cameraPitch;
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
            this.setRevengeTarget((EntityLivingBase)null);
         } else if (this.ticksExisted - this.revengeTimer > 100) {
            this.setRevengeTarget((EntityLivingBase)null);
         }
      }

      this.updatePotionEffects();
      this.prevMovedDistance = this.movedDistance;
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
         if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot")
            )
          {
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
                  EnumParticleTypes.EXPLOSION_NORMAL,
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

   public boolean canDropLoot() {
      return !this.isChild();
   }

   public int decreaseAirSupply(int n) {
      int respiration = EnchantmentHelper.getRespiration(this);
      return respiration > 0 && this.rand.nextInt(respiration + 1) > 0 ? n : n - 1;
   }

   public int getExperiencePoints(EntityPlayer player) {
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

   public void setRevengeTarget(EntityLivingBase livingBase) {
      this.entityLivingToAttack = livingBase;
      this.revengeTimer = this.ticksExisted;
   }

   public EntityLivingBase getLastAttacker() {
      return this.lastAttacker;
   }

   public int getLastAttackerTime() {
      return this.lastAttackerTime;
   }

   public void setLastAttacker(Entity entityIn) {
      if (entityIn instanceof EntityLivingBase) {
         this.lastAttacker = (EntityLivingBase)entityIn;
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
      tagCompound.setInteger("HurtByTimestamp", this.revengeTimer);
      tagCompound.setShort("DeathTime", (short)this.deathTime);
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

         for (PotionEffect potionEffect : this.activePotionsMap.values()) {
            nbttagList.appendTag(potionEffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
         }

         tagCompound.setTag("ActiveEffects", nbttagList);
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.setAbsorptionAmount(tagCompund.getFloat("AbsorptionAmount"));
      if (tagCompund.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isRemote) {
         SharedMonsterAttributes.setAttributeModifiers(this.getAttributeMap(), tagCompund.getTagList("Attributes", 10));
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
      this.revengeTimer = tagCompund.getInteger("HurtByTimestamp");
   }

   public void updatePotionEffects() {
      CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent8());
      Iterator iterator = this.activePotionsMap.keySet().iterator();

      while (iterator.hasNext()) {
         Integer integer = (Integer)iterator.next();
         PotionEffect potionEffect = this.activePotionsMap.get(integer);
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
            this.updatePotionMetadata();
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
            boolean flag3 = CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
               && CheatBreaker.getInstance().getModManager().particlesMod.showActiveEffectParticles.getBooleanValue();
            double d = (watchableObjectInt >> 16 & 0xFF) / 255.0;
            double d2 = (watchableObjectInt >> 8 & 0xFF) / 255.0;
            double d3 = (watchableObjectInt >> 0 & 0xFF) / 255.0;
            if (flag3) {
               this.worldObj
                  .spawnParticle(
                     flag ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB,
                     this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                     this.posY + this.rand.nextDouble() * this.height,
                     this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                     d,
                     d2,
                     d3
                  );
            } else if (!CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()) {
               this.worldObj
                  .spawnParticle(
                     flag ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB,
                     this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                     this.posY + this.rand.nextDouble() * this.height,
                     this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                     d,
                     d2,
                     d3
                  );
            }
         }
      }
   }

   public void updatePotionMetadata() {
      if (this.activePotionsMap.isEmpty()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(false);
      } else {
         int calcPotionLiquidColor = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
         this.dataWatcher.updateObject(8, (byte)(PotionHelper.getAreAmbient(this.activePotionsMap.values()) ? 1 : 0));
         this.dataWatcher.updateObject(7, calcPotionLiquidColor);
         this.setInvisible(this.isPotionActive(Potion.invisibility.id));
      }
   }

   public void resetPotionEffectMetadata() {
      this.dataWatcher.updateObject(8, (byte)0);
      this.dataWatcher.updateObject(7, 0);
   }

   public void clearActivePotions() {
      Iterator iterator = this.activePotionsMap.keySet().iterator();

      while (iterator.hasNext()) {
         Integer integer = (Integer)iterator.next();
         PotionEffect potionEffect = this.activePotionsMap.get(integer);
         if (!this.worldObj.isRemote) {
            iterator.remove();
            this.onFinishedPotionEffect(potionEffect);
         }
      }
   }

   public Collection<PotionEffect> getActivePotionEffects() {
      return this.activePotionsMap.values();
   }

   public boolean isPotionActive(int potionId) {
      return this.activePotionsMap.containsKey(potionId);
   }

   public boolean isPotionActive(Potion potionIn) {
      return this.activePotionsMap.containsKey(potionIn.id);
   }

   public PotionEffect getActivePotionEffect(Potion potionIn) {
      return this.activePotionsMap.get(potionIn.id);
   }

   public void addPotionEffect(PotionEffect potioneffectIn) {
      if (this.isPotionApplicable(potioneffectIn)) {
         if (this.activePotionsMap.containsKey(potioneffectIn.getPotionID())) {
            this.activePotionsMap.get(potioneffectIn.getPotionID()).combine(potioneffectIn);
            this.onChangedPotionEffect(this.activePotionsMap.get(potioneffectIn.getPotionID()), true);
         } else {
            this.activePotionsMap.put(potioneffectIn.getPotionID(), potioneffectIn);
            this.onNewPotionEffect(potioneffectIn);
            CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent42());
         }
      }
   }

   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
         int potionID = potioneffectIn.getPotionID();
         if (potionID == Potion.regeneration.id || potionID == Potion.poison.id) {
            return false;
         }
      }

      return true;
   }

   public boolean isEntityUndead() {
      return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
   }

   public void removePotionEffectClient(int potionId) {
      this.activePotionsMap.remove(potionId);
      CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent44());
   }

   public void removePotionEffect(int potionId) {
      PotionEffect potionEffect = this.activePotionsMap.remove(potionId);
      if (potionEffect != null) {
         this.onFinishedPotionEffect(potionEffect);
      }

      CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent38());
   }

   public void onNewPotionEffect(PotionEffect id) {
      this.potionsNeedUpdate = true;
      if (!this.worldObj.isRemote) {
         Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
      }
   }

   public void onChangedPotionEffect(PotionEffect id, boolean flag) {
      this.potionsNeedUpdate = true;
      if (flag && !this.worldObj.isRemote) {
         Potion.potionTypes[id.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), id.getAmplifier());
         Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
      }
   }

   public void onFinishedPotionEffect(PotionEffect effect) {
      this.potionsNeedUpdate = true;
      if (!this.worldObj.isRemote) {
         Potion.potionTypes[effect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), effect.getAmplifier());
      }
   }

   public void heal(float healAmount) {
      float health = this.getHealth();
      if (health > 0.0F) {
         this.setHealth(health + healAmount);
      }
   }

   public float getHealth() {
      return this.dataWatcher.getWatchableObjectFloat(6);
   }

   public void setHealth(float health) {
      this.dataWatcher.updateObject(6, MathHelper.clamp_float(health, 0.0F, this.getMaxHealth()));
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
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

            this.attackedAtYaw = (float)(MathHelper.atan2(d2, d) * 180.0 / Math.PI - this.rotationYaw);
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

   public void renderBrokenItemStack(ItemStack stack) {
      this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

      for (int i = 0; i < 5; i++) {
         Vec3 vec3 = new Vec3((this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
         vec3 = vec3.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
         vec3 = vec3.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
         double d = -this.rand.nextFloat() * 0.6 - 0.3;
         Vec3 vec32 = new Vec3((this.rand.nextFloat() - 0.5) * 0.3, d, 0.6);
         vec32 = vec32.rotatePitch(-this.rotationPitch * (float) Math.PI / 180.0F);
         vec32 = vec32.rotateYaw(-this.rotationYaw * (float) Math.PI / 180.0F);
         vec32 = vec32.addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.ITEM_CRACK,
               vec32.xCoord,
               vec32.yCoord,
               vec32.zCoord,
               vec3.xCoord,
               vec3.yCoord + 0.05,
               vec3.zCoord,
               Item.getIdFromItem(stack.getItem())
            );
      }
   }

   public void onDeath(DamageSource cause) {
      Entity entity = cause.getEntity();
      EntityLivingBase attackingEntity = this.getAttackingEntity();
      if (this.scoreValue >= 0 && attackingEntity != null) {
         attackingEntity.addToPlayerScore(this, this.scoreValue);
      }

      if (entity != null) {
         entity.onKillEntity(this);
      }

      this.dead = true;
      this.getCombatTracker().reset();
      if (!this.worldObj.isRemote) {
         int n = 0;
         if (entity instanceof EntityPlayer) {
            n = EnchantmentHelper.getLootingModifier((EntityLivingBase)entity);
         }

         if (this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot")) {
            this.dropFewItems(this.recentlyHit > 0, n);
            this.dropEquipment(this.recentlyHit > 0, n);
            if (this.recentlyHit > 0 && this.rand.nextFloat() < 0.025F + n * 0.01F) {
               this.addRandomDrop();
            }
         }
      }

      this.worldObj.setEntityState(this, (byte)3);
   }

   public void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
   }

   public void knockBack(Entity entityIn, float f, double d, double d2) {
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

   public void addRandomDrop() {
   }

   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
   }

   public boolean isOnLadder() {
      int n = MathHelper.floor_double(this.posX);
      int n2 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
      int n3 = MathHelper.floor_double(this.posZ);
      Block block = this.worldObj.getBlockState(new BlockPos(n, n2, n3)).getBlock();
      return (block == Blocks.ladder || block == Blocks.vine) && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).isSpectator());
   }

   @Override
   public boolean isEntityAlive() {
      return !this.isDead && this.getHealth() > 0.0F;
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
      super.fall(distance, damageMultiplier);
      PotionEffect activePotionEffect = this.getActivePotionEffect(Potion.jump);
      float f = activePotionEffect != null ? activePotionEffect.getAmplifier() + 1 : 0.0F;
      int n = MathHelper.ceiling_float_int((distance - 3.0F - f) * damageMultiplier);
      if (n > 0) {
         this.playSound(this.getFallSoundString(n), 1.0F, 1.0F);
         this.attackEntityFrom(DamageSource.fall, n);
         int n2 = MathHelper.floor_double(this.posX);
         int n3 = MathHelper.floor_double(this.posY - 0.2F);
         int n4 = MathHelper.floor_double(this.posZ);
         Block block = this.worldObj.getBlockState(new BlockPos(n2, n3, n4)).getBlock();
         if (block.getMaterial() != Material.air) {
            SoundType stepSound = block.stepSound;
            this.playSound(stepSound.getStepSound(), stepSound.getVolume() * 0.5F, stepSound.getFrequency() * 0.75F);
         }
      }
   }

   public String getFallSoundString(int damageValue) {
      return damageValue > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
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

   public float applyArmorCalculations(DamageSource source, float damage) {
      if (!source.isUnblockable()) {
         int n = 25 - this.getTotalArmorValue();
         float f = damage * n;
         this.damageArmor(damage);
         damage = f / 25.0F;
      }

      return damage;
   }

   public float applyPotionDamageCalculations(DamageSource source, float damage) {
      if (source.isDamageAbsolute()) {
         return damage;
      }

      if (this.isPotionActive(Potion.resistance) && source != DamageSource.outOfWorld) {
         int n = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
         int n2 = 25 - n;
         float f = damage * n2;
         damage = f / 25.0F;
      }

      if (damage <= 0.0F) {
         return 0.0F;
      }

      int enchantmentModifierDamage = EnchantmentHelper.getEnchantmentModifierDamage(this.getInventory(), source);
      if (enchantmentModifierDamage > 20) {
         enchantmentModifierDamage = 20;
      }

      if (enchantmentModifierDamage > 0 && enchantmentModifierDamage <= 20) {
         int n = 25 - enchantmentModifierDamage;
         float f = damage * n;
         damage = f / 25.0F;
      }

      return damage;
   }

   public void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isEntityInvulnerable(damageSrc)) {
         damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
         damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
         float damageAmount2 = damageAmount;
         damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (damageAmount2 - damageAmount));
         if (damageAmount != 0.0F) {
            float health = this.getHealth();
            this.setHealth(health - damageAmount);
            this.getCombatTracker().trackDamage(damageSrc, health, damageAmount);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - damageAmount);
         }
      }
   }

   public CombatTracker getCombatTracker() {
      return this._combatTracker;
   }

   public EntityLivingBase getAttackingEntity() {
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

   public void setArrowCountInEntity(int count) {
      this.dataWatcher.updateObject(9, (byte)count);
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
   public void handleStatusUpdate(byte id) {
      if (id == 2) {
         this.limbSwingAmount = 1.5F;
         this.hurtResistantTime = this.maxHurtResistantTime;
         this.hurtTime = this.maxHurtTime = 10;
         this.attackedAtYaw = 0.0F;
         String hurtSound = this.getHurtSound();
         if (hurtSound != null) {
            this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.attackEntityFrom(DamageSource.generic, 0.0F);
      } else if (id == 3) {
         String deathSound = this.getDeathSound();
         if (deathSound != null) {
            this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.setHealth(0.0F);
         this.onDeath(DamageSource.generic);
      } else {
         super.handleStatusUpdate(id);
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

   public abstract ItemStack getCurrentArmor(int n);

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

   public void dismountEntity(Entity entityIn) {
      double posX = entityIn.posX;
      double d = entityIn.getEntityBoundingBox().minY + entityIn.height;
      double posZ = entityIn.posZ;
      byte b = 1;

      for (int i = -b; i <= b; i++) {
         for (int j = -b; j < b; j++) {
            if (i != 0 || j != 0) {
               int n = (int)(this.posX + i);
               int n2 = (int)(this.posZ + j);
               AxisAlignedBB offset = this.getEntityBoundingBox().offset(i, 1.0, j);
               if (this.worldObj.getCollisionBoxes(offset).isEmpty()) {
                  if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(n, (int)this.posY, n2))) {
                     this.setPositionAndUpdate(this.posX + i, this.posY + 1.0, this.posZ + j);
                     return;
                  }

                  if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(n, (int)this.posY - 1, n2))
                     || this.worldObj.getBlockState(new BlockPos(n, (int)this.posY - 1, n2)).getBlock().getMaterial() == Material.water) {
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

   @Override
   public boolean getAlwaysRenderNameTagForRender() {
      return false;
   }

   public float getJumpUpwardsMotion() {
      return 0.42F;
   }

   public void jump() {
      this.motionY = this.getJumpUpwardsMotion();
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

   public void updateAITick() {
      this.motionY += 0.04F;
   }

   public void handleJumpLava() {
      this.motionY += 0.04F;
   }

   public void moveEntityWithHeading(float strafe, float forward) {
      if (this.isServerWorld()) {
         if (!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
            if (!this.isInLava() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying) {
               float f = 0.91F;
               if (this.onGround) {
                  f = this.worldObj
                        .getBlockState(
                           new BlockPos(
                              MathHelper.floor_double(this.posX),
                              MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1,
                              MathHelper.floor_double(this.posZ)
                           )
                        )
                        .getBlock()
                        .slipperiness
                     * 0.91F;
               }

               float f2 = 0.16277136F / (f * f * f);
               float f3;
               if (this.onGround) {
                  f3 = this.getAIMoveSpeed() * f2;
               } else {
                  f3 = this.jumpMovementFactor;
               }

               this.moveFlying(strafe, forward, f3);
               f = 0.91F;
               if (this.onGround) {
                  f = this.worldObj
                        .getBlockState(
                           new BlockPos(
                              MathHelper.floor_double(this.posX),
                              MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1,
                              MathHelper.floor_double(this.posZ)
                           )
                        )
                        .getBlock()
                        .slipperiness
                     * 0.91F;
               }

               if (this.isOnLadder()) {
                  float f4 = 0.15F;
                  this.motionX = MathHelper.clamp_double(this.motionX, -f4, f4);
                  this.motionZ = MathHelper.clamp_double(this.motionZ, -f4, f4);
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
                     !this.worldObj.isBlockLoaded(new BlockPos((int)this.posX, 0, (int)this.posZ))
                        || !this.worldObj.getChunkFromBlockCoords(new BlockPos((int)this.posX, 0, (int)this.posZ)).isLoaded()
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
               this.motionX *= f;
               this.motionZ *= f;
            } else {
               double posY = this.posY;
               this.moveFlying(strafe, forward, 0.02F);
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
            float f = 0.8F;
            float f2 = 0.02F;
            float depthStriderModifier = EnchantmentHelper.getDepthStriderModifier(this);
            if (depthStriderModifier > 3.0F) {
               depthStriderModifier = 3.0F;
            }

            if (!this.onGround) {
               depthStriderModifier *= 0.5F;
            }

            if (depthStriderModifier > 0.0F) {
               f += (0.54600006F - f) * depthStriderModifier / 3.0F;
               f2 += (this.getAIMoveSpeed() * 1.0F - f2) * depthStriderModifier / 3.0F;
            }

            this.moveFlying(strafe, forward, f2);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= f;
            this.motionY *= 0.8F;
            this.motionZ *= f;
            this.motionY -= 0.02;
            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6F - this.posY + posY, this.motionZ)) {
               this.motionY = 0.3F;
            }
         }
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d = this.posX - this.prevPosX;
      double d2 = this.posZ - this.prevPosZ;
      float f = MathHelper.sqrt_double(d * d + d2 * d2) * 4.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      this.limbSwingAmount = this.limbSwingAmount + (f - this.limbSwingAmount) * 0.4F;
      this.limbSwing = this.limbSwing + this.limbSwingAmount;
   }

   public float getAIMoveSpeed() {
      return this.landMovementFactor;
   }

   public void setAIMoveSpeed(float speedIn) {
      this.landMovementFactor = speedIn;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      this.setLastAttacker(entityIn);
      return false;
   }

   public boolean isPlayerSleeping() {
      return false;
   }

   @Override
   public void onUpdate() {
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
            this.getCombatTracker().reset();
         }
      }

      this.onLivingUpdate();
      double d = this.posX - this.prevPosX;
      double d2 = this.posZ - this.prevPosZ;
      float f = (float)(d * d + d2 * d2);
      float renderYawOffset = this.renderYawOffset;
      float f2 = 0.0F;
      this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
      float f3 = 0.0F;
      if (f > 0.0025000002F) {
         f3 = 1.0F;
         f2 = (float)Math.sqrt(f) * 3.0F;
         renderYawOffset = (float)MathHelper.atan2(d2, d) * 180.0F / (float) Math.PI - 90.0F;
      }

      if (this.swingProgress > 0.0F) {
         renderYawOffset = this.rotationYaw;
      }

      if (!this.onGround) {
         f3 = 0.0F;
      }

      this.onGroundSpeedFactor = this.onGroundSpeedFactor + (f3 - this.onGroundSpeedFactor) * 0.3F;
      this.worldObj.theProfiler.startSection("headTurn");
      f2 = this.updateDistance(renderYawOffset, f2);
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
      this.movedDistance += f2;
   }

   public float updateDistance(float f, float f2) {
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

   public void onLivingUpdate() {
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
         this.worldObj.theProfiler.startSection("newAi");
         this.updateEntityActionState();
         this.worldObj.theProfiler.endSection();
      }

      this.worldObj.theProfiler.endSection();
      this.worldObj.theProfiler.startSection("jump");
      if (this.isJumping) {
         if (this.isInWater()) {
            this.updateAITick();
         } else if (this.isInLava()) {
            this.handleJumpLava();
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

   public void updateEntityActionState() {
   }

   public void collideWithNearbyEntities() {
      List entitiesInAABBexcluding = this.worldObj
         .getEntitiesInAABBexcluding(
            this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
               public boolean apply(Entity entity) {
                  return entity.canBePushed();
               }
            })
         );
      if (!entitiesInAABBexcluding.isEmpty()) {
         for (int i = 0; i < entitiesInAABBexcluding.size(); i++) {
            Entity entity = (Entity)entitiesInAABBexcluding.get(i);
            this.collideWithEntity(entity);
         }
      }
   }

   public void collideWithEntity(Entity entityIn) {
      entityIn.applyEntityCollision(this);
   }

   @Override
   public void mountEntity(Entity entityIn) {
      if (this.ridingEntity != null && entityIn == null) {
         if (!this.worldObj.isRemote) {
            this.dismountEntity(this.ridingEntity);
         }

         if (this.ridingEntity != null) {
            this.ridingEntity.riddenByEntity = null;
         }

         this.ridingEntity = null;
      } else {
         super.mountEntity(entityIn);
      }
   }

   @Override
   public void updateRidden() {
      super.updateRidden();
      this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
      this.onGroundSpeedFactor = 0.0F;
      this.fallDistance = 0.0F;
   }

   @Override
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean flag) {
      this.newPosX = x;
      this.newPosY = y;
      this.newPosZ = z;
      this.newRotationYaw = yaw;
      this.newRotationPitch = pitch;
      this.newPosRotationIncrements = posRotationIncrements;
   }

   public void setJumping(boolean jumping) {
      this.isJumping = jumping;
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

   public boolean canEntityBeSeen(Entity entityIn) {
      return this.worldObj
            .rayTraceBlocks(
               new Vec3(this.posX, this.posY + this.getEyeHeight(), this.posZ), new Vec3(entityIn.posX, entityIn.posY + entityIn.getEyeHeight(), entityIn.posZ)
            )
         == null;
   }

   @Override
   public Vec3 getLookVec() {
      return this.getLook(1.0F);
   }

   @Override
   public Vec3 getLook(float partialTicks) {
      if (partialTicks == 1.0F) {
         return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
      }

      float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
      float f2 = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
      return this.getVectorForRotation(f, f2);
   }

   public float getSwingProgress(float partialTickTime) {
      float f = this.swingProgress - this.prevSwingProgress;
      if (f < 0.0F) {
         f++;
      }

      return this.prevSwingProgress + f * partialTickTime;
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

   @Override
   public void setRenderYawOffset(float offset) {
      this.renderYawOffset = offset;
   }

   public float getAbsorptionAmount() {
      return this.absorptionAmount;
   }

   public void setAbsorptionAmount(float amount) {
      if (amount < 0.0F) {
         amount = 0.0F;
      }

      this.absorptionAmount = amount;
   }

   public Team getTeam() {
      return this.worldObj.getScoreboard().getPlayersTeam(this.getUniqueID().toString());
   }

   public boolean isOnSameTeam(EntityLivingBase otherEntity) {
      return this.isOnTeam(otherEntity.getTeam());
   }

   public boolean isOnTeam(Team teamIn) {
      return this.getTeam() != null ? this.getTeam().isSameTeam(teamIn) : false;
   }

   public void sendEnterCombat() {
   }

   public void sendEndCombat() {
   }

   public void markPotionsDirty() {
      this.potionsNeedUpdate = true;
   }
}
