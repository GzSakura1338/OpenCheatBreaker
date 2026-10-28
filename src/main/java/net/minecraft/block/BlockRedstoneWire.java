package net.minecraft.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block {
    public boolean canProvidePower = true;
    public Set affectedNeighbors = new HashSet();
    public IIcon redstoneCrossIcon;
    public IIcon redstoneLineIcon;
    public IIcon redstoneCrossOverlayIcon;
    public IIcon redstoneLineOverlayIcon;

    public BlockRedstoneWire() {
        super(Material.circuits);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
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
    public int getRenderType() {
        return 5;
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return 8388608;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) || worldIn.getBlock(x, y - 1, z) == Blocks.glowstone;
    }

    public void handleWorld(World world, int n, int n2, int n3) {
        this.handleWorld(world, n, n2, n3, n, n2, n3);
        ArrayList list = new ArrayList(this.affectedNeighbors);
        this.affectedNeighbors.clear();

        for (int i = 0; i < list.size(); i++) {
            ChunkPosition chunkPosition = (ChunkPosition)list.get(i);
            world.notifyBlocksOfNeighborChange(chunkPosition.chunkPosX, chunkPosition.chunkPosY, chunkPosition.chunkPosZ, this);
        }
    }

    public void handleWorld(World world, int n, int n2, int n3, int n4, int n5, int n6) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        byte b = 0;
        int computeInt = this.computeInt(world, n4, n5, n6, b);
        this.canProvidePower = false;
        int strongestIndirectPower = world.getStrongestIndirectPower(n, n2, n3);
        this.canProvidePower = true;
        if (strongestIndirectPower > 0 && strongestIndirectPower > computeInt - 1) {
            computeInt = strongestIndirectPower;
        }

        int n7 = 0;

        for (int i = 0; i < 4; i++) {
            int n8 = n;
            int n9 = n3;
            if (i == 0) {
                n8 = n - 1;
            }

            if (i == 1) {
                n8++;
            }

            if (i == 2) {
                n9 = n3 - 1;
            }

            if (i == 3) {
                n9++;
            }

            if (n8 != n4 || n9 != n6) {
                n7 = this.computeInt(world, n8, n2, n9, n7);
            }

            if (world.getBlock(n8, n2, n9).isNormalCube() && !world.getBlock(n, n2 + 1, n3).isNormalCube()) {
                if ((n8 != n4 || n9 != n6) && n2 >= n5) {
                    n7 = this.computeInt(world, n8, n2 + 1, n9, n7);
                }
            } else if (!world.getBlock(n8, n2, n9).isNormalCube() && (n8 != n4 || n9 != n6) && n2 <= n5) {
                n7 = this.computeInt(world, n8, n2 - 1, n9, n7);
            }
        }

        if (n7 > computeInt) {
            computeInt = n7 - 1;
        } else if (computeInt > 0) {
            computeInt--;
        } else {
            computeInt = 0;
        }

        if (strongestIndirectPower > computeInt - 1) {
            computeInt = strongestIndirectPower;
        }

        if (blockMetadata != computeInt) {
            world.setBlockMetadataWithNotify(n, n2, n3, computeInt, 2);
            this.affectedNeighbors.add(new ChunkPosition(n, n2, n3));
            this.affectedNeighbors.add(new ChunkPosition(n - 1, n2, n3));
            this.affectedNeighbors.add(new ChunkPosition(n + 1, n2, n3));
            this.affectedNeighbors.add(new ChunkPosition(n, n2 - 1, n3));
            this.affectedNeighbors.add(new ChunkPosition(n, n2 + 1, n3));
            this.affectedNeighbors.add(new ChunkPosition(n, n2, n3 - 1));
            this.affectedNeighbors.add(new ChunkPosition(n, n2, n3 + 1));
        }
    }

    public void handleWorld2(World world, int n, int n2, int n3) {
        if (world.getBlock(n, n2, n3) == this) {
            world.notifyBlocksOfNeighborChange(n, n2, n3, this);
            world.notifyBlocksOfNeighborChange(n - 1, n2, n3, this);
            world.notifyBlocksOfNeighborChange(n + 1, n2, n3, this);
            world.notifyBlocksOfNeighborChange(n, n2, n3 - 1, this);
            world.notifyBlocksOfNeighborChange(n, n2, n3 + 1, this);
            world.notifyBlocksOfNeighborChange(n, n2 - 1, n3, this);
            world.notifyBlocksOfNeighborChange(n, n2 + 1, n3, this);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        super.onBlockAdded(worldIn, x, y, z);
        if (!worldIn.isRemote) {
            this.handleWorld(worldIn, x, y, z);
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            this.handleWorld2(worldIn, x - 1, y, z);
            this.handleWorld2(worldIn, x + 1, y, z);
            this.handleWorld2(worldIn, x, y, z - 1);
            this.handleWorld2(worldIn, x, y, z + 1);
            if (worldIn.getBlock(x - 1, y, z).isNormalCube()) {
                this.handleWorld2(worldIn, x - 1, y + 1, z);
            } else {
                this.handleWorld2(worldIn, x - 1, y - 1, z);
            }

            if (worldIn.getBlock(x + 1, y, z).isNormalCube()) {
                this.handleWorld2(worldIn, x + 1, y + 1, z);
            } else {
                this.handleWorld2(worldIn, x + 1, y - 1, z);
            }

            if (worldIn.getBlock(x, y, z - 1).isNormalCube()) {
                this.handleWorld2(worldIn, x, y + 1, z - 1);
            } else {
                this.handleWorld2(worldIn, x, y - 1, z - 1);
            }

            if (worldIn.getBlock(x, y, z + 1).isNormalCube()) {
                this.handleWorld2(worldIn, x, y + 1, z + 1);
            } else {
                this.handleWorld2(worldIn, x, y - 1, z + 1);
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        if (!worldIn.isRemote) {
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            this.handleWorld(worldIn, x, y, z);
            this.handleWorld2(worldIn, x - 1, y, z);
            this.handleWorld2(worldIn, x + 1, y, z);
            this.handleWorld2(worldIn, x, y, z - 1);
            this.handleWorld2(worldIn, x, y, z + 1);
            if (worldIn.getBlock(x - 1, y, z).isNormalCube()) {
                this.handleWorld2(worldIn, x - 1, y + 1, z);
            } else {
                this.handleWorld2(worldIn, x - 1, y - 1, z);
            }

            if (worldIn.getBlock(x + 1, y, z).isNormalCube()) {
                this.handleWorld2(worldIn, x + 1, y + 1, z);
            } else {
                this.handleWorld2(worldIn, x + 1, y - 1, z);
            }

            if (worldIn.getBlock(x, y, z - 1).isNormalCube()) {
                this.handleWorld2(worldIn, x, y + 1, z - 1);
            } else {
                this.handleWorld2(worldIn, x, y - 1, z - 1);
            }

            if (worldIn.getBlock(x, y, z + 1).isNormalCube()) {
                this.handleWorld2(worldIn, x, y + 1, z + 1);
            } else {
                this.handleWorld2(worldIn, x, y - 1, z + 1);
            }
        }
    }

    public int computeInt(World world, int n, int n2, int n3, int n4) {
        if (world.getBlock(n, n2, n3) != this) {
            return n4;
        }

        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        return blockMetadata > n4 ? blockMetadata : n4;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!worldIn.isRemote) {
            boolean canPlaceBlockAt = this.canPlaceBlockAt(worldIn, x, y, z);
            if (canPlaceBlockAt) {
                this.handleWorld(worldIn, x, y, z);
            } else {
                this.dropBlockAsItem(worldIn, x, y, z, 0, 0);
                worldIn.setBlockToAir(x, y, z);
            }

            super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.redstone;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return !this.canProvidePower ? 0 : this.isProvidingWeakPower(worldIn, x, y, z, side);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        if (!this.canProvidePower) {
            return 0;
        }

        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (blockMetadata == 0) {
            return 0;
        }

        if (side == 1) {
            return blockMetadata;
        }

        boolean flag = checkIBlockAccess(worldIn, x - 1, y, z, 1)
            || !worldIn.getBlock(x - 1, y, z).isNormalCube() && checkIBlockAccess(worldIn, x - 1, y - 1, z, -1);
        boolean flag2 = checkIBlockAccess(worldIn, x + 1, y, z, 3)
            || !worldIn.getBlock(x + 1, y, z).isNormalCube() && checkIBlockAccess(worldIn, x + 1, y - 1, z, -1);
        boolean flag3 = checkIBlockAccess(worldIn, x, y, z - 1, 2)
            || !worldIn.getBlock(x, y, z - 1).isNormalCube() && checkIBlockAccess(worldIn, x, y - 1, z - 1, -1);
        boolean flag4 = checkIBlockAccess(worldIn, x, y, z + 1, 0)
            || !worldIn.getBlock(x, y, z + 1).isNormalCube() && checkIBlockAccess(worldIn, x, y - 1, z + 1, -1);
        if (!worldIn.getBlock(x, y + 1, z).isNormalCube()) {
            if (worldIn.getBlock(x - 1, y, z).isNormalCube() && checkIBlockAccess(worldIn, x - 1, y + 1, z, -1)) {
                flag = true;
            }

            if (worldIn.getBlock(x + 1, y, z).isNormalCube() && checkIBlockAccess(worldIn, x + 1, y + 1, z, -1)) {
                flag2 = true;
            }

            if (worldIn.getBlock(x, y, z - 1).isNormalCube() && checkIBlockAccess(worldIn, x, y + 1, z - 1, -1)) {
                flag3 = true;
            }

            if (worldIn.getBlock(x, y, z + 1).isNormalCube() && checkIBlockAccess(worldIn, x, y + 1, z + 1, -1)) {
                flag4 = true;
            }
        }

        return !flag3 && !flag2 && !flag && !flag4 && side >= 2 && side <= 5
            ? blockMetadata
            : (
                side == 2 && flag3 && !flag && !flag2
                    ? blockMetadata
                    : (
                        side == 3 && flag4 && !flag && !flag2
                            ? blockMetadata
                            : (side == 4 && flag && !flag3 && !flag4 ? blockMetadata : (side == 5 && flag2 && !flag3 && !flag4 ? blockMetadata : 0))
                    )
            );
    }

    @Override
    public boolean canProvidePower() {
        return this.canProvidePower;
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (blockMetadata > 0) {
            double d = x + 0.5 + (random.nextFloat() - 0.5) * 0.2;
            double d2 = y + 0.0625F;
            double d3 = z + 0.5 + (random.nextFloat() - 0.5) * 0.2;
            float f = blockMetadata / 15.0F;
            float f2 = f * 0.6F + 0.4F;
            if (blockMetadata == 0) {
                f2 = 0.0F;
            }

            float f3 = f * f * 0.7F - 0.5F;
            float f4 = f * f * 0.6F - 0.7F;
            if (f3 < 0.0F) {
                f3 = 0.0F;
            }

            if (f4 < 0.0F) {
                f4 = 0.0F;
            }

            worldIn.spawnParticle("reddust", d, d2, d3, f2, f3, f4);
        }
    }

    public static boolean isPowerProviderOrWire(IBlockAccess blockAccess, int n, int n2, int n3, int n4) {
        Block block = blockAccess.getBlock(n, n2, n3);
        if (block == Blocks.redstone_wire) {
            return true;
        }

        if (!Blocks.unpowered_repeater.isComparator(block)) {
            return block.canProvidePower() && n4 != -1;
        }

        int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
        return n4 == (blockMetadata & 3) || n4 == Direction.rotateOpposite[blockMetadata & 3];
    }

    public static boolean checkIBlockAccess(IBlockAccess blockAccess, int n, int n2, int n3, int n4) {
        if (isPowerProviderOrWire(blockAccess, n, n2, n3, n4)) {
            return true;
        } else if (blockAccess.getBlock(n, n2, n3) == Blocks.powered_repeater) {
            int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
            return n4 == (blockMetadata & 3);
        } else {
            return false;
        }
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.redstone;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.redstoneCrossIcon = reg.registerIcon(this.getTextureName() + "_cross");
        this.redstoneLineIcon = reg.registerIcon(this.getTextureName() + "_line");
        this.redstoneCrossOverlayIcon = reg.registerIcon(this.getTextureName() + "_cross_overlay");
        this.redstoneLineOverlayIcon = reg.registerIcon(this.getTextureName() + "_line_overlay");
        this.blockIcon = this.redstoneCrossIcon;
    }

    public static IIcon getRedstoneWireIcon(String str) {
        return str.equals("cross")
            ? Blocks.redstone_wire.redstoneCrossIcon
            : (
                str.equals("line")
                    ? Blocks.redstone_wire.redstoneLineIcon
                    : (
                        str.equals("cross_overlay")
                            ? Blocks.redstone_wire.redstoneCrossOverlayIcon
                            : (str.equals("line_overlay") ? Blocks.redstone_wire.redstoneLineOverlayIcon : null)
                    )
            );
    }
}
