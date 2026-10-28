package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;

public class ItemEnchantedBook extends Item {
    @Override
    public boolean hasEffect(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean isItemTool(ItemStack itemStack) {
        return false;
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        return this.getEnchantmentNbt(itemStack).tagCount() > 0 ? EnumRarity.uncommon : super.getRarity(itemStack);
    }

    public NBTTagList getEnchantmentNbt(ItemStack itemStack) {
        return itemStack.stackTagCompound != null && itemStack.stackTagCompound.hasKey("StoredEnchantments", 9)
            ? (NBTTagList)itemStack.stackTagCompound.getTag("StoredEnchantments")
            : new NBTTagList();
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean flag) {
        super.addInformation(itemStack, entityPlayer, list, flag);
        NBTTagList enchantmentNbt = this.getEnchantmentNbt(itemStack);
        if (enchantmentNbt != null) {
            for (int i = 0; i < enchantmentNbt.tagCount(); i++) {
                short short2 = enchantmentNbt.getCompoundTagAt(i).getShort("id");
                short short3 = enchantmentNbt.getCompoundTagAt(i).getShort("lvl");
                if (Enchantment.enchantmentsList[short2] != null) {
                    list.add(Enchantment.enchantmentsList[short2].getTranslatedName(short3));
                }
            }
        }
    }

    public void addEnchantment(ItemStack itemStack, EnchantmentData enchantmentData) {
        NBTTagList enchantmentNbt = this.getEnchantmentNbt(itemStack);
        boolean flag = true;

        for (int i = 0; i < enchantmentNbt.tagCount(); i++) {
            NBTTagCompound compoundTagAt = enchantmentNbt.getCompoundTagAt(i);
            if (compoundTagAt.getShort("id") == enchantmentData.enchantmentobj.effectId) {
                if (compoundTagAt.getShort("lvl") < enchantmentData.enchantmentLevel) {
                    compoundTagAt.setShort("lvl", (short)enchantmentData.enchantmentLevel);
                }

                flag = false;
                break;
            }
        }

        if (flag) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setShort("id", (short)enchantmentData.enchantmentobj.effectId);
            nbttagCompound.setShort("lvl", (short)enchantmentData.enchantmentLevel);
            enchantmentNbt.appendTag(nbttagCompound);
        }

        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        itemStack.getTagCompound().setTag("StoredEnchantments", enchantmentNbt);
    }

    public ItemStack getEnchantedItemStack(EnchantmentData enchantmentData) {
        ItemStack itemStack = new ItemStack(this);
        this.addEnchantment(itemStack, enchantmentData);
        return itemStack;
    }

    public void getEnchantments(Enchantment enchantment, List list) {
        for (int minLevel = enchantment.getMinLevel(); minLevel <= enchantment.getMaxLevel(); minLevel++) {
            list.add(this.getEnchantedItemStack(new EnchantmentData(enchantment, minLevel)));
        }
    }

    public WeightedRandomChestContent getLootTable(Random random) {
        return this.getLootTable(random, 1, 1, 1);
    }

    public WeightedRandomChestContent getLootTable(Random random, int n, int n2, int n3) {
        ItemStack itemStack = new ItemStack(Items.book, 1, 0);
        EnchantmentHelper.addRandomEnchantment(random, itemStack, 30);
        return new WeightedRandomChestContent(itemStack, n, n2, n3);
    }
}
