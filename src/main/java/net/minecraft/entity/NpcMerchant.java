package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class NpcMerchant implements IMerchant {
    public EntityPlayer customer;
    public InventoryMerchant theMerchantInventory;
    public MerchantRecipeList recipeList;

    public NpcMerchant(EntityPlayer entityPlayer) {
        this.customer = entityPlayer;
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
    public void setRecipes(MerchantRecipeList merchantRecipeList) {
        this.recipeList = merchantRecipeList;
    }

    @Override
    public void useRecipe(MerchantRecipe merchantRecipe) {
    }

    @Override
    public void verifySellingItem(ItemStack itemStack) {
    }
}
