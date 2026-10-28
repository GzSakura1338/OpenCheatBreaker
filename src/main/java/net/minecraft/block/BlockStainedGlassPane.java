package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStainedGlassPane extends BlockPane {
    public static IIcon[] iIconArray = new IIcon[16];
    public static IIcon[] iIconArray2 = new IIcon[16];

    public BlockStainedGlassPane() {
        super("glass", "glass_pane_top", Material.glass, false);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public IIcon getItemIcon(int side, int meta) {
        return iIconArray[meta % iIconArray.length];
    }

    public IIcon findIIcon(int n) {
        return iIconArray2[~n & 15];
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.getItemIcon(side, ~meta & 15);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    public static int computeInt(int n) {
        return n & 15;
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
        super.registerIcons(reg);

        for (int i = 0; i < iIconArray.length; i++) {
            iIconArray[i] = reg.registerIcon(this.getTextureName() + "_" + ItemDye.dyeIcons[computeInt(i)]);
            iIconArray2[i] = reg.registerIcon(this.getTextureName() + "_pane_top_" + ItemDye.dyeIcons[computeInt(i)]);
        }
    }
}
