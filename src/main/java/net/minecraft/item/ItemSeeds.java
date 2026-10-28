package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemSeeds extends Item {
    public Block crop;
    public Block soilBlockID;

    public ItemSeeds(Block block, Block block2) {
        this.crop = block;
        this.soilBlockID = block2;
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (n4 != 1) {
            return false;
        } else if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack) || !entityPlayer.canPlayerEdit(n, n2 + 1, n3, n4, itemStack)) {
            return false;
        } else if (world.getBlock(n, n2, n3) == this.soilBlockID && world.isAirBlock(n, n2 + 1, n3)) {
            world.setBlock(n, n2 + 1, n3, this.crop);
            itemStack.stackSize--;
            return true;
        } else {
            return false;
        }
    }
}
