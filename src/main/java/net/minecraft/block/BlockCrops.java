package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCrops extends BlockBush implements IGrowable {
    public IIcon[] icons;

    public BlockCrops() {
        this.setTickRandomly(true);
        float f = 0.5F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab(null);
        this.setHardness(0.0F);
        this.setStepSound(soundTypeGrass);
        this.disableStats();
    }

    @Override
    public boolean canPlaceBlockOn(Block ground) {
        return ground == Blocks.farmland;
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        super.updateTick(worldIn, x, y, z, random);
        if (worldIn.getBlockLightValue(x, y + 1, z) >= 9) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            if (blockMetadata < 7) {
                float computeFloat = this.computeFloat(worldIn, x, y, z);
                if (random.nextInt((int)(25.0F / computeFloat) + 1) == 0) {
                    worldIn.setBlockMetadataWithNotify(x, y, z, ++blockMetadata, 2);
                }
            }
        }
    }

    public void fertilize(World world, int n, int n2, int n3) {
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
                float f2 = 0.0F;
                if (world.getBlock(i, n2 - 1, j) == Blocks.farmland) {
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
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta > 7) {
            meta = 7;
        }

        return this.icons[meta];
    }

    @Override
    public int getRenderType() {
        return 6;
    }

    public Item getSeed() {
        return Items.wheat_seeds;
    }

    public Item getCrop() {
        return Items.wheat;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, 0);
        if (!worldIn.isRemote && meta >= 7) {
            int n = 3 + fortune;

            for (int i = 0; i < n; i++) {
                if (worldIn.rand.nextInt(15) <= meta) {
                    this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(this.getSeed(), 1, 0));
                }
            }
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return meta == 7 ? this.getCrop() : this.getSeed();
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return this.getSeed();
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.icons = new IIcon[8];

        for (int i = 0; i < this.icons.length; i++) {
            this.icons[i] = reg.registerIcon(this.getTextureName() + "_stage_" + i);
        }
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
        this.fertilize(worldIn, x, y, z);
    }
}
