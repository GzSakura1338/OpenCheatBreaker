package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonExtension extends Block {
    public IIcon iIcon2;

    public BlockPistonExtension() {
        super(Material.piston);
        this.setStepSound(soundTypePiston);
        this.setHardness(0.5F);
    }

    public void setIIcon(IIcon icon) {
        this.iIcon2 = icon;
    }

    public void run() {
        this.iIcon2 = null;
    }

    @Override
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            int directionMeta = getDirectionMeta(meta);
            Block block = worldIn.getBlock(x - Facing.offsetsXForSide[directionMeta], y - Facing.offsetsYForSide[directionMeta], z - Facing.offsetsZForSide[directionMeta]);
            if (block == Blocks.piston || block == Blocks.sticky_piston) {
                worldIn.setBlockToAir(x - Facing.offsetsXForSide[directionMeta], y - Facing.offsetsYForSide[directionMeta], z - Facing.offsetsZForSide[directionMeta]);
            }
        }

        super.onBlockHarvested(worldIn, x, y, z, meta, player);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        int n = Facing.oppositeSide[getDirectionMeta(meta)];
        x += Facing.offsetsXForSide[n];
        y += Facing.offsetsYForSide[n];
        z += Facing.offsetsZForSide[n];
        Block block = worldIn.getBlock(x, y, z);
        if (block == Blocks.piston || block == Blocks.sticky_piston) {
            meta = worldIn.getBlockMetadata(x, y, z);
            if (BlockPistonBase.isExtended(meta)) {
                block.dropBlockAsItem(worldIn, x, y, z, meta, 0);
                worldIn.setBlockToAir(x, y, z);
            }
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        int directionMeta = getDirectionMeta(meta);
        return side == directionMeta
            ? (
                this.iIcon2 != null
                    ? this.iIcon2
                    : ((meta & 8) != 0 ? BlockPistonBase.getPistonBaseIcon("piston_top_sticky") : BlockPistonBase.getPistonBaseIcon("piston_top_normal"))
            )
            : (
                directionMeta < 6 && side == Facing.oppositeSide[directionMeta]
                    ? BlockPistonBase.getPistonBaseIcon("piston_top_normal")
                    : BlockPistonBase.getPistonBaseIcon("piston_side")
            );
    }

    @Override
    public void registerIcons(IIconRegister reg) {
    }

    @Override
    public int getRenderType() {
        return 17;
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
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        float f = 0.25F;
        float f2 = 0.375F;
        float f3 = 0.625F;
        float f4 = 0.25F;
        float f5 = 0.75F;
        switch (getDirectionMeta(blockMetadata)) {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                break;
            case 1:
                this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                break;
            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                break;
            case 3:
                this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                break;
            case 4:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                break;
            case 5:
                this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
                this.setBlockBounds(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        float f = 0.25F;
        switch (getDirectionMeta(blockMetadata)) {
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                break;
            case 1:
                this.setBlockBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                break;
            case 3:
                this.setBlockBounds(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                break;
            case 4:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                break;
            case 5:
                this.setBlockBounds(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        int directionMeta = getDirectionMeta(worldIn.getBlockMetadata(x, y, z));
        Block block = worldIn.getBlock(x - Facing.offsetsXForSide[directionMeta], y - Facing.offsetsYForSide[directionMeta], z - Facing.offsetsZForSide[directionMeta]);
        if (block != Blocks.piston && block != Blocks.sticky_piston) {
            worldIn.setBlockToAir(x, y, z);
        } else {
            block.onNeighborBlockChange(worldIn, x - Facing.offsetsXForSide[directionMeta], y - Facing.offsetsYForSide[directionMeta], z - Facing.offsetsZForSide[directionMeta], neighbor);
        }
    }

    public static int getDirectionMeta(int n) {
        return MathHelper.clamp_int(n & 7, 0, Facing.offsetsXForSide.length - 1);
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        return (blockMetadata & 8) != 0 ? Item.getItemFromBlock(Blocks.sticky_piston) : Item.getItemFromBlock(Blocks.piston);
    }
}
