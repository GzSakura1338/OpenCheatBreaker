package net.optifine.shaders;

import java.util.Iterator;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.optifine.BlockPosM;

public class Iterator3d implements Iterator<BlockPos> {
   public BlockPosM blockPos = new BlockPosM(0, 0, 0);
   public int axis = 0;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public IteratorAxis iteratorAxis;
   public static final int AXIS_X = 0;
   public static final int AXIS_Y = 1;
   public static final int AXIS_Z = 2;

   public Iterator3d(BlockPos blockPos, BlockPos blockPos2, int n, int n2) {
      boolean flag = blockPos.getX() > blockPos2.getX();
      boolean flag2 = blockPos.getY() > blockPos2.getY();
      boolean flag3 = blockPos.getZ() > blockPos2.getZ();
      blockPos = this.reverseCoord(blockPos, flag, flag2, flag3);
      blockPos2 = this.reverseCoord(blockPos2, flag, flag2, flag3);
      this.intValue = flag ? -1 : 1;
      this.intValue2 = flag2 ? -1 : 1;
      this.intValue3 = flag3 ? -1 : 1;
      Vec3 vec3 = new Vec3(blockPos2.getX() - blockPos.getX(), blockPos2.getY() - blockPos.getY(), blockPos2.getZ() - blockPos.getZ());
      Vec3 normalize = vec3.normalize();
      Vec3 vec32 = new Vec3(1.0, 0.0, 0.0);
      double dotProduct = normalize.dotProduct(vec32);
      double abs = Math.abs(dotProduct);
      Vec3 vec33 = new Vec3(0.0, 1.0, 0.0);
      double dotProduct2 = normalize.dotProduct(vec33);
      double abs2 = Math.abs(dotProduct2);
      Vec3 vec34 = new Vec3(0.0, 0.0, 1.0);
      double dotProduct3 = normalize.dotProduct(vec34);
      double abs3 = Math.abs(dotProduct3);
      if (abs3 >= abs2 && abs3 >= abs) {
         this.axis = 2;
         BlockPos blockPos3 = new BlockPos(blockPos.getZ(), blockPos.getY() - n, blockPos.getX() - n2);
         BlockPos blockPos4 = new BlockPos(blockPos2.getZ(), blockPos.getY() + n + 1, blockPos.getX() + n2 + 1);
         int n3 = blockPos2.getZ() - blockPos.getZ();
         double d = (blockPos2.getY() - blockPos.getY()) / (1.0 * n3);
         double d2 = (blockPos2.getX() - blockPos.getX()) / (1.0 * n3);
         this.iteratorAxis = new IteratorAxis(blockPos3, blockPos4, d, d2);
      } else if (abs2 >= abs && abs2 >= abs3) {
         this.axis = 1;
         BlockPos blockPos3 = new BlockPos(blockPos.getY(), blockPos.getX() - n, blockPos.getZ() - n2);
         BlockPos blockPos4 = new BlockPos(blockPos2.getY(), blockPos.getX() + n + 1, blockPos.getZ() + n2 + 1);
         int n3 = blockPos2.getY() - blockPos.getY();
         double d = (blockPos2.getX() - blockPos.getX()) / (1.0 * n3);
         double d2 = (blockPos2.getZ() - blockPos.getZ()) / (1.0 * n3);
         this.iteratorAxis = new IteratorAxis(blockPos3, blockPos4, d, d2);
      } else {
         this.axis = 0;
         BlockPos blockPos3 = new BlockPos(blockPos.getX(), blockPos.getY() - n, blockPos.getZ() - n2);
         BlockPos blockPos4 = new BlockPos(blockPos2.getX(), blockPos.getY() + n + 1, blockPos.getZ() + n2 + 1);
         int n3 = blockPos2.getX() - blockPos.getX();
         double d = (blockPos2.getY() - blockPos.getY()) / (1.0 * n3);
         double d2 = (blockPos2.getZ() - blockPos.getZ()) / (1.0 * n3);
         this.iteratorAxis = new IteratorAxis(blockPos3, blockPos4, d, d2);
      }
   }

   public BlockPos reverseCoord(BlockPos blockPos, boolean flag, boolean flag2, boolean flag3) {
      if (flag) {
         blockPos = new BlockPos(-blockPos.getX(), blockPos.getY(), blockPos.getZ());
      }

      if (flag2) {
         blockPos = new BlockPos(blockPos.getX(), -blockPos.getY(), blockPos.getZ());
      }

      if (flag3) {
         blockPos = new BlockPos(blockPos.getX(), blockPos.getY(), -blockPos.getZ());
      }

      return blockPos;
   }

   @Override
   public boolean hasNext() {
      return this.iteratorAxis.hasNext();
   }

   public BlockPos next() {
      BlockPos blockPos = this.iteratorAxis.next();
      switch (this.axis) {
         case 0:
            this.blockPos.setXyz(blockPos.getX() * this.intValue, blockPos.getY() * this.intValue2, blockPos.getZ() * this.intValue3);
            return this.blockPos;
         case 1:
            this.blockPos.setXyz(blockPos.getY() * this.intValue, blockPos.getX() * this.intValue2, blockPos.getZ() * this.intValue3);
            return this.blockPos;
         case 2:
            this.blockPos.setXyz(blockPos.getZ() * this.intValue, blockPos.getY() * this.intValue2, blockPos.getX() * this.intValue3);
            return this.blockPos;
         default:
            this.blockPos.setXyz(blockPos.getX() * this.intValue, blockPos.getY() * this.intValue2, blockPos.getZ() * this.intValue3);
            return this.blockPos;
      }
   }

   @Override
   public void remove() {
      throw new RuntimeException("Not supported");
   }

   public static void main(String[] strs) {
      BlockPos blockPos = new BlockPos(10, 20, 30);
      BlockPos blockPos2 = new BlockPos(30, 40, 20);
      Iterator3d iterator3d = new Iterator3d(blockPos, blockPos2, 1, 1);

      while (iterator3d.hasNext()) {
         BlockPos blockPos3 = iterator3d.next();
         System.out.println("" + blockPos3);
      }
   }
}
