package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemRedstone extends Item {
    public ItemRedstone() {
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (world.getBlock(n, n2, n3) != Blocks.snow_layer) {
            if (n4 == 0) {
                n2--;
            }

            if (n4 == 1) {
                n2++;
            }

            if (n4 == 2) {
                n3--;
            }

            if (n4 == 3) {
                n3++;
            }

            if (n4 == 4) {
                n--;
            }

            if (n4 == 5) {
                n++;
            }

            if (!world.isAirBlock(n, n2, n3)) {
                return false;
            }
        }

        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack)) {
            return false;
        }

        if (Blocks.redstone_wire.canPlaceBlockAt(world, n, n2, n3)) {
            itemStack.stackSize--;
            world.setBlock(n, n2, n3, Blocks.redstone_wire);
        }

        return true;
    }
}
