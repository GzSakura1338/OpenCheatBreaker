package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockCompressed extends Block {
    public MapColor mapColor;

    public BlockCompressed(MapColor mapColor) {
        super(Material.iron);
        this.mapColor = mapColor;
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public MapColor getMapColor(int meta) {
        return this.mapColor;
    }
}
