package net.minecraft.item;

import net.minecraft.block.BlockBed;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBed extends Item {
    public ItemBed() {
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (world.isRemote) {
            return true;
        }

        if (n4 != 1) {
            return false;
        }

        n2++;
        BlockBed bed = (BlockBed)Blocks.bed;
        int n5 = MathHelper.floor_double(entityPlayer.rotationYaw * 4.0F / 360.0F + 0.5) & 3;
        byte b = 0;
        byte b2 = 0;
        if (n5 == 0) {
            b2 = 1;
        }

        if (n5 == 1) {
            b = -1;
        }

        if (n5 == 2) {
            b2 = -1;
        }

        if (n5 == 3) {
            b = 1;
        }

        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack) || !entityPlayer.canPlayerEdit(n + b, n2, n3 + b2, n4, itemStack)) {
            return false;
        }

        if (world.isAirBlock(n, n2, n3)
            && world.isAirBlock(n + b, n2, n3 + b2)
            && World.doesBlockHaveSolidTopSurface(world, n, n2 - 1, n3)
            && World.doesBlockHaveSolidTopSurface(world, n + b, n2 - 1, n3 + b2)) {
            world.setBlock(n, n2, n3, bed, n5, 3);
            if (world.getBlock(n, n2, n3) == bed) {
                world.setBlock(n + b, n2, n3 + b2, bed, n5 + 8, 3);
            }

            itemStack.stackSize--;
            return true;
        } else {
            return false;
        }
    }
}
