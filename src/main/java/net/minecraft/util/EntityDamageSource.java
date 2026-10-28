package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityDamageSource extends DamageSource {
    public Entity damageSourceEntity;

    public EntityDamageSource(String str, Entity entity) {
        super(str);
        this.damageSourceEntity = entity;
    }

    @Override
    public Entity getEntity() {
        return this.damageSourceEntity;
    }

    @Override
    public IChatComponent getDeathMessage(EntityLivingBase entityLivingBase) {
        ItemStack itemStack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.damageSourceEntity).getHeldItem() : null;
        String str = "death.attack." + this.damageType;
        String str2 = str + ".item";
        return itemStack != null && itemStack.hasDisplayName() && StatCollector.canTranslate(str2)
            ? new ChatComponentTranslation(
                str2, entityLivingBase.getFormattedCommandSenderName(), this.damageSourceEntity.getFormattedCommandSenderName(), itemStack.toHoverableText()
            )
            : new ChatComponentTranslation(str, entityLivingBase.getFormattedCommandSenderName(), this.damageSourceEntity.getFormattedCommandSenderName());
    }

    @Override
    public boolean isDifficultyScaled() {
        return this.damageSourceEntity != null && this.damageSourceEntity instanceof EntityLivingBase && !(this.damageSourceEntity instanceof EntityPlayer);
    }
}
