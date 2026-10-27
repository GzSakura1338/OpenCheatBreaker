package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import net.minecraft.util.WeightedRandom;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.DamageSource;
import java.util.Iterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import java.util.LinkedHashMap;
import net.minecraft.init.Items;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.item.ItemStack;
import java.util.Random;

public class EnchantmentHelper
{
    public static Random enchantmentRand;
    public static ModifierDamage enchantmentModifierDamage;
    public static ModifierLiving enchantmentModifierLiving;
    public static HurtIterator ENCHANTMENT_ITERATOR_HURT;
    public static DamageIterator ENCHANTMENT_ITERATOR_DAMAGE;
    
    public static int getEnchantmentLevel(final int enchID, final ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        final NBTTagList enchantmentTagList = stack.getEnchantmentTagList();
        if (enchantmentTagList == null) {
            return 0;
        }
        for (int i = 0; i < enchantmentTagList.tagCount(); ++i) {
            final short short1 = enchantmentTagList.getCompoundTagAt(i).getShort("id");
            final short short2 = enchantmentTagList.getCompoundTagAt(i).getShort("lvl");
            if (short1 == enchID) {
                return short2;
            }
        }
        return 0;
    }
    
    public static Map<Integer, Integer> getEnchantments(final ItemStack stack) {
        final LinkedHashMap<Integer, Integer> linkedHashMap = Maps.newLinkedHashMap();
        final NBTTagList list = (stack.getItem() == Items.enchanted_book) ? Items.enchanted_book.getEnchantments(stack) : stack.getEnchantmentTagList();
        if (list != null) {
            for (int i = 0; i < list.tagCount(); ++i) {
                linkedHashMap.put((int)list.getCompoundTagAt(i).getShort("id"), (int)list.getCompoundTagAt(i).getShort("lvl"));
            }
        }
        return (Map<Integer, Integer>)linkedHashMap;
    }
    
    public static void setEnchantments(final Map<Integer, Integer> enchMap, final ItemStack stack) {
        final NBTTagList value = new NBTTagList();
        for (final int intValue : enchMap.keySet()) {
            final Enchantment enchantmentById = Enchantment.getEnchantmentById(intValue);
            if (enchantmentById != null) {
                final NBTTagCompound nbt = new NBTTagCompound();
                nbt.setShort("id", (short)intValue);
                nbt.setShort("lvl", (short)(int)enchMap.get(intValue));
                value.appendTag(nbt);
                if (stack.getItem() != Items.enchanted_book) {
                    continue;
                }
                Items.enchanted_book.addEnchantment(stack, new EnchantmentData(enchantmentById, enchMap.get(intValue)));
            }
        }
        if (value.tagCount() > 0) {
            if (stack.getItem() != Items.enchanted_book) {
                stack.setTagInfo("ench", value);
            }
        }
        else if (stack.hasTagCompound()) {
            stack.getTagCompound().removeTag("ench");
        }
    }
    
    public static int getMaxEnchantmentLevel(final int enchID, final ItemStack[] stacks) {
        if (stacks == null) {
            return 0;
        }
        int n = 0;
        for (int length = stacks.length, i = 0; i < length; ++i) {
            final int enchantmentLevel = getEnchantmentLevel(enchID, stacks[i]);
            if (enchantmentLevel > n) {
                n = enchantmentLevel;
            }
        }
        return n;
    }
    
    public static void applyEnchantmentModifier(final IModifier modifier, final ItemStack stack) {
        if (stack != null) {
            final NBTTagList enchantmentTagList = stack.getEnchantmentTagList();
            if (enchantmentTagList != null) {
                for (int i = 0; i < enchantmentTagList.tagCount(); ++i) {
                    final short short1 = enchantmentTagList.getCompoundTagAt(i).getShort("id");
                    final short short2 = enchantmentTagList.getCompoundTagAt(i).getShort("lvl");
                    if (Enchantment.getEnchantmentById(short1) != null) {
                        modifier.calculateModifier(Enchantment.getEnchantmentById(short1), short2);
                    }
                }
            }
        }
    }
    
