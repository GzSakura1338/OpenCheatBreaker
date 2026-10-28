package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockEndPortalFrame extends Block {
    public IIcon iconEndPortalFrameTop;
    public IIcon iconEndPortalFrameEye;

    public BlockEndPortalFrame() {
        super(Material.rock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iconEndPortalFrameTop : (side == 0 ? Blocks.end_stone.getBlockTextureFromSide(side) : this.blockIcon);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.iconEndPortalFrameTop = reg.registerIcon(this.getTextureName() + "_top");
        this.iconEndPortalFrameEye = reg.registerIcon(this.getTextureName() + "_eye");
    }

    public IIcon getIconEndPortalFrameEye() {
        return this.iconEndPortalFrameEye;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 26;
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (isEnderEyeInserted(blockMetadata)) {
            this.setBlockBounds(0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
            super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }

        this.setBlockBoundsForItemRender();
    }

    public static boolean isEnderEyeInserted(int n) {
        return (n & 4) != 0;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int n = ((MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 3) + 2) % 4;
        worldIn.setBlockMetadataWithNotify(x, y, z, n, 2);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        return isEnderEyeInserted(blockMetadata) ? 15 : 0;
    }
}
