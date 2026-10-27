package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockStaticLiquid extends BlockLiquid {
   public BlockStaticLiquid(Material materialIn) {
      super(materialIn);
      this.setTickRandomly(false);
      if (materialIn == Material.lava) {
         this.setTickRandomly(true);
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!this.checkForMixing(worldIn, pos, state)) {
         this.updateLiquid(worldIn, pos, state);
      }
   }

   public void updateLiquid(World worldIn, BlockPos pos, IBlockState state) {
      BlockDynamicLiquid flowingBlock = getFlowingBlock(this.blockMaterial);
      worldIn.setBlockState(pos, flowingBlock.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
      worldIn.scheduleUpdate(pos, flowingBlock, this.tickRate(worldIn));
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (this.blockMaterial == Material.lava && worldIn.getGameRules().getBoolean("doFireTick")) {
         int nextInt = rand.nextInt(3);
         if (nextInt > 0) {
            BlockPos pos2 = pos;

            for (int i = 0; i < nextInt; i++) {
               pos2 = pos2.add(rand.nextInt(3) - 1, 1, rand.nextInt(3) - 1);
               Block block = worldIn.getBlockState(pos2).getBlock();
               if (block.blockMaterial == Material.air) {
                  if (this.isSurroundingBlockFlammable(worldIn, pos2)) {
                     worldIn.setBlockState(pos2, Blocks.fire.getDefaultState());
                     return;
                  }
               } else if (block.blockMaterial.blocksMovement()) {
                  return;
               }
            }
         } else {
            for (int i = 0; i < 3; i++) {
               BlockPos blockPos = pos.add(rand.nextInt(3) - 1, 0, rand.nextInt(3) - 1);
               if (worldIn.isAirBlock(blockPos.up()) && this.getCanBlockBurn(worldIn, blockPos)) {
                  worldIn.setBlockState(blockPos.up(), Blocks.fire.getDefaultState());
               }
            }
         }
      }
   }

   public boolean isSurroundingBlockFlammable(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (this.getCanBlockBurn(worldIn, pos.offset(enumFacing))) {
            return true;
         }
      }

      return false;
   }

   public boolean getCanBlockBurn(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getBlock().getMaterial().getCanBurn();
   }
}
