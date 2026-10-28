package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockLeavesBase extends Block {
    public boolean fancyGraphics;

    public BlockLeavesBase(Material material, boolean flag) {
        super(material);
        this.fancyGraphics = flag;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        Block block = worldIn.getBlock(x, y, z);
        return (this.fancyGraphics || block != this) && super.shouldSideBeRendered(worldIn, x, y, z, side);
    }
}
