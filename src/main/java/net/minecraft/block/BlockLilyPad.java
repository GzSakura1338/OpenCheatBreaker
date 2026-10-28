package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLilyPad extends BlockBush {
    public BlockLilyPad() {
        float f = 0.5F;
        float f2 = 0.015625F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f2, 0.5F + f);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public int getRenderType() {
        return 23;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        if (collider == null || !(collider instanceof EntityBoat)) {
            super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    @Override
    public int getBlockColor() {
        return 2129968;
    }

    @Override
    public int getRenderColor(int meta) {
        return 2129968;
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return 2129968;
    }

    @Override
    public boolean canPlaceBlockOn(Block ground) {
        return ground == Blocks.water;
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        return y >= 0 && y < 256 && worldIn.getBlock(x, y - 1, z).getMaterial() == Material.water && worldIn.getBlockMetadata(x, y - 1, z) == 0;
    }
}
