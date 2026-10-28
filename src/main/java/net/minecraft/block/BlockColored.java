package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockColored extends Block {
    public IIcon[] textures;

    public BlockColored(Material material) {
        super(material);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.textures[meta % this.textures.length];
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    public static int computeInt(int n) {
        return computeInt2(n);
    }

    public static int computeInt2(int n) {
        return ~n & 15;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        for (int i = 0; i < 16; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.textures = new IIcon[16];

        for (int i = 0; i < this.textures.length; i++) {
            this.textures[i] = reg.registerIcon(this.getTextureName() + "_" + ItemDye.dyeIcons[computeInt2(i)]);
        }
    }

    @Override
    public MapColor getMapColor(int meta) {
        return MapColor.getMapColorForBlockColored(meta);
    }
}
