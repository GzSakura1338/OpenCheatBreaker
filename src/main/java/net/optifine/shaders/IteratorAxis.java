package net.optifine.shaders;

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.util.BlockPos;
import net.optifine.BlockPosM;

public class IteratorAxis implements Iterator<BlockPos> {
   public BlockPosM pos = new BlockPosM(0, 0, 0);
   public boolean hasNext = false;
   public double yDelta;
   public double zDelta;
   public int xStart;
   public int xEnd;
   public double yStart;
   public double yEnd;
   public double zStart;
   public double zEnd;
   public int xNext;
   public double yNext;
   public double zNext;

   public IteratorAxis(BlockPos blockPos, BlockPos blockPos2, double d, double d2) {
      this.yDelta = d;
      this.zDelta = d2;
      this.xStart = blockPos.getX();
      this.xEnd = blockPos2.getX();
      this.yStart = blockPos.getY();
      this.yEnd = blockPos2.getY() - 0.5;
      this.zStart = blockPos.getZ();
      this.zEnd = blockPos2.getZ() - 0.5;
      this.xNext = this.xStart;
      this.yNext = this.yStart;
      this.zNext = this.zStart;
      this.hasNext = this.xNext < this.xEnd && this.yNext < this.yEnd && this.zNext < this.zEnd;
   }

   @Override
   public boolean hasNext() {
      return this.hasNext;
   }

   public BlockPos next() {
      if (!this.hasNext) {
         throw new NoSuchElementException();
      }

      this.pos.setXyz(this.xNext, this.yNext, this.zNext);
      this.nextPos();
      this.hasNext = this.xNext < this.xEnd && this.yNext < this.yEnd && this.zNext < this.zEnd;
      return this.pos;
   }

   public void nextPos() {
      this.zNext++;
      if (this.zNext >= this.zEnd) {
         this.zNext = this.zStart;
         this.yNext++;
         if (this.yNext >= this.yEnd) {
            this.yNext = this.yStart;
            this.yStart = this.yStart + this.yDelta;
            this.yEnd = this.yEnd + this.yDelta;
            this.yNext = this.yStart;
            this.zStart = this.zStart + this.zDelta;
            this.zEnd = this.zEnd + this.zDelta;
            this.zNext = this.zStart;
            this.xNext++;
            if (this.xNext >= this.xEnd) {
            }
         }
      }
   }

   @Override
   public void remove() {
      throw new RuntimeException("Not implemented");
   }

   public static void main(String[] strs) throws Exception {
      BlockPos blockPos = new BlockPos(-2, 10, 20);
      BlockPos blockPos2 = new BlockPos(2, 12, 22);
      double d = -0.5;
      double d2 = 0.5;
      IteratorAxis iteratorAxis = new IteratorAxis(blockPos, blockPos2, d, d2);
      System.out.println("Start: " + blockPos + ", end: " + blockPos2 + ", yDelta: " + d + ", zDelta: " + d2);

      while (iteratorAxis.hasNext()) {
         BlockPos blockPos3 = iteratorAxis.next();
         System.out.println("" + blockPos3);
      }
   }
}
