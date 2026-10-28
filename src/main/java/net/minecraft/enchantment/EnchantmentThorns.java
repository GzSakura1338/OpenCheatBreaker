package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class EnchantmentThorns extends Enchantment {
    public EnchantmentThorns(int n, int n2) {
        super(n, n2, EnumEnchantmentType.armor_torso);
        this.setName("thorns");
    }

    @Override
    public int getMinEnchantability(int n) {
        return 10 + 20 * (n - 1);
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
        return itemStack.getItem() instanceof ItemArmor || super.canApply(itemStack);
    }

    @Override
    public void handleEntityLivingBase(EntityLivingBase entityLivingBase, Entity entity, int n) {
        Random rNG = entityLivingBase.getRNG();
        ItemStack chooseEquipmentWith = EnchantmentHelper.chooseEquipmentWith(Enchantment.thorns, entityLivingBase);
        if (shouldDamageAttacker(n, rNG)) {
            entity.attackEntityFrom(DamageSource.causeThornsDamage(entityLivingBase), getDamageAmount(n, rNG));
            entity.playSound("damage.thorns", 0.5F, 1.0F);
            if (chooseEquipmentWith != null) {
                chooseEquipmentWith.damageItem(3, entityLivingBase);
            }
        } else if (chooseEquipmentWith != null) {
            chooseEquipmentWith.damageItem(1, entityLivingBase);
        }
    }

    public static boolean shouldDamageAttacker(int n, Random random) {
        return n > 0 && random.nextFloat() < 0.15F * n;
    }

    public static int getDamageAmount(int n, Random random) {
        return n > 10 ? n - 10 : 1 + random.nextInt(4);
    }
}
