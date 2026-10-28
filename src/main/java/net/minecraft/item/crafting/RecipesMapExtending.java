package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class RecipesMapExtending extends ShapedRecipes {
    public RecipesMapExtending() {
        super(
            3,
            3,
            new ItemStack[]{
                new ItemStack(Items.paper),
                new ItemStack(Items.paper),
                new ItemStack(Items.paper),
                new ItemStack(Items.paper),
                new ItemStack(Items.filled_map, 0, 32767),
                new ItemStack(Items.paper),
                new ItemStack(Items.paper),
                new ItemStack(Items.paper),
                new ItemStack(Items.paper)
            },
            new ItemStack(Items.map, 0, 0)
        );
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        if (!super.matches(inventoryCrafting, world)) {
            return false;
        }

        ItemStack itemStack = null;

        for (int i = 0; i < inventoryCrafting.getSizeInventory() && itemStack == null; i++) {
            ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);
            if (stackInSlot != null && stackInSlot.getItem() == Items.filled_map) {
                itemStack = stackInSlot;
            }
        }

        if (itemStack == null) {
            return false;
        }

        MapData mapData = Items.filled_map.getMapData(itemStack, world);
        return mapData != null && mapData.scale < 4;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        ItemStack itemStack = null;

        for (int i = 0; i < inventoryCrafting.getSizeInventory() && itemStack == null; i++) {
            ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);
            if (stackInSlot != null && stackInSlot.getItem() == Items.filled_map) {
                itemStack = stackInSlot;
            }
        }

        itemStack = itemStack.copy();
        itemStack.stackSize = 1;
        if (itemStack.getTagCompound() == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        itemStack.getTagCompound().setBoolean("map_is_scaling", true);
        return itemStack;
    }
}
