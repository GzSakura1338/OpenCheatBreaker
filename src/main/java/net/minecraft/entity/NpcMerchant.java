package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class NpcMerchant implements IMerchant {
   public EntityPlayer customer;
   public IChatComponent name;
   public MerchantRecipeList recipeList;
   public InventoryMerchant theMerchantInventory;

   public NpcMerchant(EntityPlayer entityPlayer, IChatComponent chatComponent) {
      this.customer = entityPlayer;
      this.name = chatComponent;
      this.theMerchantInventory = new InventoryMerchant(entityPlayer, this);
   }

   @Override
   public EntityPlayer getCustomer() {
      return this.customer;
   }

   @Override
   public void setCustomer(EntityPlayer entityPlayer) {
   }

   @Override
   public MerchantRecipeList getRecipes(EntityPlayer entityPlayer) {
      return this.recipeList;
   }

   @Override
   public void setRecipes(MerchantRecipeList recipeList) {
      this.recipeList = recipeList;
   }

   @Override
   public void useRecipe(MerchantRecipe recipe) {
      recipe.incrementToolUses();
   }

   @Override
   public void verifySellingItem(ItemStack stack) {
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.name != null ? this.name : new ChatComponentTranslation("entity.Villager.name");
   }
}
