package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource {
    public Entity indirectEntity;

    public EntityDamageSourceIndirect(String str, Entity entity, Entity entity2) {
        super(str, entity);
        this.indirectEntity = entity2;
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
    public IChatComponent getDeathMessage(EntityLivingBase entityLivingBase) {
        IChatComponent chatComponent = this.indirectEntity == null
            ? this.damageSourceEntity.getFormattedCommandSenderName()
            : this.indirectEntity.getFormattedCommandSenderName();
        ItemStack itemStack = this.indirectEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.indirectEntity).getHeldItem() : null;
        String str = "death.attack." + this.damageType;
        String str2 = str + ".item";
        return itemStack != null && itemStack.hasDisplayName() && StatCollector.canTranslate(str2)
            ? new ChatComponentTranslation(str2, entityLivingBase.getFormattedCommandSenderName(), chatComponent, itemStack.toHoverableText())
            : new ChatComponentTranslation(str, entityLivingBase.getFormattedCommandSenderName(), chatComponent);
    }
}
