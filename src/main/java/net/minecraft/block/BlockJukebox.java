package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer {
    public IIcon iIcon2;

    public BlockJukebox() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iIcon2 : this.blockIcon;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (worldIn.getBlockMetadata(x, y, z) == 0) {
            return false;
        }

        this.handleWorld(worldIn, x, y, z);
        return true;
    }

    public void handleWorld(World world, int n, int n2, int n3, ItemStack itemStack) {
        if (!world.isRemote) {
            BlockJukebox.TileEntityJukebox tileEntity = (BlockJukebox.TileEntityJukebox)world.getTileEntity(n, n2, n3);
            if (tileEntity != null) {
                tileEntity.handleItemStack(itemStack.copy());
                world.setBlockMetadataWithNotify(n, n2, n3, 1, 2);
            }
        }
    }

    public void handleWorld(World world, int n, int n2, int n3) {
        if (!world.isRemote) {
            BlockJukebox.TileEntityJukebox tileEntity = (BlockJukebox.TileEntityJukebox)world.getTileEntity(n, n2, n3);
            if (tileEntity != null) {
                ItemStack itemStack = tileEntity.getItemStack();
                if (itemStack != null) {
                    world.playAuxSFX(1005, n, n2, n3, 0);
                    world.playRecord(null, n, n2, n3);
                    tileEntity.handleItemStack(null);
                    world.setBlockMetadataWithNotify(n, n2, n3, 0, 2);
                    float f = 0.7F;
                    double d = world.rand.nextFloat() * f + (1.0F - f) * 0.5;
                    double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.2 + 0.6;
                    double d3 = world.rand.nextFloat() * f + (1.0F - f) * 0.5;
                    ItemStack itemStack2 = itemStack.copy();
                    EntityItem entityItem = new EntityItem(world, n + d, n2 + d2, n3 + d3, itemStack2);
                    entityItem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityItem);
                }
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        this.handleWorld(worldIn, x, y, z);
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        if (!worldIn.isRemote) {
            super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, 0);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new BlockJukebox.TileEntityJukebox();
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_top");
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        ItemStack itemStack = ((BlockJukebox.TileEntityJukebox)worldIn.getTileEntity(x, y, z)).getItemStack();
        return itemStack == null ? 0 : Item.getIdFromItem(itemStack.getItem()) + 1 - Item.getIdFromItem(Items.record_13);
    }

    public static class TileEntityJukebox extends TileEntity {
        public ItemStack itemStack;

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);
            if (compound.hasKey("RecordItem", 10)) {
                this.handleItemStack(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("RecordItem")));
            } else if (compound.getInteger("Record") > 0) {
                this.handleItemStack(new ItemStack(Item.getItemById(compound.getInteger("Record")), 1, 0));
            }
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            super.writeToNBT(compound);
            if (this.getItemStack() != null) {
                compound.setTag("RecordItem", this.getItemStack().writeToNBT(new NBTTagCompound()));
                compound.setInteger("Record", Item.getIdFromItem(this.getItemStack().getItem()));
            }
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }

        public void handleItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.markDirty();
        }
    }
}
