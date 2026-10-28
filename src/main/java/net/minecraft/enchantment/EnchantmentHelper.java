package net.minecraft.enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandom;

public class EnchantmentHelper {
    public static Random enchantmentRand = new Random();
    public static EnchantmentHelper.ModifierDamage enchantmentModifierDamage = new EnchantmentHelper.ModifierDamage(null);
    public static EnchantmentHelper.ModifierLiving enchantmentModifierLiving = new EnchantmentHelper.ModifierLiving(null);
    public static EnchantmentHelper.HurtIterator hurtIterator = new EnchantmentHelper.HurtIterator(null);
    public static EnchantmentHelper.DamageIterator damageIterator = new EnchantmentHelper.DamageIterator(null);

    public static int getEnchantmentLevel(int n, ItemStack itemStack) {
        if (itemStack == null) {
            return 0;
        }

        NBTTagList enchantmentTagList = itemStack.getEnchantmentTagList();
        if (enchantmentTagList == null) {
            return 0;
        }

        for (int i = 0; i < enchantmentTagList.tagCount(); i++) {
            short short2 = enchantmentTagList.getCompoundTagAt(i).getShort("id");
            short short3 = enchantmentTagList.getCompoundTagAt(i).getShort("lvl");
            if (short2 == n) {
                return short3;
            }
        }

        return 0;
    }

    public static Map getEnchantments(ItemStack itemStack) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        NBTTagList nbttagList = itemStack.getItem() == Items.enchanted_book ? Items.enchanted_book.getEnchantmentNbt(itemStack) : itemStack.getEnchantmentTagList();
        if (nbttagList != null) {
            for (int i = 0; i < nbttagList.tagCount(); i++) {
                short short2 = nbttagList.getCompoundTagAt(i).getShort("id");
                short short3 = nbttagList.getCompoundTagAt(i).getShort("lvl");
                linkedHashMap.put(Integer.valueOf(short2), Integer.valueOf(short3));
            }
        }

