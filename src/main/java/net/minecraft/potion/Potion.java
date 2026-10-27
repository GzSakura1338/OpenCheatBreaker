package net.minecraft.potion;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class Potion {
   public static Potion[] potionTypes = new Potion[32];
   public static Map<ResourceLocation, Potion> STATUS_EFFECTS_BY_ID = Maps.newHashMap();
   public static Potion UNKNOWN = null;
   public static Potion moveSpeed = new Potion(1, new ResourceLocation("speed"), false, 8171462)
      .setPotionName("potion.moveSpeed")
      .setIconIndex(0, 0)
      .registerPotionAttributeModifier(SharedMonsterAttributes.movementSpeed, "91AEAA56-376B-4498-935B-2F7F68070635", 0.2F, 2);
   public static Potion moveSlowdown = new Potion(2, new ResourceLocation("slowness"), true, 5926017)
      .setPotionName("potion.moveSlowdown")
      .setIconIndex(1, 0)
      .registerPotionAttributeModifier(SharedMonsterAttributes.movementSpeed, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15F, 2);
   public static Potion digSpeed = new Potion(3, new ResourceLocation("haste"), false, 14270531)
      .setPotionName("potion.digSpeed")
      .setIconIndex(2, 0)
      .setEffectiveness(1.5);
   public static Potion digSlowdown = new Potion(4, new ResourceLocation("mining_fatigue"), true, 4866583)
      .setPotionName("potion.digSlowDown")
      .setIconIndex(3, 0);
   public static Potion damageBoost = new PotionAttackDamage(5, new ResourceLocation("strength"), false, 9643043)
      .setPotionName("potion.damageBoost")
      .setIconIndex(4, 0)
      .registerPotionAttributeModifier(SharedMonsterAttributes.attackDamage, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 2.5, 2);
   public static Potion heal = new PotionHealth(6, new ResourceLocation("instant_health"), false, 16262179).setPotionName("potion.heal");
   public static Potion harm = new PotionHealth(7, new ResourceLocation("instant_damage"), true, 4393481).setPotionName("potion.harm");
   public static Potion jump = new Potion(8, new ResourceLocation("jump_boost"), false, 2293580).setPotionName("potion.jump").setIconIndex(2, 1);
   public static Potion confusion = new Potion(9, new ResourceLocation("nausea"), true, 5578058)
      .setPotionName("potion.confusion")
      .setIconIndex(3, 1)
      .setEffectiveness(0.25);
   public static Potion regeneration = new Potion(10, new ResourceLocation("regeneration"), false, 13458603)
      .setPotionName("potion.regeneration")
      .setIconIndex(7, 0)
      .setEffectiveness(0.25);
   public static Potion resistance = new Potion(11, new ResourceLocation("resistance"), false, 10044730).setPotionName("potion.resistance").setIconIndex(6, 1);
   public static Potion fireResistance = new Potion(12, new ResourceLocation("fire_resistance"), false, 14981690)
      .setPotionName("potion.fireResistance")
      .setIconIndex(7, 1);
   public static Potion waterBreathing = new Potion(13, new ResourceLocation("water_breathing"), false, 3035801)
      .setPotionName("potion.waterBreathing")
      .setIconIndex(0, 2);
   public static Potion invisibility = new Potion(14, new ResourceLocation("invisibility"), false, 8356754)
      .setPotionName("potion.invisibility")
      .setIconIndex(0, 1);
   public static Potion blindness = new Potion(15, new ResourceLocation("blindness"), true, 2039587)
      .setPotionName("potion.blindness")
      .setIconIndex(5, 1)
      .setEffectiveness(0.25);
   public static Potion nightVision = new Potion(16, new ResourceLocation("night_vision"), false, 2039713)
      .setPotionName("potion.nightVision")
      .setIconIndex(4, 1);
   public static Potion hunger = new Potion(17, new ResourceLocation("hunger"), true, 5797459).setPotionName("potion.hunger").setIconIndex(1, 1);
   public static Potion weakness = new PotionAttackDamage(18, new ResourceLocation("weakness"), true, 4738376)
      .setPotionName("potion.weakness")
      .setIconIndex(5, 0)
      .registerPotionAttributeModifier(SharedMonsterAttributes.attackDamage, "22653B89-116E-49DC-9B6B-9971489B5BE5", 2.0, 0);
   public static Potion poison = new Potion(19, new ResourceLocation("poison"), true, 5149489)
      .setPotionName("potion.poison")
      .setIconIndex(6, 0)
      .setEffectiveness(0.25);
   public static Potion wither = new Potion(20, new ResourceLocation("wither"), true, 3484199)
      .setPotionName("potion.wither")
      .setIconIndex(1, 2)
      .setEffectiveness(0.25);
   public static Potion healthBoost = new PotionHealthBoost(21, new ResourceLocation("health_boost"), false, 16284963)
      .setPotionName("potion.healthBoost")
      .setIconIndex(2, 2)
      .registerPotionAttributeModifier(SharedMonsterAttributes.maxHealth, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, 0);
   public static Potion absorption = new PotionAbsorption(22, new ResourceLocation("absorption"), false, 2445989)
      .setPotionName("potion.absorption")
      .setIconIndex(2, 2);
   public static Potion saturation = new PotionHealth(23, new ResourceLocation("saturation"), false, 16262179).setPotionName("potion.saturation");
   public static Potion potion = null;
   public static Potion potion2 = null;
   public static Potion potion3 = null;
   public static Potion potion4 = null;
   public static Potion potion5 = null;
   public static Potion potion6 = null;
   public static Potion potion7 = null;
   public static Potion potion8 = null;
   public Map<IAttribute, AttributeModifier> attributeModifierMap = Maps.newHashMap();
   public String name = "";
   public int statusIconIndex = -1;
   public int id;
   public boolean isBadEffect;
   public double effectiveness;
   public int liquidColor;
   public boolean usable;

   public Potion(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
      this.id = potionID;
      potionTypes[potionID] = this;
      STATUS_EFFECTS_BY_ID.put(location, this);
      this.isBadEffect = badEffect;
      if (badEffect) {
         this.effectiveness = 0.5;
      } else {
         this.effectiveness = 1.0;
      }

      this.liquidColor = potionColor;
   }

   public static Potion getPotionFromResourceLocation(String location) {
      return STATUS_EFFECTS_BY_ID.get(new ResourceLocation(location));
   }

   public static Set<ResourceLocation> getPotionLocations() {
      return STATUS_EFFECTS_BY_ID.keySet();
   }

   public Potion setIconIndex(int n, int n2) {
      this.statusIconIndex = n + n2 * 8;
      return this;
   }

   public int getId() {
      return this.id;
   }

   public void performEffect(EntityLivingBase entityLivingBaseIn, int n) {
      if (this.id == regeneration.id) {
         if (entityLivingBaseIn.getHealth() < entityLivingBaseIn.getMaxHealth()) {
            entityLivingBaseIn.heal(1.0F);
         }
      } else if (this.id == poison.id) {
         if (entityLivingBaseIn.getHealth() > 1.0F) {
            entityLivingBaseIn.attackEntityFrom(DamageSource.magic, 1.0F);
         }
      } else if (this.id == wither.id) {
         entityLivingBaseIn.attackEntityFrom(DamageSource.wither, 1.0F);
      } else if (this.id == hunger.id && entityLivingBaseIn instanceof EntityPlayer) {
         ((EntityPlayer)entityLivingBaseIn).addExhaustion(0.025F * (n + 1));
      } else if (this.id == saturation.id && entityLivingBaseIn instanceof EntityPlayer) {
         if (!entityLivingBaseIn.worldObj.isRemote) {
            ((EntityPlayer)entityLivingBaseIn).getFoodStats().addStats(n + 1, 1.0F);
         }
      } else if ((this.id != heal.id || entityLivingBaseIn.isEntityUndead()) && (this.id != harm.id || !entityLivingBaseIn.isEntityUndead())) {
         if (this.id == harm.id && !entityLivingBaseIn.isEntityUndead() || this.id == heal.id && entityLivingBaseIn.isEntityUndead()) {
            entityLivingBaseIn.attackEntityFrom(DamageSource.magic, 6 << n);
         }
      } else {
         entityLivingBaseIn.heal(Math.max(4 << n, 0));
      }
   }

   public void affectEntity(Entity entity, Entity entity2, EntityLivingBase entityLivingBaseIn, int n, double d) {
      if ((this.id != heal.id || entityLivingBaseIn.isEntityUndead()) && (this.id != harm.id || !entityLivingBaseIn.isEntityUndead())) {
         if (this.id == harm.id && !entityLivingBaseIn.isEntityUndead() || this.id == heal.id && entityLivingBaseIn.isEntityUndead()) {
            int n2 = (int)(d * (6 << n) + 0.5);
            if (entity == null) {
               entityLivingBaseIn.attackEntityFrom(DamageSource.magic, n2);
            } else {
               entityLivingBaseIn.attackEntityFrom(DamageSource.causeIndirectMagicDamage(entity, entity2), n2);
            }
         }
      } else {
         int n2 = (int)(d * (4 << n) + 0.5);
         entityLivingBaseIn.heal(n2);
      }
   }

   public boolean isInstant() {
      return false;
   }

   public boolean isReady(int n, int n2) {
      if (this.id == regeneration.id) {
         int n3 = 50 >> n2;
         return n3 > 0 ? n % n3 == 0 : true;
      } else if (this.id == poison.id) {
         int n3 = 25 >> n2;
         return n3 > 0 ? n % n3 == 0 : true;
      } else if (this.id == wither.id) {
         int n3 = 40 >> n2;
         return n3 > 0 ? n % n3 == 0 : true;
      } else {
         return this.id == hunger.id;
      }
   }

   public Potion setPotionName(String nameIn) {
      this.name = nameIn;
      return this;
   }

   public String getName() {
      return this.name;
   }

   public boolean hasStatusIcon() {
      return this.statusIconIndex >= 0;
   }

   public int getStatusIconIndex() {
      return this.statusIconIndex;
   }

   public boolean isBadEffect() {
      return this.isBadEffect;
   }

   public static String getDurationString(PotionEffect effect) {
      if (effect.getIsPotionDurationMax()) {
         return "**:**";
      }

      int duration = effect.getDuration();
      return StringUtils.ticksToElapsedTime(duration);
   }

   public Potion setEffectiveness(double effectivenessIn) {
      this.effectiveness = effectivenessIn;
      return this;
   }

   public double getEffectiveness() {
      return this.effectiveness;
   }

   public boolean isUsable() {
      return this.usable;
   }

   public int getLiquidColor() {
      return this.liquidColor;
   }

   public Potion registerPotionAttributeModifier(IAttribute attribute, String str, double d, int n) {
      AttributeModifier attributeModifier = new AttributeModifier(UUID.fromString(str), this.getName(), d, n);
      this.attributeModifierMap.put(attribute, attributeModifier);
      return this;
   }

   public Map<IAttribute, AttributeModifier> getAttributeModifierMap() {
      return this.attributeModifierMap;
   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, BaseAttributeMap baseAttributeMap, int amplifier) {
      for (Entry entry : this.attributeModifierMap.entrySet()) {
         IAttributeInstance attributeInstance = baseAttributeMap.getAttributeInstance((IAttribute)entry.getKey());
         if (attributeInstance != null) {
            attributeInstance.removeModifier((AttributeModifier)entry.getValue());
         }
      }
   }

   public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, BaseAttributeMap baseAttributeMap, int amplifier) {
      for (Entry entry : this.attributeModifierMap.entrySet()) {
         IAttributeInstance attributeInstance = baseAttributeMap.getAttributeInstance((IAttribute)entry.getKey());
         if (attributeInstance != null) {
            AttributeModifier value = (AttributeModifier)entry.getValue();
            attributeInstance.removeModifier(value);
            attributeInstance.applyModifier(
               new AttributeModifier(value.getID(), this.getName() + " " + amplifier, this.getAttributeModifierAmount(amplifier, value), value.getOperation())
            );
         }
      }
   }

   public double getAttributeModifierAmount(int n, AttributeModifier modifier) {
      return modifier.getAmount() * (n + 1);
   }
}
