package net.minecraft.enchantment;

public class EnchantmentWaterWorker extends Enchantment {
    public EnchantmentWaterWorker(int n, int n2) {
        super(n, n2, EnumEnchantmentType.armor_head);
        this.setName("waterWorker");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 1;
    }

    @Override
    public int getMaxEnchantability(int n) {
        return this.getMinEnchantability(n) + 40;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
