package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockBrewingStand extends BlockContainer {
    public Random rand = new Random();
    public IIcon iconBrewingStandBase;

    public BlockBrewingStand() {
        super(Material.iron);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 25;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBrewingStand();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 0.875F, 0.5625F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBoundsForItemRender();
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        if (worldIn.isRemote) {
            return true;
        }

        TileEntityBrewingStand tileEntity = (TileEntityBrewingStand)worldIn.getTileEntity(x, y, z);
        if (tileEntity != null) {
            player.handleTileEntityBrewingStand(tileEntity);
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        if (itemIn.hasDisplayName()) {
            ((TileEntityBrewingStand)worldIn.getTileEntity(x, y, z)).setCustomName(itemIn.getDisplayName());
        }
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        double d = x + 0.4F + random.nextFloat() * 0.2F;
        double d2 = y + 0.7F + random.nextFloat() * 0.3F;
        double d3 = z + 0.4F + random.nextFloat() * 0.2F;
        worldIn.spawnParticle("smoke", d, d2, d3, 0.0, 0.0, 0.0);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityBrewingStand) {
            TileEntityBrewingStand tileEntityBrewingStand = (TileEntityBrewingStand)tileEntity;

            for (int i = 0; i < tileEntityBrewingStand.getSizeInventory(); i++) {
                ItemStack stackInSlot = tileEntityBrewingStand.getStackInSlot(i);
                if (stackInSlot != null) {
                    float f = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f3 = this.rand.nextFloat() * 0.8F + 0.1F;

                    while (stackInSlot.stackSize > 0) {
                        int n = this.rand.nextInt(21) + 10;
                        if (n > stackInSlot.stackSize) {
                            n = stackInSlot.stackSize;
                        }

                        stackInSlot.stackSize -= n;
                        EntityItem entityItem = new EntityItem(worldIn, x + f, y + f2, z + f3, new ItemStack(stackInSlot.getItem(), n, stackInSlot.getMetadata()));
                        float f4 = 0.05F;
                        entityItem.motionX = (float)this.rand.nextGaussian() * f4;
                        entityItem.motionY = (float)this.rand.nextGaussian() * f4 + 0.2F;
                        entityItem.motionZ = (float)this.rand.nextGaussian() * f4;
                        worldIn.spawnEntityInWorld(entityItem);
                    }
                }
            }
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.brewing_stand;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.brewing_stand;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        return Container.calcRedstoneFromInventory((IInventory)worldIn.getTileEntity(x, y, z));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        super.registerIcons(reg);
        this.iconBrewingStandBase = reg.registerIcon(this.getTextureName() + "_base");
    }

    public IIcon getIconBrewingStandBase() {
        return this.iconBrewingStandBase;
    }
}
