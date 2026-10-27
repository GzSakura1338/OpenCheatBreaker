package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipeRepairItem implements IRecipe {
   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack stackInSlot = inv.getStackInSlot(i);
         if (stackInSlot != null) {
            newArrayList.add(stackInSlot);
            if (newArrayList.size() > 1) {
               ItemStack itemStack = (ItemStack)newArrayList.get(0);
               if (stackInSlot.getItem() != itemStack.getItem() || itemStack.stackSize != 1 || stackInSlot.stackSize != 1 || !itemStack.getItem().isDamageable()) {
                  return false;
               }
            }
         }
      }

      return newArrayList.size() == 2;
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack stackInSlot = inv.getStackInSlot(i);
         if (stackInSlot != null) {
            newArrayList.add(stackInSlot);
            if (newArrayList.size() > 1) {
               ItemStack itemStack = (ItemStack)newArrayList.get(0);
               if (stackInSlot.getItem() != itemStack.getItem() || itemStack.stackSize != 1 || stackInSlot.stackSize != 1 || !itemStack.getItem().isDamageable()) {
                  return null;
               }
            }
         }
      }

      if (newArrayList.size() == 2) {
         ItemStack itemStack = (ItemStack)newArrayList.get(0);
         ItemStack itemStack2 = (ItemStack)newArrayList.get(1);
         if (itemStack.getItem() == itemStack2.getItem() && itemStack.stackSize == 1 && itemStack2.stackSize == 1 && itemStack.getItem().isDamageable()) {
            Item item = itemStack.getItem();
            int n = item.getMaxDamage() - itemStack.getItemDamage();
            int n2 = item.getMaxDamage() - itemStack2.getItemDamage();
            int n3 = n + n2 + item.getMaxDamage() * 5 / 100;
            int n4 = item.getMaxDamage() - n3;
            if (n4 < 0) {
               n4 = 0;
            }

            return new ItemStack(itemStack.getItem(), 1, n4);
         }
      }

      return null;
   }

   @Override
   public int getRecipeSize() {
      return 4;
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
