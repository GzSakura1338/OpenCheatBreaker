package net.minecraft.util;

import net.minecraft.entity.EntityLivingBase;

public class CombatEntry {
   public DamageSource damageSrc;
   public int entityAge;
   public float damage;
   public float health;
   public String fallDeathSuffix;
   public float fallDistance;

   public CombatEntry(DamageSource damageSrcIn, int n, float healthAmount, float damageAmount, String str, float fallDistanceIn) {
      this.damageSrc = damageSrcIn;
      this.entityAge = n;
      this.damage = damageAmount;
      this.health = healthAmount;
      this.fallDeathSuffix = str;
      this.fallDistance = fallDistanceIn;
   }

   public DamageSource getDamageSrc() {
      return this.damageSrc;
   }

   public float getDamage() {
      return this.damage;
   }

   public boolean isLivingDamageSrc() {
      return this.damageSrc.getEntity() instanceof EntityLivingBase;
   }

   public String getFallDeathSuffix() {
      return this.fallDeathSuffix;
   }

   public IChatComponent getDamageSrcDisplayName() {
      return this.getDamageSrc().getEntity() == null ? null : this.getDamageSrc().getEntity().getDisplayName();
   }

   public float getDamageAmount() {
      return this.damageSrc == DamageSource.outOfWorld ? Float.MAX_VALUE : this.fallDistance;
   }
}
