package net.minecraft.block;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid {
   public int adjacentSourceBlocks;

   public BlockDynamicLiquid(Material materialIn) {
      super(materialIn);
   }

   public void placeStaticBlock(World worldIn, BlockPos pos, IBlockState currentState) {
      worldIn.setBlockState(pos, getStaticBlock(this.blockMaterial).getDefaultState().withProperty(LEVEL, currentState.getValue(LEVEL)), 2);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      int value = state.getValue(LEVEL);
      byte b = 1;
      if (this.blockMaterial == Material.lava && !worldIn.provider.doesWaterVaporize()) {
         b = 2;
      }

      int tickRate = this.tickRate(worldIn);
      if (value > 0) {
         int n = -100;
         this.adjacentSourceBlocks = 0;

         for (EnumFacing enumFacing : Plane.HORIZONTAL) {
            n = this.checkAdjacentBlock(worldIn, pos.offset(enumFacing), n);
         }

         int n2 = n + b;
         if (n2 >= 8 || n < 0) {
            n2 = -1;
         }

         if (this.getLevel(worldIn, pos.up()) >= 0) {
            int level = this.getLevel(worldIn, pos.up());
            if (level >= 8) {
               n2 = level;
            } else {
               n2 = level + 8;
            }
         }

         if (this.adjacentSourceBlocks >= 2 && this.blockMaterial == Material.water) {
            IBlockState blockState = worldIn.getBlockState(pos.down());
            if (blockState.getBlock().getMaterial().isSolid()) {
               n2 = 0;
            } else if (blockState.getBlock().getMaterial() == this.blockMaterial && blockState.getValue(LEVEL) == 0) {
               n2 = 0;
            }
         }

         if (this.blockMaterial == Material.lava && value < 8 && n2 < 8 && n2 > value && rand.nextInt(4) != 0) {
            tickRate *= 4;
         }

         if (n2 == value) {
            this.placeStaticBlock(worldIn, pos, state);
         } else {
            value = n2;
            if (n2 < 0) {
               worldIn.setBlockToAir(pos);
            } else {
               state = state.withProperty(LEVEL, n2);
               worldIn.setBlockState(pos, state, 2);
               worldIn.scheduleUpdate(pos, this, tickRate);
               worldIn.notifyNeighborsOfStateChange(pos, this);
            }
         }
      } else {
         this.placeStaticBlock(worldIn, pos, state);
      }

      IBlockState blockState = worldIn.getBlockState(pos.down());
      if (this.canFlowInto(worldIn, pos.down(), blockState)) {
         if (this.blockMaterial == Material.lava && worldIn.getBlockState(pos.down()).getBlock().getMaterial() == Material.water) {
            worldIn.setBlockState(pos.down(), Blocks.stone.getDefaultState());
            this.triggerMixEffects(worldIn, pos.down());
            return;
         }

         if (value >= 8) {
            this.tryFlowInto(worldIn, pos.down(), blockState, value);
         } else {
            this.tryFlowInto(worldIn, pos.down(), blockState, value + 8);
         }
      } else if (value >= 0 && (value == 0 || this.isBlocked(worldIn, pos.down(), blockState))) {
         Set possibleFlowDirections = this.getPossibleFlowDirections(worldIn, pos);
         int n = value + b;
         if (value >= 8) {
            n = 1;
         }

         if (n >= 8) {
            return;
         }

         for (EnumFacing enumFacing : (Iterable<EnumFacing>)possibleFlowDirections) {
            this.tryFlowInto(worldIn, pos.offset(enumFacing), worldIn.getBlockState(pos.offset(enumFacing)), n);
         }
      }
   }

   public void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level) {
      if (this.canFlowInto(worldIn, pos, state)) {
         if (state.getBlock() != Blocks.air) {
            if (this.blockMaterial == Material.lava) {
               this.triggerMixEffects(worldIn, pos);
            } else {
               state.getBlock().dropBlockAsItem(worldIn, pos, state, 0);
            }
         }

         worldIn.setBlockState(pos, this.getDefaultState().withProperty(LEVEL, level), 3);
      }
   }

   public int getFlowDistance(World worldIn, BlockPos pos, int distance, EnumFacing calculateFlowCost) {
      int n = 1000;

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         if (enumFacing != calculateFlowCost) {
            BlockPos offset = pos.offset(enumFacing);
            IBlockState blockState = worldIn.getBlockState(offset);
            if (!this.isBlocked(worldIn, offset, blockState) && (blockState.getBlock().getMaterial() != this.blockMaterial || blockState.getValue(LEVEL) > 0)) {
               if (!this.isBlocked(worldIn, offset.down(), blockState)) {
                  return distance;
               }

               if (distance < 4) {
                  int flowDistance = this.getFlowDistance(worldIn, offset, distance + 1, enumFacing.getOpposite());
                  if (flowDistance < n) {
                     n = flowDistance;
                  }
               }
            }
         }
      }

      return n;
   }

   public Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos) {
      int n = 1000;
      EnumSet noneOf = EnumSet.noneOf(EnumFacing.class);

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         BlockPos offset = pos.offset(enumFacing);
         IBlockState blockState = worldIn.getBlockState(offset);
         if (!this.isBlocked(worldIn, offset, blockState) && (blockState.getBlock().getMaterial() != this.blockMaterial || blockState.getValue(LEVEL) > 0)) {
            int n2;
            if (this.isBlocked(worldIn, offset.down(), worldIn.getBlockState(offset.down()))) {
               n2 = this.getFlowDistance(worldIn, offset, 1, enumFacing.getOpposite());
            } else {
               n2 = 0;
            }

            if (n2 < n) {
               noneOf.clear();
            }

            if (n2 <= n) {
               noneOf.add(enumFacing);
               n = n2;
            }
         }
      }

      return noneOf;
   }

   public boolean isBlocked(World worldIn, BlockPos pos, IBlockState state) {
      Block block = worldIn.getBlockState(pos).getBlock();
      return !(block instanceof BlockDoor) && block != Blocks.standing_sign && block != Blocks.ladder && block != Blocks.reeds
         ? (block.blockMaterial == Material.portal ? true : block.blockMaterial.blocksMovement())
         : true;
   }

   public int checkAdjacentBlock(World worldIn, BlockPos pos, int currentMinLevel) {
      int level = this.getLevel(worldIn, pos);
      if (level < 0) {
         return currentMinLevel;
      }

      if (level == 0) {
         this.adjacentSourceBlocks++;
      }

      if (level >= 8) {
         level = 0;
      }

      return currentMinLevel >= 0 && level >= currentMinLevel ? currentMinLevel : level;
   }

   public boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state) {
      Material material = state.getBlock().getMaterial();
      return material != this.blockMaterial && material != Material.lava && !this.isBlocked(worldIn, pos, state);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.checkForMixing(worldIn, pos, state)) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }
   }
}
