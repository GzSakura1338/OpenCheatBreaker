package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public interface IMerchant {
   void setCustomer(EntityPlayer entityPlayer);

   EntityPlayer getCustomer();

   MerchantRecipeList getRecipes(EntityPlayer entityPlayer);

   void setRecipes(MerchantRecipeList merchantRecipeList);

   void useRecipe(MerchantRecipe merchantRecipe);

   void verifySellingItem(ItemStack itemStack);

   IChatComponent getDisplayName();
}
