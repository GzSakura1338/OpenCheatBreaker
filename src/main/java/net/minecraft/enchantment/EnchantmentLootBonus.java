package net.minecraft.enchantment;

public class EnchantmentLootBonus extends Enchantment {
    public EnchantmentLootBonus(int n, int n2, EnumEnchantmentType enumEnchantmentType) {
        super(n, n2, enumEnchantmentType);
        if (enumEnchantmentType == EnumEnchantmentType.digger) {
            this.setName("lootBonusDigger");
        } else if (enumEnchantmentType == EnumEnchantmentType.fishing_rod) {
            this.setName("lootBonusFishing");
        } else {
            this.setName("lootBonus");
        }
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

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return super.canApplyTogether(enchantment) && enchantment.effectId != silkTouch.effectId;
    }
}
