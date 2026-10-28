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

public class BlockStoneSlab extends BlockSlab {
    public static String[] stringArray = new String[]{"stone", "sand", "wood", "cobble", "brick", "smoothStoneBrick", "netherBrick", "quartz"};
    public IIcon iIcon2;

    public BlockStoneSlab(boolean flag) {
        super(flag, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        int n = meta & 7;
        if (this.isFullBlock && (meta & 8) != 0) {
            side = 1;
        }

        return n == 0
            ? (side != 1 && side != 0 ? this.iIcon2 : this.blockIcon)
            : (
                n == 1
                    ? Blocks.sandstone.getBlockTextureFromSide(side)
                    : (
                        n == 2
                            ? Blocks.planks.getBlockTextureFromSide(side)
                            : (
                                n == 3
                                    ? Blocks.cobblestone.getBlockTextureFromSide(side)
                                    : (
                                        n == 4
                                            ? Blocks.brick_block.getBlockTextureFromSide(side)
                                            : (
                                                n == 5
                                                    ? Blocks.stonebrick.getIcon(side, 0)
                                                    : (
                                                        n == 6
                                                            ? Blocks.nether_brick.getBlockTextureFromSide(1)
                                                            : (n == 7 ? Blocks.quartz_block.getBlockTextureFromSide(side) : this.blockIcon)
                                                    )
                                            )
                                    )
                            )
                    )
            );
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("stone_slab_top");
        this.iIcon2 = reg.registerIcon("stone_slab_side");
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(Blocks.stone_slab);
    }

    @Override
    public ItemStack createStackedBlock(int meta) {
        return new ItemStack(Item.getItemFromBlock(Blocks.stone_slab), 2, meta & 7);
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
        if (itemIn != Item.getItemFromBlock(Blocks.double_stone_slab)) {
            for (int i = 0; i <= 7; i++) {
                if (i != 2) {
                    list.add(new ItemStack(itemIn, 1, i));
                }
            }
        }
    }
}
