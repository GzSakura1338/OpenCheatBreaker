package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class RecipeFireworks implements IRecipe {
   public ItemStack ingredient;

   @Override
   public boolean matches(InventoryCrafting inv, World worldIn) {
      this.ingredient = null;
      int n = 0;
      int n2 = 0;
      int n3 = 0;
      int n4 = 0;
      int n5 = 0;
      int n6 = 0;

      for (int i = 0; i < inv.getSizeInventory(); i++) {
         ItemStack stackInSlot = inv.getStackInSlot(i);
         if (stackInSlot != null) {
            if (stackInSlot.getItem() == Items.gunpowder) {
               n2++;
            } else if (stackInSlot.getItem() == Items.firework_charge) {
               n4++;
            } else if (stackInSlot.getItem() == Items.dye) {
               n3++;
            } else if (stackInSlot.getItem() == Items.paper) {
               n++;
            } else if (stackInSlot.getItem() == Items.glowstone_dust) {
               n5++;
            } else if (stackInSlot.getItem() == Items.diamond) {
               n5++;
            } else if (stackInSlot.getItem() == Items.fire_charge) {
               n6++;
            } else if (stackInSlot.getItem() == Items.feather) {
               n6++;
            } else if (stackInSlot.getItem() == Items.gold_nugget) {
               n6++;
            } else {
               if (stackInSlot.getItem() != Items.skull) {
                  return false;
               }

               n6++;
            }
         }
      }

      n5 = n5 + n3 + n6;
      if (n2 > 3 || n > 1) {
         return false;
      }

      if (n2 >= 1 && n == 1 && n5 == 0) {
         this.ingredient = new ItemStack(Items.fireworks);
         if (n4 > 0) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            NBTTagCompound nbttagCompound2 = new NBTTagCompound();
            NBTTagList nbttagList = new NBTTagList();

            for (int i = 0; i < inv.getSizeInventory(); i++) {
               ItemStack stackInSlot = inv.getStackInSlot(i);
               if (stackInSlot != null && stackInSlot.getItem() == Items.firework_charge && stackInSlot.hasTagCompound() && stackInSlot.getTagCompound().hasKey("Explosion", 10)) {
                  nbttagList.appendTag(stackInSlot.getTagCompound().getCompoundTag("Explosion"));
               }
            }

            nbttagCompound2.setTag("Explosions", nbttagList);
            nbttagCompound2.setByte("Flight", (byte)n2);
            nbttagCompound.setTag("Fireworks", nbttagCompound2);
            this.ingredient.setTagCompound(nbttagCompound);
         }

         return true;
      } else if (n2 == 1 && n == 0 && n4 == 0 && n3 > 0 && n6 <= 1) {
         this.ingredient = new ItemStack(Items.firework_charge);
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         NBTTagCompound nbttagCompound2 = new NBTTagCompound();
         byte b = 0;
         ArrayList newArrayList = Lists.newArrayList();

         for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stackInSlot = inv.getStackInSlot(i);
            if (stackInSlot != null) {
               if (stackInSlot.getItem() == Items.dye) {
                  newArrayList.add(ItemDye.dyeColors[stackInSlot.getMetadata() & 15]);
               } else if (stackInSlot.getItem() == Items.glowstone_dust) {
                  nbttagCompound2.setBoolean("Flicker", true);
               } else if (stackInSlot.getItem() == Items.diamond) {
                  nbttagCompound2.setBoolean("Trail", true);
               } else if (stackInSlot.getItem() == Items.fire_charge) {
                  b = 1;
               } else if (stackInSlot.getItem() == Items.feather) {
                  b = 4;
               } else if (stackInSlot.getItem() == Items.gold_nugget) {
                  b = 2;
               } else if (stackInSlot.getItem() == Items.skull) {
                  b = 3;
               }
            }
         }

         int[] ints = new int[newArrayList.size()];

         for (int i = 0; i < ints.length; i++) {
            ints[i] = (Integer)newArrayList.get(i);
         }

         nbttagCompound2.setIntArray("Colors", ints);
         nbttagCompound2.setByte("Type", b);
         nbttagCompound.setTag("Explosion", nbttagCompound2);
         this.ingredient.setTagCompound(nbttagCompound);
         return true;
      } else if (n2 == 0 && n == 0 && n4 == 1 && n3 > 0 && n3 == n5) {
         ArrayList newArrayList = Lists.newArrayList();

         for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stackInSlot = inv.getStackInSlot(i);
            if (stackInSlot != null) {
               if (stackInSlot.getItem() == Items.dye) {
                  newArrayList.add(ItemDye.dyeColors[stackInSlot.getMetadata() & 15]);
               } else if (stackInSlot.getItem() == Items.firework_charge) {
                  this.ingredient = stackInSlot.copy();
                  this.ingredient.stackSize = 1;
               }
            }
         }

         int[] ints = new int[newArrayList.size()];

         for (int i = 0; i < ints.length; i++) {
            ints[i] = (Integer)newArrayList.get(i);
         }

         if (this.ingredient != null && this.ingredient.hasTagCompound()) {
            NBTTagCompound compoundTag = this.ingredient.getTagCompound().getCompoundTag("Explosion");
            if (compoundTag == null) {
               return false;
            }

            compoundTag.setIntArray("FadeColors", ints);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public ItemStack getCraftingResult(InventoryCrafting inv) {
      return this.ingredient.copy();
   }

   @Override
   public int getRecipeSize() {
      return 10;
   }

   @Override
   public ItemStack getRecipeOutput() {
      return this.ingredient;
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
