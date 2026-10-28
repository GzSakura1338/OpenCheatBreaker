package net.minecraft.enchantment;

public class EnchantmentFishingSpeed extends Enchantment {
    public EnchantmentFishingSpeed(int n, int n2, EnumEnchantmentType enumEnchantmentType) {
        super(n, n2, enumEnchantmentType);
        this.setName("fishingSpeed");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 15 + (n - 1) * 9;
    }

    @Override
    public int getMaxEnchantability(int n) {
        return super.getMinEnchantability(n) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
