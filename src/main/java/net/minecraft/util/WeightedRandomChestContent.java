package net.minecraft.util;

import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.WeightedRandom.Item;

public class WeightedRandomChestContent extends Item {
    public ItemStack theItemId;
    public int theMinimumChanceToGenerateItem;
    public int theMaximumChanceToGenerateItem;

    public WeightedRandomChestContent(net.minecraft.item.Item item, int n, int n2, int n3, int n4) {
        super(n4);
        this.theItemId = new ItemStack(item, 1, n);
        this.theMinimumChanceToGenerateItem = n2;
        this.theMaximumChanceToGenerateItem = n3;
    }

    public WeightedRandomChestContent(ItemStack itemStack, int n, int n2, int n3) {
        super(n3);
        this.theItemId = itemStack;
        this.theMinimumChanceToGenerateItem = n;
        this.theMaximumChanceToGenerateItem = n2;
    }

    public static void generateChestContents(Random random, WeightedRandomChestContent[] weightedRandomChestContents, IInventory inventory, int n) {
        for (int i = 0; i < n; i++) {
            WeightedRandomChestContent randomItem = (WeightedRandomChestContent)WeightedRandom.getRandomItem(random, weightedRandomChestContents);
            int n2 = randomItem.theMinimumChanceToGenerateItem + random.nextInt(randomItem.theMaximumChanceToGenerateItem - randomItem.theMinimumChanceToGenerateItem + 1);
            if (randomItem.theItemId.getMaxStackSize() >= n2) {
                ItemStack itemStack = randomItem.theItemId.copy();
                itemStack.stackSize = n2;
                inventory.setInventorySlotContents(random.nextInt(inventory.getSizeInventory()), itemStack);
            } else {
                for (int j = 0; j < n2; j++) {
                    ItemStack itemStack = randomItem.theItemId.copy();
                    itemStack.stackSize = 1;
                    inventory.setInventorySlotContents(random.nextInt(inventory.getSizeInventory()), itemStack);
                }
            }
        }
    }

    public static void generateDispenserContents(Random random, WeightedRandomChestContent[] weightedRandomChestContents, TileEntityDispenser tileEntityDispenser, int n) {
        for (int i = 0; i < n; i++) {
            WeightedRandomChestContent randomItem = (WeightedRandomChestContent)WeightedRandom.getRandomItem(random, weightedRandomChestContents);
            int n2 = randomItem.theMinimumChanceToGenerateItem + random.nextInt(randomItem.theMaximumChanceToGenerateItem - randomItem.theMinimumChanceToGenerateItem + 1);
            if (randomItem.theItemId.getMaxStackSize() >= n2) {
                ItemStack itemStack = randomItem.theItemId.copy();
                itemStack.stackSize = n2;
                tileEntityDispenser.setInventorySlotContents(random.nextInt(tileEntityDispenser.getSizeInventory()), itemStack);
            } else {
                for (int j = 0; j < n2; j++) {
                    ItemStack itemStack = randomItem.theItemId.copy();
                    itemStack.stackSize = 1;
                    tileEntityDispenser.setInventorySlotContents(random.nextInt(tileEntityDispenser.getSizeInventory()), itemStack);
                }
            }
        }
    }

    public static WeightedRandomChestContent[] union(WeightedRandomChestContent[] weightedRandomChestContents, WeightedRandomChestContent... weightedRandomChestContents2) {
        WeightedRandomChestContent[] weightedRandomChestContents3 = new WeightedRandomChestContent[weightedRandomChestContents.length + weightedRandomChestContents2.length];
        int n = 0;

        for (int i = 0; i < weightedRandomChestContents.length; i++) {
            weightedRandomChestContents3[n++] = weightedRandomChestContents[i];
        }

        WeightedRandomChestContent[] weightedRandomChestContents4 = weightedRandomChestContents2;
        int length = weightedRandomChestContents2.length;

        for (int i = 0; i < length; i++) {
            WeightedRandomChestContent weightedRandomChestContent = weightedRandomChestContents4[i];
            weightedRandomChestContents3[n++] = weightedRandomChestContent;
        }

        return weightedRandomChestContents3;
    }
}
