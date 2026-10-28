package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockHopper extends BlockContainer {
    public Random random = new Random();
    public IIcon hopperOutsideIcon;
    public IIcon hopperTopIcon;
    public IIcon hopperInsideIcon;

    public BlockHopper() {
        super(Material.iron);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        int n = Facing.oppositeSide[side];
        if (n == 1) {
            n = 0;
        }

        return n;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityHopper();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        super.onBlockPlacedBy(worldIn, x, y, z, placer, itemIn);
        if (itemIn.hasDisplayName()) {
            TileEntityHopper tileEntity = getTileEntity(worldIn, x, y, z);
            tileEntity.setCustomName(itemIn.getDisplayName());
        }
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        super.onBlockAdded(worldIn, x, y, z);
        this.updateBlockData(worldIn, x, y, z);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        if (worldIn.isRemote) {
            return true;
        }

        TileEntityHopper tileEntity = getTileEntity(worldIn, x, y, z);
        if (tileEntity != null) {
            player.handleTileEntityHopper(tileEntity);
        }

        return true;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        this.updateBlockData(worldIn, x, y, z);
    }

    public void updateBlockData(World world, int n, int n2, int n3) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        int directionFromMetadata = getDirectionFromMetadata(blockMetadata);
        boolean flag = !world.isBlockIndirectlyGettingPowered(n, n2, n3);
        boolean activeStateFromMetadata = getActiveStateFromMetadata(blockMetadata);
        if (flag != activeStateFromMetadata) {
            world.setBlockMetadataWithNotify(n, n2, n3, directionFromMetadata | (flag ? 0 : 8), 4);
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntityHopper tileEntity = (TileEntityHopper)worldIn.getTileEntity(x, y, z);
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

    @Override
    public int getRenderType() {
        return 38;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.hopperTopIcon : this.hopperOutsideIcon;
    }

    public static int getDirectionFromMetadata(int n) {
        return n & 7;
    }

    public static boolean getActiveStateFromMetadata(int n) {
        return (n & 8) != 8;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        return Container.calcRedstoneFromInventory(getTileEntity(worldIn, x, y, z));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.hopperOutsideIcon = reg.registerIcon("hopper_outside");
        this.hopperTopIcon = reg.registerIcon("hopper_top");
        this.hopperInsideIcon = reg.registerIcon("hopper_inside");
    }

    public static IIcon getHopperIcon(String str) {
        return str.equals("hopper_outside") ? Blocks.hopper.hopperOutsideIcon : (str.equals("hopper_inside") ? Blocks.hopper.hopperInsideIcon : null);
    }

    @Override
    public String getItemIconName() {
        return "hopper";
    }

    public static TileEntityHopper getTileEntity(IBlockAccess blockAccess, int n, int n2, int n3) {
        return (TileEntityHopper)blockAccess.getTileEntity(n, n2, n3);
    }
}
