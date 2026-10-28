package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockChest extends BlockContainer {
    public Random rand = new Random();
    public int chestType;

    public BlockChest(int type) {
        super(Material.wood);
        this.chestType = type;
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
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        if (worldIn.getBlock(x, y, z - 1) == this) {
            this.setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
        } else if (worldIn.getBlock(x, y, z + 1) == this) {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
        } else if (worldIn.getBlock(x - 1, y, z) == this) {
            this.setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        } else if (worldIn.getBlock(x + 1, y, z) == this) {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
        } else {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        super.onBlockAdded(worldIn, x, y, z);
        this.initMetadata(worldIn, x, y, z);
        Block block = worldIn.getBlock(x, y, z - 1);
        Block block2 = worldIn.getBlock(x, y, z + 1);
        Block block3 = worldIn.getBlock(x - 1, y, z);
        Block block4 = worldIn.getBlock(x + 1, y, z);
        if (block == this) {
            this.initMetadata(worldIn, x, y, z - 1);
        }

        if (block2 == this) {
            this.initMetadata(worldIn, x, y, z + 1);
        }

        if (block3 == this) {
            this.initMetadata(worldIn, x - 1, y, z);
        }

        if (block4 == this) {
            this.initMetadata(worldIn, x + 1, y, z);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        Block block = worldIn.getBlock(x, y, z - 1);
        Block block2 = worldIn.getBlock(x, y, z + 1);
        Block block3 = worldIn.getBlock(x - 1, y, z);
        Block block4 = worldIn.getBlock(x + 1, y, z);
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

        if (block != this && block2 != this && block3 != this && block4 != this) {
            worldIn.setBlockMetadataWithNotify(x, y, z, b, 3);
        } else {
            if ((block == this || block2 == this) && (b == 4 || b == 5)) {
                if (block == this) {
                    worldIn.setBlockMetadataWithNotify(x, y, z - 1, b, 3);
                } else {
                    worldIn.setBlockMetadataWithNotify(x, y, z + 1, b, 3);
                }

                worldIn.setBlockMetadataWithNotify(x, y, z, b, 3);
            }

            if ((block3 == this || block4 == this) && (b == 2 || b == 3)) {
                if (block3 == this) {
                    worldIn.setBlockMetadataWithNotify(x - 1, y, z, b, 3);
                } else {
                    worldIn.setBlockMetadataWithNotify(x + 1, y, z, b, 3);
                }

                worldIn.setBlockMetadataWithNotify(x, y, z, b, 3);
            }
        }

        if (itemIn.hasDisplayName()) {
            ((TileEntityChest)worldIn.getTileEntity(x, y, z)).setCustomName(itemIn.getDisplayName());
        }
    }

    public void initMetadata(World world, int n, int n2, int n3) {
        if (!world.isRemote) {
            Block block = world.getBlock(n, n2, n3 - 1);
            Block block2 = world.getBlock(n, n2, n3 + 1);
            Block block3 = world.getBlock(n - 1, n2, n3);
            Block block4 = world.getBlock(n + 1, n2, n3);
            boolean flag = true;
            byte b;
            if (block != this && block2 != this) {
                if (block3 != this && block4 != this) {
                    b = 3;
                    if (block.isFullBlock() && !block2.isFullBlock()) {
                        b = 3;
                    }

                    if (block2.isFullBlock() && !block.isFullBlock()) {
                        b = 2;
                    }

                    if (block3.isFullBlock() && !block4.isFullBlock()) {
                        b = 5;
                    }

                    if (block4.isFullBlock() && !block3.isFullBlock()) {
                        b = 4;
                    }
                } else {
                    int n4 = block3 == this ? n - 1 : n + 1;
                    Block block5 = world.getBlock(n4, n2, n3 - 1);
                    int n5 = block3 == this ? n - 1 : n + 1;
                    Block block6 = world.getBlock(n5, n2, n3 + 1);
                    b = 3;
                    boolean flag2 = true;
                    int n6;
                    if (block3 == this) {
                        n6 = world.getBlockMetadata(n - 1, n2, n3);
                    } else {
                        n6 = world.getBlockMetadata(n + 1, n2, n3);
                    }

                    if (n6 == 2) {
                        b = 2;
                    }

                    if ((block.isFullBlock() || block5.isFullBlock()) && !block2.isFullBlock() && !block6.isFullBlock()) {
                        b = 3;
                    }

                    if ((block2.isFullBlock() || block6.isFullBlock()) && !block.isFullBlock() && !block5.isFullBlock()) {
                        b = 2;
                    }
                }
            } else {
                int n4 = block == this ? n3 - 1 : n3 + 1;
                Block block5 = world.getBlock(n - 1, n2, n4);
                int n5 = block == this ? n3 - 1 : n3 + 1;
                Block block6 = world.getBlock(n + 1, n2, n5);
                b = 5;
                boolean flag2 = true;
                int n6;
                if (block == this) {
                    n6 = world.getBlockMetadata(n, n2, n3 - 1);
                } else {
                    n6 = world.getBlockMetadata(n, n2, n3 + 1);
                }

                if (n6 == 4) {
                    b = 4;
                }

                if ((block3.isFullBlock() || block5.isFullBlock()) && !block4.isFullBlock() && !block6.isFullBlock()) {
                    b = 5;
                }

                if ((block4.isFullBlock() || block6.isFullBlock()) && !block3.isFullBlock() && !block5.isFullBlock()) {
                    b = 4;
                }
            }

            world.setBlockMetadataWithNotify(n, n2, n3, b, 3);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        int n = 0;
        if (worldIn.getBlock(x - 1, y, z) == this) {
            n++;
        }

        if (worldIn.getBlock(x + 1, y, z) == this) {
            n++;
        }

        if (worldIn.getBlock(x, y, z - 1) == this) {
            n++;
        }

        if (worldIn.getBlock(x, y, z + 1) == this) {
            n++;
        }

        return n <= 1
            && !this.isDoubleChest(worldIn, x - 1, y, z)
            && !this.isDoubleChest(worldIn, x + 1, y, z)
            && !this.isDoubleChest(worldIn, x, y, z - 1)
            && !this.isDoubleChest(worldIn, x, y, z + 1);
    }

    public boolean isDoubleChest(World worldIn, int x, int y, int z) {
        return worldIn.getBlock(x, y, z) == this
            && (
                worldIn.getBlock(x - 1, y, z) == this
                    || worldIn.getBlock(x + 1, y, z) == this
                    || worldIn.getBlock(x, y, z - 1) == this
                    || worldIn.getBlock(x, y, z + 1) == this
            );
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
        TileEntityChest tileEntity = (TileEntityChest)worldIn.getTileEntity(x, y, z);
        if (tileEntity != null) {
            tileEntity.updateContainingBlockInfo();
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntityChest tileEntity = (TileEntityChest)worldIn.getTileEntity(x, y, z);
        if (tileEntity != null) {
            for (int i = 0; i < tileEntity.getSizeInventory(); i++) {
                ItemStack stackInSlot = tileEntity.getStackInSlot(i);
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
                        if (stackInSlot.hasTagCompound()) {
                            entityItem.getEntityItem().setTagCompound((NBTTagCompound)stackInSlot.getTagCompound().copy());
                        }

                        worldIn.spawnEntityInWorld(entityItem);
                    }
                }
            }

            worldIn.updateNeighborsAboutBlockChange(x, y, z, blockBroken);
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        if (worldIn.isRemote) {
            return true;
        }

        IInventory inventory = this.getInventory(worldIn, x, y, z);
        if (inventory != null) {
            player.displayGUIChest(inventory);
        }

        return true;
    }

    public IInventory getInventory(World world, int n, int n2, int n3) {
        Object tileEntity = world.getTileEntity(n, n2, n3);
        if (tileEntity == null) {
            return null;
        }

        if (world.getBlock(n, n2 + 1, n3).isNormalCube()) {
            return null;
        }

        if (isOcelotSittingOnTop(world, n, n2, n3)) {
            return null;
        }

        if (world.getBlock(n - 1, n2, n3) != this
            || !world.getBlock(n - 1, n2 + 1, n3).isNormalCube() && !isOcelotSittingOnTop(world, n - 1, n2, n3)) {
            if (world.getBlock(n + 1, n2, n3) != this
                || !world.getBlock(n + 1, n2 + 1, n3).isNormalCube() && !isOcelotSittingOnTop(world, n + 1, n2, n3)) {
                if (world.getBlock(n, n2, n3 - 1) != this
                    || !world.getBlock(n, n2 + 1, n3 - 1).isNormalCube() && !isOcelotSittingOnTop(world, n, n2, n3 - 1)) {
                    if (world.getBlock(n, n2, n3 + 1) != this
                        || !world.getBlock(n, n2 + 1, n3 + 1).isNormalCube() && !isOcelotSittingOnTop(world, n, n2, n3 + 1)) {
                        if (world.getBlock(n - 1, n2, n3) == this) {
                            tileEntity = new InventoryLargeChest("container.chestDouble", (TileEntityChest)world.getTileEntity(n - 1, n2, n3), (IInventory)tileEntity);
                        }

                        if (world.getBlock(n + 1, n2, n3) == this) {
                            tileEntity = new InventoryLargeChest("container.chestDouble", (IInventory)tileEntity, (TileEntityChest)world.getTileEntity(n + 1, n2, n3));
                        }

                        if (world.getBlock(n, n2, n3 - 1) == this) {
                            tileEntity = new InventoryLargeChest("container.chestDouble", (TileEntityChest)world.getTileEntity(n, n2, n3 - 1), (IInventory)tileEntity);
                        }

                        if (world.getBlock(n, n2, n3 + 1) == this) {
                            tileEntity = new InventoryLargeChest("container.chestDouble", (IInventory)tileEntity, (TileEntityChest)world.getTileEntity(n, n2, n3 + 1));
                        }

                        return (IInventory)tileEntity;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityChest();
    }

    @Override
    public boolean canProvidePower() {
        return this.chestType == 1;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        if (!this.canProvidePower()) {
            return 0;
        }

        int numPlayersUsing = ((TileEntityChest)worldIn.getTileEntity(x, y, z)).numPlayersUsing;
        return MathHelper.clamp_int(numPlayersUsing, 0, 15);
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return side == 1 ? this.isProvidingWeakPower(worldIn, x, y, z, side) : 0;
    }

    public static boolean isOcelotSittingOnTop(World world, int n, int n2, int n3) {
        for (Entity entity : (Iterable<Entity>)world.getEntitiesWithinAABB(EntityOcelot.class, AxisAlignedBB.getBoundingBox(n, n2 + 1, n3, n + 1, n2 + 2, n3 + 1))) {
            EntityOcelot entityOcelot = (EntityOcelot)entity;
            if (entityOcelot.isSitting()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        return Container.calcRedstoneFromInventory(this.getInventory(worldIn, x, y, z));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("planks_oak");
    }
}
