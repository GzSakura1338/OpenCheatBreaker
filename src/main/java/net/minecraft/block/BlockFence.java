package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemLead;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFence extends Block {
    public String text;

    public BlockFence(String str, Material material) {
        super(material);
        this.text = str;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        boolean canConnectFenceTo = this.canConnectFenceTo(worldIn, x, y, z - 1);
        boolean canConnectFenceTo2 = this.canConnectFenceTo(worldIn, x, y, z + 1);
        boolean canConnectFenceTo3 = this.canConnectFenceTo(worldIn, x - 1, y, z);
        boolean canConnectFenceTo4 = this.canConnectFenceTo(worldIn, x + 1, y, z);
        float f = 0.375F;
        float f2 = 0.625F;
        float f3 = 0.375F;
        float f4 = 0.625F;
        if (canConnectFenceTo) {
            f3 = 0.0F;
        }

        if (canConnectFenceTo2) {
            f4 = 1.0F;
        }

        if (canConnectFenceTo || canConnectFenceTo2) {
            this.setBlockBounds(f, 0.0F, f3, f2, 1.5F, f4);
            super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }

        f3 = 0.375F;
        f4 = 0.625F;
        if (canConnectFenceTo3) {
            f = 0.0F;
        }

        if (canConnectFenceTo4) {
            f2 = 1.0F;
        }

        if (canConnectFenceTo3 || canConnectFenceTo4 || !canConnectFenceTo && !canConnectFenceTo2) {
            this.setBlockBounds(f, 0.0F, f3, f2, 1.5F, f4);
            super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }

        if (canConnectFenceTo) {
            f3 = 0.0F;
        }

        if (canConnectFenceTo2) {
            f4 = 1.0F;
        }

        this.setBlockBounds(f, 0.0F, f3, f2, 1.0F, f4);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        boolean canConnectFenceTo = this.canConnectFenceTo(worldIn, x, y, z - 1);
        boolean canConnectFenceTo2 = this.canConnectFenceTo(worldIn, x, y, z + 1);
        boolean canConnectFenceTo3 = this.canConnectFenceTo(worldIn, x - 1, y, z);
        boolean canConnectFenceTo4 = this.canConnectFenceTo(worldIn, x + 1, y, z);
        float f = 0.375F;
        float f2 = 0.625F;
        float f3 = 0.375F;
        float f4 = 0.625F;
        if (canConnectFenceTo) {
            f3 = 0.0F;
        }

        if (canConnectFenceTo2) {
            f4 = 1.0F;
        }

        if (canConnectFenceTo3) {
            f = 0.0F;
        }

        if (canConnectFenceTo4) {
            f2 = 1.0F;
        }

        this.setBlockBounds(f, 0.0F, f3, f2, 1.0F, f4);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
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
    public int getRenderType() {
        return 11;
    }

    public boolean canConnectFenceTo(IBlockAccess blockAccess, int n, int n2, int n3) {
        Block block = blockAccess.getBlock(n, n2, n3);
        return block == this || block == Blocks.fence_gate || block.blockMaterial.isOpaque() && block.renderAsNormalBlock() && block.blockMaterial != Material.gourd;
    }

    public static boolean isFence(Block block) {
        return block == Blocks.fence || block == Blocks.nether_brick_fence;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.text);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        return worldIn.isRemote || ItemLead.checkEntityPlayer(player, worldIn, x, y, z);
    }
}
