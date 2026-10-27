package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesMapCloning implements IRecipe {
   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      int n = 0;
      ItemStack itemStack = null;

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack stackInSlot = inv.getStackInSlot(i);
         if (stackInSlot != null) {
            if (stackInSlot.getItem() == Items.filled_map) {
               if (itemStack != null) {
                  return false;
               }

               itemStack = stackInSlot;
            } else {
               if (stackInSlot.getItem() != Items.map) {
                  return false;
               }

               n++;
            }
         }
      }

      return itemStack != null && n > 0;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      int n = 0;
      ItemStack itemStack = null;

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack stackInSlot = inv.getStackInSlot(i);
         if (stackInSlot != null) {
            if (stackInSlot.getItem() == Items.filled_map) {
               if (itemStack != null) {
                  return null;
               }

               itemStack = stackInSlot;
            } else {
               if (stackInSlot.getItem() != Items.map) {
                  return null;
               }

               n++;
            }
         }
      }

      if (itemStack != null && n >= 1) {
         ItemStack itemStack2 = new ItemStack(Items.filled_map, n + 1, itemStack.getMetadata());
         if (itemStack.hasDisplayName()) {
            itemStack2.setStackDisplayName(itemStack.getDisplayName());
         }

         return itemStack2;
      } else {
         return null;
      }
   }

   @Override
   public int getRecipeSize() {
      return 9;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return null;
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
}
