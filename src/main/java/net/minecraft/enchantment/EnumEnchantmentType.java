package net.minecraft.enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public enum EnumEnchantmentType {
    all,
    armor,
    armor_feet,
    armor_legs,
    armor_torso,
    armor_head,
    weapon,
    digger,
    fishing_rod,
    breakable,
    bow;

    public boolean canEnchantItem(Item item) {
        if (this == all) {
            return true;
        }

        if (this == breakable && item.isDamageable()) {
            return true;
        }

        if (item instanceof ItemArmor) {
            if (this == armor) {
                return true;
            }

            ItemArmor itemArmor = (ItemArmor)item;
            return itemArmor.armorType == 0
                ? this == armor_head
                : (itemArmor.armorType == 2 ? this == armor_legs : (itemArmor.armorType == 1 ? this == armor_torso : itemArmor.armorType == 3 && this == armor_feet));
        } else {
            return item instanceof ItemSword
                ? this == weapon
                : (item instanceof ItemTool ? this == digger : (item instanceof ItemBow ? this == bow : item instanceof ItemFishingRod && this == fishing_rod));
        }
    }
}
