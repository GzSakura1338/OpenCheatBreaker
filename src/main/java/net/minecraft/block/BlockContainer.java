package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BlockContainer extends Block implements ITileEntityProvider {
   public BlockContainer(Material materialIn) {
      this(materialIn, materialIn.getMaterialMapColor());
   }

   public BlockContainer(Material material, MapColor mapColor) {
      super(material, mapColor);
      this.isBlockContainer = true;
   }

   public boolean isInvalidNeighbor(World world, BlockPos blockPos, EnumFacing enumFacing) {
      return world.getBlockState(blockPos.offset(enumFacing)).getBlock().getMaterial() == Material.cactus;
   }

   public boolean hasInvalidNeighbor(World world, BlockPos blockPos) {
      return this.isInvalidNeighbor(world, blockPos, EnumFacing.NORTH)
         || this.isInvalidNeighbor(world, blockPos, EnumFacing.SOUTH)
         || this.isInvalidNeighbor(world, blockPos, EnumFacing.WEST)
         || this.isInvalidNeighbor(world, blockPos, EnumFacing.EAST);
   }

   @Override
   public int getRenderType() {
      return -1;
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      worldIn.removeTileEntity(pos);
   }

   @Override
   public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
      super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity == null ? false : tileEntity.receiveClientEvent(eventID, eventParam);
   }
}
