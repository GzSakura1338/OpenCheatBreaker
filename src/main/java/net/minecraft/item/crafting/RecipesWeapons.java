package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesWeapons {
    public String[][] recipePatterns = new String[][]{{"X", "X", "#"}};
    public Object[][] recipeItems = new Object[][]{
        {Blocks.planks, Blocks.cobblestone, Items.iron_ingot, Items.diamond, Items.gold_ingot},
        {Items.wooden_sword, Items.stone_sword, Items.iron_sword, Items.diamond_sword, Items.golden_sword}
    };

    public void addRecipes(CraftingManager craftingManager) {
        for (int i = 0; i < this.recipeItems[0].length; i++) {
            Object obj = this.recipeItems[0][i];

            for (int j = 0; j < this.recipeItems.length - 1; j++) {
                Item item = (Item)this.recipeItems[j + 1][i];
                craftingManager.addRecipe(new ItemStack(item), this.recipePatterns[j], '#', Items.stick, 'X', obj);
            }
        }

        craftingManager.addRecipe(new ItemStack(Items.bow, 1), " #X", "# X", " #X", 'X', Items.string, '#', Items.stick);
        craftingManager.addRecipe(new ItemStack(Items.arrow, 4), "X", "#", "Y", 'Y', Items.feather, 'X', Items.flint, '#', Items.stick);
    }
}
