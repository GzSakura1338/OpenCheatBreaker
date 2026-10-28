package net.minecraft.util;

import net.minecraft.entity.EntityLivingBase;

public class CombatEntry {
    public DamageSource damageSrc;
    public int entityAge;
    public float damage;
    public float entityHealth;
    public String fallDeathSuffix;
    public float fallDistance;

    public CombatEntry(DamageSource damageSource, int n, float f, float f2, String str, float f3) {
        this.damageSrc = damageSource;
        this.entityAge = n;
        this.damage = f2;
        this.entityHealth = f;
        this.fallDeathSuffix = str;
        this.fallDistance = f3;
    }

    public DamageSource getDamageSrc() {
        return this.damageSrc;
    }

    public float getDamage() {
        return this.damage;
    }

    public boolean isAttackerLiving() {
        return this.damageSrc.getEntity() instanceof EntityLivingBase;
    }

    public String getFallDeathSuffix() {
        return this.fallDeathSuffix;
    }

    public IChatComponent getAttackerName() {
        return this.getDamageSrc().getEntity() == null ? null : this.getDamageSrc().getEntity().getFormattedCommandSenderName();
    }

    public float getFallDistance() {
        return this.damageSrc == DamageSource.outOfWorld ? Float.MAX_VALUE : this.fallDistance;
    }
}
