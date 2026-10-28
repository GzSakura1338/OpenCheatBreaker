package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;

public class BlockNote extends BlockContainer {
    public BlockNote() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        boolean isBlockIndirectlyGettingPowered = worldIn.isBlockIndirectlyGettingPowered(x, y, z);
        TileEntityNote tileEntity = (TileEntityNote)worldIn.getTileEntity(x, y, z);
        if (tileEntity != null && tileEntity.previousRedstoneState != isBlockIndirectlyGettingPowered) {
            if (isBlockIndirectlyGettingPowered) {
                tileEntity.triggerNote(worldIn, x, y, z);
            }

            tileEntity.previousRedstoneState = isBlockIndirectlyGettingPowered;
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (worldIn.isRemote) {
            return true;
        }

        TileEntityNote tileEntity = (TileEntityNote)worldIn.getTileEntity(x, y, z);
        if (tileEntity != null) {
            tileEntity.changePitch();
            tileEntity.triggerNote(worldIn, x, y, z);
        }

        return true;
    }

    @Override
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {
        if (!worldIn.isRemote) {
            TileEntityNote tileEntity = (TileEntityNote)worldIn.getTileEntity(x, y, z);
            if (tileEntity != null) {
                tileEntity.triggerNote(worldIn, x, y, z);
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityNote();
    }

    @Override
    public boolean onBlockEventReceived(World worldIn, int x, int y, int z, int eventId, int eventData) {
        float pow = (float)Math.pow(2.0, (eventData - 12) / 12.0);
        String str = "harp";
        if (eventId == 1) {
            str = "bd";
        }

        if (eventId == 2) {
            str = "snare";
        }

        if (eventId == 3) {
            str = "hat";
        }

        if (eventId == 4) {
            str = "bassattack";
        }

        worldIn.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "note." + str, 3.0F, pow);
        worldIn.spawnParticle("note", x + 0.5, y + 1.2, z + 0.5, eventData / 24.0, 0.0, 0.0);
        return true;
    }
}
