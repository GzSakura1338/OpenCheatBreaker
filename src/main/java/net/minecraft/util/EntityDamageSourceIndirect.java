package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource {
   public Entity indirectEntity;

   public EntityDamageSourceIndirect(String damageTypeIn, Entity source, Entity indirectEntityIn) {
      super(damageTypeIn, source);
      this.indirectEntity = indirectEntityIn;
   }

   @Override
   public Entity getSourceOfDamage() {
      return this.damageSourceEntity;
   }

   @Override
   public Entity getEntity() {
      return this.indirectEntity;
   }

   @Override
   public IChatComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
      IChatComponent chatComponent = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
      ItemStack itemStack = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity).getHeldItem() : null;
      String str = "death.attack." + this.damageType;
      String str2 = str + ".item";
      return itemStack != null && itemStack.hasDisplayName() && StatCollector.canTranslate(str2)
         ? new ChatComponentTranslation(str2, entityLivingBaseIn.getDisplayName(), chatComponent, itemStack.getChatComponent())
         : new ChatComponentTranslation(str, entityLivingBaseIn.getDisplayName(), chatComponent);
   }
}
