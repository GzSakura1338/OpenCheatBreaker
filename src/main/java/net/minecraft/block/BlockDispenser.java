package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistryDefaulted;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer {
    public static IRegistry dispenseBehaviorRegistry = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
    public Random random = new Random();
    public IIcon iIcon4;
    public IIcon iIcon2;
    public IIcon iIcon3;

    public BlockDispenser() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public int tickRate(World worldIn) {
        return 4;
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        super.onBlockAdded(worldIn, x, y, z);
        this.handleWorld(worldIn, x, y, z);
    }

    public void handleWorld(World world, int n, int n2, int n3) {
        if (!world.isRemote) {
            Block block = world.getBlock(n, n2, n3 - 1);
            Block block2 = world.getBlock(n, n2, n3 + 1);
            Block block3 = world.getBlock(n - 1, n2, n3);
            Block block4 = world.getBlock(n + 1, n2, n3);
            byte b = 3;
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

            world.setBlockMetadataWithNotify(n, n2, n3, b, 2);
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        int n = meta & 7;
        return side == n
            ? (n != 1 && n != 0 ? this.iIcon2 : this.iIcon3)
            : (n != 1 && n != 0 ? (side != 1 && side != 0 ? this.blockIcon : this.iIcon4) : this.iIcon4);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("furnace_side");
        this.iIcon4 = reg.registerIcon("furnace_top");
        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_front_horizontal");
        this.iIcon3 = reg.registerIcon(this.getTextureName() + "_front_vertical");
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        if (worldIn.isRemote) {
            return true;
        }

        TileEntityDispenser tileEntity = (TileEntityDispenser)worldIn.getTileEntity(x, y, z);
        if (tileEntity != null) {
            player.handleTileEntityDispenser(tileEntity);
        }

        return true;
    }

    public void handleWorld2(World world, int n, int n2, int n3) {
        BlockSourceImpl blockSourceImpl = new BlockSourceImpl(world, n, n2, n3);
        TileEntityDispenser blockTileEntity = (TileEntityDispenser)blockSourceImpl.getBlockTileEntity();
        if (blockTileEntity != null) {
            int chooseNonEmptySlot = blockTileEntity.chooseNonEmptySlot();
            if (chooseNonEmptySlot < 0) {
                world.playAuxSFX(1001, n, n2, n3, 0);
            } else {
                ItemStack stackInSlot = blockTileEntity.getStackInSlot(chooseNonEmptySlot);
                IBehaviorDispenseItem behaviorForItem = this.getBehaviorForItem(stackInSlot);
                if (behaviorForItem != IBehaviorDispenseItem.itemDispenseBehaviorProvider) {
                    ItemStack dispense = behaviorForItem.dispense(blockSourceImpl, stackInSlot);
                    blockTileEntity.setInventorySlotContents(chooseNonEmptySlot, dispense.stackSize == 0 ? null : dispense);
                }
            }
        }
    }

    public IBehaviorDispenseItem getBehaviorForItem(ItemStack itemStack) {
        return (IBehaviorDispenseItem)dispenseBehaviorRegistry.getObject(itemStack.getItem());
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        boolean flag = worldIn.isBlockIndirectlyGettingPowered(x, y, z) || worldIn.isBlockIndirectlyGettingPowered(x, y + 1, z);
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        boolean flag2 = (blockMetadata & 8) != 0;
        if (flag && !flag2) {
            worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
            worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata | 8, 4);
        } else if (!flag && flag2) {
            worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata & -9, 4);
        }
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            this.handleWorld2(worldIn, x, y, z);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDispenser();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int determineOrientation = BlockPistonBase.determineOrientation(worldIn, x, y, z, placer);
        worldIn.setBlockMetadataWithNotify(x, y, z, determineOrientation, 2);
        if (itemIn.hasDisplayName()) {
            ((TileEntityDispenser)worldIn.getTileEntity(x, y, z)).setCustomName(itemIn.getDisplayName());
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntityDispenser tileEntity = (TileEntityDispenser)worldIn.getTileEntity(x, y, z);
        if (tileEntity != null) {
            for (int i = 0; i < tileEntity.getSizeInventory(); i++) {
                ItemStack stackInSlot = tileEntity.getStackInSlot(i);
                if (stackInSlot != null) {
                    float f = this.random.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.random.nextFloat() * 0.8F + 0.1F;
                    float f3 = this.random.nextFloat() * 0.8F + 0.1F;

                    while (stackInSlot.stackSize > 0) {
                        int n = this.random.nextInt(21) + 10;
                        if (n > stackInSlot.stackSize) {
                            n = stackInSlot.stackSize;
                        }

                        stackInSlot.stackSize -= n;
                        EntityItem entityItem = new EntityItem(worldIn, x + f, y + f2, z + f3, new ItemStack(stackInSlot.getItem(), n, stackInSlot.getMetadata()));
                        if (stackInSlot.hasTagCompound()) {
                            entityItem.getEntityItem().setTagCompound((NBTTagCompound)stackInSlot.getTagCompound().copy());
                        }

                        float f4 = 0.05F;
                        entityItem.motionX = (float)this.random.nextGaussian() * f4;
                        entityItem.motionY = (float)this.random.nextGaussian() * f4 + 0.2F;
                        entityItem.motionZ = (float)this.random.nextGaussian() * f4;
                        worldIn.spawnEntityInWorld(entityItem);
                    }
                }
            }

            worldIn.updateNeighborsAboutBlockChange(x, y, z, blockBroken);
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    public static IPosition getIPositionFromBlockSource(IBlockSource blockSource) {
        EnumFacing facingDirection = getFacingDirection(blockSource.getBlockMetadata());
        double d = blockSource.getX() + 0.7 * facingDirection.getFrontOffsetX();
        double d2 = blockSource.getY() + 0.7 * facingDirection.getFrontOffsetY();
        double d3 = blockSource.getZ() + 0.7 * facingDirection.getFrontOffsetZ();
        return new PositionImpl(d, d2, d3);
    }

    public static EnumFacing getFacingDirection(int n) {
        return EnumFacing.getFront(n & 7);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        return Container.calcRedstoneFromInventory((IInventory)worldIn.getTileEntity(x, y, z));
    }
}
