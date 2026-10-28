package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockSandStone extends Block {
    public static String[] VARIANTS = new String[]{"default", "chiseled", "smooth"};
    public static String[] TEXTURE_NAMES = new String[]{"normal", "carved", "smooth"};
    public IIcon[] sides;
    public IIcon top;
    public IIcon bottom;

    public BlockSandStone() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side != 1 && (side != 0 || meta != 1 && meta != 2)) {
            if (side == 0) {
                return this.bottom;
            }

            if (meta < 0 || meta >= this.sides.length) {
                meta = 0;
            }

            return this.sides[meta];
        } else {
            return this.top;
        }
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
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.sides = new IIcon[TEXTURE_NAMES.length];

        for (int i = 0; i < this.sides.length; i++) {
            this.sides[i] = reg.registerIcon(this.getTextureName() + "_" + TEXTURE_NAMES[i]);
        }

        this.top = reg.registerIcon(this.getTextureName() + "_top");
        this.bottom = reg.registerIcon(this.getTextureName() + "_bottom");
    }
}