        return linkedHashMap;
    }

    public static void setEnchantments(Map map, ItemStack itemStack) {
        NBTTagList nbttagList = new NBTTagList();

        for (int n : (Iterable<Integer>)map.keySet()) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setShort("id", (short)n);
            nbttagCompound.setShort("lvl", (short)((Integer)map.get(n)).intValue());
            nbttagList.appendTag(nbttagCompound);
            if (itemStack.getItem() == Items.enchanted_book) {
                Items.enchanted_book.addEnchantment(itemStack, new EnchantmentData(n, (Integer)map.get(n)));
            }
        }

        if (nbttagList.tagCount() > 0) {
            if (itemStack.getItem() != Items.enchanted_book) {
                itemStack.setTagInfo("ench", nbttagList);
            }
        } else if (itemStack.hasTagCompound()) {
            itemStack.getTagCompound().removeTag("ench");
        }
    }

    public static int getMaxEnchantmentLevel(int n, ItemStack[] itemStacks) {
        if (itemStacks == null) {
            return 0;
        }

        int n2 = 0;
        ItemStack[] itemStacks2 = itemStacks;
        int length = itemStacks.length;

        for (int i = 0; i < length; i++) {
            ItemStack itemStack = itemStacks2[i];
            int enchantmentLevel = getEnchantmentLevel(n, itemStack);
            if (enchantmentLevel > n2) {
                n2 = enchantmentLevel;
            }
        }

        return n2;
    }

    public static void applyEnchantmentModifier(EnchantmentHelper.IModifier modifier, ItemStack itemStack) {
        if (itemStack != null) {
            NBTTagList enchantmentTagList = itemStack.getEnchantmentTagList();
            if (enchantmentTagList != null) {
                for (int i = 0; i < enchantmentTagList.tagCount(); i++) {
                    short short2 = enchantmentTagList.getCompoundTagAt(i).getShort("id");
                    short short3 = enchantmentTagList.getCompoundTagAt(i).getShort("lvl");
                    if (Enchantment.enchantmentsList[short2] != null) {
                        modifier.calculateModifier(Enchantment.enchantmentsList[short2], short3);
                    }
                }
            }
        }
    }

    public static void applyEnchantmentModifierArray(EnchantmentHelper.IModifier modifier, ItemStack[] itemStacks) {
        ItemStack[] itemStacks2 = itemStacks;
        int length = itemStacks.length;

        for (int i = 0; i < length; i++) {
            ItemStack itemStack = itemStacks2[i];
            applyEnchantmentModifier(modifier, itemStack);
        }
    }

    public static int getEnchantmentModifierDamage(ItemStack[] itemStacks, DamageSource damageSource) {
        enchantmentModifierDamage.damageModifier = 0;
        enchantmentModifierDamage.source = damageSource;
        applyEnchantmentModifierArray(enchantmentModifierDamage, itemStacks);
        if (enchantmentModifierDamage.damageModifier > 25) {
            enchantmentModifierDamage.damageModifier = 25;
        }

        return (enchantmentModifierDamage.damageModifier + 1 >> 1) + enchantmentRand.nextInt((enchantmentModifierDamage.damageModifier >> 1) + 1);
    }

    public static float getEnchantmentModifierLiving(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        return getAttackDamage(entityLivingBase.getHeldItem(), entityLivingBase2.getCreatureAttribute());
    }

    public static float getAttackDamage(ItemStack itemStack, EnumCreatureAttribute enumCreatureAttribute) {
        enchantmentModifierLiving.livingModifier = 0.0F;
        enchantmentModifierLiving.entityLiving = enumCreatureAttribute;
        applyEnchantmentModifier(enchantmentModifierLiving, itemStack);
        return enchantmentModifierLiving.livingModifier;
    }

    public static void onUserDamaged(EntityLivingBase entityLivingBase, Entity entity) {
        hurtIterator.entity = entity;
        hurtIterator.entityLivingBase = entityLivingBase;
        applyEnchantmentModifierArray(hurtIterator, entityLivingBase.getInventory());
        if (entity instanceof EntityPlayer) {
            applyEnchantmentModifier(hurtIterator, entityLivingBase.getHeldItem());
        }
    }

    public static void onTargetDamaged(EntityLivingBase entityLivingBase, Entity entity) {
        damageIterator.entityLivingBase = entityLivingBase;
        damageIterator.entity = entity;
        applyEnchantmentModifierArray(damageIterator, entityLivingBase.getInventory());
        if (entityLivingBase instanceof EntityPlayer) {
            applyEnchantmentModifier(damageIterator, entityLivingBase.getHeldItem());
        }
    }

    public static int getKnockbackModifier(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        return getEnchantmentLevel(Enchantment.knockback.effectId, entityLivingBase.getHeldItem());
    }

    public static int getFireAspectModifier(EntityLivingBase entityLivingBase) {
        return getEnchantmentLevel(Enchantment.fireAspect.effectId, entityLivingBase.getHeldItem());
    }

    public static int getRespiration(EntityLivingBase entityLivingBase) {
        return getMaxEnchantmentLevel(Enchantment.respiration.effectId, entityLivingBase.getInventory());
    }

    public static int getEfficiencyModifier(EntityLivingBase entityLivingBase) {
        return getEnchantmentLevel(Enchantment.efficiency.effectId, entityLivingBase.getHeldItem());
    }

    public static boolean getSilkTouchModifier(EntityLivingBase entityLivingBase) {
        return getEnchantmentLevel(Enchantment.silkTouch.effectId, entityLivingBase.getHeldItem()) > 0;
    }

    public static int getFortuneModifier(EntityLivingBase entityLivingBase) {
        return getEnchantmentLevel(Enchantment.fortune.effectId, entityLivingBase.getHeldItem());
    }

    public static int getLuckOfTheSea(EntityLivingBase entityLivingBase) {
        return getEnchantmentLevel(Enchantment.luckOfTheSea.effectId, entityLivingBase.getHeldItem());
    }

    public static int getLure(EntityLivingBase entityLivingBase) {
        return getEnchantmentLevel(Enchantment.lure.effectId, entityLivingBase.getHeldItem());
    }

    public static int getLootingModifier(EntityLivingBase entityLivingBase) {
        return getEnchantmentLevel(Enchantment.looting.effectId, entityLivingBase.getHeldItem());
    }

    public static boolean getAquaAffinityModifier(EntityLivingBase entityLivingBase) {
        return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, entityLivingBase.getInventory()) > 0;
    }

    public static ItemStack chooseEquipmentWith(Enchantment enchantment, EntityLivingBase entityLivingBase) {
        for (ItemStack itemStack : entityLivingBase.getInventory()) {
            if (itemStack != null && getEnchantmentLevel(enchantment.effectId, itemStack) > 0) {
                return itemStack;
            }
        }

        return null;
    }

    public static int calcItemStackEnchantability(Random random, int n, int n2, ItemStack itemStack) {
        Item item = itemStack.getItem();
        int itemEnchantability = item.getItemEnchantability();
        if (itemEnchantability <= 0) {
            return 0;
        }

        if (n2 > 15) {
            n2 = 15;
        }

        int n3 = random.nextInt(8) + 1 + (n2 >> 1) + random.nextInt(n2 + 1);
        return n == 0 ? Math.max(n3 / 3, 1) : (n == 1 ? n3 * 2 / 3 + 1 : Math.max(n3, n2 * 2));
    }

    public static ItemStack addRandomEnchantment(Random random, ItemStack itemStack, int n) {
        List buildEnchantmentList2 = buildEnchantmentList(random, itemStack, n);
        boolean flag = itemStack.getItem() == Items.book;
        if (flag) {
            itemStack.setItem(Items.enchanted_book);
        }

        if (buildEnchantmentList2 != null) {
            for (EnchantmentData enchantmentData : (Iterable<EnchantmentData>)buildEnchantmentList2) {
                if (flag) {
                    Items.enchanted_book.addEnchantment(itemStack, enchantmentData);
                } else {
                    itemStack.addEnchantment(enchantmentData.enchantmentobj, enchantmentData.enchantmentLevel);
                }
            }
        }

        return itemStack;
    }

    public static List buildEnchantmentList(Random random, ItemStack itemStack, int n) {
        Item item = itemStack.getItem();
        int itemEnchantability = item.getItemEnchantability();
        if (itemEnchantability <= 0) {
            return null;
        }

        itemEnchantability /= 2;
        itemEnchantability = 1 + random.nextInt((itemEnchantability >> 1) + 1) + random.nextInt((itemEnchantability >> 1) + 1);
        int n2 = itemEnchantability + n;
        float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
        int n3 = (int)(n2 * (1.0F + f) + 0.5F);
        if (n3 < 1) {
            n3 = 1;
        }

        ArrayList list = null;
        Map mapEnchantmentData2 = mapEnchantmentData(n3, itemStack);
        if (mapEnchantmentData2 != null && !mapEnchantmentData2.isEmpty()) {
            EnchantmentData randomItem = (EnchantmentData)WeightedRandom.getRandomItem(random, mapEnchantmentData2.values());
            if (randomItem != null) {
                list = new ArrayList();
                list.add(randomItem);

                for (int i = n3; random.nextInt(50) <= i; i >>= 1) {
                    Iterator iterator = mapEnchantmentData2.keySet().iterator();

                    while (iterator.hasNext()) {
                        Integer integer = (Integer)iterator.next();
                        boolean flag = true;

                        for (EnchantmentData enchantmentData : (Iterable<EnchantmentData>)list) {
                            if (!enchantmentData.enchantmentobj.canApplyTogether(Enchantment.enchantmentsList[integer])) {
                                flag = false;
                                break;
                            }
                        }

                        if (!flag) {
                            iterator.remove();
                        }
                    }

                    if (!mapEnchantmentData2.isEmpty()) {
                        EnchantmentData randomItem2 = (EnchantmentData)WeightedRandom.getRandomItem(random, mapEnchantmentData2.values());
                        list.add(randomItem2);
                    }
                }
            }
        }

        return list;
    }

    public static Map mapEnchantmentData(int n, ItemStack itemStack) {
        Item item = itemStack.getItem();
        HashMap map = null;
        boolean flag = itemStack.getItem() == Items.book;

        for (Enchantment enchantment : Enchantment.enchantmentsList) {
            if (enchantment != null && (enchantment.type.canEnchantItem(item) || flag)) {
                for (int minLevel = enchantment.getMinLevel(); minLevel <= enchantment.getMaxLevel(); minLevel++) {
                    if (n >= enchantment.getMinEnchantability(minLevel) && n <= enchantment.getMaxEnchantability(minLevel)) {
                        if (map == null) {
                            map = new HashMap();
                        }

                        map.put(enchantment.effectId, new EnchantmentData(enchantment, minLevel));
                    }
                }
            }
        }

        return map;
    }

    public static class DamageIterator implements EnchantmentHelper.IModifier {
        public EntityLivingBase entityLivingBase;
        public Entity entity;

        public DamageIterator() {
        }

        @Override
        public void calculateModifier(Enchantment enchantment, int n) {
            enchantment.handleEntityLivingBase2(this.entityLivingBase, this.entity, n);
        }

        public DamageIterator(Object obj) {
            this();
        }
    }

    public static class HurtIterator implements EnchantmentHelper.IModifier {
        public EntityLivingBase entityLivingBase;
        public Entity entity;

        public HurtIterator() {
        }

        @Override
        public void calculateModifier(Enchantment enchantment, int n) {
            enchantment.handleEntityLivingBase(this.entityLivingBase, this.entity, n);
        }

        public HurtIterator(Object obj) {
            this();
        }
    }

    public interface IModifier {
        void calculateModifier(Enchantment enchantment, int n);
    }

    public static class ModifierDamage implements EnchantmentHelper.IModifier {
        public int damageModifier;
        public DamageSource source;

        public ModifierDamage() {
        }

        @Override
        public void calculateModifier(Enchantment enchantment, int n) {
            this.damageModifier = this.damageModifier + enchantment.calcModifierDamage(n, this.source);
        }

        public ModifierDamage(Object obj) {
            this();
        }
    }

    public static class ModifierLiving implements EnchantmentHelper.IModifier {
        public float livingModifier;
        public EnumCreatureAttribute entityLiving;

        public ModifierLiving() {
        }

        @Override
        public void calculateModifier(Enchantment enchantment, int n) {
            this.livingModifier = this.livingModifier + enchantment.computeFloat(n, this.entityLiving);
        }

        public ModifierLiving(Object obj) {
            this();
        }
    }
}
