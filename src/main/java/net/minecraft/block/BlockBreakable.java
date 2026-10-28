package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;

public class BlockBreakable extends Block {
    public boolean ignoreSimilarity;
    public String name;

    public BlockBreakable(String name, Material materialIn, boolean ignoreSimilarity) {
        super(materialIn);
        this.ignoreSimilarity = ignoreSimilarity;
        this.name = name;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        Block block = worldIn.getBlock(x, y, z);
        if (this == Blocks.glass || this == Blocks.stained_glass) {
            if (worldIn.getBlockMetadata(x, y, z)
                != worldIn.getBlockMetadata(x - Facing.offsetsXForSide[side], y - Facing.offsetsYForSide[side], z - Facing.offsetsZForSide[side])) {
                return true;
            }

            if (block == this) {
                return false;
            }
        }

        return (this.ignoreSimilarity || block != this) && super.shouldSideBeRendered(worldIn, x, y, z, side);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.name);
    }
}
