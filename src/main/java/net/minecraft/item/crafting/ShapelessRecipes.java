package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShapelessRecipes implements IRecipe {
   public ItemStack recipeOutput;
   public List<ItemStack> recipeItems;

   public ShapelessRecipes(ItemStack output, List<ItemStack> inputList) {
      this.recipeOutput = output;
      this.recipeItems = inputList;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return this.recipeOutput;
   }

   @Override
   public ItemStack[] getRemainingItems(InventoryCrafting inv) {
      ItemStack[] itemStacks = new ItemStack[inv.getSizeInventory()];

      for (int i = 0; i < itemStacks.length; i++) {
         ItemStack stackInSlot = inv.getStackInSlot(i);
         if (stackInSlot != null && stackInSlot.getItem().hasContainerItem()) {
            itemStacks[i] = new ItemStack(stackInSlot.getItem().getContainerItem());
         }
      }

      return itemStacks;
   }

   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      ArrayList newArrayList = Lists.newArrayList(this.recipeItems);

      for (int i = 0; i < inv.getHeight(); i++) {
         for (int j = 0; j < inv.getWidth(); j++) {
            ItemStack stackInRowAndColumn = inv.getStackInRowAndColumn(j, i);
            if (stackInRowAndColumn != null) {
               boolean flag = false;

               for (ItemStack itemStack : (Iterable<ItemStack>)newArrayList) {
                  if (stackInRowAndColumn.getItem() == itemStack.getItem() && (itemStack.getMetadata() == 32767 || stackInRowAndColumn.getMetadata() == itemStack.getMetadata())) {
                     flag = true;
                     newArrayList.remove(itemStack);
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }
         }
      }

      return newArrayList.isEmpty();
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      return this.recipeOutput.copy();
   }

   @Override
   public int getRecipeSize() {
      return this.recipeItems.size();
   }
}
