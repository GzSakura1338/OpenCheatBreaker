package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemAppleGold extends ItemFood {
    public ItemAppleGold(int n, float f, boolean flag) {
        super(n, f, flag);
        this.setHasSubtypes(true);
    }

    @Override
    public boolean hasEffect(ItemStack itemStack) {
        return itemStack.getMetadata() > 0;
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        return itemStack.getMetadata() == 0 ? EnumRarity.rare : EnumRarity.epic;
    }

    @Override
    public void onFoodEaten(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (!world.isRemote) {
            entityPlayer.addPotionEffect(new PotionEffect(Potion.absorption.id, 2400, 0));
        }

        if (itemStack.getMetadata() > 0) {
            if (!world.isRemote) {
                entityPlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, 600, 4));
                entityPlayer.addPotionEffect(new PotionEffect(Potion.resistance.id, 6000, 0));
                entityPlayer.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 6000, 0));
            }
        } else {
            super.onFoodEaten(itemStack, world, entityPlayer);
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }
}
