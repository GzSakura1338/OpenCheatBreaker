package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

public class BlockGlowstone extends Block {
    public BlockGlowstone(Material material) {
        super(material);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public int quantityDroppedWithBonus(int maxBonus, Random random) {
        return MathHelper.clamp_int(this.quantityDropped(random) + random.nextInt(maxBonus + 1), 1, 4);
    }

    @Override
    public int quantityDropped(Random random) {
        return 2 + random.nextInt(3);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.glowstone_dust;
    }

    @Override
    public MapColor getMapColor(int meta) {
        return MapColor.sandColor;
    }
}
