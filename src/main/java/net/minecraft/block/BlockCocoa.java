package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCocoa extends BlockDirectional implements IGrowable {
    public IIcon[] iIconArray;

    public BlockCocoa() {
        super(Material.plants);
        this.setTickRandomly(true);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.iIconArray[2];
    }

    public IIcon getCocoaIcon(int n) {
        if (n < 0 || n >= this.iIconArray.length) {
            n = this.iIconArray.length - 1;
        }

        return this.iIconArray[n];
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!this.canBlockStay(worldIn, x, y, z)) {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlock(x, y, z, getBlockById(0), 0, 2);
        } else if (worldIn.rand.nextInt(5) == 0) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            int computeInt2 = computeInt(blockMetadata);
            if (computeInt2 < 2) {
                worldIn.setBlockMetadataWithNotify(x, y, z, ++computeInt2 << 2 | getDirection(blockMetadata), 2);
            }
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        int direction = getDirection(worldIn.getBlockMetadata(x, y, z));
        x += Direction.offsetX[direction];
        z += Direction.offsetZ[direction];
        Block block = worldIn.getBlock(x, y, z);
        return block == Blocks.log && BlockLog.computeInt4(worldIn.getBlockMetadata(x, y, z)) == 3;
    }

    @Override
    public int getRenderType() {
        return 28;
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
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int direction = getDirection(blockMetadata);
        int computeInt2 = computeInt(blockMetadata);
        int n = 4 + computeInt2 * 2;
        int n2 = 5 + computeInt2 * 2;
        float f = n / 2.0F;
        switch (direction) {
            case 0:
                this.setBlockBounds((8.0F - f) / 16.0F, (12.0F - n2) / 16.0F, (15.0F - n) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F);
                break;
            case 1:
                this.setBlockBounds(0.0625F, (12.0F - n2) / 16.0F, (8.0F - f) / 16.0F, (1.0F + n) / 16.0F, 0.75F, (8.0F + f) / 16.0F);
                break;
            case 2:
                this.setBlockBounds((8.0F - f) / 16.0F, (12.0F - n2) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + n) / 16.0F);
                break;
            case 3:
                this.setBlockBounds((15.0F - n) / 16.0F, (12.0F - n2) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int n = ((MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 3) + 0) % 4;
        worldIn.setBlockMetadataWithNotify(x, y, z, n, 2);
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        if (side == 1 || side == 0) {
            side = 2;
        }

        return Direction.rotateOpposite[Direction.facingToDirection[side]];
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!this.canBlockStay(worldIn, x, y, z)) {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlock(x, y, z, getBlockById(0), 0, 2);
        }
    }

    public static int computeInt(int n) {
        return (n & 12) >> 2;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        int computeInt2 = computeInt(meta);
        byte b = 1;
        if (computeInt2 >= 2) {
            b = 3;
        }

        for (int i = 0; i < b; i++) {
            this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(Items.dye, 1, 3));
        }
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.dye;
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return 3;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[3];

        for (int i = 0; i < this.iIconArray.length; i++) {
            this.iIconArray[i] = reg.registerIcon(this.getTextureName() + "_stage_" + i);
        }
    }

    @Override
    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int computeInt2 = computeInt(blockMetadata);
        return computeInt2 < 2;
    }

    @Override
    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z) {
        return true;
    }

    @Override
    public void fertilize(World worldIn, Random random, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int direction = BlockDirectional.getDirection(blockMetadata);
        int computeInt2 = computeInt(blockMetadata);
        worldIn.setBlockMetadataWithNotify(x, y, z, ++computeInt2 << 2 | direction, 2);
    }
}