    public static void applyEnchantmentModifierArray(final IModifier modifier, final ItemStack[] stacks) {
        for (int length = stacks.length, i = 0; i < length; ++i) {
            applyEnchantmentModifier(modifier, stacks[i]);
        }
    }
    
    public static int getEnchantmentModifierDamage(final ItemStack[] stacks, final DamageSource source) {
        EnchantmentHelper.enchantmentModifierDamage.damageModifier = 0;
        EnchantmentHelper.enchantmentModifierDamage.source = source;
        applyEnchantmentModifierArray(EnchantmentHelper.enchantmentModifierDamage, stacks);
        if (EnchantmentHelper.enchantmentModifierDamage.damageModifier > 25) {
            EnchantmentHelper.enchantmentModifierDamage.damageModifier = 25;
        }
        else if (EnchantmentHelper.enchantmentModifierDamage.damageModifier < 0) {
            EnchantmentHelper.enchantmentModifierDamage.damageModifier = 0;
        }
        return (EnchantmentHelper.enchantmentModifierDamage.damageModifier + 1 >> 1) + EnchantmentHelper.enchantmentRand.nextInt((EnchantmentHelper.enchantmentModifierDamage.damageModifier >> 1) + 1);
    }
    
    public static float getModifierForCreature(final ItemStack stack, final EnumCreatureAttribute entityLiving) {
        EnchantmentHelper.enchantmentModifierLiving.livingModifier = 0.0f;
        EnchantmentHelper.enchantmentModifierLiving.entityLiving = entityLiving;
        applyEnchantmentModifier(EnchantmentHelper.enchantmentModifierLiving, stack);
        return EnchantmentHelper.enchantmentModifierLiving.livingModifier;
    }
    
    public static void applyThornEnchantments(final EntityLivingBase user, final Entity attacker) {
        EnchantmentHelper.ENCHANTMENT_ITERATOR_HURT.attacker = attacker;
        EnchantmentHelper.ENCHANTMENT_ITERATOR_HURT.user = user;
        if (user != null) {
            applyEnchantmentModifierArray(EnchantmentHelper.ENCHANTMENT_ITERATOR_HURT, user.getInventory());
        }
        if (attacker instanceof EntityPlayer) {
            applyEnchantmentModifier(EnchantmentHelper.ENCHANTMENT_ITERATOR_HURT, user.getHeldItem());
        }
    }
    
    public static void applyArthropodEnchantments(final EntityLivingBase user, final Entity target) {
        EnchantmentHelper.ENCHANTMENT_ITERATOR_DAMAGE.user = user;
        EnchantmentHelper.ENCHANTMENT_ITERATOR_DAMAGE.target = target;
        if (user != null) {
            applyEnchantmentModifierArray(EnchantmentHelper.ENCHANTMENT_ITERATOR_DAMAGE, user.getInventory());
        }
        if (user instanceof EntityPlayer) {
            applyEnchantmentModifier(EnchantmentHelper.ENCHANTMENT_ITERATOR_DAMAGE, user.getHeldItem());
        }
    }
    
    public static int getKnockbackModifier(final EntityLivingBase player) {
        return getEnchantmentLevel(Enchantment.knockback.effectId, player.getHeldItem());
    }
    
    public static int getFireAspectModifier(final EntityLivingBase player) {
        return getEnchantmentLevel(Enchantment.fireAspect.effectId, player.getHeldItem());
    }
    
    public static int getRespiration(final Entity player) {
        return getMaxEnchantmentLevel(Enchantment.respiration.effectId, player.getInventory());
    }
    
    public static int getDepthStriderModifier(final Entity player) {
        return getMaxEnchantmentLevel(Enchantment.depthStrider.effectId, player.getInventory());
    }
    
    public static int getEfficiencyModifier(final EntityLivingBase player) {
        return getEnchantmentLevel(Enchantment.efficiency.effectId, player.getHeldItem());
    }
    
