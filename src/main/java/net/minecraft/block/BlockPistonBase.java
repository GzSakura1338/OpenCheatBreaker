package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block {
    public boolean isSticky;
    public IIcon innerTopIcon;
    public IIcon bottomIcon;
    public IIcon topIcon;

    public BlockPistonBase(boolean flag) {
        super(Material.piston);
        this.isSticky = flag;
        this.setStepSound(soundTypePiston);
        this.setHardness(0.5F);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    public IIcon getPistonExtensionTexture() {
        return this.topIcon;
    }

    public void process(float f, float f2, float f3, float f4, float f5, float f6) {
        this.setBlockBounds(f, f2, f3, f4, f5, f6);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        int pistonOrientation = getPistonOrientation(meta);
        return pistonOrientation > 5
            ? this.topIcon
            : (
                side == pistonOrientation
                    ? (
                        !isExtended(meta) && this.minX <= 0.0 && this.minY <= 0.0 && this.minZ <= 0.0 && this.maxX >= 1.0 && this.maxY >= 1.0 && this.maxZ >= 1.0
                            ? this.topIcon
                            : this.innerTopIcon
                    )
                    : (side == Facing.oppositeSide[pistonOrientation] ? this.bottomIcon : this.blockIcon)
            );
    }

    public static IIcon getPistonBaseIcon(String str) {
        return str == "piston_side"
            ? Blocks.piston.blockIcon
            : (
                str == "piston_top_normal"
                    ? Blocks.piston.topIcon
                    : (str == "piston_top_sticky" ? Blocks.sticky_piston.topIcon : (str == "piston_inner" ? Blocks.piston.innerTopIcon : null))
            );
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("piston_side");
        this.topIcon = reg.registerIcon(this.isSticky ? "piston_top_sticky" : "piston_top_normal");
        this.innerTopIcon = reg.registerIcon("piston_inner");
        this.bottomIcon = reg.registerIcon("piston_bottom");
    }

    @Override
    public int getRenderType() {
        return 16;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int determineOrientation2 = determineOrientation(worldIn, x, y, z, placer);
        worldIn.setBlockMetadataWithNotify(x, y, z, determineOrientation2, 2);
        if (!worldIn.isRemote) {
            this.updatePistonState(worldIn, x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!worldIn.isRemote) {
            this.updatePistonState(worldIn, x, y, z);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        if (!worldIn.isRemote && worldIn.getTileEntity(x, y, z) == null) {
            this.updatePistonState(worldIn, x, y, z);
        }
    }

    public void updatePistonState(World world, int n, int n2, int n3) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        int pistonOrientation = getPistonOrientation(blockMetadata);
        if (pistonOrientation != 7) {
            boolean isIndirectlyPowered = this.isIndirectlyPowered(world, n, n2, n3, pistonOrientation);
            if (isIndirectlyPowered && !isExtended(blockMetadata)) {
                if (canExtend(world, n, n2, n3, pistonOrientation)) {
                    world.addBlockEvent(n, n2, n3, this, 0, pistonOrientation);
                }
            } else if (!isIndirectlyPowered && isExtended(blockMetadata)) {
                world.setBlockMetadataWithNotify(n, n2, n3, pistonOrientation, 2);
                world.addBlockEvent(n, n2, n3, this, 1, pistonOrientation);
            }
        }
    }

    public boolean isIndirectlyPowered(World world, int n, int n2, int n3, int n4) {
        return n4 != 0 && world.getIndirectPowerOutput(n, n2 - 1, n3, 0)
            || n4 != 1 && world.getIndirectPowerOutput(n, n2 + 1, n3, 1)
            || n4 != 2 && world.getIndirectPowerOutput(n, n2, n3 - 1, 2)
            || n4 != 3 && world.getIndirectPowerOutput(n, n2, n3 + 1, 3)
            || n4 != 5 && world.getIndirectPowerOutput(n + 1, n2, n3, 5)
            || n4 != 4 && world.getIndirectPowerOutput(n - 1, n2, n3, 4)
            || world.getIndirectPowerOutput(n, n2, n3, 0)
            || world.getIndirectPowerOutput(n, n2 + 2, n3, 1)
            || world.getIndirectPowerOutput(n, n2 + 1, n3 - 1, 2)
            || world.getIndirectPowerOutput(n, n2 + 1, n3 + 1, 3)
            || world.getIndirectPowerOutput(n - 1, n2 + 1, n3, 4)
            || world.getIndirectPowerOutput(n + 1, n2 + 1, n3, 5);
    }

    @Override
    public boolean onBlockEventReceived(World worldIn, int x, int y, int z, int eventId, int eventData) {
        if (!worldIn.isRemote) {
            boolean isIndirectlyPowered = this.isIndirectlyPowered(worldIn, x, y, z, eventData);
            if (isIndirectlyPowered && eventId == 1) {
                worldIn.setBlockMetadataWithNotify(x, y, z, eventData | 8, 2);
                return false;
            }

            if (!isIndirectlyPowered && eventId == 0) {
                return false;
            }
        }

        if (eventId == 0) {
            if (!this.tryExtend(worldIn, x, y, z, eventData)) {
                return false;
            }

            worldIn.setBlockMetadataWithNotify(x, y, z, eventData | 8, 2);
            worldIn.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "tile.piston.out", 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
        } else if (eventId == 1) {
            TileEntity tileEntity = worldIn.getTileEntity(
                x + Facing.offsetsXForSide[eventData], y + Facing.offsetsYForSide[eventData], z + Facing.offsetsZForSide[eventData]
            );
            if (tileEntity instanceof TileEntityPiston) {
                ((TileEntityPiston)tileEntity).clearPistonTileEntity();
            }

            worldIn.setBlock(x, y, z, Blocks.piston_extension, eventData, 3);
            worldIn.setTileEntity(x, y, z, BlockPistonMoving.getTileEntity(this, eventData, eventData, false, true));
            if (this.isSticky) {
                int n = x + Facing.offsetsXForSide[eventData] * 2;
                int n2 = y + Facing.offsetsYForSide[eventData] * 2;
                int n3 = z + Facing.offsetsZForSide[eventData] * 2;
                Block block = worldIn.getBlock(n, n2, n3);
                int blockMetadata = worldIn.getBlockMetadata(n, n2, n3);
                boolean flag = false;
                if (block == Blocks.piston_extension) {
                    TileEntity tileEntity2 = worldIn.getTileEntity(n, n2, n3);
                    if (tileEntity2 instanceof TileEntityPiston) {
                        TileEntityPiston tileEntityPiston = (TileEntityPiston)tileEntity2;
                        if (tileEntityPiston.getPistonOrientation() == eventData && tileEntityPiston.isExtending()) {
                            tileEntityPiston.clearPistonTileEntity();
                            block = tileEntityPiston.getStoredBlockID();
                            blockMetadata = tileEntityPiston.getBlockMetadata();
                            flag = true;
                        }
                    }
                }

                if (flag
                    || block.getMaterial() == Material.air
                    || !canPushBlock(block, worldIn, n, n2, n3, false)
                    || block.getMobilityFlag() != 0 && block != Blocks.piston && block != Blocks.sticky_piston) {
                    if (!flag) {
                        worldIn.setBlockToAir(x + Facing.offsetsXForSide[eventData], y + Facing.offsetsYForSide[eventData], z + Facing.offsetsZForSide[eventData]);
                    }
                } else {
                    x += Facing.offsetsXForSide[eventData];
                    y += Facing.offsetsYForSide[eventData];
                    z += Facing.offsetsZForSide[eventData];
                    worldIn.setBlock(x, y, z, Blocks.piston_extension, blockMetadata, 3);
                    worldIn.setTileEntity(x, y, z, BlockPistonMoving.getTileEntity(block, blockMetadata, eventData, false, false));
                    worldIn.setBlockToAir(n, n2, n3);
                }
            } else {
                worldIn.setBlockToAir(x + Facing.offsetsXForSide[eventData], y + Facing.offsetsYForSide[eventData], z + Facing.offsetsZForSide[eventData]);
            }

            worldIn.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "tile.piston.in", 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (isExtended(blockMetadata)) {
            float f = 0.25F;
            switch (getPistonOrientation(blockMetadata)) {
                case 0:
                    this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case 1:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                    break;
                case 2:
                    this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                    break;
                case 3:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                    break;
                case 4:
                    this.setBlockBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case 5:
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            }
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    public static int getPistonOrientation(int n) {
        return n & 7;
    }

    public static boolean isExtended(int n) {
        return (n & 8) != 0;
    }

    public static int determineOrientation(World world, int n, int n2, int n3, EntityLivingBase entityLivingBase) {
        if (MathHelper.abs((float)entityLivingBase.posX - n) < 2.0F && MathHelper.abs((float)entityLivingBase.posZ - n3) < 2.0F) {
            double d = entityLivingBase.posY + 1.82 - entityLivingBase.yOffset;
            if (d - n2 > 2.0) {
                return 1;
            }

            if (n2 - d > 0.0) {
                return 0;
            }
        }

        int n4 = MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5) & 3;
        return n4 == 0 ? 2 : (n4 == 1 ? 5 : (n4 == 2 ? 3 : (n4 == 3 ? 4 : 0)));
    }

    public static boolean canPushBlock(Block block, World world, int n, int n2, int n3, boolean flag) {
        if (block == Blocks.obsidian) {
            return false;
        }

        if (block != Blocks.piston && block != Blocks.sticky_piston) {
            if (block.getBlockHardness(world, n, n2, n3) == -1.0F) {
                return false;
            }

            if (block.getMobilityFlag() == 2) {
                return false;
            }

            if (block.getMobilityFlag() == 1) {
                return flag;
            }
        } else if (isExtended(world.getBlockMetadata(n, n2, n3))) {
            return false;
        }

        return !(block instanceof ITileEntityProvider);
    }

    public static boolean canExtend(World world, int n, int n2, int n3, int n4) {
        int n5 = n + Facing.offsetsXForSide[n4];
        int n6 = n2 + Facing.offsetsYForSide[n4];
        int n7 = n3 + Facing.offsetsZForSide[n4];

        for (int i = 0; i < 13; i++) {
            if (n6 <= 0 || n6 >= 255) {
                return false;
            }

            Block block = world.getBlock(n5, n6, n7);
            if (block.getMaterial() != Material.air) {
                if (!canPushBlock(block, world, n5, n6, n7, true)) {
                    return false;
                }

                if (block.getMobilityFlag() != 1) {
                    if (i == 12) {
                        return false;
                    }

                    n5 += Facing.offsetsXForSide[n4];
                    n6 += Facing.offsetsYForSide[n4];
                    n7 += Facing.offsetsZForSide[n4];
                    continue;
                }
            }
            break;
        }

        return true;
    }

    public boolean tryExtend(World world, int n, int n2, int n3, int n4) {
        int n5 = n + Facing.offsetsXForSide[n4];
        int n6 = n2 + Facing.offsetsYForSide[n4];
        int n7 = n3 + Facing.offsetsZForSide[n4];
        int n8 = 0;

        while (n8 < 13) {
            if (n6 <= 0 || n6 >= 255) {
                return false;
            }

            Block block = world.getBlock(n5, n6, n7);
            if (block.getMaterial() == Material.air) {
                break;
            }

            if (!canPushBlock(block, world, n5, n6, n7, true)) {
                return false;
            }

            if (block.getMobilityFlag() != 1) {
                if (n8 == 12) {
                    return false;
                }

                n5 += Facing.offsetsXForSide[n4];
                n6 += Facing.offsetsYForSide[n4];
                n7 += Facing.offsetsZForSide[n4];
                n8++;
            } else {
                block.dropBlockAsItem(world, n5, n6, n7, world.getBlockMetadata(n5, n6, n7), 0);
                world.setBlockToAir(n5, n6, n7);
                break;
            }
        }

        n8 = n5;
        int n9 = n6;
        int n10 = n7;
        int n11 = 0;
        int n12 = 0;
        Block[] blocks = new Block[13];

        while (n5 != n || n6 != n2 || n7 != n3) {
            int n13 = n5 - Facing.offsetsXForSide[n4];
            int n14 = n6 - Facing.offsetsYForSide[n4];
            n12 = n7 - Facing.offsetsZForSide[n4];
            Block block = world.getBlock(n13, n14, n12);
            int blockMetadata = world.getBlockMetadata(n13, n14, n12);
            if (block == this && n13 == n && n14 == n2 && n12 == n3) {
                world.setBlock(n5, n6, n7, Blocks.piston_extension, n4 | (this.isSticky ? 8 : 0), 4);
                world.setTileEntity(n5, n6, n7, BlockPistonMoving.getTileEntity(Blocks.piston_head, n4 | (this.isSticky ? 8 : 0), n4, true, false));
            } else {
                world.setBlock(n5, n6, n7, Blocks.piston_extension, blockMetadata, 4);
                world.setTileEntity(n5, n6, n7, BlockPistonMoving.getTileEntity(block, blockMetadata, n4, true, false));
            }

            blocks[n11++] = block;
            n5 = n13;
            n6 = n14;
            n7 = n12;
        }

        n5 = n8;
        n6 = n9;
        n7 = n10;
        n11 = 0;

        while (n5 != n || n6 != n2 || n7 != n3) {
            int n13 = n5 - Facing.offsetsXForSide[n4];
            int n14 = n6 - Facing.offsetsYForSide[n4];
            n12 = n7 - Facing.offsetsZForSide[n4];
            world.notifyBlocksOfNeighborChange(n13, n14, n12, blocks[n11++]);
            n5 = n13;
            n6 = n14;
            n7 = n12;
        }

        return true;
    }
}
