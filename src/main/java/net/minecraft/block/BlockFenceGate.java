package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional {
    public BlockFenceGate() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getBlockTextureFromSide(side);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return worldIn.getBlock(x, y - 1, z).getMaterial().isSolid() && super.canPlaceBlockAt(worldIn, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        return isFenceGateOpen(blockMetadata)
            ? null
            : (
                blockMetadata != 2 && blockMetadata != 0
                    ? AxisAlignedBB.getBoundingBox(x + 0.375F, y, z, x + 0.625F, y + 1.5F, z + 1)
                    : AxisAlignedBB.getBoundingBox(x, y, z + 0.375F, x + 1, y + 1.5F, z + 0.625F)
            );
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int direction = getDirection(worldIn.getBlockMetadata(x, y, z));
        if (direction != 2 && direction != 0) {
            this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z) {
        return isFenceGateOpen(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    public int getRenderType() {
        return 21;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int n = (MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 3) % 4;
        worldIn.setBlockMetadataWithNotify(x, y, z, n, 2);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (isFenceGateOpen(blockMetadata)) {
            worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata & -5, 2);
        } else {
            int n = (MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5) & 3) % 4;
            int direction = getDirection(blockMetadata);
            if (direction == (n + 2) % 4) {
                blockMetadata = n;
            }

            worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata | 4, 2);
        }

        worldIn.playAuxSFXAtEntity(player, 1003, x, y, z, 0);
        return true;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!worldIn.isRemote) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            boolean isBlockIndirectlyGettingPowered = worldIn.isBlockIndirectlyGettingPowered(x, y, z);
            if (isBlockIndirectlyGettingPowered || neighbor.canProvidePower()) {
                if (isBlockIndirectlyGettingPowered && !isFenceGateOpen(blockMetadata)) {
                    worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata | 4, 2);
                    worldIn.playAuxSFXAtEntity(null, 1003, x, y, z, 0);
                } else if (!isBlockIndirectlyGettingPowered && isFenceGateOpen(blockMetadata)) {
                    worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata & -5, 2);
                    worldIn.playAuxSFXAtEntity(null, 1003, x, y, z, 0);
                }
            }
        }
    }

    public static boolean isFenceGateOpen(int n) {
        return (n & 4) != 0;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
    }
}
