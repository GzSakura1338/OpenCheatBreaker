package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMycelium extends Block {
    public IIcon iIcon3;
    public IIcon iIcon2;

    public BlockMycelium() {
        super(Material.grass);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iIcon3 : (side == 0 ? Blocks.dirt.getBlockTextureFromSide(side) : this.blockIcon);
    }

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        if (side == 1) {
            return this.iIcon3;
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
        this.iIcon3 = reg.registerIcon(this.getTextureName() + "_top");
        this.iIcon2 = reg.registerIcon("grass_side_snowed");
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
                        worldIn.setBlock(n, n2, n3, this);
                    }
                }
            }
        }
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        super.randomDisplayTick(worldIn, x, y, z, random);
        if (random.nextInt(10) == 0) {
            worldIn.spawnParticle("townaura", x + random.nextFloat(), y + 1.1F, z + random.nextFloat(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Blocks.dirt.getItemDropped(0, random, fortune);
    }
}
