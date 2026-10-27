package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CombatTracker {
   public List<CombatEntry> combatEntries = Lists.newArrayList();
   public EntityLivingBase fighter;
   public int ageOnLastDamage;
   public int ageOnLastAttacked;
   public int ageOnLastUpdate;
   public boolean recentlyAttacked;
   public boolean hasDamage;
   public String fallDeathSuffix;

   public CombatTracker(EntityLivingBase fighterIn) {
      this.fighter = fighterIn;
   }

   public void setFallDeathSuffix() {
      this.clearFallDeathSuffix();
      if (this.fighter.isOnLadder()) {
         Block block = this.fighter
            .worldObj
            .getBlockState(new BlockPos(this.fighter.posX, this.fighter.getEntityBoundingBox().minY, this.fighter.posZ))
            .getBlock();
         if (block == Blocks.ladder) {
            this.fallDeathSuffix = "ladder";
         } else if (block == Blocks.vine) {
            this.fallDeathSuffix = "vines";
         }
      } else if (this.fighter.isInWater()) {
         this.fallDeathSuffix = "water";
      }
   }

   public void trackDamage(DamageSource damageSrc, float healthIn, float damageAmount) {
      this.reset();
      this.setFallDeathSuffix();
      CombatEntry combatEntry = new CombatEntry(damageSrc, this.fighter.ticksExisted, healthIn, damageAmount, this.fallDeathSuffix, this.fighter.fallDistance);
      this.combatEntries.add(combatEntry);
      this.ageOnLastDamage = this.fighter.ticksExisted;
      this.hasDamage = true;
      if (combatEntry.isLivingDamageSrc() && !this.recentlyAttacked && this.fighter.isEntityAlive()) {
         this.recentlyAttacked = true;
         this.ageOnLastAttacked = this.fighter.ticksExisted;
         this.ageOnLastUpdate = this.ageOnLastAttacked;
         this.fighter.sendEnterCombat();
      }
   }

   public IChatComponent getDeathMessage() {
      if (this.combatEntries.size() == 0) {
         return new ChatComponentTranslation("death.attack.generic", this.fighter.getDisplayName());
      }

      CombatEntry biggestFall = this.getBiggestFall();
      CombatEntry combatEntry = this.combatEntries.get(this.combatEntries.size() - 1);
      IChatComponent damageSrcDisplayName = combatEntry.getDamageSrcDisplayName();
      Entity entity = combatEntry.getDamageSrc().getEntity();
      IChatComponent chatComponent;
      if (biggestFall != null && combatEntry.getDamageSrc() == DamageSource.fall) {
         IChatComponent damageSrcDisplayName2 = biggestFall.getDamageSrcDisplayName();
         if (biggestFall.getDamageSrc() == DamageSource.fall || biggestFall.getDamageSrc() == DamageSource.outOfWorld) {
            chatComponent = new ChatComponentTranslation("death.fell.accident." + this.getFallDeathSuffix(biggestFall), this.fighter.getDisplayName());
         } else if (damageSrcDisplayName2 != null && (damageSrcDisplayName == null || !damageSrcDisplayName2.equals(damageSrcDisplayName))) {
            Entity entity2 = biggestFall.getDamageSrc().getEntity();
            ItemStack itemStack = entity2 instanceof EntityLivingBase ? ((EntityLivingBase)entity2).getHeldItem() : null;
            if (itemStack != null && itemStack.hasDisplayName()) {
               chatComponent = new ChatComponentTranslation("death.fell.assist.item", this.fighter.getDisplayName(), damageSrcDisplayName2, itemStack.getChatComponent());
            } else {
               chatComponent = new ChatComponentTranslation("death.fell.assist", this.fighter.getDisplayName(), damageSrcDisplayName2);
            }
         } else if (damageSrcDisplayName != null) {
            ItemStack itemStack = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHeldItem() : null;
            if (itemStack != null && itemStack.hasDisplayName()) {
               chatComponent = new ChatComponentTranslation("death.fell.finish.item", this.fighter.getDisplayName(), damageSrcDisplayName, itemStack.getChatComponent());
            } else {
               chatComponent = new ChatComponentTranslation("death.fell.finish", this.fighter.getDisplayName(), damageSrcDisplayName);
            }
         } else {
            chatComponent = new ChatComponentTranslation("death.fell.killer", this.fighter.getDisplayName());
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

      for (CombatEntry combatEntry : this.combatEntries) {
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
         CombatEntry combatEntry3 = this.combatEntries.get(i);
         CombatEntry combatEntry4 = i > 0 ? this.combatEntries.get(i - 1) : null;
         if ((combatEntry3.getDamageSrc() == DamageSource.fall || combatEntry3.getDamageSrc() == DamageSource.outOfWorld)
            && combatEntry3.getDamageAmount() > 0.0F
            && (combatEntry == null || combatEntry3.getDamageAmount() > f)) {
            if (i > 0) {
               combatEntry = combatEntry4;
            } else {
               combatEntry = combatEntry3;
            }

            f = combatEntry3.getDamageAmount();
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

   public int getTimeSinceLastAttack() {
      return this.recentlyAttacked ? this.fighter.ticksExisted - this.ageOnLastAttacked : this.ageOnLastUpdate - this.ageOnLastAttacked;
   }

   public void clearFallDeathSuffix() {
      this.fallDeathSuffix = null;
   }

   public void reset() {
      int n = this.recentlyAttacked ? 300 : 100;
      if (this.hasDamage && (!this.fighter.isEntityAlive() || this.fighter.ticksExisted - this.ageOnLastDamage > n)) {
         boolean recentlyAttacked = this.recentlyAttacked;
         this.hasDamage = false;
         this.recentlyAttacked = false;
         this.ageOnLastUpdate = this.fighter.ticksExisted;
         if (recentlyAttacked) {
            this.fighter.sendEndCombat();
         }

         this.combatEntries.clear();
      }
   }

   public EntityLivingBase getFighter() {
      return this.fighter;
   }
}
