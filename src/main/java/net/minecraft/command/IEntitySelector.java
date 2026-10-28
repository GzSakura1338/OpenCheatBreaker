package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IEntitySelector {
    IEntitySelector selectAnything = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity.isEntityAlive();
        }
    };
    IEntitySelector INSTANCE = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity.isEntityAlive() && entity.riddenByEntity == null && entity.ridingEntity == null;
        }
    };
    IEntitySelector selectInventories = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity instanceof IInventory && entity.isEntityAlive();
        }
    };

    boolean isEntityApplicable(Entity entity);

    class ArmoredMob implements IEntitySelector {
        public ItemStack stack;

        public ArmoredMob(ItemStack itemStack) {
            this.stack = itemStack;
        }

        @Override
        public boolean isEntityApplicable(Entity entity) {
            if (!entity.isEntityAlive()) {
                return false;
            }

            if (!(entity instanceof EntityLivingBase)) {
                return false;
            }

            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            return entityLivingBase.getEquipmentInSlot(EntityLiving.getArmorPosition(this.stack)) == null
                && (entityLivingBase instanceof EntityLiving ? ((EntityLiving)entityLivingBase).canPickUpLoot() : entityLivingBase instanceof EntityPlayer);
        }
    }
}
