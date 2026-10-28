package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockWood extends Block {
    public static String[] stringArray = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "big_oak"};
    public IIcon[] iIconArray;

    public BlockWood() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.iIconArray.length) {
            meta = 0;
        }

        return this.iIconArray[meta];
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
        list.add(new ItemStack(itemIn, 1, 2));
        list.add(new ItemStack(itemIn, 1, 3));
        list.add(new ItemStack(itemIn, 1, 4));
        list.add(new ItemStack(itemIn, 1, 5));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[stringArray.length];

        for (int i = 0; i < this.iIconArray.length; i++) {
            this.iIconArray[i] = reg.registerIcon(this.getTextureName() + "_" + stringArray[i]);
        }
    }
}
