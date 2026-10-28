package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipesIngots {
    public Object[][] recipeItems = new Object[][]{
        {Blocks.gold_block, new ItemStack(Items.gold_ingot, 9)},
        {Blocks.iron_block, new ItemStack(Items.iron_ingot, 9)},
        {Blocks.diamond_block, new ItemStack(Items.diamond, 9)},
        {Blocks.emerald_block, new ItemStack(Items.emerald, 9)},
        {Blocks.lapis_block, new ItemStack(Items.dye, 9, 4)},
        {Blocks.redstone_block, new ItemStack(Items.redstone, 9)},
        {Blocks.coal_block, new ItemStack(Items.coal, 9, 0)},
        {Blocks.hay_block, new ItemStack(Items.wheat, 9)}
    };

    public void addRecipes(CraftingManager craftingManager) {
        for (int i = 0; i < this.recipeItems.length; i++) {
            Block block = (Block)this.recipeItems[i][0];
            ItemStack itemStack = (ItemStack)this.recipeItems[i][1];
            craftingManager.addRecipe(new ItemStack(block), "###", "###", "###", '#', itemStack);
            craftingManager.addRecipe(itemStack, "#", '#', block);
        }

        craftingManager.addRecipe(new ItemStack(Items.gold_ingot), "###", "###", "###", '#', Items.gold_nugget);
        craftingManager.addRecipe(new ItemStack(Items.gold_nugget, 9), "#", '#', Items.gold_ingot);
    }
}
