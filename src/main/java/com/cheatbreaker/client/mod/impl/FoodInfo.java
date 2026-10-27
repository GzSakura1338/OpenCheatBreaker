package com.cheatbreaker.client.mod.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class FoodInfo {
   public static FoodValue createFoodValue(ItemStack itemStack) {
      ItemFood item = (ItemFood)itemStack.getItem();
      int healAmount = item.getHealAmount(itemStack);
      float saturationModifier = item.getSaturationModifier(itemStack);
      return new FoodValue(healAmount, saturationModifier);
   }

   public static FoodValue createFoodValue(ItemStack itemStack, EntityPlayer entityPlayer) {
      return createFoodValue(itemStack);
   }

   public static boolean getItem(ItemStack itemStack) {
      return itemStack != null && itemStack.getItem() instanceof ItemFood;
   }
}
