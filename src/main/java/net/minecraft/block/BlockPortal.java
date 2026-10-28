package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable {
    public static int[][] intMatrix = new int[][]{new int[0], {3, 1}, {2, 0}};

    public BlockPortal() {
        super("portal", Material.portal, false);
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        super.updateTick(worldIn, x, y, z, random);
        if (worldIn.provider.isSurfaceWorld()
            && worldIn.getGameRules().getGameRuleBooleanValue("doMobSpawning")
            && random.nextInt(2000) < worldIn.difficultySetting.getDifficultyId()) {
            int n = y;

            while (!World.doesBlockHaveSolidTopSurface(worldIn, x, n, z) && n > 0) {
                n--;
            }

            if (n > 0 && !worldIn.getBlock(x, n + 1, z).isNormalCube()) {
                Entity spawnCreature = ItemMonsterPlacer.spawnCreature(worldIn, 57, x + 0.5, n + 1.1, z + 0.5);
                if (spawnCreature != null) {
                    spawnCreature.timeUntilPortal = spawnCreature.getPortalCooldown();
                }
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int computeInt2 = computeInt(worldIn.getBlockMetadata(x, y, z));
        if (computeInt2 == 0) {
            if (worldIn.getBlock(x - 1, y, z) != this && worldIn.getBlock(x + 1, y, z) != this) {
                computeInt2 = 2;
            } else {
                computeInt2 = 1;
            }

            if (worldIn instanceof World && !((World)worldIn).isRemote) {
                ((World)worldIn).setBlockMetadataWithNotify(x, y, z, computeInt2, 2);
            }
        }

        float f = 0.125F;
        float f2 = 0.125F;
        if (computeInt2 == 1) {
            f = 0.5F;
        }

        if (computeInt2 == 2) {
            f2 = 0.5F;
        }

        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f2, 0.5F + f, 1.0F, 0.5F + f2);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean tryToCreatePortal(World world, int n, int n2, int n3) {
        BlockPortal.Size size = new BlockPortal.Size(world, n, n2, n3, 1);
        BlockPortal.Size size2 = new BlockPortal.Size(world, n, n2, n3, 2);
        if (size.check() && size.intValue == 0) {
            size.setBlock();
            return true;
        } else if (size2.check() && size2.intValue == 0) {
            size2.setBlock();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        int computeInt2 = computeInt(worldIn.getBlockMetadata(x, y, z));
        BlockPortal.Size size = new BlockPortal.Size(worldIn, x, y, z, 1);
        BlockPortal.Size size2 = new BlockPortal.Size(worldIn, x, y, z, 2);
        if (computeInt2 != 1 || size.check() && size.intValue >= size.intValue5 * size.intValue6) {
            if (computeInt2 != 2 || size2.check() && size2.intValue >= size2.intValue5 * size2.intValue6) {
                if (computeInt2 == 0 && !size.check() && !size2.check()) {
                    worldIn.setBlock(x, y, z, Blocks.air);
                }
            } else {
                worldIn.setBlock(x, y, z, Blocks.air);
            }
        } else {
            worldIn.setBlock(x, y, z, Blocks.air);
        }
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        int n = 0;
        if (worldIn.getBlock(x, y, z) == this) {
            n = computeInt(worldIn.getBlockMetadata(x, y, z));
            if (n == 0) {
                return false;
            }

            if (n == 2 && side != 5 && side != 4) {
                return false;
            }

            if (n == 1 && side != 3 && side != 2) {
                return false;
            }
        }

        boolean flag = worldIn.getBlock(x - 1, y, z) == this && worldIn.getBlock(x - 2, y, z) != this;
        boolean flag2 = worldIn.getBlock(x + 1, y, z) == this && worldIn.getBlock(x + 2, y, z) != this;
        boolean flag3 = worldIn.getBlock(x, y, z - 1) == this && worldIn.getBlock(x, y, z - 2) != this;
        boolean flag4 = worldIn.getBlock(x, y, z + 1) == this && worldIn.getBlock(x, y, z + 2) != this;
        boolean flag5 = flag || flag2 || n == 1;
        boolean flag6 = flag3 || flag4 || n == 2;
        return flag5 && side == 4 || flag5 && side == 5 || flag6 && side == 2 || flag6 && side == 3;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        if (entityIn.ridingEntity == null && entityIn.riddenByEntity == null) {
            entityIn.setInPortal();
        }
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        if (random.nextInt(100) == 0) {
            worldIn.playSound(x + 0.5, y + 0.5, z + 0.5, "portal.portal", 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
        }

        for (int i = 0; i < 4; i++) {
            double d = x + random.nextFloat();
            double d2 = y + random.nextFloat();
            double d3 = z + random.nextFloat();
            double d4 = 0.0;
            double d5 = 0.0;
            double d6 = 0.0;
            int n = random.nextInt(2) * 2 - 1;
            d4 = (random.nextFloat() - 0.5) * 0.5;
            d5 = (random.nextFloat() - 0.5) * 0.5;
            d6 = (random.nextFloat() - 0.5) * 0.5;
            if (worldIn.getBlock(x - 1, y, z) != this && worldIn.getBlock(x + 1, y, z) != this) {
                d = x + 0.5 + 0.25 * n;
                d4 = random.nextFloat() * 2.0F * n;
            } else {
                d3 = z + 0.5 + 0.25 * n;
                d6 = random.nextFloat() * 2.0F * n;
            }

            worldIn.spawnParticle("portal", d, d2, d3, d4, d5, d6);
        }
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemById(0);
    }

    public static int computeInt(int n) {
        return n & 3;
    }

    public static class Size {
        public int intValue = 0;
        public World world;
        public int intValue2;
        public int intValue3;
        public int intValue4;
        public ChunkCoordinates chunkCoordinates;
        public int intValue5;
        public int intValue6;

        public Size(World world, int n, int n2, int n3, int n4) {
            this.world = world;
            this.intValue2 = n4;
            this.intValue3 = BlockPortal.intMatrix[n4][0];
            this.intValue4 = BlockPortal.intMatrix[n4][1];
            int n5 = n2;

            while (n2 > n5 - 21 && n2 > 0 && this.checkBlock(world.getBlock(n, n2 - 1, n3))) {
                n2--;
            }

            n5 = this.computeInt(n, n2, n3, this.intValue3) - 1;
            if (n5 >= 0) {
                this.chunkCoordinates = new ChunkCoordinates(n + n5 * Direction.offsetX[this.intValue3], n2, n3 + n5 * Direction.offsetZ[this.intValue3]);
                this.intValue5 = this.computeInt(this.chunkCoordinates.posX, this.chunkCoordinates.posY, this.chunkCoordinates.posZ, this.intValue4);
                if (this.intValue5 < 2 || this.intValue5 > 21) {
                    this.chunkCoordinates = null;
                    this.intValue5 = 0;
                }
            }

            if (this.chunkCoordinates != null) {
                this.intValue6 = this.getIntValue();
            }
        }

        public int computeInt(int n, int n2, int n3, int n4) {
            int n5 = Direction.offsetX[n4];
            int n6 = Direction.offsetZ[n4];

            int n7;
            for (n7 = 0; n7 < 22; n7++) {
                Block block = this.world.getBlock(n + n5 * n7, n2, n3 + n6 * n7);
                if (!this.checkBlock(block)) {
                    break;
                }

                Block block2 = this.world.getBlock(n + n5 * n7, n2 - 1, n3 + n6 * n7);
                if (block2 != Blocks.obsidian) {
                    break;
                }
            }

            Block block = this.world.getBlock(n + n5 * n7, n2, n3 + n6 * n7);
            return block == Blocks.obsidian ? n7 : 0;
        }

        public int getIntValue() {
            label56:
            for (this.intValue6 = 0; this.intValue6 < 21; this.intValue6++) {
                int n = this.chunkCoordinates.posY + this.intValue6;

                for (int i = 0; i < this.intValue5; i++) {
                    int n2 = this.chunkCoordinates.posX + i * Direction.offsetX[BlockPortal.intMatrix[this.intValue2][1]];
                    int n3 = this.chunkCoordinates.posZ + i * Direction.offsetZ[BlockPortal.intMatrix[this.intValue2][1]];
                    Block block = this.world.getBlock(n2, n, n3);
                    if (!this.checkBlock(block)) {
                        break label56;
                    }

                    if (block == Blocks.portal) {
                        this.intValue++;
                    }

                    if (i == 0) {
                        block = this.world
                            .getBlock(
                                n2 + Direction.offsetX[BlockPortal.intMatrix[this.intValue2][0]],
                                n,
                                n3 + Direction.offsetZ[BlockPortal.intMatrix[this.intValue2][0]]
                            );
                        if (block != Blocks.obsidian) {
                            break label56;
                        }
                    } else if (i == this.intValue5 - 1) {
                        block = this.world
                            .getBlock(
                                n2 + Direction.offsetX[BlockPortal.intMatrix[this.intValue2][1]],
                                n,
                                n3 + Direction.offsetZ[BlockPortal.intMatrix[this.intValue2][1]]
                            );
                        if (block != Blocks.obsidian) {
                            break label56;
                        }
                    }
                }
            }

            for (int i = 0; i < this.intValue5; i++) {
                int n = this.chunkCoordinates.posX + i * Direction.offsetX[BlockPortal.intMatrix[this.intValue2][1]];
                int n2 = this.chunkCoordinates.posY + this.intValue6;
                int n3 = this.chunkCoordinates.posZ + i * Direction.offsetZ[BlockPortal.intMatrix[this.intValue2][1]];
                if (this.world.getBlock(n, n2, n3) != Blocks.obsidian) {
                    this.intValue6 = 0;
                    break;
                }
            }

            if (this.intValue6 <= 21 && this.intValue6 >= 3) {
                return this.intValue6;
            }

            this.chunkCoordinates = null;
            this.intValue5 = 0;
            this.intValue6 = 0;
            return 0;
        }

        public boolean checkBlock(Block block) {
            return block.blockMaterial == Material.air || block == Blocks.fire || block == Blocks.portal;
        }

        public boolean check() {
            return this.chunkCoordinates != null && this.intValue5 >= 2 && this.intValue5 <= 21 && this.intValue6 >= 3 && this.intValue6 <= 21;
        }

        public void setBlock() {
            for (int i = 0; i < this.intValue5; i++) {
                int n = this.chunkCoordinates.posX + Direction.offsetX[this.intValue4] * i;
                int n2 = this.chunkCoordinates.posZ + Direction.offsetZ[this.intValue4] * i;

                for (int j = 0; j < this.intValue6; j++) {
                    int n3 = this.chunkCoordinates.posY + j;
                    this.world.setBlock(n, n3, n2, Blocks.portal, this.intValue2, 2);
                }
            }
        }
    }
}
