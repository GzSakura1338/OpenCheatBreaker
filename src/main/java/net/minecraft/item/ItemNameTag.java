package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ItemNameTag extends Item {
    public ItemNameTag() {
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if (!stack.hasDisplayName()) {
            return false;
        } else if (target instanceof EntityLiving) {
            EntityLiving target2 = (EntityLiving)target;
            target2.setCustomNameTag(stack.getDisplayName());
            target2.enablePersistence();
            stack.stackSize--;
            return true;
        } else {
            return super.itemInteractionForEntity(stack, player, target);
        }
    }
}
