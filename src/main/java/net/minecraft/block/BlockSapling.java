package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;

public class BlockSapling extends BlockBush implements IGrowable {
    public static String[] VARIANTS = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "roofed_oak"};
    public static IIcon[] TEXTURES = new IIcon[VARIANTS.length];

    public BlockSapling() {
        float f = 0.4F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            super.updateTick(worldIn, x, y, z, random);
            if (worldIn.getBlockLightValue(x, y + 1, z) >= 9 && random.nextInt(7) == 0) {
                this.markOrGrowMarked(worldIn, x, y, z, random);
            }
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        meta &= 7;
        return TEXTURES[MathHelper.clamp_int(meta, 0, 5)];
    }

    public void markOrGrowMarked(World world, int n, int n2, int n3, Random random) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        if ((blockMetadata & 8) == 0) {
            world.setBlockMetadataWithNotify(n, n2, n3, blockMetadata | 8, 4);
        } else {
            this.growTree(world, n, n2, n3, random);
        }
    }

    public void growTree(World world, int n, int n2, int n3, Random random) {
        int n4 = world.getBlockMetadata(n, n2, n3) & 7;
        WorldGenAbstractTree worldGenAbstractTree = random.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
        int n5 = 0;
        int n6 = 0;
        boolean flag = false;
        switch (n4) {
            case 0:
            default:
                break;
            case 1:
                label81:
                for (n5 = 0; n5 >= -1; n5--) {
                    for (n6 = 0; n6 >= -1; n6--) {
                        if (this.checkWorld(world, n + n5, n2, n3 + n6, 1)
                            && this.checkWorld(world, n + n5 + 1, n2, n3 + n6, 1)
                            && this.checkWorld(world, n + n5, n2, n3 + n6 + 1, 1)
                            && this.checkWorld(world, n + n5 + 1, n2, n3 + n6 + 1, 1)) {
                            worldGenAbstractTree = new WorldGenMegaPineTree(false, random.nextBoolean());
                            flag = true;
                            break label81;
                        }
                    }
                }

                if (!flag) {
                    n6 = 0;
                    n5 = 0;
                    worldGenAbstractTree = new WorldGenTaiga2(true);
                }
                break;
            case 2:
                worldGenAbstractTree = new WorldGenForest(true, false);
                break;
            case 3:
                label98:
                for (n5 = 0; n5 >= -1; n5--) {
                    for (n6 = 0; n6 >= -1; n6--) {
                        if (this.checkWorld(world, n + n5, n2, n3 + n6, 3)
                            && this.checkWorld(world, n + n5 + 1, n2, n3 + n6, 3)
                            && this.checkWorld(world, n + n5, n2, n3 + n6 + 1, 3)
                            && this.checkWorld(world, n + n5 + 1, n2, n3 + n6 + 1, 3)) {
                            worldGenAbstractTree = new WorldGenMegaJungle(true, 10, 20, 3, 3);
                            flag = true;
                            break label98;
                        }
                    }
                }

                if (!flag) {
                    n6 = 0;
                    n5 = 0;
                    worldGenAbstractTree = new WorldGenTrees(true, 4 + random.nextInt(7), 3, 3, false);
                }
                break;
            case 4:
                worldGenAbstractTree = new WorldGenSavannaTree(true);
                break;
            case 5:
                label115:
                for (n5 = 0; n5 >= -1; n5--) {
                    for (n6 = 0; n6 >= -1; n6--) {
                        if (this.checkWorld(world, n + n5, n2, n3 + n6, 5)
                            && this.checkWorld(world, n + n5 + 1, n2, n3 + n6, 5)
                            && this.checkWorld(world, n + n5, n2, n3 + n6 + 1, 5)
                            && this.checkWorld(world, n + n5 + 1, n2, n3 + n6 + 1, 5)) {
                            worldGenAbstractTree = new WorldGenCanopyTree(true);
                            flag = true;
                            break label115;
                        }
                    }
                }

                if (!flag) {
                    return;
                }
        }

        Block air = Blocks.air;
        if (flag) {
            world.setBlock(n + n5, n2, n3 + n6, air, 0, 4);
            world.setBlock(n + n5 + 1, n2, n3 + n6, air, 0, 4);
            world.setBlock(n + n5, n2, n3 + n6 + 1, air, 0, 4);
            world.setBlock(n + n5 + 1, n2, n3 + n6 + 1, air, 0, 4);
        } else {
            world.setBlock(n, n2, n3, air, 0, 4);
        }

        if (!worldGenAbstractTree.generate(world, random, n + n5, n2, n3 + n6)) {
            if (flag) {
                world.setBlock(n + n5, n2, n3 + n6, this, n4, 4);
                world.setBlock(n + n5 + 1, n2, n3 + n6, this, n4, 4);
                world.setBlock(n + n5, n2, n3 + n6 + 1, this, n4, 4);
                world.setBlock(n + n5 + 1, n2, n3 + n6 + 1, this, n4, 4);
            } else {
                world.setBlock(n, n2, n3, this, n4, 4);
            }
        }
    }

    public boolean checkWorld(World world, int n, int n2, int n3, int n4) {
        return world.getBlock(n, n2, n3) == this && (world.getBlockMetadata(n, n2, n3) & 7) == n4;
    }

    @Override
    public int damageDropped(int meta) {
        return MathHelper.clamp_int(meta & 7, 0, 5);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
        list.add(new ItemStack(itemIn, 1, 2));
        list.add(new ItemStack(itemIn, 1, 3));
        list.add(new ItemStack(itemIn, 1, 4));
        list.add(new ItemStack(itemIn, 1, 5));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = reg.registerIcon(this.getTextureName() + "_" + VARIANTS[i]);
        }
    }

    @Override
    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient) {
        return true;
    }

    @Override
    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z) {
        return worldIn.rand.nextFloat() < 0.45;
    }

    @Override
    public void fertilize(World worldIn, Random random, int x, int y, int z) {
        this.markOrGrowMarked(worldIn, x, y, z, random);
    }
}
