package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockGrass extends Block implements IGrowable {
    public static Logger logger = LogManager.getLogger();
    public IIcon iIcon4;
    public IIcon iIcon2;
    public IIcon iIcon3;

    public BlockGrass() {
        super(Material.grass);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iIcon4 : (side == 0 ? Blocks.dirt.getBlockTextureFromSide(side) : this.blockIcon);
    }

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        if (side == 1) {
            return this.iIcon4;
        }

        if (side == 0) {
            return Blocks.dirt.getBlockTextureFromSide(side);
        }

        Material material = worldIn.getBlock(x, y + 1, z).getMaterial();
        return material != Material.snow && material != Material.craftedSnow ? this.blockIcon : this.iIcon2;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.iIcon4 = reg.registerIcon(this.getTextureName() + "_top");
        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_side_snowed");
        this.iIcon3 = reg.registerIcon(this.getTextureName() + "_side_overlay");
    }

    @Override
    public int getBlockColor() {
        double d = 0.5;
        double d2 = 1.0;
        return ColorizerGrass.getGrassColor(d, d2);
    }

    @Override
    public int getRenderColor(int meta) {
        return this.getBlockColor();
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int biomeGrassColor = worldIn.getBiomeGenForCoords(x + j, z + i).getBiomeGrassColor(x + j, y, z + i);
                n += (biomeGrassColor & 0xFF0000) >> 16;
                n2 += (biomeGrassColor & 0xFF00) >> 8;
                n3 += biomeGrassColor & 0xFF;
            }
        }

        return (n / 9 & 0xFF) << 16 | (n2 / 9 & 0xFF) << 8 | n3 / 9 & 0xFF;
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            if (worldIn.getBlockLightValue(x, y + 1, z) < 4 && worldIn.getBlock(x, y + 1, z).getLightOpacity() > 2) {
                worldIn.setBlock(x, y, z, Blocks.dirt);
            } else if (worldIn.getBlockLightValue(x, y + 1, z) >= 9) {
                for (int i = 0; i < 4; i++) {
                    int n = x + random.nextInt(3) - 1;
                    int n2 = y + random.nextInt(5) - 3;
                    int n3 = z + random.nextInt(3) - 1;
                    Block block = worldIn.getBlock(n, n2 + 1, n3);
                    if (worldIn.getBlock(n, n2, n3) == Blocks.dirt
                        && worldIn.getBlockMetadata(n, n2, n3) == 0
                        && worldIn.getBlockLightValue(n, n2 + 1, n3) >= 4
                        && block.getLightOpacity() <= 2) {
                        worldIn.setBlock(n, n2, n3, Blocks.grass);
                    }
                }
            }
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Blocks.dirt.getItemDropped(0, random, fortune);
    }

    public static IIcon getIconSideOverlay() {
        return Blocks.grass.iIcon3;
    }

    @Override
    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient) {
        return true;
    }

    @Override
    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z) {
        return true;
    }

    @Override
    public void fertilize(World worldIn, Random random, int x, int y, int z) {
        label38:
        for (int i = 0; i < 128; i++) {
            int n = x;
            int n2 = y + 1;
            int n3 = z;

            for (int j = 0; j < i / 16; j++) {
                n += random.nextInt(3) - 1;
                n2 += (random.nextInt(3) - 1) * random.nextInt(3) / 2;
                n3 += random.nextInt(3) - 1;
                if (worldIn.getBlock(n, n2 - 1, n3) != Blocks.grass || worldIn.getBlock(n, n2, n3).isNormalCube()) {
                    continue label38;
                }
            }

            if (worldIn.getBlock(n, n2, n3).blockMaterial == Material.air) {
                if (random.nextInt(8) != 0) {
                    if (Blocks.tallgrass.canBlockStay(worldIn, n, n2, n3)) {
                        worldIn.setBlock(n, n2, n3, Blocks.tallgrass, 1, 3);
                    }
                } else {
                    String formatText = worldIn.getBiomeGenForCoords(n, n3).formatText(random, n, n2, n3);
                    logger.debug("Flower in " + worldIn.getBiomeGenForCoords(n, n3).biomeName + ": " + formatText);
                    BlockFlower findBlockFlower = BlockFlower.findBlockFlower(formatText);
                    if (findBlockFlower != null && findBlockFlower.canBlockStay(worldIn, n, n2, n3)) {
                        int computeInt = BlockFlower.computeInt(formatText);
                        worldIn.setBlock(n, n2, n3, findBlockFlower, computeInt, 3);
                    }
                }
            }
        }
    }
}
