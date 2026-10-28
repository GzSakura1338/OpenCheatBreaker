package net.minecraft.enchantment;

import net.minecraft.util.WeightedRandom.Item;

public class EnchantmentData extends Item {
    public Enchantment enchantmentobj;
    public int enchantmentLevel;

    public EnchantmentData(Enchantment enchantment, int n) {
        super(enchantment.getWeight());
        this.enchantmentobj = enchantment;
        this.enchantmentLevel = n;
    }

    public EnchantmentData(int n, int n2) {
        this(Enchantment.enchantmentsList[n], n2);
    }
}
