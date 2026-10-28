package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockFlower extends BlockBush {
    public static String[][] stringMatrix = new String[][]{
        {"flower_dandelion"},
        {
                "flower_rose",
                "flower_blue_orchid",
                "flower_allium",
                "flower_houstonia",
                "flower_tulip_red",
                "flower_tulip_orange",
                "flower_tulip_white",
                "flower_tulip_pink",
                "flower_oxeye_daisy"
        }
    };
    public static String[] stringArray = new String[]{
        "poppy", "blueOrchid", "allium", "houstonia", "tulipRed", "tulipOrange", "tulipWhite", "tulipPink", "oxeyeDaisy"
    };
    public static String[] stringArray2 = new String[]{"dandelion"};
    public IIcon[] iIconArray;
    public int intValue;

    public BlockFlower(int n) {
        super(Material.plants);
        this.intValue = n;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta >= this.iIconArray.length) {
            meta = 0;
        }

        return this.iIconArray[meta];
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[stringMatrix[this.intValue].length];

        for (int i = 0; i < this.iIconArray.length; i++) {
            this.iIconArray[i] = reg.registerIcon(stringMatrix[this.intValue][i]);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        for (int i = 0; i < this.iIconArray.length; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    public static BlockFlower findBlockFlower(String str) {
        for (String str2 : stringArray2) {
            if (str2.equals(str)) {
                return Blocks.yellow_flower;
            }
        }

        for (String str2 : stringArray) {
            if (str2.equals(str)) {
                return Blocks.red_flower;
            }
        }

        return null;
    }

    public static int computeInt(String str) {
        for (int i = 0; i < stringArray2.length; i++) {
            if (stringArray2[i].equals(str)) {
                return i;
            }
        }

        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].equals(str)) {
                return i;
            }
        }

        return 0;
    }
}
