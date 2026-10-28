package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockEnderChest extends BlockContainer {
    public BlockEnderChest() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
        return 22;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(Blocks.obsidian);
    }

    @Override
    public int quantityDropped(Random random) {
        return 8;
    }

    @Override
    public boolean canSilkHarvest() {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        byte b = 0;
        int n = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 3;
        if (n == 0) {
            b = 2;
        }

        if (n == 1) {
            b = 5;
        }

        if (n == 2) {
            b = 3;
        }

        if (n == 3) {
            b = 4;
        }

        worldIn.setBlockMetadataWithNotify(x, y, z, b, 2);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        InventoryEnderChest inventoryEnderChest = player.getInventoryEnderChest();
        TileEntityEnderChest tileEntity = (TileEntityEnderChest)worldIn.getTileEntity(x, y, z);
        if (inventoryEnderChest != null && tileEntity != null) {
            if (worldIn.getBlock(x, y + 1, z).isNormalCube()) {
                return true;
            }

            if (worldIn.isRemote) {
                return true;
            }

            inventoryEnderChest.setChestTileEntity(tileEntity);
            player.displayGUIChest(inventoryEnderChest);
            return true;
        } else {
            return true;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEnderChest();
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        for (int i = 0; i < 3; i++) {
            double d = x + random.nextFloat();
            double d2 = y + random.nextFloat();
            d = z + random.nextFloat();
            double d3 = 0.0;
            double d4 = 0.0;
            double d5 = 0.0;
            int n = random.nextInt(2) * 2 - 1;
            int n2 = random.nextInt(2) * 2 - 1;
            d3 = (random.nextFloat() - 0.5) * 0.125;
            d4 = (random.nextFloat() - 0.5) * 0.125;
            d5 = (random.nextFloat() - 0.5) * 0.125;
            double d6 = z + 0.5 + 0.25 * n2;
            d5 = random.nextFloat() * 1.0F * n2;
            double d7 = x + 0.5 + 0.25 * n;
            d3 = random.nextFloat() * 1.0F * n;
            worldIn.spawnParticle("portal", d7, d2, d6, d3, d4, d5);
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("obsidian");
    }
}
