package net.minecraft.block;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockOldLeaf extends BlockLeaves {
    public static String[][] stringMatrix = new String[][]{
        {"leaves_oak", "leaves_spruce", "leaves_birch", "leaves_jungle"},
        {"leaves_oak_opaque", "leaves_spruce_opaque", "leaves_birch_opaque", "leaves_jungle_opaque"}
    };
    public static String[] stringArray = new String[]{"oak", "spruce", "birch", "jungle"};

    @Override
    public int getRenderColor(int meta) {
        return (meta & 3) == 1
            ? ColorizerFoliage.getFoliageColorPine()
            : ((meta & 3) == 2 ? ColorizerFoliage.getFoliageColorBirch() : super.getRenderColor(meta));
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        return (blockMetadata & 3) == 1
            ? ColorizerFoliage.getFoliageColorPine()
            : ((blockMetadata & 3) == 2 ? ColorizerFoliage.getFoliageColorBirch() : super.colorMultiplier(worldIn, x, y, z));
    }

    @Override
    public void handleWorld(World world, int n, int n2, int n3, int n4, int n5) {
        if ((n4 & 3) == 0 && world.rand.nextInt(n5) == 0) {
            this.dropBlockAsItem(world, n, n2, n3, new ItemStack(Items.apple, 1, 0));
        }
    }

    @Override
    public int computeInt(int n) {
        int computeInt = super.computeInt(n);
        if ((n & 3) == 3) {
            computeInt = 40;
        }

        return computeInt;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return (meta & 3) == 1
            ? this.iIconMatrix[this.fancyIndex][1]
            : (
                (meta & 3) == 3
                    ? this.iIconMatrix[this.fancyIndex][3]
                    : ((meta & 3) == 2 ? this.iIconMatrix[this.fancyIndex][2] : this.iIconMatrix[this.fancyIndex][0])
            );
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
        list.add(new ItemStack(itemIn, 1, 2));
        list.add(new ItemStack(itemIn, 1, 3));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        for (int i = 0; i < stringMatrix.length; i++) {
            this.iIconMatrix[i] = new IIcon[stringMatrix[i].length];

            for (int j = 0; j < stringMatrix[i].length; j++) {
                this.iIconMatrix[i][j] = reg.registerIcon(stringMatrix[i][j]);
            }
        }
    }

    @Override
    public String[] getStringArray() {
        return stringArray;
    }
}
