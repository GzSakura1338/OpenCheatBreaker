package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDaylightDetector extends BlockContainer {
    public IIcon[] iIconArray = new IIcon[2];

    public BlockDaylightDetector() {
        super(Material.wood);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return worldIn.getBlockMetadata(x, y, z);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
    }

    public void handleWorld(World world, int n, int n2, int n3) {
        if (!world.provider.hasNoSky) {
            int blockMetadata = world.getBlockMetadata(n, n2, n3);
            int n4 = world.getSavedLightValue(EnumSkyBlock.Sky, n, n2, n3) - world.skylightSubtracted;
            float celestialAngleRadians = world.getCelestialAngleRadians(1.0F);
            if (celestialAngleRadians < (float) Math.PI) {
                celestialAngleRadians += (0.0F - celestialAngleRadians) * 0.2F;
            } else {
                celestialAngleRadians += ((float) (Math.PI * 2) - celestialAngleRadians) * 0.2F;
            }

            n4 = Math.round(n4 * MathHelper.cos(celestialAngleRadians));
            if (n4 < 0) {
                n4 = 0;
            }

            if (n4 > 15) {
                n4 = 15;
            }

            if (blockMetadata != n4) {
                world.setBlockMetadataWithNotify(n, n2, n3, n4, 3);
            }
        }
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDaylightDetector();
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iIconArray[0] : this.iIconArray[1];
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray[0] = reg.registerIcon(this.getTextureName() + "_top");
        this.iIconArray[1] = reg.registerIcon(this.getTextureName() + "_side");
    }
}
