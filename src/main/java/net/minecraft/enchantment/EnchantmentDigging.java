package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EnchantmentDigging extends Enchantment {
    public EnchantmentDigging(int n, int n2) {
        super(n, n2, EnumEnchantmentType.digger);
        this.setName("digging");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 1 + 10 * (n - 1);
    }

    @Override
    public int getMaxEnchantability(int n) {
        return super.getMinEnchantability(n) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canApply(ItemStack itemStack) {
        return itemStack.getItem() == Items.shears || super.canApply(itemStack);
    }
}