    public static boolean getSilkTouchModifier(final EntityLivingBase player) {
        return getEnchantmentLevel(Enchantment.silkTouch.effectId, player.getHeldItem()) > 0;
    }
    
    public static int getFortuneModifier(final EntityLivingBase player) {
        return getEnchantmentLevel(Enchantment.fortune.effectId, player.getHeldItem());
    }
    
    public static int getLuckOfSeaModifier(final EntityLivingBase player) {
        return getEnchantmentLevel(Enchantment.luckOfTheSea.effectId, player.getHeldItem());
    }
    
    public static int getLureModifier(final EntityLivingBase player) {
        return getEnchantmentLevel(Enchantment.lure.effectId, player.getHeldItem());
    }
    
    public static int getLootingModifier(final EntityLivingBase player) {
        return getEnchantmentLevel(Enchantment.looting.effectId, player.getHeldItem());
    }
    
    public static boolean getAquaAffinityModifier(final EntityLivingBase player) {
        return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, player.getInventory()) > 0;
    }
    
    public static ItemStack getEnchantedItem(final Enchantment enchantment, final EntityLivingBase entityLivingBase) {
        for (final ItemStack stack : entityLivingBase.getInventory()) {
            if (stack != null && getEnchantmentLevel(enchantment.effectId, stack) > 0) {
                return stack;
            }
        }
        return null;
    }
    
    public static int calcItemStackEnchantability(final Random rand, final int enchantNum, int power, final ItemStack stack) {
        if (stack.getItem().getItemEnchantability() <= 0) {
            return 0;
        }
        if (power > 15) {
            power = 15;
        }
        final int a = rand.nextInt(8) + 1 + (power >> 1) + rand.nextInt(power + 1);
        return (enchantNum == 0) ? Math.max(a / 3, 1) : ((enchantNum == 1) ? (a * 2 / 3 + 1) : Math.max(a, power * 2));
    }
    
    public static ItemStack addRandomEnchantment(final Random randomIn, final ItemStack itemStack, final int n) {
        final List<EnchantmentData> buildEnchantmentList = buildEnchantmentList(randomIn, itemStack, n);
        final boolean b = itemStack.getItem() == Items.book;
        if (b) {
            itemStack.setItem(Items.enchanted_book);
        }
        if (buildEnchantmentList != null) {
            for (final EnchantmentData enchantment : buildEnchantmentList) {
                if (b) {
                    Items.enchanted_book.addEnchantment(itemStack, enchantment);
                }
                else {
                    itemStack.addEnchantment(enchantment.enchantmentobj, enchantment.enchantmentLevel);
                }
            }
        }
        return itemStack;
    }
    
    public static List<EnchantmentData> buildEnchantmentList(final Random randomIn, final ItemStack itemStackIn, final int n) {
        final int itemEnchantability = itemStackIn.getItem().getItemEnchantability();
        if (itemEnchantability <= 0) {
            return null;
        }
        final int n2 = itemEnchantability / 2;
        int n3 = (int)((1 + randomIn.nextInt((n2 >> 1) + 1) + randomIn.nextInt((n2 >> 1) + 1) + n) * (1.0f + (randomIn.nextFloat() + randomIn.nextFloat() - 1.0f) * 0.15f) + 0.5f);
        if (n3 < 1) {
            n3 = 1;
        }
        Object arrayList = null;
        final Map<Integer, EnchantmentData> mapEnchantmentData = mapEnchantmentData(n3, itemStackIn);
        if (mapEnchantmentData != null && !mapEnchantmentData.isEmpty()) {
            final EnchantmentData enchantmentData = WeightedRandom.getRandomItem(randomIn, mapEnchantmentData.values());
            if (enchantmentData != null) {
                arrayList = Lists.newArrayList();
                ((List<EnchantmentData>)arrayList).add(enchantmentData);
                for (int n4 = n3; randomIn.nextInt(50) <= n4; n4 >>= 1) {
                    final Iterator iterator = mapEnchantmentData.keySet().iterator();
                    while (iterator.hasNext()) {
                        final Integer n5 = (Integer)iterator.next();
                        boolean b = true;
                        final Iterator<EnchantmentData> iterator2 = ((List<EnchantmentData>)arrayList).iterator();
                        while (iterator2.hasNext()) {
                            if (!iterator2.next().enchantmentobj.canApplyTogether(Enchantment.getEnchantmentById(n5))) {
                                b = false;
                                break;
                            }
                        }
                        if (!b) {
                            iterator.remove();
                        }
                    }
                    if (!mapEnchantmentData.isEmpty()) {
                        ((List<EnchantmentData>)arrayList).add(WeightedRandom.getRandomItem(randomIn, mapEnchantmentData.values()));
                    }
                }
            }
        }
        return (List<EnchantmentData>)arrayList;
    }
    
    public static Map<Integer, EnchantmentData> mapEnchantmentData(final int n, final ItemStack itemStack) {
        final Item item = itemStack.getItem();
        Object hashMap = null;
        final boolean b = itemStack.getItem() == Items.book;
        for (final Enchantment enchantmentObj : Enchantment.enchantmentsBookList) {
            if (enchantmentObj != null && (enchantmentObj.type.canEnchantItem(item) || b)) {
                for (int j = enchantmentObj.getMinLevel(); j <= enchantmentObj.getMaxLevel(); ++j) {
                    if (n >= enchantmentObj.getMinEnchantability(j) && n <= enchantmentObj.getMaxEnchantability(j)) {
                        if (hashMap == null) {
                            hashMap = Maps.newHashMap();
                        }
                        ((Map<Integer, EnchantmentData>)hashMap).put(Integer.valueOf(enchantmentObj.effectId), new EnchantmentData(enchantmentObj, j));
                    }
                }
            }
        }
        return (Map<Integer, EnchantmentData>)hashMap;
    }
    
    static {
        EnchantmentHelper.enchantmentRand = new Random();
        EnchantmentHelper.enchantmentModifierDamage = new ModifierDamage();
        EnchantmentHelper.enchantmentModifierLiving = new ModifierLiving();
        EnchantmentHelper.ENCHANTMENT_ITERATOR_HURT = new HurtIterator();
        EnchantmentHelper.ENCHANTMENT_ITERATOR_DAMAGE = new DamageIterator();
    }
    
    static final class DamageIterator implements IModifier
    {
        public EntityLivingBase user;
        public Entity target;
        
        public DamageIterator() {
        }
        
        @Override
        public void calculateModifier(final Enchantment enchantmentIn, final int enchantmentLevel) {
            enchantmentIn.onEntityDamaged(this.user, this.target, enchantmentLevel);
        }
    }
    
    interface IModifier
    {
        void calculateModifier(final Enchantment p0, final int p1);
    }
    
    static final class HurtIterator implements IModifier
    {
        public EntityLivingBase user;
        public Entity attacker;
        
        public HurtIterator() {
        }
        
        @Override
        public void calculateModifier(final Enchantment enchantmentIn, final int enchantmentLevel) {
            enchantmentIn.onUserHurt(this.user, this.attacker, enchantmentLevel);
        }
    }
    
    static final class ModifierLiving implements IModifier
    {
        public float livingModifier;
        public EnumCreatureAttribute entityLiving;
        
        public ModifierLiving() {
        }
        
        @Override
        public void calculateModifier(final Enchantment enchantmentIn, final int enchantmentLevel) {
            this.livingModifier += enchantmentIn.calcDamageByCreature(enchantmentLevel, this.entityLiving);
        }
    }
    
    static final class ModifierDamage implements IModifier
    {
        public int damageModifier;
        public DamageSource source;
        
        public ModifierDamage() {
        }
        
        @Override
        public void calculateModifier(final Enchantment enchantmentIn, final int enchantmentLevel) {
            this.damageModifier += enchantmentIn.calcModifierDamage(enchantmentLevel, this.source);
        }
    }
}
