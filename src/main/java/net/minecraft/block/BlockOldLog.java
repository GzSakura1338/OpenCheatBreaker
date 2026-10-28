package net.minecraft.block;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockOldLog extends BlockLog {
    public static String[] stringArray = new String[]{"oak", "spruce", "birch", "jungle"};

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
        list.add(new ItemStack(itemIn, 1, 2));
        list.add(new ItemStack(itemIn, 1, 3));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[stringArray.length];
        this.iIconArray2 = new IIcon[stringArray.length];

        for (int i = 0; i < this.iIconArray.length; i++) {
            this.iIconArray[i] = reg.registerIcon(this.getTextureName() + "_" + stringArray[i]);
            this.iIconArray2[i] = reg.registerIcon(this.getTextureName() + "_" + stringArray[i] + "_top");
        }
    }
}
