package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ShapedRecipes implements IRecipe {
    public int recipeWidth;
    public int recipeHeight;
    public ItemStack[] recipeItems;
    public ItemStack recipeOutput;
    public boolean copyIngredientsNbt;

    public ShapedRecipes(int n, int n2, ItemStack[] itemStacks, ItemStack itemStack) {
        this.recipeWidth = n;
        this.recipeHeight = n2;
        this.recipeItems = itemStacks;
        this.recipeOutput = itemStack;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        for (int i = 0; i <= 3 - this.recipeWidth; i++) {
            for (int j = 0; j <= 3 - this.recipeHeight; j++) {
                if (this.checkMatch(inventoryCrafting, i, j, true)) {
                    return true;
                }

                if (this.checkMatch(inventoryCrafting, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean checkMatch(InventoryCrafting inventoryCrafting, int n, int n2, boolean flag) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int n3 = i - n;
                int n4 = j - n2;
                ItemStack itemStack = null;
                if (n3 >= 0 && n4 >= 0 && n3 < this.recipeWidth && n4 < this.recipeHeight) {
                    if (flag) {
                        itemStack = this.recipeItems[this.recipeWidth - n3 - 1 + n4 * this.recipeWidth];
                    } else {
                        itemStack = this.recipeItems[n3 + n4 * this.recipeWidth];
                    }
                }

                ItemStack stackInRowAndColumn = inventoryCrafting.getStackInRowAndColumn(i, j);
                if (stackInRowAndColumn != null || itemStack != null) {
                    if (stackInRowAndColumn == null && itemStack != null || stackInRowAndColumn != null && itemStack == null) {
                        return false;
                    }

                    if (itemStack.getItem() != stackInRowAndColumn.getItem()) {
                        return false;
                    }

                    if (itemStack.getMetadata() != 32767 && itemStack.getMetadata() != stackInRowAndColumn.getMetadata()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        ItemStack itemStack = this.getRecipeOutput().copy();
        if (this.copyIngredientsNbt) {
            for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
                ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);
                if (stackInSlot != null && stackInSlot.hasTagCompound()) {
                    itemStack.setTagCompound((NBTTagCompound)stackInSlot.stackTagCompound.copy());
                }
            }
        }

        return itemStack;
    }

    @Override
    public int getRecipeSize() {
        return this.recipeWidth * this.recipeHeight;
    }

    public ShapedRecipes getShapedRecipes() {
        this.copyIngredientsNbt = true;
        return this;
    }
}
