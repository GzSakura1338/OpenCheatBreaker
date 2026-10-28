package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemFireball extends Item {
    public ItemFireball() {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (world.isRemote) {
            return true;
        }

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

        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack)) {
            return false;
        }

        if (world.getBlock(n, n2, n3).getMaterial() == Material.air) {
            world.playSoundEffect(n + 0.5, n2 + 0.5, n3 + 0.5, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            world.setBlock(n, n2, n3, Blocks.fire);
        }

        if (!entityPlayer.capabilities.isCreativeMode) {
            itemStack.stackSize--;
        }

        return true;
    }
}
