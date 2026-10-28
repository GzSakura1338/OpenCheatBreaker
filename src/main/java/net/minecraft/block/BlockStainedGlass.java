package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStainedGlass extends BlockBreakable {
    public static IIcon[] iIconArray = new IIcon[16];

    public BlockStainedGlass(Material material) {
        super("glass", material, false);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return iIconArray[meta % iIconArray.length];
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    public static int computeInt(int n) {
        return ~n & 15;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        for (int i = 0; i < iIconArray.length; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        for (int i = 0; i < iIconArray.length; i++) {
            iIconArray[i] = reg.registerIcon(this.getTextureName() + "_" + ItemDye.dyeIcons[computeInt(i)]);
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean canSilkHarvest() {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
}
