package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.World;

public class BlockCommandBlock extends BlockContainer {
    public BlockCommandBlock() {
        super(Material.iron);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCommandBlock();
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!worldIn.isRemote) {
            boolean isBlockIndirectlyGettingPowered = worldIn.isBlockIndirectlyGettingPowered(x, y, z);
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            boolean flag = (blockMetadata & 1) != 0;
            if (isBlockIndirectlyGettingPowered && !flag) {
                worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata | 1, 4);
                worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
            } else if (!isBlockIndirectlyGettingPowered && flag) {
                worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata & -2, 4);
            }
        }
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof TileEntityCommandBlock) {
            CommandBlockLogic commandExecutor = ((TileEntityCommandBlock)tileEntity).getCommandExecutor();
            commandExecutor.execute(worldIn);
            worldIn.updateNeighborsAboutBlockChange(x, y, z, this);
        }
    }

    @Override
    public int tickRate(World worldIn) {
        return 1;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        TileEntityCommandBlock tileEntity = (TileEntityCommandBlock)worldIn.getTileEntity(x, y, z);
        if (tileEntity != null) {
            player.displayGUIEditSign(tileEntity);
        }

        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        return tileEntity != null && tileEntity instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)tileEntity).getCommandExecutor().getSuccessCount() : 0;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        TileEntityCommandBlock tileEntity = (TileEntityCommandBlock)worldIn.getTileEntity(x, y, z);
        if (itemIn.hasDisplayName()) {
            tileEntity.getCommandExecutor().setName(itemIn.getDisplayName());
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }
}
