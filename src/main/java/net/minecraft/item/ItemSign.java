package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemSign extends Item {
    public ItemSign() {
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) throws java.io.IOException {
        if (n4 == 0) {
            return false;
        }

        if (!world.getBlock(n, n2, n3).getMaterial().isSolid()) {
            return false;
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

        if (!Blocks.standing_sign.canPlaceBlockAt(world, n, n2, n3)) {
            return false;
        }

        if (world.isRemote) {
            return true;
        }

        if (n4 == 1) {
            int n5 = MathHelper.floor_double((entityPlayer.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5) & 15;
            world.setBlock(n, n2, n3, Blocks.standing_sign, n5, 3);
        } else {
            world.setBlock(n, n2, n3, Blocks.wall_sign, n4, 3);
        }

        itemStack.stackSize--;
        TileEntitySign tileEntity = (TileEntitySign)world.getTileEntity(n, n2, n3);
        if (tileEntity != null) {
            entityPlayer.displayGUIEditSign(tileEntity);
        }

        return true;
    }
}
