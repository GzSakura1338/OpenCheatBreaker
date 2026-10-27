package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.StatCollector;

public class ItemFireworkCharge extends Item {
   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      if (renderPass != 1) {
         return super.getColorFromItemStack(stack, renderPass);
      }

      NBTBase explosionTag = getExplosionTag(stack, "Colors");
      if (!(explosionTag instanceof NBTTagIntArray)) {
         return 9079434;
      }

      NBTTagIntArray nbttagIntArray = (NBTTagIntArray)explosionTag;
      int[] intArray = nbttagIntArray.getIntArray();
      if (intArray.length == 1) {
         return intArray[0];
      }

      int n = 0;
      int n2 = 0;
      int n3 = 0;

      for (int n4 : intArray) {
         n += (n4 & 0xFF0000) >> 16;
         n2 += (n4 & 0xFF00) >> 8;
         n3 += (n4 & 0xFF) >> 0;
      }

      n /= intArray.length;
      n2 /= intArray.length;
      n3 /= intArray.length;
      return n << 16 | n2 << 8 | n3;
   }

   public static NBTBase getExplosionTag(ItemStack stack, String key) {
      if (stack.hasTagCompound()) {
         NBTTagCompound compoundTag = stack.getTagCompound().getCompoundTag("Explosion");
         if (compoundTag != null) {
            return compoundTag.getTag(key);
         }
      }

      return null;
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.hasTagCompound()) {
         NBTTagCompound compoundTag = stack.getTagCompound().getCompoundTag("Explosion");
         if (compoundTag != null) {
            addExplosionInfo(compoundTag, tooltip);
         }
      }
   }

   public static void addExplosionInfo(NBTTagCompound nbt, List<String> tooltip) {
      byte byte2 = nbt.getByte("Type");
      if (byte2 >= 0 && byte2 <= 4) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type." + byte2).trim());
      } else {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type").trim());
      }

      int[] intArray = nbt.getIntArray("Colors");
      if (intArray.length > 0) {
         boolean flag = true;
         String str = "";

         for (int n : intArray) {
            if (!flag) {
               str = str + ", ";
            }

            flag = false;
            boolean flag2 = false;

            for (int i = 0; i < ItemDye.dyeColors.length; i++) {
               if (n == ItemDye.dyeColors[i]) {
                  flag2 = true;
                  str = str + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(i).getUnlocalizedName());
                  break;
               }
            }

            if (!flag2) {
               str = str + StatCollector.translateToLocal("item.fireworksCharge.customColor");
            }
         }

         tooltip.add(str);
      }

      int[] intArray2 = nbt.getIntArray("FadeColors");
      if (intArray2.length > 0) {
         boolean flag = true;
         String str = StatCollector.translateToLocal("item.fireworksCharge.fadeTo") + " ";

         for (int n : intArray2) {
            if (!flag) {
               str = str + ", ";
            }

            flag = false;
            boolean flag2 = false;

            for (int i = 0; i < 16; i++) {
               if (n == ItemDye.dyeColors[i]) {
                  flag2 = true;
                  str = str + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(i).getUnlocalizedName());
                  break;
               }
            }

            if (!flag2) {
               str = str + StatCollector.translateToLocal("item.fireworksCharge.customColor");
            }
         }

         tooltip.add(str);
      }

      boolean boolean2 = nbt.getBoolean("Trail");
      if (boolean2) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.trail"));
      }

      boolean boolean3 = nbt.getBoolean("Flicker");
      if (boolean3) {
         tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
      }
   }
}
