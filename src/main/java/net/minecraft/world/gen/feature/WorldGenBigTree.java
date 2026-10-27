package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenBigTree extends WorldGenAbstractTree {
   public Random rand;
   public World world;
   public BlockPos basePos = BlockPos.ORIGIN;
   public int heightLimit;
   public int height;
   public double heightAttenuation = 0.618;
   public double branchSlope = 0.381;
   public double scaleWidth = 1.0;
   public double leafDensity = 1.0;
   public int trunkSize = 1;
   public int heightLimitLimit = 12;
   public int leafDistanceLimit = 4;
   public List<WorldGenBigTree.FoliageCoordinates> blockCoords;

   public WorldGenBigTree(boolean flag) {
      super(flag);
   }

   public void generateLeafNodeList() {
      this.height = (int)(this.heightLimit * this.heightAttenuation);
      if (this.height >= this.heightLimit) {
         this.height = this.heightLimit - 1;
      }

      int n = (int)(1.382 + Math.pow(this.leafDensity * this.heightLimit / 13.0, 2.0));
      if (n < 1) {
         n = 1;
      }

      int n2 = this.basePos.getY() + this.height;
      int n3 = this.heightLimit - this.leafDistanceLimit;
      this.blockCoords = Lists.newArrayList();
      this.blockCoords.add(new WorldGenBigTree.FoliageCoordinates(this.basePos.up(n3), n2));

      for (; n3 >= 0; n3--) {
         float layerSize = this.layerSize(n3);
         if (layerSize >= 0.0F) {
            for (int i = 0; i < n; i++) {
               double d = this.scaleWidth * layerSize * (this.rand.nextFloat() + 0.328);
               double d2 = this.rand.nextFloat() * 2.0F * Math.PI;
               double d3 = d * Math.sin(d2) + 0.5;
               double d4 = d * Math.cos(d2) + 0.5;
               BlockPos blockPos = this.basePos.add(d3, n3 - 1, d4);
               BlockPos up = blockPos.up(this.leafDistanceLimit);
               if (this.checkBlockLine(blockPos, up) == -1) {
                  int n4 = this.basePos.getX() - blockPos.getX();
                  int n5 = this.basePos.getZ() - blockPos.getZ();
                  double d5 = blockPos.getY() - Math.sqrt(n4 * n4 + n5 * n5) * this.branchSlope;
                  int n6 = d5 > n2 ? n2 : (int)d5;
                  BlockPos blockPos2 = new BlockPos(this.basePos.getX(), n6, this.basePos.getZ());
                  if (this.checkBlockLine(blockPos2, blockPos) == -1) {
                     this.blockCoords.add(new WorldGenBigTree.FoliageCoordinates(blockPos, blockPos2.getY()));
                  }
               }
            }
         }
      }
   }

   public void generateLeaves(BlockPos blockPos, float f, IBlockState blockState) {
      int n = (int)(f + 0.618);

      for (int i = -n; i <= n; i++) {
         for (int j = -n; j <= n; j++) {
            if (Math.pow(Math.abs(i) + 0.5, 2.0) + Math.pow(Math.abs(j) + 0.5, 2.0) <= f * f) {
               BlockPos blockPos2 = blockPos.add(i, 0, j);
               Material material = this.world.getBlockState(blockPos2).getBlock().getMaterial();
               if (material == Material.air || material == Material.leaves) {
                  this.setBlockAndNotifyAdequately(this.world, blockPos2, blockState);
               }
            }
         }
      }
   }

   public float layerSize(int n) {
      if (n < this.heightLimit * 0.3F) {
         return -1.0F;
      }

      float f = this.heightLimit / 2.0F;
      float f2 = f - n;
      float f3 = MathHelper.sqrt_float(f * f - f2 * f2);
      if (f2 == 0.0F) {
         f3 = f;
      } else if (Math.abs(f2) >= f) {
         return 0.0F;
      }

      return f3 * 0.5F;
   }

   public float leafSize(int n) {
      return n >= 0 && n < this.leafDistanceLimit ? (n != 0 && n != this.leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
   }

   public void generateLeafNode(BlockPos pos) {
      for (int i = 0; i < this.leafDistanceLimit; i++) {
         this.generateLeaves(pos.up(i), this.leafSize(i), Blocks.leaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, false));
      }
   }

   public void setLogsAt(BlockPos blockPos, BlockPos blockPos2, Block block) {
      BlockPos blockPos3 = blockPos2.add(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
      int greatestDistance = this.getGreatestDistance(blockPos3);
      float f = (float)blockPos3.getX() / greatestDistance;
      float f2 = (float)blockPos3.getY() / greatestDistance;
      float f3 = (float)blockPos3.getZ() / greatestDistance;

      for (int i = 0; i <= greatestDistance; i++) {
         BlockPos blockPos4 = blockPos.add(0.5F + i * f, 0.5F + i * f2, 0.5F + i * f3);
         EnumAxis logOrientation = this.getLogOrientation(blockPos, blockPos4);
         this.setBlockAndNotifyAdequately(this.world, blockPos4, block.getDefaultState().withProperty(BlockLog.LOG_AXIS, logOrientation));
      }
   }

   public int getGreatestDistance(BlockPos posIn) {
      int n = MathHelper.abs_int(posIn.getX());
      int n2 = MathHelper.abs_int(posIn.getY());
      int n3 = MathHelper.abs_int(posIn.getZ());
      return n3 > n && n3 > n2 ? n3 : (n2 > n ? n2 : n);
   }

   public EnumAxis getLogOrientation(BlockPos blockPos, BlockPos blockPos2) {
      EnumAxis enumAxis = EnumAxis.Y;
      int abs = Math.abs(blockPos2.getX() - blockPos.getX());
      int abs2 = Math.abs(blockPos2.getZ() - blockPos.getZ());
      int max = Math.max(abs, abs2);
      if (max > 0) {
         if (abs == max) {
            enumAxis = EnumAxis.X;
         } else if (abs2 == max) {
            enumAxis = EnumAxis.Z;
         }
      }

      return enumAxis;
   }

   public void generateLeaves() {
      for (WorldGenBigTree.FoliageCoordinates foliageCoordinates : this.blockCoords) {
         this.generateLeafNode(foliageCoordinates);
      }
   }

   public boolean leafNodeNeedsBase(int n) {
      return n >= this.heightLimit * 0.2;
   }

   public void generateTrunk() {
      BlockPos basePos = this.basePos;
      BlockPos up = this.basePos.up(this.height);
      Block log = Blocks.log;
      this.setLogsAt(basePos, up, log);
      if (this.trunkSize == 2) {
         this.setLogsAt(basePos.east(), up.east(), log);
         this.setLogsAt(basePos.east().south(), up.east().south(), log);
         this.setLogsAt(basePos.south(), up.south(), log);
      }
   }

   public void generateLeafNodeBases() {
      for (WorldGenBigTree.FoliageCoordinates foliageCoordinates : this.blockCoords) {
         int branchBaseY = foliageCoordinates.getBranchBaseY();
         BlockPos blockPos = new BlockPos(this.basePos.getX(), branchBaseY, this.basePos.getZ());
         if (!blockPos.equals(foliageCoordinates) && this.leafNodeNeedsBase(branchBaseY - this.basePos.getY())) {
            this.setLogsAt(blockPos, foliageCoordinates, Blocks.log);
         }
      }
   }

   public int checkBlockLine(BlockPos posOne, BlockPos posTwo) {
      BlockPos blockPos = posTwo.add(-posOne.getX(), -posOne.getY(), -posOne.getZ());
      int greatestDistance = this.getGreatestDistance(blockPos);
      float f = (float)blockPos.getX() / greatestDistance;
      float f2 = (float)blockPos.getY() / greatestDistance;
      float f3 = (float)blockPos.getZ() / greatestDistance;
      if (greatestDistance == 0) {
         return -1;
      }

      for (int i = 0; i <= greatestDistance; i++) {
         BlockPos blockPos2 = posOne.add(0.5F + i * f, 0.5F + i * f2, 0.5F + i * f3);
         if (!this.isBlockReplaceable(this.world.getBlockState(blockPos2).getBlock())) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public void setLeafRadius() {
      this.leafDistanceLimit = 5;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      this.world = worldIn;
      this.basePos = position;
      this.rand = new Random(rand.nextLong());
      if (this.heightLimit == 0) {
         this.heightLimit = 5 + this.rand.nextInt(this.heightLimitLimit);
      }

      if (!this.validTreeLocation()) {
         return false;
      }

      this.generateLeafNodeList();
      this.generateLeaves();
      this.generateTrunk();
      this.generateLeafNodeBases();
      return true;
   }

   public boolean validTreeLocation() {
      Block block = this.world.getBlockState(this.basePos.down()).getBlock();
      if (block != Blocks.dirt && block != Blocks.grass && block != Blocks.farmland) {
         return false;
      }

      int checkBlockLine = this.checkBlockLine(this.basePos, this.basePos.up(this.heightLimit - 1));
      if (checkBlockLine == -1) {
         return true;
      }

      if (checkBlockLine < 6) {
         return false;
      }

      this.heightLimit = checkBlockLine;
      return true;
   }

   static class FoliageCoordinates extends BlockPos {
      public int branchBaseY;

      public FoliageCoordinates(BlockPos blockPos, int n) {
         super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
         this.branchBaseY = n;
      }

      public int getBranchBaseY() {
         return this.branchBaseY;
      }
   }
}
