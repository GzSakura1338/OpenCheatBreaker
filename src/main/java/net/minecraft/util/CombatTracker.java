package net.minecraft.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CombatTracker {
    public List combatEntries = new ArrayList();
    public EntityLivingBase fighter;
    public int ageOnLastDamage;
    public int ageOnLastAttacked;
    public int ageOnLastUpdate;
    public boolean recentlyAttacked;
    public boolean hasDamage;
    public String fallDeathSuffix;

    public CombatTracker(EntityLivingBase entityLivingBase) {
        this.fighter = entityLivingBase;
    }

    public void setFallDeathSuffix() {
        this.clearFallDeathSuffix();
        if (this.fighter.isOnLadder()) {
            Block block = this.fighter
                .worldObj
                .getBlock(
                    MathHelper.floor_double(this.fighter.posX), MathHelper.floor_double(this.fighter.boundingBox.minY), MathHelper.floor_double(this.fighter.posZ)
                );
            if (block == Blocks.ladder) {
                this.fallDeathSuffix = "ladder";
            } else if (block == Blocks.vine) {
                this.fallDeathSuffix = "vines";
            }
        } else if (this.fighter.isInWater()) {
            this.fallDeathSuffix = "water";
        }
    }

    public void onDamage(DamageSource damageSource, float f, float f2) {
        this.update();
        this.setFallDeathSuffix();
        CombatEntry combatEntry = new CombatEntry(damageSource, this.fighter.ticksExisted, f, f2, this.fallDeathSuffix, this.fighter.fallDistance);
        this.combatEntries.add(combatEntry);
        this.ageOnLastDamage = this.fighter.ticksExisted;
        this.hasDamage = true;
        if (combatEntry.isAttackerLiving() && !this.recentlyAttacked && this.fighter.isEntityAlive()) {
            this.recentlyAttacked = true;
            this.ageOnLastAttacked = this.fighter.ticksExisted;
            this.ageOnLastUpdate = this.ageOnLastAttacked;
            this.fighter.enterCombat();
        }
    }

    public IChatComponent getDeathMessage() {
        if (this.combatEntries.size() == 0) {
            return new ChatComponentTranslation("death.attack.generic", this.fighter.getFormattedCommandSenderName());
        }

        CombatEntry biggestFall = this.getBiggestFall();
        CombatEntry combatEntry = (CombatEntry)this.combatEntries.get(this.combatEntries.size() - 1);
        IChatComponent attackerName = combatEntry.getAttackerName();
        Entity entity = combatEntry.getDamageSrc().getEntity();
        IChatComponent chatComponent;
        if (biggestFall != null && combatEntry.getDamageSrc() == DamageSource.fall) {
            IChatComponent attackerName2 = biggestFall.getAttackerName();
            if (biggestFall.getDamageSrc() == DamageSource.fall || biggestFall.getDamageSrc() == DamageSource.outOfWorld) {
                chatComponent = new ChatComponentTranslation("death.fell.accident." + this.getFallDeathSuffix(biggestFall), this.fighter.getFormattedCommandSenderName());
            } else if (attackerName2 != null && (attackerName == null || !attackerName2.equals(attackerName))) {
                Entity entity2 = biggestFall.getDamageSrc().getEntity();
                ItemStack itemStack = entity2 instanceof EntityLivingBase ? ((EntityLivingBase)entity2).getHeldItem() : null;
                if (itemStack != null && itemStack.hasDisplayName()) {
                    chatComponent = new ChatComponentTranslation("death.fell.assist.item", this.fighter.getFormattedCommandSenderName(), attackerName2, itemStack.toHoverableText());
                } else {
                    chatComponent = new ChatComponentTranslation("death.fell.assist", this.fighter.getFormattedCommandSenderName(), attackerName2);
                }
            } else if (attackerName != null) {
                ItemStack itemStack = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHeldItem() : null;
                if (itemStack != null && itemStack.hasDisplayName()) {
                    chatComponent = new ChatComponentTranslation("death.fell.finish.item", this.fighter.getFormattedCommandSenderName(), attackerName, itemStack.toHoverableText());
                } else {
                    chatComponent = new ChatComponentTranslation("death.fell.finish", this.fighter.getFormattedCommandSenderName(), attackerName);
                }
            } else {
                chatComponent = new ChatComponentTranslation("death.fell.killer", this.fighter.getFormattedCommandSenderName());
            }
        } else {
            chatComponent = combatEntry.getDamageSrc().getDeathMessage(this.fighter);
        }

        return chatComponent;
    }

    public EntityLivingBase getLastAttacker() {
        EntityLivingBase entityLivingBase = null;
        EntityPlayer entityPlayer = null;
        float f = 0.0F;
        float f2 = 0.0F;

        for (CombatEntry combatEntry : (Iterable<CombatEntry>)this.combatEntries) {
            if (combatEntry.getDamageSrc().getEntity() instanceof EntityPlayer && (entityPlayer == null || combatEntry.getDamage() > f2)) {
                f2 = combatEntry.getDamage();
                entityPlayer = (EntityPlayer)combatEntry.getDamageSrc().getEntity();
            }

            if (combatEntry.getDamageSrc().getEntity() instanceof EntityLivingBase && (entityLivingBase == null || combatEntry.getDamage() > f)) {
                f = combatEntry.getDamage();
                entityLivingBase = (EntityLivingBase)combatEntry.getDamageSrc().getEntity();
            }
        }

        return entityPlayer != null && f2 >= f / 3.0F ? entityPlayer : entityLivingBase;
    }

    public CombatEntry getBiggestFall() {
        CombatEntry combatEntry = null;
        CombatEntry combatEntry2 = null;
        byte b = 0;
        float f = 0.0F;

        for (int i = 0; i < this.combatEntries.size(); i++) {
            CombatEntry combatEntry3 = (CombatEntry)this.combatEntries.get(i);
            CombatEntry combatEntry4 = i > 0 ? (CombatEntry)this.combatEntries.get(i - 1) : null;
            if ((combatEntry3.getDamageSrc() == DamageSource.fall || combatEntry3.getDamageSrc() == DamageSource.outOfWorld)
                && combatEntry3.getFallDistance() > 0.0F
                && (combatEntry == null || combatEntry3.getFallDistance() > f)) {
                if (i > 0) {
                    combatEntry = combatEntry4;
                } else {
                    combatEntry = combatEntry3;
                }

                f = combatEntry3.getFallDistance();
            }

            if (combatEntry3.getFallDeathSuffix() != null && (combatEntry2 == null || combatEntry3.getDamage() > b)) {
                combatEntry2 = combatEntry3;
            }
        }

        if (f > 5.0F && combatEntry != null) {
            return combatEntry;
        } else {
            return b > 5 && combatEntry2 != null ? combatEntry2 : null;
        }
    }

    public String getFallDeathSuffix(CombatEntry combatEntry) {
        return combatEntry.getFallDeathSuffix() == null ? "generic" : combatEntry.getFallDeathSuffix();
    }

    public void clearFallDeathSuffix() {
        this.fallDeathSuffix = null;
    }

    public void update() {
        int n = this.recentlyAttacked ? 300 : 100;
        if (this.hasDamage && (!this.fighter.isEntityAlive() || this.fighter.ticksExisted - this.ageOnLastDamage > n)) {
            boolean recentlyAttacked = this.recentlyAttacked;
            this.hasDamage = false;
            this.recentlyAttacked = false;
            this.ageOnLastUpdate = this.fighter.ticksExisted;
            if (recentlyAttacked) {
                this.fighter.endCombat();
            }

            this.combatEntries.clear();
        }
    }
}
