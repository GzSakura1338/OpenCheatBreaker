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
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   @Override
   public boolean isItemTool(ItemStack stack) {
      return false;
   }

   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return this.getEnchantments(stack).tagCount() > 0 ? EnumRarity.UNCOMMON : super.getRarity(stack);
   }

   public NBTTagList getEnchantments(ItemStack stack) {
      NBTTagCompound tagCompound = stack.getTagCompound();
      return tagCompound != null && tagCompound.hasKey("StoredEnchantments", 9) ? (NBTTagList)tagCompound.getTag("StoredEnchantments") : new NBTTagList();
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      super.addInformation(stack, playerIn, tooltip, advanced);
      NBTTagList enchantments = this.getEnchantments(stack);
      if (enchantments != null) {
         for (int i = 0; i < enchantments.tagCount(); i++) {
            short short2 = enchantments.getCompoundTagAt(i).getShort("id");
            short short3 = enchantments.getCompoundTagAt(i).getShort("lvl");
            if (Enchantment.getEnchantmentById(short2) != null) {
               tooltip.add(Enchantment.getEnchantmentById(short2).getTranslatedName(short3));
            }
         }
      }
   }

   public void addEnchantment(ItemStack stack, EnchantmentData enchantment) {
      NBTTagList enchantments = this.getEnchantments(stack);
      boolean flag = true;

      for (int i = 0; i < enchantments.tagCount(); i++) {
         NBTTagCompound compoundTagAt = enchantments.getCompoundTagAt(i);
         if (compoundTagAt.getShort("id") == enchantment.enchantmentobj.effectId) {
            if (compoundTagAt.getShort("lvl") < enchantment.enchantmentLevel) {
               compoundTagAt.setShort("lvl", (short)enchantment.enchantmentLevel);
            }

            flag = false;
            break;
         }
      }

      if (flag) {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         nbttagCompound.setShort("id", (short)enchantment.enchantmentobj.effectId);
         nbttagCompound.setShort("lvl", (short)enchantment.enchantmentLevel);
         enchantments.appendTag(nbttagCompound);
      }

      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      stack.getTagCompound().setTag("StoredEnchantments", enchantments);
   }

   public ItemStack getEnchantedItemStack(EnchantmentData data) {
      ItemStack itemStack = new ItemStack(this);
      this.addEnchantment(itemStack, data);
      return itemStack;
   }

   public void getAll(Enchantment enchantment, List<ItemStack> list) {
      for (int minLevel = enchantment.getMinLevel(); minLevel <= enchantment.getMaxLevel(); minLevel++) {
         list.add(this.getEnchantedItemStack(new EnchantmentData(enchantment, minLevel)));
      }
   }

   public WeightedRandomChestContent getRandom(Random rand) {
      return this.getRandom(rand, 1, 1, 1);
   }

   public WeightedRandomChestContent getRandom(Random rand, int minChance, int maxChance, int weight) {
      ItemStack itemStack = new ItemStack(Items.book, 1, 0);
      EnchantmentHelper.addRandomEnchantment(rand, itemStack, 30);
      return new WeightedRandomChestContent(itemStack, minChance, maxChance, weight);
   }
}
