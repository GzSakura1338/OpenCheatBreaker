package net.minecraft.item.crafting;

import net.minecraft.block.BlockColored;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesDyes {
    public void addRecipes(CraftingManager craftingManager) {
        for (int i = 0; i < 16; i++) {
            craftingManager.addShapelessRecipe(
                new ItemStack(Blocks.wool, 1, BlockColored.computeInt2(i)),
                new ItemStack(Items.dye, 1, i),
                new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 0)
            );
            craftingManager.addRecipe(
                new ItemStack(Blocks.stained_hardened_clay, 8, BlockColored.computeInt2(i)),
                "###",
                "#X#",
                "###",
                '#',
                new ItemStack(Blocks.hardened_clay),
                'X',
                new ItemStack(Items.dye, 1, i)
            );
            craftingManager.addRecipe(
                new ItemStack(Blocks.stained_glass, 8, BlockColored.computeInt2(i)),
                "###",
                "#X#",
                "###",
                '#',
                new ItemStack(Blocks.glass),
                'X',
                new ItemStack(Items.dye, 1, i)
            );
            craftingManager.addRecipe(new ItemStack(Blocks.stained_glass_pane, 16, i), "###", "###", '#', new ItemStack(Blocks.stained_glass, 1, i));
        }

        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 1, 11), new ItemStack(Blocks.yellow_flower, 1, 0));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 1, 1), new ItemStack(Blocks.red_flower, 1, 0));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 3, 15), Items.bone);
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 9), new ItemStack(Items.dye, 1, 1), new ItemStack(Items.dye, 1, 15));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 14), new ItemStack(Items.dye, 1, 1), new ItemStack(Items.dye, 1, 11));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 10), new ItemStack(Items.dye, 1, 2), new ItemStack(Items.dye, 1, 15));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 8), new ItemStack(Items.dye, 1, 0), new ItemStack(Items.dye, 1, 15));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 7), new ItemStack(Items.dye, 1, 8), new ItemStack(Items.dye, 1, 15));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 3, 7), new ItemStack(Items.dye, 1, 0), new ItemStack(Items.dye, 1, 15), new ItemStack(Items.dye, 1, 15));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 12), new ItemStack(Items.dye, 1, 4), new ItemStack(Items.dye, 1, 15));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 6), new ItemStack(Items.dye, 1, 4), new ItemStack(Items.dye, 1, 2));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 5), new ItemStack(Items.dye, 1, 4), new ItemStack(Items.dye, 1, 1));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 13), new ItemStack(Items.dye, 1, 5), new ItemStack(Items.dye, 1, 9));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 3, 13), new ItemStack(Items.dye, 1, 4), new ItemStack(Items.dye, 1, 1), new ItemStack(Items.dye, 1, 9));
        craftingManager.addShapelessRecipe(
            new ItemStack(Items.dye, 4, 13),
            new ItemStack(Items.dye, 1, 4),
            new ItemStack(Items.dye, 1, 1),
            new ItemStack(Items.dye, 1, 1),
            new ItemStack(Items.dye, 1, 15)
        );
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 1, 12), new ItemStack(Blocks.red_flower, 1, 1));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 1, 13), new ItemStack(Blocks.red_flower, 1, 2));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 1, 7), new ItemStack(Blocks.red_flower, 1, 3));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 1, 1), new ItemStack(Blocks.red_flower, 1, 4));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 1, 14), new ItemStack(Blocks.red_flower, 1, 5));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 1, 7), new ItemStack(Blocks.red_flower, 1, 6));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 1, 9), new ItemStack(Blocks.red_flower, 1, 7));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 1, 7), new ItemStack(Blocks.red_flower, 1, 8));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 11), new ItemStack(Blocks.double_plant, 1, 0));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 13), new ItemStack(Blocks.double_plant, 1, 1));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 1), new ItemStack(Blocks.double_plant, 1, 4));
        craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 2, 9), new ItemStack(Blocks.double_plant, 1, 5));

        for (int i = 0; i < 16; i++) {
            craftingManager.addRecipe(new ItemStack(Blocks.carpet, 3, i), "##", '#', new ItemStack(Blocks.wool, 1, i));
        }
    }
}
