package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemShears extends Item {
    public ItemShears() {
        this.setMaxStackSize(1);
        this.setMaxDurability(238);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, int n, int n2, int n3, EntityLivingBase entityLivingBase) {
        if (blockIn.getMaterial() != Material.leaves
            && blockIn != Blocks.web
            && blockIn != Blocks.tallgrass
            && blockIn != Blocks.vine
            && blockIn != Blocks.tripwire) {
            return super.onBlockDestroyed(stack, worldIn, blockIn, n, n2, n3, entityLivingBase);
        }

        stack.damageItem(1, entityLivingBase);
        return true;
    }

    @Override
    public boolean canItemHarvestBlock(Block block) {
        return block == Blocks.web || block == Blocks.redstone_wire || block == Blocks.tripwire;
    }

    @Override
    public float getStrVsBlock(ItemStack itemStack, Block block) {
        return block != Blocks.web && block.getMaterial() != Material.leaves ? (block == Blocks.wool ? 5.0F : super.getStrVsBlock(itemStack, block)) : 15.0F;
    }
}
