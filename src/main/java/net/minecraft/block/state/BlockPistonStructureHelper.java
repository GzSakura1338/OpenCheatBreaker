package net.minecraft.block.state;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockPistonStructureHelper {
   public List<BlockPos> toMove = Lists.newArrayList();
   public List<BlockPos> toDestroy = Lists.newArrayList();
   public World world;
   public BlockPos pistonPos;
   public EnumFacing moveDirection;
   public BlockPos blockToMove;

   public BlockPistonStructureHelper(World worldIn, BlockPos posIn, EnumFacing pistonFacing, boolean extending) {
      this.world = worldIn;
      this.pistonPos = posIn;
      if (extending) {
         this.moveDirection = pistonFacing;
         this.blockToMove = posIn.offset(pistonFacing);
      } else {
         this.moveDirection = pistonFacing.getOpposite();
         this.blockToMove = posIn.offset(pistonFacing, 2);
      }
   }

   public boolean canMove() {
      this.toMove.clear();
      this.toDestroy.clear();
      Block block = this.world.getBlockState(this.blockToMove).getBlock();
      if (!BlockPistonBase.canPush(block, this.world, this.blockToMove, this.moveDirection, false)) {
         if (block.getMobilityFlag() != 1) {
            return false;
         }

         this.toDestroy.add(this.blockToMove);
         return true;
      } else {
         if (!this.tryMove(this.blockToMove)) {
            return false;
         }

         for (int i = 0; i < this.toMove.size(); i++) {
            BlockPos blockPos = this.toMove.get(i);
            if (this.world.getBlockState(blockPos).getBlock() == Blocks.slime_block && !this.canMoveAdjacentBlock(blockPos)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean tryMove(BlockPos origin) {
      Block block = this.world.getBlockState(origin).getBlock();
      if (block.getMaterial() == Material.air) {
         return true;
      }

      if (!BlockPistonBase.canPush(block, this.world, origin, this.moveDirection, false)) {
         return true;
      }

      if (origin.equals(this.pistonPos)) {
         return true;
      }

      if (this.toMove.contains(origin)) {
         return true;
      }

      int n = 1;
      if (n + this.toMove.size() > 12) {
         return false;
      }

      while (block == Blocks.slime_block) {
         BlockPos offset = origin.offset(this.moveDirection.getOpposite(), n);
         block = this.world.getBlockState(offset).getBlock();
         if (block.getMaterial() == Material.air || !BlockPistonBase.canPush(block, this.world, offset, this.moveDirection, false) || offset.equals(this.pistonPos)) {
            break;
         }

         if (++n + this.toMove.size() > 12) {
            return false;
         }
      }

      int n2 = 0;

      for (int i = n - 1; i >= 0; i--) {
         this.toMove.add(origin.offset(this.moveDirection.getOpposite(), i));
         n2++;
      }

      int n3 = 1;

      while (true) {
         BlockPos offset = origin.offset(this.moveDirection, n3);
         int indexOf = this.toMove.indexOf(offset);
         if (indexOf > -1) {
            this.setMovedBlocks(n2, indexOf);

            for (int i = 0; i <= indexOf + n2; i++) {
               BlockPos blockPos = this.toMove.get(i);
               if (this.world.getBlockState(blockPos).getBlock() == Blocks.slime_block && !this.canMoveAdjacentBlock(blockPos)) {
                  return false;
               }
            }

            return true;
         }

         block = this.world.getBlockState(offset).getBlock();
         if (block.getMaterial() == Material.air) {
            return true;
         }

         if (!BlockPistonBase.canPush(block, this.world, offset, this.moveDirection, true) || offset.equals(this.pistonPos)) {
            return false;
         }

         if (block.getMobilityFlag() == 1) {
            this.toDestroy.add(offset);
            return true;
         }

         if (this.toMove.size() >= 12) {
            return false;
         }

         this.toMove.add(offset);
         n2++;
         n3++;
      }
   }

   public void setMovedBlocks(int n, int n2) {
      ArrayList newArrayList = Lists.newArrayList();
      ArrayList newArrayList2 = Lists.newArrayList();
      ArrayList newArrayList3 = Lists.newArrayList();
      newArrayList.addAll(this.toMove.subList(0, n2));
      newArrayList2.addAll(this.toMove.subList(this.toMove.size() - n, this.toMove.size()));
      newArrayList3.addAll(this.toMove.subList(n2, this.toMove.size() - n));
      this.toMove.clear();
      this.toMove.addAll(newArrayList);
      this.toMove.addAll(newArrayList2);
      this.toMove.addAll(newArrayList3);
   }

   public boolean canMoveAdjacentBlock(BlockPos blockPos) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (enumFacing.getAxis() != this.moveDirection.getAxis() && !this.tryMove(blockPos.offset(enumFacing))) {
            return false;
         }
      }

      return true;
   }

   public List<BlockPos> getBlocksToMove() {
      return this.toMove;
   }

   public List<BlockPos> getBlocksToDestroy() {
      return this.toDestroy;
   }
}
