package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockSand extends BlockFalling {
    public static String[] VARIANTS = new String[]{"default", "red"};
    public static IIcon sandIcon;
    public static IIcon redSandIcon;

    @Override
    public IIcon getIcon(int side, int meta) {
        return meta == 1 ? redSandIcon : sandIcon;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        sandIcon = reg.registerIcon("sand");
        redSandIcon = reg.registerIcon("red_sand");
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
    }

    @Override
    public MapColor getMapColor(int meta) {
        return meta == 1 ? MapColor.dirtColor : MapColor.sandColor;
    }
}
