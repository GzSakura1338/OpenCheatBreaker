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
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockLeaves extends BlockLeavesBase {
    public int[] neighborBlockDecayInfo;
    public int fancyIndex;
    public IIcon[][] iIconMatrix = new IIcon[2][];

    public BlockLeaves() {
        super(Material.leaves, false);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(0.2F);
        this.setLightOpacity(1);
        this.setStepSound(soundTypeGrass);
    }

    @Override
    public int getBlockColor() {
        double d = 0.5;
        double d2 = 1.0;
        return ColorizerFoliage.getFoliageColor(d, d2);
    }

    @Override
    public int getRenderColor(int meta) {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int biomeFoliageColor = worldIn.getBiomeGenForCoords(x + j, z + i).getBiomeFoliageColor(x + j, y, z + i);
                n += (biomeFoliageColor & 0xFF0000) >> 16;
                n2 += (biomeFoliageColor & 0xFF00) >> 8;
                n3 += biomeFoliageColor & 0xFF;
            }
        }

        return (n / 9 & 0xFF) << 16 | (n2 / 9 & 0xFF) << 8 | n3 / 9 & 0xFF;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        byte b = 1;
        int n = b + 1;
        if (worldIn.checkChunksExist(x - n, y - n, z - n, x + n, y + n, z + n)) {
            for (int i = -b; i <= b; i++) {
                for (int j = -b; j <= b; j++) {
                    for (int k = -b; k <= b; k++) {
                        if (worldIn.getBlock(x + i, y + j, z + k).getMaterial() == Material.leaves) {
                            int blockMetadata = worldIn.getBlockMetadata(x + i, y + j, z + k);
                            worldIn.setBlockMetadataWithNotify(x + i, y + j, z + k, blockMetadata | 8, 4);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            if ((blockMetadata & 8) != 0 && (blockMetadata & 4) == 0) {
                byte b = 4;
                int n = b + 1;
                byte b2 = 32;
                int n2 = b2 * b2;
                int n3 = b2 / 2;
                if (this.neighborBlockDecayInfo == null) {
                    this.neighborBlockDecayInfo = new int[b2 * b2 * b2];
                }

                if (worldIn.checkChunksExist(x - n, y - n, z - n, x + n, y + n, z + n)) {
                    for (int i = -b; i <= b; i++) {
                        for (int j = -b; j <= b; j++) {
                            for (int k = -b; k <= b; k++) {
                                Block block = worldIn.getBlock(x + i, y + j, z + k);
                                if (block != Blocks.log && block != Blocks.log2) {
                                    if (block.getMaterial() == Material.leaves) {
                                        this.neighborBlockDecayInfo[(i + n3) * n2 + (j + n3) * b2 + k + n3] = -2;
                                    } else {
                                        this.neighborBlockDecayInfo[(i + n3) * n2 + (j + n3) * b2 + k + n3] = -1;
                                    }
                                } else {
                                    this.neighborBlockDecayInfo[(i + n3) * n2 + (j + n3) * b2 + k + n3] = 0;
                                }
                            }
                        }
                    }

                    for (int i = 1; i <= 4; i++) {
                        for (int j = -b; j <= b; j++) {
                            for (int k = -b; k <= b; k++) {
                                for (int m = -b; m <= b; m++) {
                                    if (this.neighborBlockDecayInfo[(j + n3) * n2 + (k + n3) * b2 + m + n3] == i - 1) {
                                        if (this.neighborBlockDecayInfo[(j + n3 - 1) * n2 + (k + n3) * b2 + m + n3] == -2) {
                                            this.neighborBlockDecayInfo[(j + n3 - 1) * n2 + (k + n3) * b2 + m + n3] = i;
                                        }

                                        if (this.neighborBlockDecayInfo[(j + n3 + 1) * n2 + (k + n3) * b2 + m + n3] == -2) {
                                            this.neighborBlockDecayInfo[(j + n3 + 1) * n2 + (k + n3) * b2 + m + n3] = i;
                                        }

                                        if (this.neighborBlockDecayInfo[(j + n3) * n2 + (k + n3 - 1) * b2 + m + n3] == -2) {
                                            this.neighborBlockDecayInfo[(j + n3) * n2 + (k + n3 - 1) * b2 + m + n3] = i;
                                        }

                                        if (this.neighborBlockDecayInfo[(j + n3) * n2 + (k + n3 + 1) * b2 + m + n3] == -2) {
                                            this.neighborBlockDecayInfo[(j + n3) * n2 + (k + n3 + 1) * b2 + m + n3] = i;
                                        }

                                        if (this.neighborBlockDecayInfo[(j + n3) * n2 + (k + n3) * b2 + (m + n3 - 1)] == -2) {
                                            this.neighborBlockDecayInfo[(j + n3) * n2 + (k + n3) * b2 + (m + n3 - 1)] = i;
                                        }

                                        if (this.neighborBlockDecayInfo[(j + n3) * n2 + (k + n3) * b2 + m + n3 + 1] == -2) {
                                            this.neighborBlockDecayInfo[(j + n3) * n2 + (k + n3) * b2 + m + n3 + 1] = i;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int n4 = this.neighborBlockDecayInfo[n3 * n2 + n3 * b2 + n3];
                if (n4 >= 0) {
                    worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata & -9, 4);
                } else {
                    this.removeLeaves(worldIn, x, y, z);
                }
            }
        }
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        if (worldIn.isRainingAt(x, y + 1, z) && !World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && random.nextInt(15) == 1) {
            double d = x + random.nextFloat();
            double d2 = y - 0.05;
            double d3 = z + random.nextFloat();
            worldIn.spawnParticle("dripWater", d, d2, d3, 0.0, 0.0, 0.0);
        }
    }

    public void removeLeaves(World world, int n, int n2, int n3) {
        this.dropBlockAsItem(world, n, n2, n3, world.getBlockMetadata(n, n2, n3), 0);
        world.setBlockToAir(n, n2, n3);
    }

    @Override
    public int quantityDropped(Random random) {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(Blocks.sapling);
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        if (!worldIn.isRemote) {
            int computeInt = this.computeInt(meta);
            if (fortune > 0) {
                computeInt -= 2 << fortune;
                if (computeInt < 10) {
                    computeInt = 10;
                }
            }

            if (worldIn.rand.nextInt(computeInt) == 0) {
                Item itemDropped = this.getItemDropped(meta, worldIn.rand, fortune);
                this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(itemDropped, 1, this.damageDropped(meta)));
            }

            computeInt = 200;
            if (fortune > 0) {
                computeInt -= 10 << fortune;
                if (computeInt < 40) {
                    computeInt = 40;
                }
            }

            this.handleWorld(worldIn, x, y, z, meta, computeInt);
        }
    }

    public void handleWorld(World world, int n, int n2, int n3, int n4, int n5) {
    }

    public int computeInt(int n) {
        return 20;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta) {
        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
            player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(this)], 1);
            this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(Item.getItemFromBlock(this), 1, meta & 3));
        } else {
            super.harvestBlock(worldIn, player, x, y, z, meta);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta & 3;
    }

    @Override
    public boolean isOpaqueCube() {
        return !this.fancyGraphics;
    }

    @Override
    public abstract IIcon getIcon(int n, int n2);

    public void setGraphicsLevel(boolean flag) {
        this.fancyGraphics = flag;
        this.fancyIndex = flag ? 0 : 1;
    }

    @Override
    public ItemStack createStackedBlock(int meta) {
        return new ItemStack(Item.getItemFromBlock(this), 1, meta & 3);
    }

    public abstract String[] getStringArray();
}
