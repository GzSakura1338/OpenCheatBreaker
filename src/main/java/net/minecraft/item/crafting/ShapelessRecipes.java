package net.minecraft.item.crafting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShapelessRecipes implements IRecipe {
    public ItemStack recipeOutput;
    public List recipeItems;

    public ShapelessRecipes(ItemStack itemStack, List list) {
        this.recipeOutput = itemStack;
        this.recipeItems = list;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        ArrayList list = new ArrayList(this.recipeItems);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ItemStack stackInRowAndColumn = inventoryCrafting.getStackInRowAndColumn(j, i);
                if (stackInRowAndColumn != null) {
                    boolean flag = false;

                    for (ItemStack itemStack : (Iterable<ItemStack>)list) {
                        if (stackInRowAndColumn.getItem() == itemStack.getItem() && (itemStack.getMetadata() == 32767 || stackInRowAndColumn.getMetadata() == itemStack.getMetadata())) {
                            flag = true;
                            list.remove(itemStack);
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }
            }
        }

        return list.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        return this.recipeOutput.copy();
    }

    @Override
    public int getRecipeSize() {
        return this.recipeItems.size();
    }
}
