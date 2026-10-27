package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesArmorDyes implements IRecipe {
   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      ItemStack itemStack = null;
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack stackInSlot = inv.getStackInSlot(i);
         if (stackInSlot != null) {
            if (stackInSlot.getItem() instanceof ItemArmor) {
               ItemArmor item = (ItemArmor)stackInSlot.getItem();
               if (item.getArmorMaterial() != ArmorMaterial.LEATHER || itemStack != null) {
                  return false;
               }

               itemStack = stackInSlot;
            } else {
               if (stackInSlot.getItem() != Items.dye) {
                  return false;
               }

               newArrayList.add(stackInSlot);
            }
         }
      }

      return itemStack != null && !newArrayList.isEmpty();
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack itemStack = null;
      int[] ints = new int[3];
      int n = 0;
      int n2 = 0;
      ItemArmor itemArmor = null;

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack stackInSlot = inv.getStackInSlot(i);
         if (stackInSlot != null) {
            if (stackInSlot.getItem() instanceof ItemArmor) {
               itemArmor = (ItemArmor)stackInSlot.getItem();
               if (itemArmor.getArmorMaterial() != ArmorMaterial.LEATHER || itemStack != null) {
                  return null;
               }

               itemStack = stackInSlot.copy();
               itemStack.stackSize = 1;
               if (itemArmor.hasColor(stackInSlot)) {
                  int color = itemArmor.getColor(itemStack);
                  float f = (color >> 16 & 0xFF) / 255.0F;
                  float f2 = (color >> 8 & 0xFF) / 255.0F;
                  float f3 = (color & 0xFF) / 255.0F;
                  n = (int)(n + Math.max(f, Math.max(f2, f3)) * 255.0F);
                  ints[0] = (int)(ints[0] + f * 255.0F);
                  ints[1] = (int)(ints[1] + f2 * 255.0F);
                  ints[2] = (int)(ints[2] + f3 * 255.0F);
                  n2++;
               }
            } else {
               if (stackInSlot.getItem() != Items.dye) {
                  return null;
               }

               float[] dyeRgb = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(stackInSlot.getMetadata()));
               int n3 = (int)(dyeRgb[0] * 255.0F);
               int n4 = (int)(dyeRgb[1] * 255.0F);
               int n5 = (int)(dyeRgb[2] * 255.0F);
               n += Math.max(n3, Math.max(n4, n5));
               ints[0] += n3;
               ints[1] += n4;
               ints[2] += n5;
               n2++;
            }
         }
      }

      if (itemArmor == null) {
         return null;
      }

      int n3 = ints[0] / n2;
      int n4 = ints[1] / n2;
      int n5 = ints[2] / n2;
      float f = (float)n / n2;
      float max = Math.max(n3, Math.max(n4, n5));
      n3 = (int)(n3 * f / max);
      n4 = (int)(n4 * f / max);
      n5 = (int)(n5 * f / max);
      int n6 = (n3 << 8) + n4;
      n6 = (n6 << 8) + n5;
      itemArmor.setColor(itemStack, n6);
      return itemStack;
   }

   @Override
   public int getRecipeSize() {
      return 10;
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
