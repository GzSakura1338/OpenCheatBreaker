package net.minecraft.item.crafting;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemFishFood.FishType;

public class FurnaceRecipes {
    public static FurnaceRecipes smeltingBase = new FurnaceRecipes();
    public Map smeltingList = new HashMap();
    public Map experienceList = new HashMap();

    public static FurnaceRecipes instance() {
        return smeltingBase;
    }

    public FurnaceRecipes() {
        this.addSmeltingRecipeForBlock(Blocks.iron_ore, new ItemStack(Items.iron_ingot), 0.7F);
        this.addSmeltingRecipeForBlock(Blocks.gold_ore, new ItemStack(Items.gold_ingot), 1.0F);
        this.addSmeltingRecipeForBlock(Blocks.diamond_ore, new ItemStack(Items.diamond), 1.0F);
        this.addSmeltingRecipeForBlock(Blocks.sand, new ItemStack(Blocks.glass), 0.1F);
        this.addSmelting(Items.porkchop, new ItemStack(Items.cooked_porkchop), 0.35F);
        this.addSmelting(Items.beef, new ItemStack(Items.cooked_beef), 0.35F);
        this.addSmelting(Items.chicken, new ItemStack(Items.cooked_chicken), 0.35F);
        this.addSmeltingRecipeForBlock(Blocks.cobblestone, new ItemStack(Blocks.stone), 0.1F);
        this.addSmelting(Items.clay_ball, new ItemStack(Items.brick), 0.3F);
        this.addSmeltingRecipeForBlock(Blocks.clay, new ItemStack(Blocks.hardened_clay), 0.35F);
        this.addSmeltingRecipeForBlock(Blocks.cactus, new ItemStack(Items.dye, 1, 2), 0.2F);
        this.addSmeltingRecipeForBlock(Blocks.log, new ItemStack(Items.coal, 1, 1), 0.15F);
        this.addSmeltingRecipeForBlock(Blocks.log2, new ItemStack(Items.coal, 1, 1), 0.15F);
        this.addSmeltingRecipeForBlock(Blocks.emerald_ore, new ItemStack(Items.emerald), 1.0F);
        this.addSmelting(Items.potato, new ItemStack(Items.baked_potato), 0.35F);
        this.addSmeltingRecipeForBlock(Blocks.netherrack, new ItemStack(Items.netherbrick), 0.1F);

        for (FishType fishType : FishType.values()) {
            if (fishType.getCookable()) {
                this.addSmeltingRecipe(new ItemStack(Items.fish, 1, fishType.getItemDamage()), new ItemStack(Items.cooked_fish, 1, fishType.getItemDamage()), 0.35F);
            }
        }

        this.addSmeltingRecipeForBlock(Blocks.coal_ore, new ItemStack(Items.coal), 0.1F);
        this.addSmeltingRecipeForBlock(Blocks.redstone_ore, new ItemStack(Items.redstone), 0.7F);
        this.addSmeltingRecipeForBlock(Blocks.lapis_ore, new ItemStack(Items.dye, 1, 4), 0.2F);
        this.addSmeltingRecipeForBlock(Blocks.quartz_ore, new ItemStack(Items.quartz), 0.2F);
    }

    public void addSmeltingRecipeForBlock(Block block, ItemStack itemStack, float f) {
        this.addSmelting(Item.getItemFromBlock(block), itemStack, f);
    }

    public void addSmelting(Item item, ItemStack itemStack, float f) {
        this.addSmeltingRecipe(new ItemStack(item, 1, 32767), itemStack, f);
    }

    public void addSmeltingRecipe(ItemStack itemStack, ItemStack itemStack2, float f) {
        this.smeltingList.put(itemStack, itemStack2);
        this.experienceList.put(itemStack2, f);
    }

    public ItemStack getSmeltingResult(ItemStack itemStack) {
        for (Entry entry : (Iterable<Entry>)this.smeltingList.entrySet()) {
            if (this.stackEquals(itemStack, (ItemStack)entry.getKey())) {
                return (ItemStack)entry.getValue();
            }
        }

        return null;
    }

    public boolean stackEquals(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack2.getItem() == itemStack.getItem() && (itemStack2.getMetadata() == 32767 || itemStack2.getMetadata() == itemStack.getMetadata());
    }

    public Map getSmeltingList() {
        return this.smeltingList;
    }

    public float getSmeltingExperience(ItemStack itemStack) {
        for (Entry entry : (Iterable<Entry>)this.experienceList.entrySet()) {
            if (this.stackEquals(itemStack, (ItemStack)entry.getKey())) {
                return (Float)entry.getValue();
            }
        }

        return 0.0F;
    }
}
