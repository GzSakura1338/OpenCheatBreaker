package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {
    public BlockRedstoneComparator(boolean flag) {
        super(flag);
        this.isBlockContainer = true;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.comparator;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.comparator;
    }

    @Override
    public int computeInt(int n) {
        return 2;
    }

    @Override
    public BlockRedstoneDiode getBlockPowered() {
        return Blocks.powered_comparator;
    }

    @Override
    public BlockRedstoneDiode getBlockUnpowered() {
        return Blocks.unpowered_comparator;
    }

    @Override
    public int getRenderType() {
        return 37;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        boolean flag = this.isRepeaterPowered || (meta & 8) != 0;
        return side == 0
            ? (flag ? Blocks.redstone_torch.getBlockTextureFromSide(side) : Blocks.unlit_redstone_torch.getBlockTextureFromSide(side))
            : (side == 1 ? (flag ? Blocks.powered_comparator.blockIcon : this.blockIcon) : Blocks.double_stone_slab.getBlockTextureFromSide(1));
    }

    @Override
    public boolean check(int n) {
        return this.isRepeaterPowered || (n & 8) != 0;
    }

    @Override
    public int getInputLevel(IBlockAccess blockAccess, int n, int n2, int n3, int n4) {
        return this.getTileEntityComparator(blockAccess, n, n2, n3).getOutputSignal();
    }

    public int getOutputStrength(World world, int n, int n2, int n3, int n4) {
        return !this.check2(n4)
            ? this.getInputStrength(world, n, n2, n3, n4)
            : Math.max(this.getInputStrength(world, n, n2, n3, n4) - this.computeInt(world, n, n2, n3, n4), 0);
    }

    public boolean check2(int n) {
        return (n & 4) == 4;
    }

    @Override
    public boolean isGettingInput(World world, int n, int n2, int n3, int n4) {
        int inputStrength = this.getInputStrength(world, n, n2, n3, n4);
        if (inputStrength >= 15) {
            return true;
        }

        if (inputStrength == 0) {
            return false;
        }

        int computeInt = this.computeInt(world, n, n2, n3, n4);
        return computeInt == 0 || inputStrength >= computeInt;
    }

    @Override
    public int getInputStrength(World world, int n, int n2, int n3, int n4) {
        int inputStrength = super.getInputStrength(world, n, n2, n3, n4);
        int direction = getDirection(n4);
        int n5 = n + Direction.offsetX[direction];
        int n6 = n3 + Direction.offsetZ[direction];
        Block block = world.getBlock(n5, n2, n6);
        if (block.hasComparatorInputOverride()) {
            inputStrength = block.getComparatorInputOverride(world, n5, n2, n6, Direction.rotateOpposite[direction]);
        } else if (inputStrength < 15 && block.isNormalCube()) {
            n5 += Direction.offsetX[direction];
            n6 += Direction.offsetZ[direction];
            block = world.getBlock(n5, n2, n6);
            if (block.hasComparatorInputOverride()) {
                inputStrength = block.getComparatorInputOverride(world, n5, n2, n6, Direction.rotateOpposite[direction]);
            }
        }

        return inputStrength;
    }

    public TileEntityComparator getTileEntityComparator(IBlockAccess blockAccess, int n, int n2, int n3) {
        return (TileEntityComparator)blockAccess.getTileEntity(n, n2, n3);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        boolean flag = this.isRepeaterPowered | (blockMetadata & 8) != 0;
        boolean flag2 = !this.check2(blockMetadata);
        int n = flag2 ? 4 : 0;
        n |= flag ? 8 : 0;
        worldIn.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3F, flag2 ? 0.55F : 0.5F);
        worldIn.setBlockMetadataWithNotify(x, y, z, n | blockMetadata & 3, 2);
        this.handleWorld(worldIn, x, y, z, worldIn.rand);
        return true;
    }

    @Override
    public void updatePoweredBlock(World world, int n, int n2, int n3, Block block) {
        if (!world.isBlockTickScheduledThisTick(n, n2, n3, this)) {
            int blockMetadata = world.getBlockMetadata(n, n2, n3);
            int outputStrength = this.getOutputStrength(world, n, n2, n3, blockMetadata);
            int outputSignal = this.getTileEntityComparator(world, n, n2, n3).getOutputSignal();
            if (outputStrength != outputSignal || this.check(blockMetadata) != this.isGettingInput(world, n, n2, n3, blockMetadata)) {
                if (this.isUnableToPowerBlockAtOutputFace(world, n, n2, n3, blockMetadata)) {
                    world.scheduleBlockUpdateWithPriority(n, n2, n3, this, this.computeInt(0), -1);
                } else {
                    world.scheduleBlockUpdateWithPriority(n, n2, n3, this, this.computeInt(0), 0);
                }
            }
        }
    }

    public void handleWorld(World world, int n, int n2, int n3, Random random) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        int outputStrength = this.getOutputStrength(world, n, n2, n3, blockMetadata);
        int outputSignal = this.getTileEntityComparator(world, n, n2, n3).getOutputSignal();
        this.getTileEntityComparator(world, n, n2, n3).setOutputSignal(outputStrength);
        if (outputSignal != outputStrength || !this.check2(blockMetadata)) {
            boolean isGettingInput = this.isGettingInput(world, n, n2, n3, blockMetadata);
            boolean flag = this.isRepeaterPowered || (blockMetadata & 8) != 0;
            if (flag && !isGettingInput) {
                world.setBlockMetadataWithNotify(n, n2, n3, blockMetadata & -9, 2);
            } else if (!flag && isGettingInput) {
                world.setBlockMetadataWithNotify(n, n2, n3, blockMetadata | 8, 2);
            }

            this.handleWorld(world, n, n2, n3);
        }
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (this.isRepeaterPowered) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            worldIn.setBlock(x, y, z, this.getBlockUnpowered(), blockMetadata | 8, 4);
        }

        this.handleWorld(worldIn, x, y, z, random);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        super.onBlockAdded(worldIn, x, y, z);
        worldIn.setTileEntity(x, y, z, this.createNewTileEntity(worldIn, 0));
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        worldIn.removeTileEntity(x, y, z);
        this.handleWorld(worldIn, x, y, z);
    }

    @Override
    public boolean onBlockEventReceived(World worldIn, int x, int y, int z, int eventId, int eventData) {
        super.onBlockEventReceived(worldIn, x, y, z, eventId, eventData);
        TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        return tileEntity != null && tileEntity.receiveClientEvent(eventId, eventData);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityComparator();
    }
}
