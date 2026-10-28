package net.minecraft.util;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom.Item;

public class WeightedRandomFishable extends Item {
    public ItemStack returnStack;
    public float maxDamagePercent;
    public boolean enchantable;

    public WeightedRandomFishable(ItemStack itemStack, int n) {
        super(n);
        this.returnStack = itemStack;
    }

    public ItemStack getItemStack(Random random) {
        ItemStack itemStack = this.returnStack.copy();
        if (this.maxDamagePercent > 0.0F) {
            int n = (int)(this.maxDamagePercent * this.returnStack.getMaxDurability());
            int n2 = itemStack.getMaxDurability() - random.nextInt(random.nextInt(n) + 1);
            if (n2 > n) {
                n2 = n;
            }

            if (n2 < 1) {
                n2 = 1;
            }

            itemStack.setMetadata(n2);
        }

        if (this.enchantable) {
            EnchantmentHelper.addRandomEnchantment(random, itemStack, 30);
        }

        return itemStack;
    }

    public WeightedRandomFishable setMaxDamagePercent(float f) {
        this.maxDamagePercent = f;
        return this;
    }

    public WeightedRandomFishable setEnchantable() {
        this.enchantable = true;
        return this;
    }
}
