package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

public class BlockMelon extends Block {
    public IIcon melonTopIcon;

    public BlockMelon() {
        super(Material.gourd);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side != 1 && side != 0 ? this.blockIcon : this.melonTopIcon;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.melon;
    }

    @Override
    public int quantityDropped(Random random) {
        return 3 + random.nextInt(5);
    }

    @Override
    public int quantityDroppedWithBonus(int maxBonus, Random random) {
        int n = this.quantityDropped(random) + random.nextInt(1 + maxBonus);
        if (n > 9) {
            n = 9;
        }

        return n;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.melonTopIcon = reg.registerIcon(this.getTextureName() + "_top");
    }
}
