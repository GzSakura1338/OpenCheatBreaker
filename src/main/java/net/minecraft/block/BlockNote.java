package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockNote extends BlockContainer {
   public static List<String> INSTRUMENTS = Lists.newArrayList(new String[]{"harp", "bd", "snare", "hat", "bassattack"});

   public BlockNote() {
      super(Material.wood);
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      boolean isBlockPowered = worldIn.isBlockPowered(pos);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityNote) {
         TileEntityNote tileEntityNote = (TileEntityNote)tileEntity;
         if (tileEntityNote.previousRedstoneState != isBlockPowered) {
            if (isBlockPowered) {
               tileEntityNote.triggerNote(worldIn, pos);
            }

            tileEntityNote.previousRedstoneState = isBlockPowered;
         }
      }
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityNote) {
         TileEntityNote tileEntityNote = (TileEntityNote)tileEntity;
         tileEntityNote.changePitch();
         tileEntityNote.triggerNote(worldIn, pos);
         playerIn.triggerAchievement(StatList.NOTEBLOCK_TUNED);
      }

      return true;
   }

   @Override
   public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
      if (!worldIn.isRemote) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof TileEntityNote) {
            ((TileEntityNote)tileEntity).triggerNote(worldIn, pos);
            playerIn.triggerAchievement(StatList.NOTEBLOCK_PLAYED);
         }
      }
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityNote();
   }

   public String getInstrument(int id) {
      if (id < 0 || id >= INSTRUMENTS.size()) {
         id = 0;
      }

      return INSTRUMENTS.get(id);
   }

   @Override
   public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
      float pow = (float)Math.pow(2.0, (eventParam - 12) / 12.0);
      worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "note." + this.getInstrument(eventID), 3.0F, pow);
      worldIn.spawnParticle(EnumParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, eventParam / 24.0, 0.0, 0.0);
      return true;
   }

   @Override
   public int getRenderType() {
      return 3;
   }
}
