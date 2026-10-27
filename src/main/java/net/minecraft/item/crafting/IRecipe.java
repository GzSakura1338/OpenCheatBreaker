package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IRecipe {
   boolean matches(InventoryCrafting inventoryCrafting, World world);

   ItemStack getCraftingResult(InventoryCrafting inventoryCrafting);

   int getRecipeSize();

   ItemStack getRecipeOutput();

   ItemStack[] getRemainingItems(InventoryCrafting inventoryCrafting);
}
