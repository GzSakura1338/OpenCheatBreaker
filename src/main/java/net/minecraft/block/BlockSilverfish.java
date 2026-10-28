package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class BlockSilverfish extends Block {
    public static String[] stringArray = new String[]{"stone", "cobble", "brick", "mossybrick", "crackedbrick", "chiseledbrick"};

    public BlockSilverfish() {
        super(Material.clay);
        this.setHardness(0.0F);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        switch (meta) {
            case 1:
                return Blocks.cobblestone.getBlockTextureFromSide(side);
            case 2:
                return Blocks.stonebrick.getBlockTextureFromSide(side);
            case 3:
                return Blocks.stonebrick.getIcon(side, 1);
            case 4:
                return Blocks.stonebrick.getIcon(side, 2);
            case 5:
                return Blocks.stonebrick.getIcon(side, 3);
            default:
                return Blocks.stone.getBlockTextureFromSide(side);
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, int x, int y, int z, int meta) {
        if (!worldIn.isRemote) {
            EntitySilverfish entitySilverfish = new EntitySilverfish(worldIn);
            entitySilverfish.setLocationAndAngles(x + 0.5, y, z + 0.5, 0.0F, 0.0F);
            worldIn.spawnEntityInWorld(entitySilverfish);
            entitySilverfish.spawnExplosionParticle();
        }

        super.onBlockDestroyedByPlayer(worldIn, x, y, z, meta);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    public static boolean checkBlock(Block block) {
        return block == Blocks.stone || block == Blocks.cobblestone || block == Blocks.stonebrick;
    }

    public static int computeInt(Block block, int n) {
        if (n == 0) {
            if (block == Blocks.cobblestone) {
                return 1;
            }

            if (block == Blocks.stonebrick) {
                return 2;
            }
        } else if (block == Blocks.stonebrick) {
            switch (n) {
                case 1:
                    return 3;
                case 2:
                    return 4;
                case 3:
                    return 5;
            }
        }

        return 0;
    }

    public static ImmutablePair findImmutablePair(int n) {
        switch (n) {
            case 1:
                return new ImmutablePair(Blocks.cobblestone, 0);
            case 2:
                return new ImmutablePair(Blocks.stonebrick, 0);
            case 3:
                return new ImmutablePair(Blocks.stonebrick, 1);
            case 4:
                return new ImmutablePair(Blocks.stonebrick, 2);
            case 5:
                return new ImmutablePair(Blocks.stonebrick, 3);
            default:
                return new ImmutablePair(Blocks.stone, 0);
        }
    }

    @Override
    public ItemStack createStackedBlock(int meta) {
        switch (meta) {
            case 1:
                return new ItemStack(Blocks.cobblestone);
            case 2:
                return new ItemStack(Blocks.stonebrick);
            case 3:
                return new ItemStack(Blocks.stonebrick, 1, 1);
            case 4:
                return new ItemStack(Blocks.stonebrick, 1, 2);
            case 5:
                return new ItemStack(Blocks.stonebrick, 1, 3);
            default:
                return new ItemStack(Blocks.stone);
        }
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        if (!worldIn.isRemote) {
            EntitySilverfish entitySilverfish = new EntitySilverfish(worldIn);
            entitySilverfish.setLocationAndAngles(x + 0.5, y, z + 0.5, 0.0F, 0.0F);
            worldIn.spawnEntityInWorld(entitySilverfish);
            entitySilverfish.spawnExplosionParticle();
        }
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return worldIn.getBlockMetadata(x, y, z);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        for (int i = 0; i < stringArray.length; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }
}
