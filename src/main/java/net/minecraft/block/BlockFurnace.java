package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockFurnace extends BlockContainer {
    public Random random = new Random();
    public boolean isBurning;
    public static boolean keepInventory;
    public IIcon iconTop;
    public IIcon iconFront;

    public BlockFurnace(boolean flag) {
        super(Material.rock);
        this.isBurning = flag;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(Blocks.furnace);
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
        return side == 1 ? this.iconTop : (side == 0 ? this.iconTop : (side != meta ? this.blockIcon : this.iconFront));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("furnace_side");
        this.iconFront = reg.registerIcon(this.isBurning ? "furnace_front_on" : "furnace_front_off");
        this.iconTop = reg.registerIcon("furnace_top");
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        if (this.isBurning) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            float f = x + 0.5F;
            float f2 = y + 0.0F + random.nextFloat() * 6.0F / 16.0F;
            float f3 = z + 0.5F;
            float f4 = 0.52F;
            float f5 = random.nextFloat() * 0.6F - 0.3F;
            if (blockMetadata == 4) {
                worldIn.spawnParticle("smoke", f - f4, f2, f3 + f5, 0.0, 0.0, 0.0);
                worldIn.spawnParticle("flame", f - f4, f2, f3 + f5, 0.0, 0.0, 0.0);
            } else if (blockMetadata == 5) {
                worldIn.spawnParticle("smoke", f + f4, f2, f3 + f5, 0.0, 0.0, 0.0);
                worldIn.spawnParticle("flame", f + f4, f2, f3 + f5, 0.0, 0.0, 0.0);
            } else if (blockMetadata == 2) {
                worldIn.spawnParticle("smoke", f + f5, f2, f3 - f4, 0.0, 0.0, 0.0);
                worldIn.spawnParticle("flame", f + f5, f2, f3 - f4, 0.0, 0.0, 0.0);
            } else if (blockMetadata == 3) {
                worldIn.spawnParticle("smoke", f + f5, f2, f3 + f4, 0.0, 0.0, 0.0);
                worldIn.spawnParticle("flame", f + f5, f2, f3 + f4, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        if (worldIn.isRemote) {
            return true;
        }

        TileEntityFurnace tileEntity = (TileEntityFurnace)worldIn.getTileEntity(x, y, z);
        if (tileEntity != null) {
            player.handleTileEntityFurnace(tileEntity);
        }

        return true;
    }

    public static void updateFurnaceBlockState(boolean flag, World world, int n, int n2, int n3) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        TileEntity tileEntity = world.getTileEntity(n, n2, n3);
        keepInventory = true;
        if (flag) {
            world.setBlock(n, n2, n3, Blocks.lit_furnace);
        } else {
            world.setBlock(n, n2, n3, Blocks.furnace);
        }

        keepInventory = false;
        world.setBlockMetadataWithNotify(n, n2, n3, blockMetadata, 2);
        if (tileEntity != null) {
            tileEntity.validate();
            world.setTileEntity(n, n2, n3, tileEntity);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityFurnace();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int n = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 3;
        if (n == 0) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }

        if (n == 1) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 5, 2);
        }

        if (n == 2) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }

        if (n == 3) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 4, 2);
        }

        if (itemIn.hasDisplayName()) {
            ((TileEntityFurnace)worldIn.getTileEntity(x, y, z)).setCustomInventoryName(itemIn.getDisplayName());
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        if (!keepInventory) {
            TileEntityFurnace tileEntity = (TileEntityFurnace)worldIn.getTileEntity(x, y, z);
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
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
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
    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemFromBlock(Blocks.furnace);
    }
}
