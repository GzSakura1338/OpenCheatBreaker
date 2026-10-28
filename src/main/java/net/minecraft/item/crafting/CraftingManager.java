package net.minecraft.item.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CraftingManager {
    public static CraftingManager instance = new CraftingManager();
    public List recipes = new ArrayList();

    public static CraftingManager getInstance() {
        return instance;
    }

    public CraftingManager() {
        new RecipesTools().addRecipes(this);
        new RecipesWeapons().addRecipes(this);
        new RecipesIngots().addRecipes(this);
        new RecipesFood().addRecipes(this);
        new RecipesCrafting().addRecipes(this);
        new RecipesArmor().addRecipes(this);
        new RecipesDyes().addRecipes(this);
        this.recipes.add(new RecipesArmorDyes());
        this.recipes.add(new RecipeBookCloning());
        this.recipes.add(new RecipesMapCloning());
        this.recipes.add(new RecipesMapExtending());
        this.recipes.add(new RecipeFireworks());
        this.addRecipe(new ItemStack(Items.paper, 3), "###", '#', Items.reeds);
        this.addShapelessRecipe(new ItemStack(Items.book, 1), Items.paper, Items.paper, Items.paper, Items.leather);
        this.addShapelessRecipe(new ItemStack(Items.writable_book, 1), Items.book, new ItemStack(Items.dye, 1, 0), Items.feather);
        this.addRecipe(new ItemStack(Blocks.fence, 2), "###", "###", '#', Items.stick);
        this.addRecipe(new ItemStack(Blocks.cobblestone_wall, 6, 0), "###", "###", '#', Blocks.cobblestone);
        this.addRecipe(new ItemStack(Blocks.cobblestone_wall, 6, 1), "###", "###", '#', Blocks.mossy_cobblestone);
        this.addRecipe(new ItemStack(Blocks.nether_brick_fence, 6), "###", "###", '#', Blocks.nether_brick);
        this.addRecipe(new ItemStack(Blocks.fence_gate, 1), "#W#", "#W#", '#', Items.stick, 'W', Blocks.planks);
        this.addRecipe(new ItemStack(Blocks.jukebox, 1), "###", "#X#", "###", '#', Blocks.planks, 'X', Items.diamond);
        this.addRecipe(new ItemStack(Items.lead, 2), "~~ ", "~O ", "  ~", '~', Items.string, 'O', Items.slime_ball);
        this.addRecipe(new ItemStack(Blocks.noteblock, 1), "###", "#X#", "###", '#', Blocks.planks, 'X', Items.redstone);
        this.addRecipe(new ItemStack(Blocks.bookshelf, 1), "###", "XXX", "###", '#', Blocks.planks, 'X', Items.book);
        this.addRecipe(new ItemStack(Blocks.snow, 1), "##", "##", '#', Items.snowball);
        this.addRecipe(new ItemStack(Blocks.snow_layer, 6), "###", '#', Blocks.snow);
        this.addRecipe(new ItemStack(Blocks.clay, 1), "##", "##", '#', Items.clay_ball);
        this.addRecipe(new ItemStack(Blocks.brick_block, 1), "##", "##", '#', Items.brick);
        this.addRecipe(new ItemStack(Blocks.glowstone, 1), "##", "##", '#', Items.glowstone_dust);
        this.addRecipe(new ItemStack(Blocks.quartz_block, 1), "##", "##", '#', Items.quartz);
        this.addRecipe(new ItemStack(Blocks.wool, 1), "##", "##", '#', Items.string);
        this.addRecipe(new ItemStack(Blocks.tnt, 1), "X#X", "#X#", "X#X", 'X', Items.gunpowder, '#', Blocks.sand);
        this.addRecipe(new ItemStack(Blocks.stone_slab, 6, 3), "###", '#', Blocks.cobblestone);
        this.addRecipe(new ItemStack(Blocks.stone_slab, 6, 0), "###", '#', Blocks.stone);
        this.addRecipe(new ItemStack(Blocks.stone_slab, 6, 1), "###", '#', Blocks.sandstone);
        this.addRecipe(new ItemStack(Blocks.stone_slab, 6, 4), "###", '#', Blocks.brick_block);
        this.addRecipe(new ItemStack(Blocks.stone_slab, 6, 5), "###", '#', Blocks.stonebrick);
        this.addRecipe(new ItemStack(Blocks.stone_slab, 6, 6), "###", '#', Blocks.nether_brick);
        this.addRecipe(new ItemStack(Blocks.stone_slab, 6, 7), "###", '#', Blocks.quartz_block);
        this.addRecipe(new ItemStack(Blocks.wooden_slab, 6, 0), "###", '#', new ItemStack(Blocks.planks, 1, 0));
        this.addRecipe(new ItemStack(Blocks.wooden_slab, 6, 2), "###", '#', new ItemStack(Blocks.planks, 1, 2));
        this.addRecipe(new ItemStack(Blocks.wooden_slab, 6, 1), "###", '#', new ItemStack(Blocks.planks, 1, 1));
        this.addRecipe(new ItemStack(Blocks.wooden_slab, 6, 3), "###", '#', new ItemStack(Blocks.planks, 1, 3));
        this.addRecipe(new ItemStack(Blocks.wooden_slab, 6, 4), "###", '#', new ItemStack(Blocks.planks, 1, 4));
        this.addRecipe(new ItemStack(Blocks.wooden_slab, 6, 5), "###", '#', new ItemStack(Blocks.planks, 1, 5));
        this.addRecipe(new ItemStack(Blocks.ladder, 3), "# #", "###", "# #", '#', Items.stick);
        this.addRecipe(new ItemStack(Items.wooden_door, 1), "##", "##", "##", '#', Blocks.planks);
        this.addRecipe(new ItemStack(Blocks.trapdoor, 2), "###", "###", '#', Blocks.planks);
        this.addRecipe(new ItemStack(Items.iron_door, 1), "##", "##", "##", '#', Items.iron_ingot);
        this.addRecipe(new ItemStack(Items.sign, 3), "###", "###", " X ", '#', Blocks.planks, 'X', Items.stick);
        this.addRecipe(new ItemStack(Items.cake, 1), "AAA", "BEB", "CCC", 'A', Items.milk_bucket, 'B', Items.sugar, 'C', Items.wheat, 'E', Items.egg);
        this.addRecipe(new ItemStack(Items.sugar, 1), "#", '#', Items.reeds);
        this.addRecipe(new ItemStack(Blocks.planks, 4, 0), "#", '#', new ItemStack(Blocks.log, 1, 0));
        this.addRecipe(new ItemStack(Blocks.planks, 4, 1), "#", '#', new ItemStack(Blocks.log, 1, 1));
        this.addRecipe(new ItemStack(Blocks.planks, 4, 2), "#", '#', new ItemStack(Blocks.log, 1, 2));
        this.addRecipe(new ItemStack(Blocks.planks, 4, 3), "#", '#', new ItemStack(Blocks.log, 1, 3));
        this.addRecipe(new ItemStack(Blocks.planks, 4, 4), "#", '#', new ItemStack(Blocks.log2, 1, 0));
        this.addRecipe(new ItemStack(Blocks.planks, 4, 5), "#", '#', new ItemStack(Blocks.log2, 1, 1));
        this.addRecipe(new ItemStack(Items.stick, 4), "#", "#", '#', Blocks.planks);
        this.addRecipe(new ItemStack(Blocks.torch, 4), "X", "#", 'X', Items.coal, '#', Items.stick);
        this.addRecipe(new ItemStack(Blocks.torch, 4), "X", "#", 'X', new ItemStack(Items.coal, 1, 1), '#', Items.stick);
        this.addRecipe(new ItemStack(Items.bowl, 4), "# #", " # ", '#', Blocks.planks);
        this.addRecipe(new ItemStack(Items.glass_bottle, 3), "# #", " # ", '#', Blocks.glass);
        this.addRecipe(new ItemStack(Blocks.rail, 16), "X X", "X#X", "X X", 'X', Items.iron_ingot, '#', Items.stick);
        this.addRecipe(new ItemStack(Blocks.golden_rail, 6), "X X", "X#X", "XRX", 'X', Items.gold_ingot, 'R', Items.redstone, '#', Items.stick);
        this.addRecipe(new ItemStack(Blocks.activator_rail, 6), "XSX", "X#X", "XSX", 'X', Items.iron_ingot, '#', Blocks.redstone_torch, 'S', Items.stick);
        this.addRecipe(new ItemStack(Blocks.detector_rail, 6), "X X", "X#X", "XRX", 'X', Items.iron_ingot, 'R', Items.redstone, '#', Blocks.stone_pressure_plate);
        this.addRecipe(new ItemStack(Items.minecart, 1), "# #", "###", '#', Items.iron_ingot);
        this.addRecipe(new ItemStack(Items.cauldron, 1), "# #", "# #", "###", '#', Items.iron_ingot);
        this.addRecipe(new ItemStack(Items.brewing_stand, 1), " B ", "###", '#', Blocks.cobblestone, 'B', Items.blaze_rod);
        this.addRecipe(new ItemStack(Blocks.lit_pumpkin, 1), "A", "B", 'A', Blocks.pumpkin, 'B', Blocks.torch);
        this.addRecipe(new ItemStack(Items.chest_minecart, 1), "A", "B", 'A', Blocks.chest, 'B', Items.minecart);
        this.addRecipe(new ItemStack(Items.furnace_minecart, 1), "A", "B", 'A', Blocks.furnace, 'B', Items.minecart);
        this.addRecipe(new ItemStack(Items.tnt_minecart, 1), "A", "B", 'A', Blocks.tnt, 'B', Items.minecart);
        this.addRecipe(new ItemStack(Items.hopper_minecart, 1), "A", "B", 'A', Blocks.hopper, 'B', Items.minecart);
        this.addRecipe(new ItemStack(Items.boat, 1), "# #", "###", '#', Blocks.planks);
        this.addRecipe(new ItemStack(Items.bucket, 1), "# #", " # ", '#', Items.iron_ingot);
        this.addRecipe(new ItemStack(Items.flower_pot, 1), "# #", " # ", '#', Items.brick);
        this.addShapelessRecipe(new ItemStack(Items.flint_and_steel, 1), new ItemStack(Items.iron_ingot, 1), new ItemStack(Items.flint, 1));
        this.addRecipe(new ItemStack(Items.bread, 1), "###", '#', Items.wheat);
        this.addRecipe(new ItemStack(Blocks.oak_stairs, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.planks, 1, 0));
        this.addRecipe(new ItemStack(Blocks.birch_stairs, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.planks, 1, 2));
        this.addRecipe(new ItemStack(Blocks.spruce_stairs, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.planks, 1, 1));
        this.addRecipe(new ItemStack(Blocks.jungle_stairs, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.planks, 1, 3));
        this.addRecipe(new ItemStack(Blocks.acacia_stairs, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.planks, 1, 4));
        this.addRecipe(new ItemStack(Blocks.dark_oak_stairs, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.planks, 1, 5));
        this.addRecipe(new ItemStack(Items.fishing_rod, 1), "  #", " #X", "# X", '#', Items.stick, 'X', Items.string);
        this.addRecipe(new ItemStack(Items.carrot_on_a_stick, 1), "# ", " X", '#', Items.fishing_rod, 'X', Items.carrot).getShapedRecipes();
        this.addRecipe(new ItemStack(Blocks.stone_stairs, 4), "#  ", "## ", "###", '#', Blocks.cobblestone);
        this.addRecipe(new ItemStack(Blocks.brick_stairs, 4), "#  ", "## ", "###", '#', Blocks.brick_block);
        this.addRecipe(new ItemStack(Blocks.stone_brick_stairs, 4), "#  ", "## ", "###", '#', Blocks.stonebrick);
        this.addRecipe(new ItemStack(Blocks.nether_brick_stairs, 4), "#  ", "## ", "###", '#', Blocks.nether_brick);
        this.addRecipe(new ItemStack(Blocks.sandstone_stairs, 4), "#  ", "## ", "###", '#', Blocks.sandstone);
        this.addRecipe(new ItemStack(Blocks.quartz_stairs, 4), "#  ", "## ", "###", '#', Blocks.quartz_block);
        this.addRecipe(new ItemStack(Items.painting, 1), "###", "#X#", "###", '#', Items.stick, 'X', Blocks.wool);
        this.addRecipe(new ItemStack(Items.item_frame, 1), "###", "#X#", "###", '#', Items.stick, 'X', Items.leather);
        this.addRecipe(new ItemStack(Items.golden_apple, 1, 0), "###", "#X#", "###", '#', Items.gold_ingot, 'X', Items.apple);
        this.addRecipe(new ItemStack(Items.golden_apple, 1, 1), "###", "#X#", "###", '#', Blocks.gold_block, 'X', Items.apple);
        this.addRecipe(new ItemStack(Items.golden_carrot, 1, 0), "###", "#X#", "###", '#', Items.gold_nugget, 'X', Items.carrot);
        this.addRecipe(new ItemStack(Items.speckled_melon, 1), "###", "#X#", "###", '#', Items.gold_nugget, 'X', Items.melon);
        this.addRecipe(new ItemStack(Blocks.lever, 1), "X", "#", '#', Blocks.cobblestone, 'X', Items.stick);
        this.addRecipe(new ItemStack(Blocks.tripwire_hook, 2), "I", "S", "#", '#', Blocks.planks, 'S', Items.stick, 'I', Items.iron_ingot);
        this.addRecipe(new ItemStack(Blocks.redstone_torch, 1), "X", "#", '#', Items.stick, 'X', Items.redstone);
        this.addRecipe(new ItemStack(Items.repeater, 1), "#X#", "III", '#', Blocks.redstone_torch, 'X', Items.redstone, 'I', Blocks.stone);
        this.addRecipe(new ItemStack(Items.comparator, 1), " # ", "#X#", "III", '#', Blocks.redstone_torch, 'X', Items.quartz, 'I', Blocks.stone);
        this.addRecipe(new ItemStack(Items.clock, 1), " # ", "#X#", " # ", '#', Items.gold_ingot, 'X', Items.redstone);
        this.addRecipe(new ItemStack(Items.compass, 1), " # ", "#X#", " # ", '#', Items.iron_ingot, 'X', Items.redstone);
        this.addRecipe(new ItemStack(Items.map, 1), "###", "#X#", "###", '#', Items.paper, 'X', Items.compass);
        this.addRecipe(new ItemStack(Blocks.stone_button, 1), "#", '#', Blocks.stone);
        this.addRecipe(new ItemStack(Blocks.wooden_button, 1), "#", '#', Blocks.planks);
        this.addRecipe(new ItemStack(Blocks.stone_pressure_plate, 1), "##", '#', Blocks.stone);
        this.addRecipe(new ItemStack(Blocks.wooden_pressure_plate, 1), "##", '#', Blocks.planks);
        this.addRecipe(new ItemStack(Blocks.heavy_weighted_pressure_plate, 1), "##", '#', Items.iron_ingot);
        this.addRecipe(new ItemStack(Blocks.light_weighted_pressure_plate, 1), "##", '#', Items.gold_ingot);
        this.addRecipe(new ItemStack(Blocks.dispenser, 1), "###", "#X#", "#R#", '#', Blocks.cobblestone, 'X', Items.bow, 'R', Items.redstone);
        this.addRecipe(new ItemStack(Blocks.dropper, 1), "###", "# #", "#R#", '#', Blocks.cobblestone, 'R', Items.redstone);
        this.addRecipe(
            new ItemStack(Blocks.piston, 1), "TTT", "#X#", "#R#", '#', Blocks.cobblestone, 'X', Items.iron_ingot, 'R', Items.redstone, 'T', Blocks.planks
        );
        this.addRecipe(new ItemStack(Blocks.sticky_piston, 1), "S", "P", 'S', Items.slime_ball, 'P', Blocks.piston);
        this.addRecipe(new ItemStack(Items.bed, 1), "###", "XXX", '#', Blocks.wool, 'X', Blocks.planks);
        this.addRecipe(new ItemStack(Blocks.enchanting_table, 1), " B ", "D#D", "###", '#', Blocks.obsidian, 'B', Items.book, 'D', Items.diamond);
        this.addRecipe(new ItemStack(Blocks.anvil, 1), "III", " i ", "iii", 'I', Blocks.iron_block, 'i', Items.iron_ingot);
        this.addShapelessRecipe(new ItemStack(Items.ender_eye, 1), Items.ender_pearl, Items.blaze_powder);
        this.addShapelessRecipe(new ItemStack(Items.fire_charge, 3), Items.gunpowder, Items.blaze_powder, Items.coal);
        this.addShapelessRecipe(new ItemStack(Items.fire_charge, 3), Items.gunpowder, Items.blaze_powder, new ItemStack(Items.coal, 1, 1));
        this.addRecipe(new ItemStack(Blocks.daylight_detector), "GGG", "QQQ", "WWW", 'G', Blocks.glass, 'Q', Items.quartz, 'W', Blocks.wooden_slab);
        this.addRecipe(new ItemStack(Blocks.hopper), "I I", "ICI", " I ", 'I', Items.iron_ingot, 'C', Blocks.chest);
        Collections.sort(
            this.recipes,
            new Comparator() {
                public int compare(IRecipe recipe, IRecipe recipe2) {
                    return recipe instanceof ShapelessRecipes && recipe2 instanceof ShapedRecipes
                        ? 1
                        : (
                            recipe2 instanceof ShapelessRecipes && recipe instanceof ShapedRecipes
                                ? -1
                                : (recipe2.getRecipeSize() < recipe.getRecipeSize() ? -1 : (recipe2.getRecipeSize() > recipe.getRecipeSize() ? 1 : 0))
                        );
                }

                @Override
                public int compare(Object obj, Object obj2) {
                    return this.compare((IRecipe)obj, (IRecipe)obj2);
                }
            }
        );
    }

    public ShapedRecipes addRecipe(ItemStack itemStack, Object... objs) {
        String str = "";
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        if (objs[n] instanceof String[]) {
            String[] strs = (String[])objs[n++];

            for (int i = 0; i < strs.length; i++) {
                String str2 = strs[i];
                n3++;
                n2 = str2.length();
                str = str + str2;
            }
        } else {
            while (objs[n] instanceof String) {
                String str2 = (String)objs[n++];
                n3++;
                n2 = str2.length();
                str = str + str2;
            }
        }

        HashMap map = new HashMap();

        while (n < objs.length) {
            Character character = (Character)objs[n];
            ItemStack itemStack2 = null;
            if (objs[n + 1] instanceof Item) {
                itemStack2 = new ItemStack((Item)objs[n + 1]);
            } else if (objs[n + 1] instanceof Block) {
                itemStack2 = new ItemStack((Block)objs[n + 1], 1, 32767);
            } else if (objs[n + 1] instanceof ItemStack) {
                itemStack2 = (ItemStack)objs[n + 1];
            }

            map.put(character, itemStack2);
            n += 2;
        }

        ItemStack[] itemStacks = new ItemStack[n2 * n3];

        for (int i = 0; i < n2 * n3; i++) {
            char charAt = str.charAt(i);
            if (map.containsKey(charAt)) {
                itemStacks[i] = ((ItemStack)map.get(charAt)).copy();
            } else {
                itemStacks[i] = null;
            }
        }

        ShapedRecipes shapedRecipes = new ShapedRecipes(n2, n3, itemStacks, itemStack);
        this.recipes.add(shapedRecipes);
        return shapedRecipes;
    }

    public void addShapelessRecipe(ItemStack itemStack, Object... objs) {
        ArrayList list = new ArrayList();
        Object[] objs2 = objs;
        int length = objs.length;

        for (int i = 0; i < length; i++) {
            Object obj = objs2[i];
            if (obj instanceof ItemStack) {
                list.add(((ItemStack)obj).copy());
            } else if (obj instanceof Item) {
                list.add(new ItemStack((Item)obj));
            } else {
                if (!(obj instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless recipy!");
                }

                list.add(new ItemStack((Block)obj));
            }
        }

        this.recipes.add(new ShapelessRecipes(itemStack, list));
    }

    public ItemStack findMatchingRecipe(InventoryCrafting inventoryCrafting, World world) {
        int n = 0;
        ItemStack itemStack = null;
        ItemStack itemStack2 = null;

        for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
            ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);
            if (stackInSlot != null) {
                if (n == 0) {
                    itemStack = stackInSlot;
                }

                if (n == 1) {
                    itemStack2 = stackInSlot;
                }

                n++;
            }
        }

        if (n == 2 && itemStack.getItem() == itemStack2.getItem() && itemStack.stackSize == 1 && itemStack2.stackSize == 1 && itemStack.getItem().isDamageable()) {
            Item item = itemStack.getItem();
            int n2 = item.getMaxDurability() - itemStack.getCurrentDurability();
            int n3 = item.getMaxDurability() - itemStack2.getCurrentDurability();
            int n4 = n2 + n3 + item.getMaxDurability() * 5 / 100;
            int n5 = item.getMaxDurability() - n4;
            if (n5 < 0) {
                n5 = 0;
            }

            return new ItemStack(itemStack.getItem(), 1, n5);
        } else {
            for (int i = 0; i < this.recipes.size(); i++) {
                IRecipe recipe = (IRecipe)this.recipes.get(i);
                if (recipe.matches(inventoryCrafting, world)) {
                    return recipe.getCraftingResult(inventoryCrafting);
                }
            }

            return null;
        }
    }

    public List getRecipeList() {
        return this.recipes;
    }
}
