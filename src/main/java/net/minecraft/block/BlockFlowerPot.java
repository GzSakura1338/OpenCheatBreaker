package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;

public class BlockFlowerPot extends BlockContainer {
    public BlockFlowerPot() {
        super(Material.circuits);
        this.setBlockBoundsForItemRender();
    }

    @Override
    public void setBlockBoundsForItemRender() {
        float f = 0.375F;
        float f2 = f / 2.0F;
        this.setBlockBounds(0.5F - f2, 0.0F, 0.5F - f2, 0.5F + f2, f, 0.5F + f2);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 33;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() instanceof ItemBlock) {
            TileEntityFlowerPot tileEntity = this.getTileEntity(worldIn, x, y, z);
            if (tileEntity == null) {
                return false;
            }

            if (tileEntity.getFlowerPotItem() != null) {
                return false;
            }

            Block blockFromItem = Block.getBlockFromItem(currentItem.getItem());
            if (!this.isPottable(blockFromItem, currentItem.getMetadata())) {
                return false;
            }

            tileEntity.setFlower(currentItem.getItem(), currentItem.getMetadata());
            tileEntity.markDirty();
            if (!worldIn.setBlockMetadataWithNotify(x, y, z, currentItem.getMetadata(), 2)) {
                worldIn.markBlockForUpdate(x, y, z);
            }

            if (!player.capabilities.isCreativeMode && --currentItem.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean isPottable(Block block, int n) {
        return block == Blocks.yellow_flower
            || block == Blocks.red_flower
            || block == Blocks.cactus
            || block == Blocks.brown_mushroom
            || block == Blocks.red_mushroom
            || block == Blocks.sapling
            || block == Blocks.deadbush
            || block == Blocks.tallgrass && n == 2;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        TileEntityFlowerPot tileEntity = this.getTileEntity(worldIn, x, y, z);
        return tileEntity != null && tileEntity.getFlowerPotItem() != null ? tileEntity.getFlowerPotItem() : Items.flower_pot;
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        TileEntityFlowerPot tileEntity = this.getTileEntity(worldIn, x, y, z);
        return tileEntity != null && tileEntity.getFlowerPotItem() != null ? tileEntity.getFlowerPotData() : 0;
    }

    @Override
    public boolean isFlowerPot() {
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return super.canPlaceBlockAt(worldIn, x, y, z) && World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z)) {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntityFlowerPot tileEntity = this.getTileEntity(worldIn, x, y, z);
        if (tileEntity != null && tileEntity.getFlowerPotItem() != null) {
            this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(tileEntity.getFlowerPotItem(), 1, tileEntity.getFlowerPotData()));
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player) {
        super.onBlockHarvested(worldIn, x, y, z, meta, player);
        if (player.capabilities.isCreativeMode) {
            TileEntityFlowerPot tileEntity = this.getTileEntity(worldIn, x, y, z);
            if (tileEntity != null) {
                tileEntity.setFlower(Item.getItemById(0), 0);
            }
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.flower_pot;
    }

    public TileEntityFlowerPot getTileEntity(World world, int n, int n2, int n3) {
        TileEntity tileEntity = world.getTileEntity(n, n2, n3);
        return tileEntity != null && tileEntity instanceof TileEntityFlowerPot ? (TileEntityFlowerPot)tileEntity : null;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        Block block = null;
        byte b = 0;
        switch (meta) {
            case 1:
                block = Blocks.red_flower;
                b = 0;
                break;
            case 2:
                block = Blocks.yellow_flower;
                break;
            case 3:
                block = Blocks.sapling;
                b = 0;
                break;
            case 4:
                block = Blocks.sapling;
                b = 1;
                break;
            case 5:
                block = Blocks.sapling;
                b = 2;
                break;
            case 6:
                block = Blocks.sapling;
                b = 3;
                break;
            case 7:
                block = Blocks.red_mushroom;
                break;
            case 8:
                block = Blocks.brown_mushroom;
                break;
            case 9:
                block = Blocks.cactus;
                break;
            case 10:
                block = Blocks.deadbush;
                break;
            case 11:
                block = Blocks.tallgrass;
                b = 2;
                break;
            case 12:
                block = Blocks.sapling;
                b = 4;
                break;
            case 13:
                block = Blocks.sapling;
                b = 5;
        }

        return new TileEntityFlowerPot(Item.getItemFromBlock(block), b);
    }
}
