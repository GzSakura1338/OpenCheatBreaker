package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockVine extends Block {
    public BlockVine() {
        super(Material.vine);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public int getRenderType() {
        return 20;
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
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        float f = 0.0625F;
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 1.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        float f7 = 0.0F;
        boolean flag = blockMetadata > 0;
        if ((blockMetadata & 2) != 0) {
            f5 = Math.max(f5, 0.0625F);
            f2 = 0.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            f4 = 0.0F;
            f7 = 1.0F;
            flag = true;
        }

        if ((blockMetadata & 8) != 0) {
            f2 = Math.min(f2, 0.9375F);
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            f4 = 0.0F;
            f7 = 1.0F;
            flag = true;
        }

        if ((blockMetadata & 4) != 0) {
            f7 = Math.max(f7, 0.0625F);
            f4 = 0.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if ((blockMetadata & 1) != 0) {
            f4 = Math.min(f4, 0.9375F);
            f7 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if (!flag && this.canPlaceOn(worldIn.getBlock(x, y + 1, z))) {
            f3 = Math.min(f3, 0.9375F);
            f6 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f4 = 0.0F;
            f7 = 1.0F;
        }

        this.setBlockBounds(f2, f3, f4, f5, f6, f7);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side) {
        switch (side) {
            case 1:
                return this.canPlaceOn(worldIn.getBlock(x, y + 1, z));
            case 2:
                return this.canPlaceOn(worldIn.getBlock(x, y, z + 1));
            case 3:
                return this.canPlaceOn(worldIn.getBlock(x, y, z - 1));
            case 4:
                return this.canPlaceOn(worldIn.getBlock(x + 1, y, z));
            case 5:
                return this.canPlaceOn(worldIn.getBlock(x - 1, y, z));
            default:
                return false;
        }
    }

    public boolean canPlaceOn(Block block) {
        return block.renderAsNormalBlock() && block.blockMaterial.blocksMovement();
    }

    public boolean checkWorld(World world, int n, int n2, int n3) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        int n4 = blockMetadata;
        if (blockMetadata > 0) {
            for (int i = 0; i <= 3; i++) {
                int n5 = 1 << i;
                if ((blockMetadata & n5) != 0
                    && !this.canPlaceOn(world.getBlock(n + Direction.offsetX[i], n2, n3 + Direction.offsetZ[i]))
                    && (world.getBlock(n, n2 + 1, n3) != this || (world.getBlockMetadata(n, n2 + 1, n3) & n5) == 0)) {
                    n4 &= ~n5;
                }
            }
        }

        if (n4 == 0 && !this.canPlaceOn(world.getBlock(n, n2 + 1, n3))) {
            return false;
        }

        if (n4 != blockMetadata) {
            world.setBlockMetadataWithNotify(n, n2, n3, n4, 2);
        }

        return true;
    }

    @Override
    public int getBlockColor() {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    @Override
    public int getRenderColor(int meta) {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return worldIn.getBiomeGenForCoords(x, z).getBiomeFoliageColor(x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!worldIn.isRemote && !this.checkWorld(worldIn, x, y, z)) {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote && worldIn.rand.nextInt(4) == 0) {
            byte b = 4;
            int n = 5;
            boolean flag = false;

            label134:
            for (int i = x - b; i <= x + b; i++) {
                for (int j = z - b; j <= z + b; j++) {
                    for (int k = y - 1; k <= y + 1; k++) {
                        if (worldIn.getBlock(i, k, j) == this) {
                            if (--n <= 0) {
                                flag = true;
                                break label134;
                            }
                        }
                    }
                }
            }

            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            int nextInt = worldIn.rand.nextInt(6);
            int n2 = Direction.facingToDirection[nextInt];
            if (nextInt == 1 && y < 255 && worldIn.isAirBlock(x, y + 1, z)) {
                if (flag) {
                    return;
                }

                int n3 = worldIn.rand.nextInt(16) & blockMetadata;
                if (n3 > 0) {
                    for (int i = 0; i <= 3; i++) {
                        if (!this.canPlaceOn(worldIn.getBlock(x + Direction.offsetX[i], y + 1, z + Direction.offsetZ[i]))) {
                            n3 &= ~(1 << i);
                        }
                    }

                    if (n3 > 0) {
                        worldIn.setBlock(x, y + 1, z, this, n3, 2);
                    }
                }
            } else if (nextInt >= 2 && nextInt <= 5 && (blockMetadata & 1 << n2) == 0) {
                if (flag) {
                    return;
                }

                Block block = worldIn.getBlock(x + Direction.offsetX[n2], y, z + Direction.offsetZ[n2]);
                if (block.blockMaterial == Material.air) {
                    int n3 = n2 + 1 & 3;
                    int n4 = n2 + 3 & 3;
                    if ((blockMetadata & 1 << n3) != 0
                        && this.canPlaceOn(
                            worldIn.getBlock(x + Direction.offsetX[n2] + Direction.offsetX[n3], y, z + Direction.offsetZ[n2] + Direction.offsetZ[n3])
                        )) {
                        worldIn.setBlock(x + Direction.offsetX[n2], y, z + Direction.offsetZ[n2], this, 1 << n3, 2);
                    } else if ((blockMetadata & 1 << n4) != 0
                        && this.canPlaceOn(
                            worldIn.getBlock(x + Direction.offsetX[n2] + Direction.offsetX[n4], y, z + Direction.offsetZ[n2] + Direction.offsetZ[n4])
                        )) {
                        worldIn.setBlock(x + Direction.offsetX[n2], y, z + Direction.offsetZ[n2], this, 1 << n4, 2);
                    } else if ((blockMetadata & 1 << n3) != 0
                        && worldIn.isAirBlock(x + Direction.offsetX[n2] + Direction.offsetX[n3], y, z + Direction.offsetZ[n2] + Direction.offsetZ[n3])
                        && this.canPlaceOn(worldIn.getBlock(x + Direction.offsetX[n3], y, z + Direction.offsetZ[n3]))) {
                        worldIn.setBlock(
                            x + Direction.offsetX[n2] + Direction.offsetX[n3],
                            y,
                            z + Direction.offsetZ[n2] + Direction.offsetZ[n3],
                            this,
                            1 << (n2 + 2 & 3),
                            2
                        );
                    } else if ((blockMetadata & 1 << n4) != 0
                        && worldIn.isAirBlock(x + Direction.offsetX[n2] + Direction.offsetX[n4], y, z + Direction.offsetZ[n2] + Direction.offsetZ[n4])
                        && this.canPlaceOn(worldIn.getBlock(x + Direction.offsetX[n4], y, z + Direction.offsetZ[n4]))) {
                        worldIn.setBlock(
                            x + Direction.offsetX[n2] + Direction.offsetX[n4],
                            y,
                            z + Direction.offsetZ[n2] + Direction.offsetZ[n4],
                            this,
                            1 << (n2 + 2 & 3),
                            2
                        );
                    } else if (this.canPlaceOn(worldIn.getBlock(x + Direction.offsetX[n2], y + 1, z + Direction.offsetZ[n2]))) {
                        worldIn.setBlock(x + Direction.offsetX[n2], y, z + Direction.offsetZ[n2], this, 0, 2);
                    }
                } else if (block.blockMaterial.isOpaque() && block.renderAsNormalBlock()) {
                    worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata | 1 << n2, 2);
                }
            } else if (y > 1) {
                Block block = worldIn.getBlock(x, y - 1, z);
                if (block.blockMaterial == Material.air) {
                    int n3 = worldIn.rand.nextInt(16) & blockMetadata;
                    if (n3 > 0) {
                        worldIn.setBlock(x, y - 1, z, this, n3, 2);
                    }
                } else if (block == this) {
                    int n3 = worldIn.rand.nextInt(16) & blockMetadata;
                    int blockMetadata2 = worldIn.getBlockMetadata(x, y - 1, z);
                    if (blockMetadata2 != (blockMetadata2 | n3)) {
                        worldIn.setBlockMetadataWithNotify(x, y - 1, z, blockMetadata2 | n3, 2);
                    }
                }
            }
        }
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        byte b = 0;
        switch (side) {
            case 2:
                b = 1;
                break;
            case 3:
                b = 4;
                break;
            case 4:
                b = 8;
                break;
            case 5:
                b = 2;
        }

        return b != 0 ? b : meta;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta) {
        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
            player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(this)], 1);
            this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(Blocks.vine, 1, 0));
        } else {
            super.harvestBlock(worldIn, player, x, y, z, meta);
        }
    }
}
