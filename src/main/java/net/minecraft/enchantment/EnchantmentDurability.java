package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class EnchantmentDurability extends Enchantment {
    public EnchantmentDurability(int n, int n2) {
        super(n, n2, EnumEnchantmentType.breakable);
        this.setName("durability");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 5 + (n - 1) * 8;
    }

    @Override
    public int getMaxEnchantability(int n) {
        return super.getMinEnchantability(n) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApply(ItemStack itemStack) {
        return itemStack.isItemStackDamageable() || super.canApply(itemStack);
    }

    public static boolean negateDamage(ItemStack itemStack, int n, Random random) {
        return (!(itemStack.getItem() instanceof ItemArmor) || !(random.nextFloat() < 0.6F)) && random.nextInt(n + 1) > 0;
    }
}
