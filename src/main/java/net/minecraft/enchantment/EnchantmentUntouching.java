package net.minecraft.enchantment;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EnchantmentUntouching extends Enchantment {
    public EnchantmentUntouching(int n, int n2) {
        super(n, n2, EnumEnchantmentType.digger);
        this.setName("untouching");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 15;
    }

    @Override
    public int getMaxEnchantability(int n) {
        return super.getMinEnchantability(n) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return super.canApplyTogether(enchantment) && enchantment.effectId != fortune.effectId;
    }

    @Override
    public boolean canApply(ItemStack itemStack) {
        return itemStack.getItem() == Items.shears || super.canApply(itemStack);
    }
}
