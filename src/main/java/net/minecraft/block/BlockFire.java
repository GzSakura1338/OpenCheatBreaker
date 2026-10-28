package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class BlockFire extends Block {
    public int[] intArray = new int[256];
    public int[] intArray2 = new int[256];
    public IIcon[] iIconArray;

    public BlockFire() {
        super(Material.fire);
        this.setTickRandomly(true);
    }

    public static void registerDefaultFlammables() {
        Blocks.fire.process(getIdFromBlock(Blocks.planks), 5, 20);
        Blocks.fire.process(getIdFromBlock(Blocks.double_wooden_slab), 5, 20);
        Blocks.fire.process(getIdFromBlock(Blocks.wooden_slab), 5, 20);
        Blocks.fire.process(getIdFromBlock(Blocks.fence), 5, 20);
        Blocks.fire.process(getIdFromBlock(Blocks.oak_stairs), 5, 20);
        Blocks.fire.process(getIdFromBlock(Blocks.birch_stairs), 5, 20);
        Blocks.fire.process(getIdFromBlock(Blocks.spruce_stairs), 5, 20);
        Blocks.fire.process(getIdFromBlock(Blocks.jungle_stairs), 5, 20);
        Blocks.fire.process(getIdFromBlock(Blocks.log), 5, 5);
        Blocks.fire.process(getIdFromBlock(Blocks.log2), 5, 5);
        Blocks.fire.process(getIdFromBlock(Blocks.leaves), 30, 60);
        Blocks.fire.process(getIdFromBlock(Blocks.leaves2), 30, 60);
        Blocks.fire.process(getIdFromBlock(Blocks.bookshelf), 30, 20);
        Blocks.fire.process(getIdFromBlock(Blocks.tnt), 15, 100);
        Blocks.fire.process(getIdFromBlock(Blocks.tallgrass), 60, 100);
        Blocks.fire.process(getIdFromBlock(Blocks.double_plant), 60, 100);
        Blocks.fire.process(getIdFromBlock(Blocks.yellow_flower), 60, 100);
        Blocks.fire.process(getIdFromBlock(Blocks.red_flower), 60, 100);
        Blocks.fire.process(getIdFromBlock(Blocks.wool), 30, 60);
        Blocks.fire.process(getIdFromBlock(Blocks.vine), 15, 100);
        Blocks.fire.process(getIdFromBlock(Blocks.coal_block), 5, 5);
        Blocks.fire.process(getIdFromBlock(Blocks.hay_block), 60, 20);
        Blocks.fire.process(getIdFromBlock(Blocks.carpet), 60, 20);
    }

    public void process(int n, int n2, int n3) {
        this.intArray[n] = n2;
        this.intArray2[n] = n3;
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
        return 3;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public int tickRate(World worldIn) {
        return 30;
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (worldIn.getGameRules().getGameRuleBooleanValue("doFireTick")) {
            boolean flag = worldIn.getBlock(x, y - 1, z) == Blocks.netherrack;
            if (worldIn.provider instanceof WorldProviderEnd && worldIn.getBlock(x, y - 1, z) == Blocks.bedrock) {
                flag = true;
            }

            if (!this.canPlaceBlockAt(worldIn, x, y, z)) {
                worldIn.setBlockToAir(x, y, z);
            }

            if (flag
                || !worldIn.isRaining()
                || !worldIn.isRainingAt(x, y, z)
                    && !worldIn.isRainingAt(x - 1, y, z)
                    && !worldIn.isRainingAt(x + 1, y, z)
                    && !worldIn.isRainingAt(x, y, z - 1)
                    && !worldIn.isRainingAt(x, y, z + 1)) {
                int blockMetadata = worldIn.getBlockMetadata(x, y, z);
                if (blockMetadata < 15) {
                    worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata + random.nextInt(3) / 2, 4);
                }

                worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn) + random.nextInt(10));
                if (!flag && !this.canNeighborBurn(worldIn, x, y, z)) {
                    if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) || blockMetadata > 3) {
                        worldIn.setBlockToAir(x, y, z);
                    }
                } else if (!flag && !this.canBlockCatchFire(worldIn, x, y - 1, z) && blockMetadata == 15 && random.nextInt(4) == 0) {
                    worldIn.setBlockToAir(x, y, z);
                } else {
                    boolean isBlockHighHumidity = worldIn.isBlockHighHumidity(x, y, z);
                    byte b = 0;
                    if (isBlockHighHumidity) {
                        b = -50;
                    }

                    this.tryCatchFire(worldIn, x + 1, y, z, 300 + b, random, blockMetadata);
                    this.tryCatchFire(worldIn, x - 1, y, z, 300 + b, random, blockMetadata);
                    this.tryCatchFire(worldIn, x, y - 1, z, 250 + b, random, blockMetadata);
                    this.tryCatchFire(worldIn, x, y + 1, z, 250 + b, random, blockMetadata);
                    this.tryCatchFire(worldIn, x, y, z - 1, 300 + b, random, blockMetadata);
                    this.tryCatchFire(worldIn, x, y, z + 1, 300 + b, random, blockMetadata);

                    for (int i = x - 1; i <= x + 1; i++) {
                        for (int j = z - 1; j <= z + 1; j++) {
                            for (int k = y - 1; k <= y + 4; k++) {
                                if (i != x || k != y || j != z) {
                                    int n = 100;
                                    if (k > y + 1) {
                                        n += (k - (y + 1)) * 100;
                                    }

                                    int n2 = this.getChanceOfNeighborsEncouragingFire(worldIn, i, k, j);
                                    if (n2 > 0) {
                                        int n3 = (n2 + 40 + worldIn.difficultySetting.getDifficultyId() * 7) / (blockMetadata + 30);
                                        if (isBlockHighHumidity) {
                                            n3 /= 2;
                                        }

                                        if (n3 > 0
                                            && random.nextInt(n) <= n3
                                            && (!worldIn.isRaining() || !worldIn.isRainingAt(i, k, j))
                                            && !worldIn.isRainingAt(i - 1, k, z)
                                            && !worldIn.isRainingAt(i + 1, k, j)
                                            && !worldIn.isRainingAt(i, k, j - 1)
                                            && !worldIn.isRainingAt(i, k, j + 1)) {
                                            int n4 = blockMetadata + random.nextInt(5) / 4;
                                            if (n4 > 15) {
                                                n4 = 15;
                                            }

                                            worldIn.setBlock(i, k, j, this, n4, 3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                worldIn.setBlockToAir(x, y, z);
            }
        }
    }

    @Override
    public boolean requiresUpdates() {
        return false;
    }

    public void tryCatchFire(World world, int n, int n2, int n3, int n4, Random random, int n5) {
        int n6 = this.intArray2[Block.getIdFromBlock(world.getBlock(n, n2, n3))];
        if (random.nextInt(n4) < n6) {
            boolean flag = world.getBlock(n, n2, n3) == Blocks.tnt;
            if (random.nextInt(n5 + 10) < 5 && !world.isRainingAt(n, n2, n3)) {
                int n7 = n5 + random.nextInt(5) / 4;
                if (n7 > 15) {
                    n7 = 15;
                }

                world.setBlock(n, n2, n3, this, n7, 3);
            } else {
                world.setBlockToAir(n, n2, n3);
            }

            if (flag) {
                Blocks.tnt.onBlockDestroyedByPlayer(world, n, n2, n3, 1);
            }
        }
    }

    public boolean canNeighborBurn(World world, int n, int n2, int n3) {
        return this.canBlockCatchFire(world, n + 1, n2, n3)
            || this.canBlockCatchFire(world, n - 1, n2, n3)
            || this.canBlockCatchFire(world, n, n2 - 1, n3)
            || this.canBlockCatchFire(world, n, n2 + 1, n3)
            || this.canBlockCatchFire(world, n, n2, n3 - 1)
            || this.canBlockCatchFire(world, n, n2, n3 + 1);
    }

    public int getChanceOfNeighborsEncouragingFire(World world, int n, int n2, int n3) {
        byte b = 0;
        if (!world.isAirBlock(n, n2, n3)) {
            return 0;
        }

        int computeInt = this.computeInt(world, n + 1, n2, n3, b);
        computeInt = this.computeInt(world, n - 1, n2, n3, computeInt);
        computeInt = this.computeInt(world, n, n2 - 1, n3, computeInt);
        computeInt = this.computeInt(world, n, n2 + 1, n3, computeInt);
        computeInt = this.computeInt(world, n, n2, n3 - 1, computeInt);
        return this.computeInt(world, n, n2, n3 + 1, computeInt);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    public boolean canBlockCatchFire(IBlockAccess blockAccess, int n, int n2, int n3) {
        return this.intArray[Block.getIdFromBlock(blockAccess.getBlock(n, n2, n3))] > 0;
    }

    public int computeInt(World world, int n, int n2, int n3, int n4) {
        int n5 = this.intArray[Block.getIdFromBlock(world.getBlock(n, n2, n3))];
        return n5 > n4 ? n5 : n4;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) || this.canNeighborBurn(worldIn, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && !this.canNeighborBurn(worldIn, x, y, z)) {
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        if (worldIn.provider.dimensionId > 0 || !Blocks.portal.tryToCreatePortal(worldIn, x, y, z)) {
            if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && !this.canNeighborBurn(worldIn, x, y, z)) {
                worldIn.setBlockToAir(x, y, z);
            } else {
                worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn) + worldIn.rand.nextInt(10));
            }
        }
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        if (random.nextInt(24) == 0) {
            worldIn.playSound(x + 0.5F, y + 0.5F, z + 0.5F, "fire.fire", 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }

        if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && !Blocks.fire.canBlockCatchFire(worldIn, x, y - 1, z)) {
            if (Blocks.fire.canBlockCatchFire(worldIn, x - 1, y, z)) {
                for (int i = 0; i < 2; i++) {
                    float f = x + random.nextFloat() * 0.1F;
                    float f2 = y + random.nextFloat();
                    float f3 = z + random.nextFloat();
                    worldIn.spawnParticle("largesmoke", f, f2, f3, 0.0, 0.0, 0.0);
                }
            }

            if (Blocks.fire.canBlockCatchFire(worldIn, x + 1, y, z)) {
                for (int i = 0; i < 2; i++) {
                    float f = x + 1 - random.nextFloat() * 0.1F;
                    float f2 = y + random.nextFloat();
                    float f3 = z + random.nextFloat();
                    worldIn.spawnParticle("largesmoke", f, f2, f3, 0.0, 0.0, 0.0);
                }
            }

            if (Blocks.fire.canBlockCatchFire(worldIn, x, y, z - 1)) {
                for (int i = 0; i < 2; i++) {
                    float f = x + random.nextFloat();
                    float f2 = y + random.nextFloat();
                    float f3 = z + random.nextFloat() * 0.1F;
                    worldIn.spawnParticle("largesmoke", f, f2, f3, 0.0, 0.0, 0.0);
                }
            }

            if (Blocks.fire.canBlockCatchFire(worldIn, x, y, z + 1)) {
                for (int i = 0; i < 2; i++) {
                    float f = x + random.nextFloat();
                    float f2 = y + random.nextFloat();
                    float f3 = z + 1 - random.nextFloat() * 0.1F;
                    worldIn.spawnParticle("largesmoke", f, f2, f3, 0.0, 0.0, 0.0);
                }
            }

            if (Blocks.fire.canBlockCatchFire(worldIn, x, y + 1, z)) {
                for (int i = 0; i < 2; i++) {
                    float f = x + random.nextFloat();
                    float f2 = y + 1 - random.nextFloat() * 0.1F;
                    float f3 = z + random.nextFloat();
                    worldIn.spawnParticle("largesmoke", f, f2, f3, 0.0, 0.0, 0.0);
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
                float f = x + random.nextFloat();
                float f2 = y + random.nextFloat() * 0.5F + 0.5F;
                float f3 = z + random.nextFloat();
                worldIn.spawnParticle("largesmoke", f, f2, f3, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[]{reg.registerIcon(this.getTextureName() + "_layer_0"), reg.registerIcon(this.getTextureName() + "_layer_1")};
    }

    public IIcon getFireIcon(int n) {
        return this.iIconArray[n];
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.iIconArray[0];
    }

    @Override
    public MapColor getMapColor(int meta) {
        return MapColor.tntColor;
    }
}
