package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBanner.EnumBannerPattern;
import net.minecraft.world.World;

public class RecipesBanners {
   public void addRecipes(CraftingManager craftingManager) {
      for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
         craftingManager.addRecipe(
            new ItemStack(Items.banner, 1, enumDyeColor.getDyeDamage()), "###", "###", " | ", '#', new ItemStack(Blocks.wool, 1, enumDyeColor.getMetadata()), '|', Items.stick
         );
      }

      craftingManager.addRecipe(new RecipesBanners.RecipeDuplicatePattern());
      craftingManager.addRecipe(new RecipesBanners.RecipeAddPattern());
   }

   static class RecipeAddPattern implements IRecipe {
      public RecipeAddPattern() {
      }

      @Override
      public boolean matches(InventoryCrafting inv, World worldIn) {
         boolean flag = false;

         for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stackInSlot = inv.getStackInSlot(i);
            if (stackInSlot != null && stackInSlot.getItem() == Items.banner) {
               if (flag) {
                  return false;
               }

               if (TileEntityBanner.getPatterns(stackInSlot) >= 6) {
                  return false;
               }

               flag = true;
            }
         }

         return !flag ? false : this.findEnumBannerPattern(inv) != null;
      }

      @Override
      public ItemStack getCraftingResult(InventoryCrafting inv) {
         ItemStack itemStack = null;

         for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stackInSlot = inv.getStackInSlot(i);
            if (stackInSlot != null && stackInSlot.getItem() == Items.banner) {
               itemStack = stackInSlot.copy();
               itemStack.stackSize = 1;
               break;
            }
         }

         EnumBannerPattern findEnumBannerPattern = this.findEnumBannerPattern(inv);
         if (findEnumBannerPattern != null) {
            int n = 0;

            for (int i = 0; i < inv.getSizeInventory(); i++) {
               ItemStack stackInSlot = inv.getStackInSlot(i);
               if (stackInSlot != null && stackInSlot.getItem() == Items.dye) {
                  n = stackInSlot.getMetadata();
                  break;
               }
            }

            NBTTagCompound subCompound = itemStack.getSubCompound("BlockEntityTag", true);
            NBTTagList nbttagList = null;
            if (subCompound.hasKey("Patterns", 9)) {
               nbttagList = subCompound.getTagList("Patterns", 10);
            } else {
               nbttagList = new NBTTagList();
               subCompound.setTag("Patterns", nbttagList);
            }

            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setString("Pattern", findEnumBannerPattern.getPatternID());
            nbttagCompound.setInteger("Color", n);
            nbttagList.appendTag(nbttagCompound);
         }

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

      public EnumBannerPattern findEnumBannerPattern(InventoryCrafting inventoryCrafting) {
         for (EnumBannerPattern enumBannerPattern : EnumBannerPattern.values()) {
            if (enumBannerPattern.hasValidCrafting()) {
               boolean flag = true;
               if (enumBannerPattern.hasCraftingStack()) {
                  boolean flag2 = false;
                  boolean flag3 = false;

                  for (int i = 0; i < inventoryCrafting.getSizeInventory() && flag; i++) {
                     ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);
                     if (stackInSlot != null && stackInSlot.getItem() != Items.banner) {
                        if (stackInSlot.getItem() == Items.dye) {
                           if (flag3) {
                              flag = false;
                              break;
                           }

                           flag3 = true;
                        } else {
                           if (flag2 || !stackInSlot.isItemEqual(enumBannerPattern.getCraftingStack())) {
                              flag = false;
                              break;
                           }

                           flag2 = true;
                        }
                     }
                  }

                  if (!flag2) {
                     flag = false;
                  }
               } else if (inventoryCrafting.getSizeInventory() == enumBannerPattern.getCraftingLayers().length * enumBannerPattern.getCraftingLayers()[0].length()) {
                  int n = -1;

                  for (int i = 0; i < inventoryCrafting.getSizeInventory() && flag; i++) {
                     int n2 = i / 3;
                     int n3 = i % 3;
                     ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);
                     if (stackInSlot != null && stackInSlot.getItem() != Items.banner) {
                        if (stackInSlot.getItem() != Items.dye) {
                           flag = false;
                           break;
                        }

                        if (n != -1 && n != stackInSlot.getMetadata()) {
                           flag = false;
                           break;
                        }

                        if (enumBannerPattern.getCraftingLayers()[n2].charAt(n3) == ' ') {
                           flag = false;
                           break;
                        }

                        n = stackInSlot.getMetadata();
                     } else if (enumBannerPattern.getCraftingLayers()[n2].charAt(n3) != ' ') {
                        flag = false;
                        break;
                     }
                  }
               } else {
                  flag = false;
               }

               if (flag) {
                  return enumBannerPattern;
               }
            }
         }

         return null;
      }
   }

   static class RecipeDuplicatePattern implements IRecipe {
      public RecipeDuplicatePattern() {
      }

      @Override
      public boolean matches(InventoryCrafting inv, World worldIn) {
         ItemStack itemStack = null;
         ItemStack itemStack2 = null;

         for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stackInSlot = inv.getStackInSlot(i);
            if (stackInSlot != null) {
               if (stackInSlot.getItem() != Items.banner) {
                  return false;
               }

               if (itemStack != null && itemStack2 != null) {
                  return false;
               }

               int baseColor = TileEntityBanner.getBaseColor(stackInSlot);
               boolean flag = TileEntityBanner.getPatterns(stackInSlot) > 0;
               if (itemStack != null) {
                  if (flag) {
                     return false;
                  }

                  if (baseColor != TileEntityBanner.getBaseColor(itemStack)) {
                     return false;
                  }

                  itemStack2 = stackInSlot;
               } else if (itemStack2 != null) {
                  if (!flag) {
                     return false;
                  }

                  if (baseColor != TileEntityBanner.getBaseColor(itemStack2)) {
                     return false;
                  }

                  itemStack = stackInSlot;
               } else if (flag) {
                  itemStack = stackInSlot;
               } else {
                  itemStack2 = stackInSlot;
               }
            }
         }

         return itemStack != null && itemStack2 != null;
      }

      @Override
      public ItemStack getCraftingResult(InventoryCrafting inv) {
         for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stackInSlot = inv.getStackInSlot(i);
            if (stackInSlot != null && TileEntityBanner.getPatterns(stackInSlot) > 0) {
               ItemStack itemStack = stackInSlot.copy();
               itemStack.stackSize = 1;
               return itemStack;
            }
         }

         return null;
      }

      @Override
      public int getRecipeSize() {
         return 2;
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
            if (stackInSlot != null) {
               if (stackInSlot.getItem().hasContainerItem()) {
                  itemStacks[i] = new ItemStack(stackInSlot.getItem().getContainerItem());
               } else if (stackInSlot.hasTagCompound() && TileEntityBanner.getPatterns(stackInSlot) > 0) {
                  itemStacks[i] = stackInSlot.copy();
                  itemStacks[i].stackSize = 1;
               }
            }
         }

         return itemStacks;
      }
   }
}
