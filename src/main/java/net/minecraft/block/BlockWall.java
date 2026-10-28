package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWall extends Block {
    public static String[] stringArray = new String[]{"normal", "mossy"};

    public BlockWall(Block block) {
        super(block.blockMaterial);
        this.setHardness(block.blockHardness);
        this.setResistance(block.blockResistance / 3.0F);
        this.setStepSound(block.stepSound);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return meta == 1 ? Blocks.mossy_cobblestone.getBlockTextureFromSide(side) : Blocks.cobblestone.getBlockTextureFromSide(side);
    }

    @Override
    public int getRenderType() {
        return 32;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        boolean canConnectWallTo = this.canConnectWallTo(worldIn, x, y, z - 1);
        boolean canConnectWallTo2 = this.canConnectWallTo(worldIn, x, y, z + 1);
        boolean canConnectWallTo3 = this.canConnectWallTo(worldIn, x - 1, y, z);
        boolean canConnectWallTo4 = this.canConnectWallTo(worldIn, x + 1, y, z);
        float f = 0.25F;
        float f2 = 0.75F;
        float f3 = 0.25F;
        float f4 = 0.75F;
        float f5 = 1.0F;
        if (canConnectWallTo) {
            f3 = 0.0F;
        }

        if (canConnectWallTo2) {
            f4 = 1.0F;
        }

        if (canConnectWallTo3) {
            f = 0.0F;
        }

        if (canConnectWallTo4) {
            f2 = 1.0F;
        }

        if (canConnectWallTo && canConnectWallTo2 && !canConnectWallTo3 && !canConnectWallTo4) {
            f5 = 0.8125F;
            f = 0.3125F;
            f2 = 0.6875F;
        } else if (!canConnectWallTo && !canConnectWallTo2 && canConnectWallTo3 && canConnectWallTo4) {
            f5 = 0.8125F;
            f3 = 0.3125F;
            f4 = 0.6875F;
        }

        this.setBlockBounds(f, 0.0F, f3, f2, f5, f4);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        this.maxY = 1.5;
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    public boolean canConnectWallTo(IBlockAccess blockAccess, int n, int n2, int n3) {
        Block block = blockAccess.getBlock(n, n2, n3);
        return block == this || block == Blocks.fence_gate || block.blockMaterial.isOpaque() && block.renderAsNormalBlock() && block.blockMaterial != Material.gourd;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return side != 0 || super.shouldSideBeRendered(worldIn, x, y, z, side);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
    }
}
