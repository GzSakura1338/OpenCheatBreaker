package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockQuartz extends Block {
    public static String[] stringArray = new String[]{"default", "chiseled", "lines"};
    public static String[] stringArray2 = new String[]{"side", "chiseled", "lines", null, null};
    public IIcon[] iIconArray;
    public IIcon iIcon5;
    public IIcon iIcon2;
    public IIcon iIcon3;
    public IIcon iIcon4;

    public BlockQuartz() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta != 2 && meta != 3 && meta != 4) {
            if (side != 1 && (side != 0 || meta != 1)) {
                if (side == 0) {
                    return this.iIcon4;
                }

                if (meta < 0 || meta >= this.iIconArray.length) {
                    meta = 0;
                }

                return this.iIconArray[meta];
            } else {
                return meta == 1 ? this.iIcon5 : this.iIcon3;
            }
        } else {
            return meta != 2 || side != 1 && side != 0
                ? (meta != 3 || side != 5 && side != 4 ? (meta != 4 || side != 2 && side != 3 ? this.iIconArray[meta] : this.iIcon2) : this.iIcon2)
                : this.iIcon2;
        }
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        if (meta == 2) {
            switch (side) {
                case 0:
                case 1:
                    meta = 2;
                    break;
                case 2:
                case 3:
                    meta = 4;
                    break;
                case 4:
                case 5:
                    meta = 3;
            }
        }

        return meta;
    }

    @Override
    public int damageDropped(int meta) {
        return meta != 3 && meta != 4 ? meta : 2;
    }

    @Override
    public ItemStack createStackedBlock(int meta) {
        return meta != 3 && meta != 4 ? super.createStackedBlock(meta) : new ItemStack(Item.getItemFromBlock(this), 1, 2);
    }

    @Override
    public int getRenderType() {
        return 39;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
        list.add(new ItemStack(itemIn, 1, 2));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[stringArray2.length];

        for (int i = 0; i < this.iIconArray.length; i++) {
            if (stringArray2[i] == null) {
                this.iIconArray[i] = this.iIconArray[i - 1];
            } else {
                this.iIconArray[i] = reg.registerIcon(this.getTextureName() + "_" + stringArray2[i]);
            }
        }

        this.iIcon3 = reg.registerIcon(this.getTextureName() + "_top");
        this.iIcon5 = reg.registerIcon(this.getTextureName() + "_chiseled_top");
        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_lines_top");
        this.iIcon4 = reg.registerIcon(this.getTextureName() + "_bottom");
    }

    @Override
    public MapColor getMapColor(int meta) {
        return MapColor.quartzColor;
    }
}
