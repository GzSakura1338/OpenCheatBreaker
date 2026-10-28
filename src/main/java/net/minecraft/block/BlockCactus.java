package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockCactus extends Block {
    public IIcon iconTop;
    public IIcon iconBottom;

    public BlockCactus() {
        super(Material.cactus);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (worldIn.isAirBlock(x, y + 1, z)) {
            int n = 1;

            while (worldIn.getBlock(x, y - n, z) == this) {
                n++;
            }

            if (n < 3) {
                int blockMetadata = worldIn.getBlockMetadata(x, y, z);
                if (blockMetadata == 15) {
                    worldIn.setBlock(x, y + 1, z, this);
                    worldIn.setBlockMetadataWithNotify(x, y, z, 0, 4);
                    this.onNeighborBlockChange(worldIn, x, y + 1, z, this);
                } else {
                    worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata + 1, 4);
                }
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        float f = 0.0625F;
        return AxisAlignedBB.getBoundingBox(x + f, y, z + f, x + 1 - f, y + 1 - f, z + 1 - f);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        float f = 0.0625F;
        return AxisAlignedBB.getBoundingBox(x + f, y, z + f, x + 1 - f, y + 1, z + 1 - f);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iconTop : (side == 0 ? this.iconBottom : this.blockIcon);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 13;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return super.canPlaceBlockAt(worldIn, x, y, z) && this.canBlockStay(worldIn, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!this.canBlockStay(worldIn, x, y, z)) {
            worldIn.breakBlock(x, y, z, true);
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        if (worldIn.getBlock(x - 1, y, z).getMaterial().isSolid()) {
            return false;
        }

        if (worldIn.getBlock(x + 1, y, z).getMaterial().isSolid()) {
            return false;
        }

        if (worldIn.getBlock(x, y, z - 1).getMaterial().isSolid()) {
            return false;
        }

        if (worldIn.getBlock(x, y, z + 1).getMaterial().isSolid()) {
            return false;
        }

        Block block = worldIn.getBlock(x, y - 1, z);
        return block == Blocks.cactus || block == Blocks.sand;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        entityIn.attackEntityFrom(DamageSource.cactus, 1.0F);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.iconTop = reg.registerIcon(this.getTextureName() + "_top");
        this.iconBottom = reg.registerIcon(this.getTextureName() + "_bottom");
    }
}
