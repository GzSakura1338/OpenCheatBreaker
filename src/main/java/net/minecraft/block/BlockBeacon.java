package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class BlockBeacon extends BlockContainer {
   public BlockBeacon() {
      super(Material.glass, MapColor.diamondColor);
      this.setHardness(3.0F);
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityBeacon();
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityBeacon) {
         playerIn.displayGUIChest((TileEntityBeacon)tileEntity);
         playerIn.triggerAchievement(StatList.INTERACTIONS_WITH_BEACON);
      }

      return true;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      if (stack.hasDisplayName()) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof TileEntityBeacon) {
            ((TileEntityBeacon)tileEntity).setName(stack.getDisplayName());
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityBeacon) {
         ((TileEntityBeacon)tileEntity).updateBeacon();
         worldIn.addBlockEvent(pos, this, 1, 0);
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public static void updateColorAsync(final World worldIn, final BlockPos glassPos) {
      HttpUtil.downloadExecutor.submit(new Runnable() {
         @Override
         public void run() {
            Chunk chunkFromBlockCoords = worldIn.getChunkFromBlockCoords(glassPos);

            for (int i = glassPos.getY() - 1; i >= 0; i--) {
               final BlockPos blockPos = new BlockPos(glassPos.getX(), i, glassPos.getZ());
               if (!chunkFromBlockCoords.canSeeSky(blockPos)) {
                  break;
               }

               IBlockState blockState = worldIn.getBlockState(blockPos);
               if (blockState.getBlock() == Blocks.beacon) {
                  ((WorldServer)worldIn).addScheduledTask(new Runnable() {
                     @Override
                     public void run() {
                        TileEntity tileEntity = worldIn.getTileEntity(blockPos);
                        if (tileEntity instanceof TileEntityBeacon) {
                           ((TileEntityBeacon)tileEntity).updateBeacon();
                           worldIn.addBlockEvent(blockPos, Blocks.beacon, 1, 0);
                        }
                     }
                  });
               }
            }
         }
      });
   }
}
