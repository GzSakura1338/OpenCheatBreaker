package net.minecraft.util;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class EntitySelectors {
   public static Predicate<Entity> selectAnything = new Predicate<Entity>() {
      public boolean apply(Entity entity) {
         return entity.isEntityAlive();
      }
   };
   public static Predicate<Entity> IS_STANDALONE = new Predicate<Entity>() {
      public boolean apply(Entity entity) {
         return entity.isEntityAlive() && entity.riddenByEntity == null && entity.ridingEntity == null;
      }
   };
   public static Predicate<Entity> selectInventories = new Predicate<Entity>() {
      public boolean apply(Entity entity) {
         return entity instanceof IInventory && entity.isEntityAlive();
      }
   };
   public static Predicate<Entity> NOT_SPECTATING = new Predicate<Entity>() {
      public boolean apply(Entity entity) {
         return !(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator();
      }
   };

   public static class ArmoredMob implements Predicate<Entity> {
      public ItemStack armor;

      public ArmoredMob(ItemStack armor) {
         this.armor = armor;
      }

      public boolean apply(Entity entity) {
         if (!entity.isEntityAlive()) {
            return false;
         }

         if (!(entity instanceof EntityLivingBase)) {
            return false;
         }

         EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
         return entityLivingBase.getEquipmentInSlot(EntityLiving.getArmorPosition(this.armor)) != null
            ? false
            : (entityLivingBase instanceof EntityLiving ? ((EntityLiving)entityLivingBase).canPickUpLoot() : (entityLivingBase instanceof EntityArmorStand ? true : entityLivingBase instanceof EntityPlayer));
      }
   }
}
