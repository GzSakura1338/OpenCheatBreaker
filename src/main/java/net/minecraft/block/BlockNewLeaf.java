package net.minecraft.block;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockNewLeaf extends BlockLeaves {
    public static String[][] stringMatrix = new String[][]{{"leaves_acacia", "leaves_big_oak"}, {"leaves_acacia_opaque", "leaves_big_oak_opaque"}};
    public static String[] stringArray = new String[]{"acacia", "big_oak"};

    @Override
    public void handleWorld(World world, int n, int n2, int n3, int n4, int n5) {
        if ((n4 & 3) == 1 && world.rand.nextInt(n5) == 0) {
            this.dropBlockAsItem(world, n, n2, n3, new ItemStack(Items.apple, 1, 0));
        }
    }

    @Override
    public int damageDropped(int meta) {
        return super.damageDropped(meta) + 4;
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return worldIn.getBlockMetadata(x, y, z) & 3;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return (meta & 3) == 1 ? this.iIconMatrix[this.fancyIndex][1] : this.iIconMatrix[this.fancyIndex][0];
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
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
