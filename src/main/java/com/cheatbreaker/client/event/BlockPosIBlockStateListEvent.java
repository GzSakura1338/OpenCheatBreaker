package com.cheatbreaker.client.event;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class BlockPosIBlockStateListEvent extends CancellableEvent {
   public List<AxisAlignedBB> list;
   public BlockPos blockPos;
   public IBlockState blockState;

   public IBlockState getBlockState() {
      return this.blockState;
   }

   public BlockPosIBlockStateListEvent(List<AxisAlignedBB> list, BlockPos blockPos, IBlockState blockState) {
      this.list = list;
      this.blockPos = blockPos;
      this.blockState = blockState;
   }

   public List<AxisAlignedBB> getList() {
      return this.list;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }
}
