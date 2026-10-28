package net.minecraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
    public boolean isPowered;

    public static boolean isRailBlockAt(World world, int n, int n2, int n3) {
        return isRailBlock(world.getBlock(n, n2, n3));
    }

    public static boolean isRailBlock(Block block) {
        return block == Blocks.rail || block == Blocks.golden_rail || block == Blocks.detector_rail || block == Blocks.activator_rail;
    }

    public BlockRailBase(boolean flag) {
        super(Material.circuits);
        this.isPowered = flag;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    public boolean isPowered() {
        return this.isPowered;
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
    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.collisionRayTrace(worldIn, x, y, z, startVec, endVec);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (blockMetadata >= 2 && blockMetadata <= 5) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        }
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 9;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        if (!worldIn.isRemote) {
            this.refreshTrackShape(worldIn, x, y, z, true);
            if (this.isPowered) {
                this.onNeighborBlockChange(worldIn, x, y, z, this);
            }
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!worldIn.isRemote) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            int n = blockMetadata;
            if (this.isPowered) {
                n = blockMetadata & 7;
            }

            boolean flag = !World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z);
            if (n == 2 && !World.doesBlockHaveSolidTopSurface(worldIn, x + 1, y, z)) {
                flag = true;
            }

            if (n == 3 && !World.doesBlockHaveSolidTopSurface(worldIn, x - 1, y, z)) {
                flag = true;
            }

            if (n == 4 && !World.doesBlockHaveSolidTopSurface(worldIn, x, y, z - 1)) {
                flag = true;
            }

            if (n == 5 && !World.doesBlockHaveSolidTopSurface(worldIn, x, y, z + 1)) {
                flag = true;
            }

            if (flag) {
                this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
                worldIn.setBlockToAir(x, y, z);
            } else {
                this.onRedstoneSignal(worldIn, x, y, z, blockMetadata, n, neighbor);
            }
        }
    }

    public void onRedstoneSignal(World world, int n, int n2, int n3, int n4, int n5, Block block) {
    }

    public void refreshTrackShape(World world, int n, int n2, int n3, boolean flag) {
        if (!world.isRemote) {
            new BlockRailBase.Rail(world, n, n2, n3).process(world.isBlockIndirectlyGettingPowered(n, n2, n3), flag);
        }
    }

    @Override
    public int getMobilityFlag() {
        return 0;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        int meta2 = meta;
        if (this.isPowered) {
            meta2 = meta & 7;
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        if (meta2 == 2 || meta2 == 3 || meta2 == 4 || meta2 == 5) {
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, blockBroken);
        }

        if (this.isPowered) {
            worldIn.notifyBlocksOfNeighborChange(x, y, z, blockBroken);
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, blockBroken);
        }
    }

    public class Rail {
        public List list = new ArrayList();
        public World world;
        public int intValue;
        public int intValue2;
        public int intValue3;
        public boolean flag;

        public Rail(World world, int n, int n2, int n3) {
            this.world = world;
            this.intValue = n;
            this.intValue2 = n2;
            this.intValue3 = n3;
            Block block = world.getBlock(n, n2, n3);
            int blockMetadata = world.getBlockMetadata(n, n2, n3);
            if (((BlockRailBase)block).isPowered) {
                this.flag = true;
                blockMetadata &= -9;
            } else {
                this.flag = false;
            }

            this.process(blockMetadata);
        }

        public void process(int n) {
            this.list.clear();
            if (n == 0) {
                this.list.add(new ChunkPosition(this.intValue, this.intValue2, this.intValue3 - 1));
                this.list.add(new ChunkPosition(this.intValue, this.intValue2, this.intValue3 + 1));
            } else if (n == 1) {
                this.list.add(new ChunkPosition(this.intValue - 1, this.intValue2, this.intValue3));
                this.list.add(new ChunkPosition(this.intValue + 1, this.intValue2, this.intValue3));
            } else if (n == 2) {
                this.list.add(new ChunkPosition(this.intValue - 1, this.intValue2, this.intValue3));
                this.list.add(new ChunkPosition(this.intValue + 1, this.intValue2 + 1, this.intValue3));
            } else if (n == 3) {
                this.list.add(new ChunkPosition(this.intValue - 1, this.intValue2 + 1, this.intValue3));
                this.list.add(new ChunkPosition(this.intValue + 1, this.intValue2, this.intValue3));
            } else if (n == 4) {
                this.list.add(new ChunkPosition(this.intValue, this.intValue2 + 1, this.intValue3 - 1));
                this.list.add(new ChunkPosition(this.intValue, this.intValue2, this.intValue3 + 1));
            } else if (n == 5) {
                this.list.add(new ChunkPosition(this.intValue, this.intValue2, this.intValue3 - 1));
                this.list.add(new ChunkPosition(this.intValue, this.intValue2 + 1, this.intValue3 + 1));
            } else if (n == 6) {
                this.list.add(new ChunkPosition(this.intValue + 1, this.intValue2, this.intValue3));
                this.list.add(new ChunkPosition(this.intValue, this.intValue2, this.intValue3 + 1));
            } else if (n == 7) {
                this.list.add(new ChunkPosition(this.intValue - 1, this.intValue2, this.intValue3));
                this.list.add(new ChunkPosition(this.intValue, this.intValue2, this.intValue3 + 1));
            } else if (n == 8) {
                this.list.add(new ChunkPosition(this.intValue - 1, this.intValue2, this.intValue3));
                this.list.add(new ChunkPosition(this.intValue, this.intValue2, this.intValue3 - 1));
            } else if (n == 9) {
                this.list.add(new ChunkPosition(this.intValue + 1, this.intValue2, this.intValue3));
                this.list.add(new ChunkPosition(this.intValue, this.intValue2, this.intValue3 - 1));
            }
        }

        public void run() {
            for (int i = 0; i < this.list.size(); i++) {
                BlockRailBase.Rail findRail = this.findRail((ChunkPosition)this.list.get(i));
                if (findRail != null && findRail.checkRail(this)) {
                    this.list.set(i, new ChunkPosition(findRail.intValue, findRail.intValue2, findRail.intValue3));
                } else {
                    this.list.remove(i--);
                }
            }
        }

        public boolean isRailBlockAt(int n, int n2, int n3) {
            return BlockRailBase.isRailBlockAt(this.world, n, n2, n3)
                || BlockRailBase.isRailBlockAt(this.world, n, n2 + 1, n3)
                || BlockRailBase.isRailBlockAt(this.world, n, n2 - 1, n3);
        }

        public BlockRailBase.Rail findRail(ChunkPosition chunkPosition) {
            return BlockRailBase.isRailBlockAt(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY, chunkPosition.chunkPosZ)
                ? BlockRailBase.this.new Rail(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY, chunkPosition.chunkPosZ)
                : (
                    BlockRailBase.isRailBlockAt(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY + 1, chunkPosition.chunkPosZ)
                        ? BlockRailBase.this.new Rail(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY + 1, chunkPosition.chunkPosZ)
                        : (
                            BlockRailBase.isRailBlockAt(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY - 1, chunkPosition.chunkPosZ)
                                ? BlockRailBase.this.new Rail(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY - 1, chunkPosition.chunkPosZ)
                                : null
                        )
                );
        }

        public boolean checkRail(BlockRailBase.Rail rail) {
            for (int i = 0; i < this.list.size(); i++) {
                ChunkPosition chunkPosition = (ChunkPosition)this.list.get(i);
                if (chunkPosition.chunkPosX == rail.intValue && chunkPosition.chunkPosZ == rail.intValue3) {
                    return true;
                }
            }

            return false;
        }

        public boolean check(int n, int n2, int n3) {
            for (int i = 0; i < this.list.size(); i++) {
                ChunkPosition chunkPosition = (ChunkPosition)this.list.get(i);
                if (chunkPosition.chunkPosX == n && chunkPosition.chunkPosZ == n3) {
                    return true;
                }
            }

            return false;
        }

        public int countAdjacentRails() {
            int n = 0;
            if (this.isRailBlockAt(this.intValue, this.intValue2, this.intValue3 - 1)) {
                n++;
            }

            if (this.isRailBlockAt(this.intValue, this.intValue2, this.intValue3 + 1)) {
                n++;
            }

            if (this.isRailBlockAt(this.intValue - 1, this.intValue2, this.intValue3)) {
                n++;
            }

            if (this.isRailBlockAt(this.intValue + 1, this.intValue2, this.intValue3)) {
                n++;
            }

            return n;
        }

        public boolean checkRail2(BlockRailBase.Rail rail) {
            return this.checkRail(rail) || this.list.size() != 2;
        }

        public void handleRail(BlockRailBase.Rail rail) {
            this.list.add(new ChunkPosition(rail.intValue, rail.intValue2, rail.intValue3));
            boolean check = this.check(this.intValue, this.intValue2, this.intValue3 - 1);
            boolean check2 = this.check(this.intValue, this.intValue2, this.intValue3 + 1);
            boolean check3 = this.check(this.intValue - 1, this.intValue2, this.intValue3);
            boolean check4 = this.check(this.intValue + 1, this.intValue2, this.intValue3);
            byte b = -1;
            if (check || check2) {
                b = 0;
            }

            if (check3 || check4) {
                b = 1;
            }

            if (!this.flag) {
                if (check2 && check4 && !check && !check3) {
                    b = 6;
                }

                if (check2 && check3 && !check && !check4) {
                    b = 7;
                }

                if (check && check3 && !check2 && !check4) {
                    b = 8;
                }

                if (check && check4 && !check2 && !check3) {
                    b = 9;
                }
            }

            if (b == 0) {
                if (BlockRailBase.isRailBlockAt(this.world, this.intValue, this.intValue2 + 1, this.intValue3 - 1)) {
                    b = 4;
                }

                if (BlockRailBase.isRailBlockAt(this.world, this.intValue, this.intValue2 + 1, this.intValue3 + 1)) {
                    b = 5;
                }
            }

            if (b == 1) {
                if (BlockRailBase.isRailBlockAt(this.world, this.intValue + 1, this.intValue2 + 1, this.intValue3)) {
                    b = 2;
                }

                if (BlockRailBase.isRailBlockAt(this.world, this.intValue - 1, this.intValue2 + 1, this.intValue3)) {
                    b = 3;
                }
            }

            if (b < 0) {
                b = 0;
            }

            int n = b;
            if (this.flag) {
                n = this.world.getBlockMetadata(this.intValue, this.intValue2, this.intValue3) & 8 | b;
            }

            this.world.setBlockMetadataWithNotify(this.intValue, this.intValue2, this.intValue3, n, 3);
        }

        public boolean check2(int n, int n2, int n3) {
            BlockRailBase.Rail findRail = this.findRail(new ChunkPosition(n, n2, n3));
            if (findRail == null) {
                return false;
            }

            findRail.run();
            return findRail.checkRail2(this);
        }

        public void process(boolean flag, boolean flag2) {
            boolean check2 = this.check2(this.intValue, this.intValue2, this.intValue3 - 1);
            boolean check22 = this.check2(this.intValue, this.intValue2, this.intValue3 + 1);
            boolean check23 = this.check2(this.intValue - 1, this.intValue2, this.intValue3);
            boolean check24 = this.check2(this.intValue + 1, this.intValue2, this.intValue3);
            byte b = -1;
            if ((check2 || check22) && !check23 && !check24) {
                b = 0;
            }

            if ((check23 || check24) && !check2 && !check22) {
                b = 1;
            }

            if (!this.flag) {
                if (check22 && check24 && !check2 && !check23) {
                    b = 6;
                }

                if (check22 && check23 && !check2 && !check24) {
                    b = 7;
                }

                if (check2 && check23 && !check22 && !check24) {
                    b = 8;
                }

                if (check2 && check24 && !check22 && !check23) {
                    b = 9;
                }
            }

            if (b == -1) {
                if (check2 || check22) {
                    b = 0;
                }

                if (check23 || check24) {
                    b = 1;
                }

                if (!this.flag) {
                    if (flag) {
                        if (check22 && check24) {
                            b = 6;
                        }

                        if (check23 && check22) {
                            b = 7;
                        }

                        if (check24 && check2) {
                            b = 9;
                        }

                        if (check2 && check23) {
                            b = 8;
                        }
                    } else {
                        if (check2 && check23) {
                            b = 8;
                        }

                        if (check24 && check2) {
                            b = 9;
                        }

                        if (check23 && check22) {
                            b = 7;
                        }

                        if (check22 && check24) {
                            b = 6;
                        }
                    }
                }
            }

            if (b == 0) {
                if (BlockRailBase.isRailBlockAt(this.world, this.intValue, this.intValue2 + 1, this.intValue3 - 1)) {
                    b = 4;
                }

                if (BlockRailBase.isRailBlockAt(this.world, this.intValue, this.intValue2 + 1, this.intValue3 + 1)) {
                    b = 5;
                }
            }

            if (b == 1) {
                if (BlockRailBase.isRailBlockAt(this.world, this.intValue + 1, this.intValue2 + 1, this.intValue3)) {
                    b = 2;
                }

                if (BlockRailBase.isRailBlockAt(this.world, this.intValue - 1, this.intValue2 + 1, this.intValue3)) {
                    b = 3;
                }
            }

            if (b < 0) {
                b = 0;
            }

            this.process(b);
            int n = b;
            if (this.flag) {
                n = this.world.getBlockMetadata(this.intValue, this.intValue2, this.intValue3) & 8 | b;
            }

            if (flag2 || this.world.getBlockMetadata(this.intValue, this.intValue2, this.intValue3) != n) {
                this.world.setBlockMetadataWithNotify(this.intValue, this.intValue2, this.intValue3, n, 3);

                for (int i = 0; i < this.list.size(); i++) {
                    BlockRailBase.Rail findRail = this.findRail((ChunkPosition)this.list.get(i));
                    if (findRail != null) {
                        findRail.run();
                        if (findRail.checkRail2(this)) {
                            findRail.handleRail(this);
                        }
                    }
                }
            }
        }
    }
}
