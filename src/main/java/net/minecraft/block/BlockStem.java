package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStem extends BlockBush implements IGrowable {
    public Block mainBlock;
    public IIcon iIcon2;

    public BlockStem(Block block) {
        this.mainBlock = block;
        this.setTickRandomly(true);
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab(null);
    }

    @Override
    public boolean canPlaceBlockOn(Block ground) {
        return ground == Blocks.farmland;
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        super.updateTick(worldIn, x, y, z, random);
        if (worldIn.getBlockLightValue(x, y + 1, z) >= 9) {
            float computeFloat = this.computeFloat(worldIn, x, y, z);
            if (random.nextInt((int)(25.0F / computeFloat) + 1) == 0) {
                int blockMetadata = worldIn.getBlockMetadata(x, y, z);
                if (blockMetadata < 7) {
                    worldIn.setBlockMetadataWithNotify(x, y, z, ++blockMetadata, 2);
                } else {
                    if (worldIn.getBlock(x - 1, y, z) == this.mainBlock) {
                        return;
                    }

                    if (worldIn.getBlock(x + 1, y, z) == this.mainBlock) {
                        return;
                    }

                    if (worldIn.getBlock(x, y, z - 1) == this.mainBlock) {
                        return;
                    }

                    if (worldIn.getBlock(x, y, z + 1) == this.mainBlock) {
                        return;
                    }

                    int nextInt = random.nextInt(4);
                    int n = x;
                    int n2 = z;
                    if (nextInt == 0) {
                        n = x - 1;
                    }

                    if (nextInt == 1) {
                        n++;
                    }

                    if (nextInt == 2) {
                        n2 = z - 1;
                    }

                    if (nextInt == 3) {
                        n2++;
                    }

                    Block block = worldIn.getBlock(n, y - 1, n2);
                    if (worldIn.getBlock(n, y, n2).blockMaterial == Material.air
                        && (block == Blocks.farmland || block == Blocks.dirt || block == Blocks.grass)) {
                        worldIn.setBlock(n, y, n2, this.mainBlock);
                    }
                }
            }
        }
    }

    public void fertilizeStem(World world, int n, int n2, int n3) {
        int n4 = world.getBlockMetadata(n, n2, n3) + MathHelper.getRandomIntegerInRange(world.rand, 2, 5);
        if (n4 > 7) {
            n4 = 7;
        }

        world.setBlockMetadataWithNotify(n, n2, n3, n4, 2);
    }

    public float computeFloat(World world, int n, int n2, int n3) {
        float f = 1.0F;
        Block block = world.getBlock(n, n2, n3 - 1);
        Block block2 = world.getBlock(n, n2, n3 + 1);
        Block block3 = world.getBlock(n - 1, n2, n3);
        Block block4 = world.getBlock(n + 1, n2, n3);
        Block block5 = world.getBlock(n - 1, n2, n3 - 1);
        Block block6 = world.getBlock(n + 1, n2, n3 - 1);
        Block block7 = world.getBlock(n + 1, n2, n3 + 1);
        Block block8 = world.getBlock(n - 1, n2, n3 + 1);
        boolean flag = block3 == this || block4 == this;
        boolean flag2 = block == this || block2 == this;
        boolean flag3 = block5 == this || block6 == this || block7 == this || block8 == this;

        for (int i = n - 1; i <= n + 1; i++) {
            for (int j = n3 - 1; j <= n3 + 1; j++) {
                Block block9 = world.getBlock(i, n2 - 1, j);
                float f2 = 0.0F;
                if (block9 == Blocks.farmland) {
                    f2 = 1.0F;
                    if (world.getBlockMetadata(i, n2 - 1, j) > 0) {
                        f2 = 3.0F;
                    }
                }

                if (i != n || j != n3) {
                    f2 /= 4.0F;
                }

                f += f2;
            }
        }

        if (flag3 || flag && flag2) {
            f /= 2.0F;
        }

        return f;
    }

    @Override
    public int getRenderColor(int meta) {
        int n = meta * 32;
        int n2 = 255 - meta * 8;
        int n3 = meta * 4;
        return n << 16 | n2 << 8 | n3;
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return this.getRenderColor(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    public void setBlockBoundsForItemRender() {
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        this.maxY = (worldIn.getBlockMetadata(x, y, z) * 2 + 2) / 16.0F;
        float f = 0.125F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (float)this.maxY, 0.5F + f);
    }

    @Override
    public int getRenderType() {
        return 19;
    }

    public int getState(IBlockAccess blockAccess, int n, int n2, int n3) {
        int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
        return blockMetadata < 7
            ? -1
            : (
                blockAccess.getBlock(n - 1, n2, n3) == this.mainBlock
                    ? 0
                    : (
                        blockAccess.getBlock(n + 1, n2, n3) == this.mainBlock
                            ? 1
                            : (blockAccess.getBlock(n, n2, n3 - 1) == this.mainBlock ? 2 : (blockAccess.getBlock(n, n2, n3 + 1) == this.mainBlock ? 3 : -1))
                    )
            );
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, fortune);
        if (!worldIn.isRemote) {
            Item item = null;
            if (this.mainBlock == Blocks.pumpkin) {
                item = Items.pumpkin_seeds;
            }

            if (this.mainBlock == Blocks.melon_block) {
                item = Items.melon_seeds;
            }

            for (int i = 0; i < 3; i++) {
                if (worldIn.rand.nextInt(15) <= meta) {
                    this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(item));
                }
            }
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return this.mainBlock == Blocks.pumpkin ? Items.pumpkin_seeds : (this.mainBlock == Blocks.melon_block ? Items.melon_seeds : Item.getItemById(0));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_disconnected");
        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_connected");
    }

    public IIcon getStemIcon() {
        return this.iIcon2;
    }

    @Override
    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient) {
        return worldIn.getBlockMetadata(x, y, z) != 7;
    }

    @Override
    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z) {
        return true;
    }

    @Override
    public void fertilize(World worldIn, Random random, int x, int y, int z) {
        this.fertilizeStem(worldIn, x, y, z);
    }
}
