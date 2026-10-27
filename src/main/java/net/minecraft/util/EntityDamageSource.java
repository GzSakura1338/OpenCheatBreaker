package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityDamageSource extends DamageSource {
   public boolean isThornsDamage = false;
   public Entity damageSourceEntity;

   public EntityDamageSource(String damageTypeIn, Entity damageSourceEntityIn) {
      super(damageTypeIn);
      this.damageSourceEntity = damageSourceEntityIn;
   }

   public EntityDamageSource setIsThornsDamage() {
      this.isThornsDamage = true;
      return this;
   }

   public boolean getIsThornsDamage() {
      return this.isThornsDamage;
   }

   @Override
   public Entity getEntity() {
      return this.damageSourceEntity;
   }

   @Override
   public IChatComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
      ItemStack itemStack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.damageSourceEntity).getHeldItem() : null;
      String str = "death.attack." + this.damageType;
      String str2 = str + ".item";
      return itemStack != null && itemStack.hasDisplayName() && StatCollector.canTranslate(str2)
         ? new ChatComponentTranslation(str2, entityLivingBaseIn.getDisplayName(), this.damageSourceEntity.getDisplayName(), itemStack.getChatComponent())
         : new ChatComponentTranslation(str, entityLivingBaseIn.getDisplayName(), this.damageSourceEntity.getDisplayName());
   }

   @Override
   public boolean isDifficultyScaled() {
      return this.damageSourceEntity != null && this.damageSourceEntity instanceof EntityLivingBase && !(this.damageSourceEntity instanceof EntityPlayer);
   }
}
