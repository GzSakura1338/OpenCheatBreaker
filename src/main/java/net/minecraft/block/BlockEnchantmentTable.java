package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockEnchantmentTable extends BlockContainer {
    public IIcon iIcon3;
    public IIcon iIcon2;

    public BlockEnchantmentTable() {
        super(Material.rock);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
        this.setLightOpacity(0);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        super.randomDisplayTick(worldIn, x, y, z, random);

        for (int i = x - 2; i <= x + 2; i++) {
            for (int j = z - 2; j <= z + 2; j++) {
                if (i > x - 2 && i < x + 2 && j == z - 1) {
                    j = z + 2;
                }

                if (random.nextInt(16) == 0) {
                    for (int k = y; k <= y + 1; k++) {
                        if (worldIn.getBlock(i, k, j) == Blocks.bookshelf) {
                            if (!worldIn.isAirBlock((i - x) / 2 + x, k, (j - z) / 2 + z)) {
                                break;
                            }

                            worldIn.spawnParticle(
                                "enchantmenttable",
                                x + 0.5,
                                y + 2.0,
                                z + 0.5,
                                i - x + random.nextFloat() - 0.5,
                                k - y - random.nextFloat() - 1.0F,
                                j - z + random.nextFloat() - 0.5
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 0 ? this.iIcon2 : (side == 1 ? this.iIcon3 : this.blockIcon);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEnchantmentTable();
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        if (worldIn.isRemote) {
            return true;
        }

        TileEntityEnchantmentTable tileEntity = (TileEntityEnchantmentTable)worldIn.getTileEntity(x, y, z);
        player.displayGUIEnchantment(x, y, z, tileEntity.length() ? tileEntity.getText() : null);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, itemIn);
        if (itemIn.hasDisplayName()) {
            ((TileEntityEnchantmentTable)worldIn.getTileEntity(x, y, z)).setCustomName(itemIn.getDisplayName());
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.iIcon3 = reg.registerIcon(this.getTextureName() + "_top");
        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_bottom");
    }
}
