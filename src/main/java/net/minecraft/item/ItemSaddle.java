package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;

public class ItemSaddle extends Item {
    public ItemSaddle() {
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if (target instanceof EntityPig) {
            EntityPig target2 = (EntityPig)target;
            if (!target2.getSaddled() && !target2.isChild()) {
                target2.setSaddled(true);
                target2.worldObj.playSoundAtEntity(target2, "mob.horse.leather", 0.5F, 1.0F);
                stack.stackSize--;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        this.itemInteractionForEntity(stack, null, entityLivingBase);
        return true;
    }
}
