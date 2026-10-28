package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDirt extends Block {
    public static String[] stringArray = new String[]{"default", "default", "podzol"};
    public IIcon iIcon3;
    public IIcon iIcon2;

    public BlockDirt() {
        super(Material.ground);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 2) {
            if (side == 1) {
                return this.iIcon3;
            }

            if (side != 0) {
                return this.iIcon2;
            }
        }

        return this.blockIcon;
    }

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (blockMetadata == 2) {
            if (side == 1) {
                return this.iIcon3;
            }

            if (side != 0) {
                Material material = worldIn.getBlock(x, y + 1, z).getMaterial();
                if (material == Material.snow || material == Material.craftedSnow) {
                    return Blocks.grass.getIcon(worldIn, x, y, z, side);
                }

                Block block = worldIn.getBlock(x, y + 1, z);
                if (block != Blocks.dirt && block != Blocks.grass) {
                    return this.iIcon2;
                }
            }
        }

        return this.blockIcon;
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    public ItemStack createStackedBlock(int meta) {
        if (meta == 1) {
            meta = 0;
        }

        return super.createStackedBlock(meta);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 2));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        super.registerIcons(reg);
        this.iIcon3 = reg.registerIcon(this.getTextureName() + "_podzol_top");
        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_podzol_side");
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (blockMetadata == 1) {
            blockMetadata = 0;
        }

        return blockMetadata;
    }
}
