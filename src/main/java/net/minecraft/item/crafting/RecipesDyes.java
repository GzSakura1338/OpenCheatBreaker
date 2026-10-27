package net.minecraft.item.crafting;

import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesDyes {
   public void addRecipes(CraftingManager craftingManager) {
      for (int i = 0; i < 16; i++) {
         craftingManager.addShapelessRecipe(
            new ItemStack(Blocks.wool, 1, i), new ItemStack(Items.dye, 1, 15 - i), new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 0)
         );
         craftingManager.addRecipe(
            new ItemStack(Blocks.stained_hardened_clay, 8, 15 - i),
            "###",
            "#X#",
            "###",
            '#',
            new ItemStack(Blocks.hardened_clay),
            'X',
            new ItemStack(Items.dye, 1, i)
         );
         craftingManager.addRecipe(
            new ItemStack(Blocks.stained_glass, 8, 15 - i), "###", "#X#", "###", '#', new ItemStack(Blocks.glass), 'X', new ItemStack(Items.dye, 1, i)
         );
         craftingManager.addRecipe(new ItemStack(Blocks.stained_glass_pane, 16, i), "###", "###", '#', new ItemStack(Blocks.stained_glass, 1, i));
      }

      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.YELLOW.getDyeDamage()),
         new ItemStack(Blocks.yellow_flower, 1, EnumFlowerType.DANDELION.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, EnumFlowerType.POPPY.getMeta())
      );
      craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 3, EnumDyeColor.WHITE.getDyeDamage()), Items.bone);
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.PINK.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.ORANGE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.YELLOW.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.LIME.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.GREEN.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.GRAY.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.SILVER.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.GRAY.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 3, EnumDyeColor.SILVER.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.LIGHT_BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.CYAN.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.GREEN.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.PURPLE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.MAGENTA.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.PURPLE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.PINK.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 3, EnumDyeColor.MAGENTA.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.PINK.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 4, EnumDyeColor.MAGENTA.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.LIGHT_BLUE.getDyeDamage()),
         new ItemStack(Blocks.red_flower, 1, EnumFlowerType.BLUE_ORCHID.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.MAGENTA.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, EnumFlowerType.ALLIUM.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, EnumFlowerType.HOUSTONIA.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, EnumFlowerType.RED_TULIP.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.ORANGE.getDyeDamage()),
         new ItemStack(Blocks.red_flower, 1, EnumFlowerType.ORANGE_TULIP.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, EnumFlowerType.WHITE_TULIP.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.PINK.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, EnumFlowerType.PINK_TULIP.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, EnumFlowerType.OXEYE_DAISY.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.YELLOW.getDyeDamage()),
         new ItemStack(Blocks.double_plant, 1, EnumPlantType.SUNFLOWER.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.MAGENTA.getDyeDamage()),
         new ItemStack(Blocks.double_plant, 1, EnumPlantType.SYRINGA.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Blocks.double_plant, 1, EnumPlantType.ROSE.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.PINK.getDyeDamage()), new ItemStack(Blocks.double_plant, 1, EnumPlantType.PAEONIA.getMeta())
      );

      for (int i = 0; i < 16; i++) {
         craftingManager.addRecipe(new ItemStack(Blocks.carpet, 3, i), "##", '#', new ItemStack(Blocks.wool, 1, i));
      }
   }
}
