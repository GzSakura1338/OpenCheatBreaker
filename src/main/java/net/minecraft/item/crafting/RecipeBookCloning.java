package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeBookCloning implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        int n = 0;
        ItemStack itemStack = null;

        for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
            ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);
            if (stackInSlot != null) {
                if (stackInSlot.getItem() == Items.written_book) {
                    if (itemStack != null) {
                        return false;
                    }

                    itemStack = stackInSlot;
                } else {
                    if (stackInSlot.getItem() != Items.writable_book) {
                        return false;
                    }

                    n++;
                }
            }
        }

        return itemStack != null && n > 0;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        int n = 0;
        ItemStack itemStack = null;

        for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
            ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);
            if (stackInSlot != null) {
                if (stackInSlot.getItem() == Items.written_book) {
                    if (itemStack != null) {
                        return null;
                    }

                    itemStack = stackInSlot;
                } else {
                    if (stackInSlot.getItem() != Items.writable_book) {
                        return null;
                    }

                    n++;
                }
            }
        }

        if (itemStack != null && n >= 1) {
            ItemStack itemStack2 = new ItemStack(Items.written_book, n + 1);
            itemStack2.setTagCompound((NBTTagCompound)itemStack.getTagCompound().copy());
            if (itemStack.hasDisplayName()) {
                itemStack2.setStackDisplayName(itemStack.getDisplayName());
            }

            return itemStack2;
        } else {
            return null;
        }
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
