package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;

public class WeightedRandomChestContent extends WeightedRandom.Item {
   public ItemStack theItemId;
   public int minStackSize;
   public int maxStackSize;

   public WeightedRandomChestContent(Item item, int n, int minimumChance, int maximumChance, int itemWeightIn) {
      super(itemWeightIn);
      this.theItemId = new ItemStack(item, 1, n);
      this.minStackSize = minimumChance;
      this.maxStackSize = maximumChance;
   }

   public WeightedRandomChestContent(ItemStack stack, int minimumChance, int maximumChance, int itemWeightIn) {
      super(itemWeightIn);
      this.theItemId = stack;
      this.minStackSize = minimumChance;
      this.maxStackSize = maximumChance;
   }

   public static void generateChestContents(Random random, List<WeightedRandomChestContent> listIn, IInventory inv, int max) {
      for (int i = 0; i < max; i++) {
         WeightedRandomChestContent randomItem = WeightedRandom.getRandomItem(random, listIn);
         int n = randomItem.minStackSize + random.nextInt(randomItem.maxStackSize - randomItem.minStackSize + 1);
         if (randomItem.theItemId.getMaxStackSize() >= n) {
            ItemStack itemStack = randomItem.theItemId.copy();
            itemStack.stackSize = n;
            inv.setInventorySlotContents(random.nextInt(inv.getSizeInventory()), itemStack);
         } else {
            for (int j = 0; j < n; j++) {
               ItemStack itemStack = randomItem.theItemId.copy();
               itemStack.stackSize = 1;
               inv.setInventorySlotContents(random.nextInt(inv.getSizeInventory()), itemStack);
            }
         }
      }
   }

   public static void generateDispenserContents(Random random, List<WeightedRandomChestContent> listIn, TileEntityDispenser dispenser, int max) {
      for (int i = 0; i < max; i++) {
         WeightedRandomChestContent randomItem = WeightedRandom.getRandomItem(random, listIn);
         int n = randomItem.minStackSize + random.nextInt(randomItem.maxStackSize - randomItem.minStackSize + 1);
         if (randomItem.theItemId.getMaxStackSize() >= n) {
            ItemStack itemStack = randomItem.theItemId.copy();
            itemStack.stackSize = n;
            dispenser.setInventorySlotContents(random.nextInt(dispenser.getSizeInventory()), itemStack);
         } else {
            for (int j = 0; j < n; j++) {
               ItemStack itemStack = randomItem.theItemId.copy();
               itemStack.stackSize = 1;
               dispenser.setInventorySlotContents(random.nextInt(dispenser.getSizeInventory()), itemStack);
            }
         }
      }
   }

   public static List<WeightedRandomChestContent> combineLootTables(List<WeightedRandomChestContent> list, WeightedRandomChestContent... weightedRandomChestContents) {
      ArrayList newArrayList = Lists.newArrayList(list);
      Collections.addAll(newArrayList, weightedRandomChestContents);
      return newArrayList;
   }
}
