package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockWoodSlab extends BlockSlab {
    public static String[] stringArray = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "big_oak"};

    public BlockWoodSlab(boolean flag) {
        super(flag, Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getIcon(side, meta & 7);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(Blocks.wooden_slab);
    }

    @Override
    public ItemStack createStackedBlock(int meta) {
        return new ItemStack(Item.getItemFromBlock(Blocks.wooden_slab), 2, meta & 7);
    }

    @Override
    public String getFullSlabName(int n) {
        if (n < 0 || n >= stringArray.length) {
            n = 0;
        }

        return super.getUnlocalizedName() + "." + stringArray[n];
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        if (itemIn != Item.getItemFromBlock(Blocks.double_wooden_slab)) {
            for (int i = 0; i < stringArray.length; i++) {
                list.add(new ItemStack(itemIn, 1, i));
            }
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
    }
}
