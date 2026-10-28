package net.minecraft.item.crafting;

import java.util.ArrayList;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.world.World;

public class RecipesArmorDyes implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        ItemStack itemStack = null;
        ArrayList list = new ArrayList();

        for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
            ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);
            if (stackInSlot != null) {
                if (stackInSlot.getItem() instanceof ItemArmor) {
                    ItemArmor item = (ItemArmor)stackInSlot.getItem();
                    if (item.getArmorMaterial() != ArmorMaterial.CLOTH || itemStack != null) {
                        return false;
                    }

                    itemStack = stackInSlot;
                } else {
                    if (stackInSlot.getItem() != Items.dye) {
                        return false;
                    }

                    list.add(stackInSlot);
                }
            }
        }

        return itemStack != null && !list.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        ItemStack itemStack = null;
        int[] ints = new int[3];
        int n = 0;
        int n2 = 0;
        ItemArmor itemArmor = null;

        for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
            ItemStack stackInSlot = inventoryCrafting.getStackInSlot(i);
            if (stackInSlot != null) {
                if (stackInSlot.getItem() instanceof ItemArmor) {
                    itemArmor = (ItemArmor)stackInSlot.getItem();
                    if (itemArmor.getArmorMaterial() != ArmorMaterial.CLOTH || itemStack != null) {
                        return null;
                    }

                    itemStack = stackInSlot.copy();
                    itemStack.stackSize = 1;
                    if (itemArmor.hasColor(stackInSlot)) {
                        int color = itemArmor.getColor(itemStack);
                        float f = (color >> 16 & 0xFF) / 255.0F;
                        float f2 = (color >> 8 & 0xFF) / 255.0F;
                        float f3 = (color & 0xFF) / 255.0F;
                        n = (int)(n + Math.max(f, Math.max(f2, f3)) * 255.0F);
                        ints[0] = (int)(ints[0] + f * 255.0F);
                        ints[1] = (int)(ints[1] + f2 * 255.0F);
                        ints[2] = (int)(ints[2] + f3 * 255.0F);
                        n2++;
                    }
                } else {
                    if (stackInSlot.getItem() != Items.dye) {
                        return null;
                    }

                    float[] floats = EntitySheep.fleeceColorTable[BlockColored.computeInt(stackInSlot.getMetadata())];
                    int n3 = (int)(floats[0] * 255.0F);
                    int n4 = (int)(floats[1] * 255.0F);
                    int n5 = (int)(floats[2] * 255.0F);
                    n += Math.max(n3, Math.max(n4, n5));
                    ints[0] += n3;
                    ints[1] += n4;
                    ints[2] += n5;
                    n2++;
                }
            }
        }

        if (itemArmor == null) {
            return null;
        }

        int n3 = ints[0] / n2;
        int n4 = ints[1] / n2;
        int n5 = ints[2] / n2;
        float f = (float)n / n2;
        float max = Math.max(n3, Math.max(n4, n5));
        n3 = (int)(n3 * f / max);
        n4 = (int)(n4 * f / max);
        n5 = (int)(n5 * f / max);
        int n6 = (n3 << 8) + n4;
        n6 = (n6 << 8) + n5;
        itemArmor.setColor(itemStack, n6);
        return itemStack;
    }

    @Override
    public int getRecipeSize() {
        return 10;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
