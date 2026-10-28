package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStoneBrick extends Block {
    public static String[] stringArray = new String[]{"default", "mossy", "cracked", "chiseled"};
    public static String[] stringArray2 = new String[]{null, "mossy", "cracked", "carved"};
    public IIcon[] iIconArray;

    public BlockStoneBrick() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta >= stringArray2.length) {
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
        for (int i = 0; i < 4; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[stringArray2.length];

        for (int i = 0; i < this.iIconArray.length; i++) {
            String textureName = this.getTextureName();
            if (stringArray2[i] != null) {
                textureName = textureName + "_" + stringArray2[i];
            }

            this.iIconArray[i] = reg.registerIcon(textureName);
        }
    }
}
