package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPane extends Block {
    public String text;
    public boolean canDrop;
    public String text2;
    public IIcon iIcon2;

    public BlockPane(String str, String str2, Material material, boolean flag) {
        super(material);
        this.text = str2;
        this.canDrop = flag;
        this.text2 = str;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return !this.canDrop ? null : super.getItemDropped(meta, random, fortune);
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
    public int getRenderType() {
        return this.blockMaterial == Material.glass ? 41 : 18;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return worldIn.getBlock(x, y, z) != this && super.shouldSideBeRendered(worldIn, x, y, z, side);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        boolean canPaneConnectToBlock = this.canPaneConnectToBlock(worldIn.getBlock(x, y, z - 1));
        boolean canPaneConnectToBlock2 = this.canPaneConnectToBlock(worldIn.getBlock(x, y, z + 1));
        boolean canPaneConnectToBlock3 = this.canPaneConnectToBlock(worldIn.getBlock(x - 1, y, z));
        boolean canPaneConnectToBlock4 = this.canPaneConnectToBlock(worldIn.getBlock(x + 1, y, z));
        if ((!canPaneConnectToBlock3 || !canPaneConnectToBlock4) && (canPaneConnectToBlock3 || canPaneConnectToBlock4 || canPaneConnectToBlock || canPaneConnectToBlock2)) {
            if (canPaneConnectToBlock3 && !canPaneConnectToBlock4) {
                this.setBlockBounds(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
            } else if (!canPaneConnectToBlock3 && canPaneConnectToBlock4) {
                this.setBlockBounds(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
            }
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }

        if ((!canPaneConnectToBlock || !canPaneConnectToBlock2) && (canPaneConnectToBlock3 || canPaneConnectToBlock4 || canPaneConnectToBlock || canPaneConnectToBlock2)) {
            if (canPaneConnectToBlock && !canPaneConnectToBlock2) {
                this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
            } else if (!canPaneConnectToBlock && canPaneConnectToBlock2) {
                this.setBlockBounds(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
                super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
            }
        } else {
            this.setBlockBounds(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
            super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        float f = 0.4375F;
        float f2 = 0.5625F;
        float f3 = 0.4375F;
        float f4 = 0.5625F;
        boolean canPaneConnectToBlock = this.canPaneConnectToBlock(worldIn.getBlock(x, y, z - 1));
        boolean canPaneConnectToBlock2 = this.canPaneConnectToBlock(worldIn.getBlock(x, y, z + 1));
        boolean canPaneConnectToBlock3 = this.canPaneConnectToBlock(worldIn.getBlock(x - 1, y, z));
        boolean canPaneConnectToBlock4 = this.canPaneConnectToBlock(worldIn.getBlock(x + 1, y, z));
        if ((!canPaneConnectToBlock3 || !canPaneConnectToBlock4) && (canPaneConnectToBlock3 || canPaneConnectToBlock4 || canPaneConnectToBlock || canPaneConnectToBlock2)) {
            if (canPaneConnectToBlock3 && !canPaneConnectToBlock4) {
                f = 0.0F;
            } else if (!canPaneConnectToBlock3 && canPaneConnectToBlock4) {
                f2 = 1.0F;
            }
        } else {
            f = 0.0F;
            f2 = 1.0F;
        }

        if ((!canPaneConnectToBlock || !canPaneConnectToBlock2) && (canPaneConnectToBlock3 || canPaneConnectToBlock4 || canPaneConnectToBlock || canPaneConnectToBlock2)) {
            if (canPaneConnectToBlock && !canPaneConnectToBlock2) {
                f3 = 0.0F;
            } else if (!canPaneConnectToBlock && canPaneConnectToBlock2) {
                f4 = 1.0F;
            }
        } else {
            f3 = 0.0F;
            f4 = 1.0F;
        }

        this.setBlockBounds(f, 0.0F, f3, f2, 1.0F, f4);
    }

    public IIcon getIIcon() {
        return this.iIcon2;
    }

    public boolean canPaneConnectToBlock(Block block) {
        return block.isFullBlock()
            || block == this
            || block == Blocks.glass
            || block == Blocks.stained_glass
            || block == Blocks.stained_glass_pane
            || block instanceof BlockPane;
    }

    @Override
    public boolean canSilkHarvest() {
        return true;
    }

    @Override
    public ItemStack createStackedBlock(int meta) {
        return new ItemStack(Item.getItemFromBlock(this), 1, meta);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.text2);
        this.iIcon2 = reg.registerIcon(this.text);
    }
}
