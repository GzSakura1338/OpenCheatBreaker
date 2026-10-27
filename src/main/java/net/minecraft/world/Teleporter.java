package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;

public class Teleporter {
   public LongHashMap<Teleporter.PortalPosition> destinationCoordinateCache = new LongHashMap<>();
   public List<Long> destinationCoordinateKeys = Lists.newArrayList();
   public WorldServer worldServerInstance;
   public Random random;

   public Teleporter(WorldServer worldIn) {
      this.worldServerInstance = worldIn;
      this.random = new Random(worldIn.getSeed());
   }

   public void placeInPortal(Entity entityIn, float rotationYaw) {
      if (this.worldServerInstance.provider.getDimensionId() != 1) {
         if (!this.placeInExistingPortal(entityIn, rotationYaw)) {
            this.makePortal(entityIn);
            this.placeInExistingPortal(entityIn, rotationYaw);
         }
      } else {
         int n = MathHelper.floor_double(entityIn.posX);
         int n2 = MathHelper.floor_double(entityIn.posY) - 1;
         int n3 = MathHelper.floor_double(entityIn.posZ);
         byte b = 1;
         byte b2 = 0;

         for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
               for (int k = -1; k < 3; k++) {
                  int n4 = n + j * b + i * b2;
                  int n5 = n2 + k;
                  int n6 = n3 + j * b2 - i * b;
                  boolean flag = k < 0;
                  this.worldServerInstance
                     .setBlockState(new BlockPos(n4, n5, n6), flag ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
               }
            }
         }

         entityIn.setLocationAndAngles(n, n2, n3, entityIn.rotationYaw, 0.0F);
         entityIn.motionX = entityIn.motionY = entityIn.motionZ = 0.0;
      }
   }

   public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
      short s = 128;
      double d = -1.0;
      int n = MathHelper.floor_double(entityIn.posX);
      int n2 = MathHelper.floor_double(entityIn.posZ);
      boolean flag = true;
      BlockPos blockPos = BlockPos.ORIGIN;
      long chunkXZ2Int = ChunkCoordIntPair.chunkXZ2Int(n, n2);
      if (this.destinationCoordinateCache.containsItem(chunkXZ2Int)) {
         Teleporter.PortalPosition valueByKey = this.destinationCoordinateCache.getValueByKey(chunkXZ2Int);
         d = 0.0;
         blockPos = valueByKey;
         valueByKey.lastUpdateTime = this.worldServerInstance.getTotalWorldTime();
         flag = false;
      } else {
         BlockPos blockPos2 = new BlockPos(entityIn);

         for (int i = -128; i <= 128; i++) {
            for (int j = -128; j <= 128; j++) {
               BlockPos blockPos3 = blockPos2.add(i, this.worldServerInstance.getActualHeight() - 1 - blockPos2.getY(), j);

               while (blockPos3.getY() >= 0) {
                  BlockPos down = blockPos3.down();
                  if (this.worldServerInstance.getBlockState(blockPos3).getBlock() == Blocks.portal) {
                     while (this.worldServerInstance.getBlockState(down = blockPos3.down()).getBlock() == Blocks.portal) {
                        blockPos3 = down;
                     }

                     double distanceSq = blockPos3.distanceSq(blockPos2);
                     if (d < 0.0 || distanceSq < d) {
                        d = distanceSq;
                        blockPos = blockPos3;
                     }
                  }

                  blockPos3 = down;
               }
            }
         }
      }

      if (d >= 0.0) {
         if (flag) {
            this.destinationCoordinateCache.add(chunkXZ2Int, new Teleporter.PortalPosition(blockPos, this.worldServerInstance.getTotalWorldTime()));
            this.destinationCoordinateKeys.add(chunkXZ2Int);
         }

         double d2 = blockPos.getX() + 0.5;
         double d3 = blockPos.getY() + 0.5;
         double d4 = blockPos.getZ() + 0.5;
         PatternHelper findPortal = Blocks.portal.findPortal(this.worldServerInstance, blockPos);
         boolean flag2 = findPortal.getFinger().rotateY().getAxisDirection() == AxisDirection.NEGATIVE;
         double d5 = findPortal.getFinger().getAxis() == Axis.X ? findPortal.getPos().getZ() : findPortal.getPos().getX();
         d3 = findPortal.getPos().getY() + 1 - entityIn.getLastNetherPortalDirectionVector().yCoord * findPortal.getHeight();
         if (flag2) {
            d5++;
         }

         if (findPortal.getFinger().getAxis() == Axis.X) {
            d4 = d5
               + (1.0 - entityIn.getLastNetherPortalDirectionVector().xCoord) * findPortal.getWidth() * findPortal.getFinger().rotateY().getAxisDirection().getOffset();
         } else {
            d2 = d5
               + (1.0 - entityIn.getLastNetherPortalDirectionVector().xCoord) * findPortal.getWidth() * findPortal.getFinger().rotateY().getAxisDirection().getOffset();
         }

         float f = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;
         if (findPortal.getFinger().getOpposite() == entityIn.getTeleportDirection()) {
            f = 1.0F;
            f2 = 1.0F;
         } else if (findPortal.getFinger().getOpposite() == entityIn.getTeleportDirection().getOpposite()) {
            f = -1.0F;
            f2 = -1.0F;
         } else if (findPortal.getFinger().getOpposite() == entityIn.getTeleportDirection().rotateY()) {
            f3 = 1.0F;
            f4 = -1.0F;
         } else {
            f3 = -1.0F;
            f4 = 1.0F;
         }

         double motionX = entityIn.motionX;
         double motionZ = entityIn.motionZ;
         entityIn.motionX = motionX * f + motionZ * f4;
         entityIn.motionZ = motionX * f3 + motionZ * f2;
         entityIn.rotationYaw = rotationYaw
            - entityIn.getTeleportDirection().getOpposite().getHorizontalIndex() * 90
            + findPortal.getFinger().getHorizontalIndex() * 90;
         entityIn.setLocationAndAngles(d2, d3, d4, entityIn.rotationYaw, entityIn.rotationPitch);
         return true;
      } else {
         return false;
      }
   }

   public boolean makePortal(Entity entityIn) {
      byte b = 16;
      double d = -1.0;
      int n = MathHelper.floor_double(entityIn.posX);
      int n2 = MathHelper.floor_double(entityIn.posY);
      int n3 = MathHelper.floor_double(entityIn.posZ);
      int n4 = n;
      int n5 = n2;
      int n6 = n3;
      int n7 = 0;
      int nextInt = this.random.nextInt(4);
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = n - b; i <= n + b; i++) {
         double d2 = i + 0.5 - entityIn.posX;

         for (int j = n3 - b; j <= n3 + b; j++) {
            double d3 = j + 0.5 - entityIn.posZ;

            label294:
            for (int k = this.worldServerInstance.getActualHeight() - 1; k >= 0; k--) {
               if (this.worldServerInstance.isAirBlock(mutableBlockPos.set(i, k, j))) {
                  while (k > 0 && this.worldServerInstance.isAirBlock(mutableBlockPos.set(i, k - 1, j))) {
                     k--;
                  }

                  for (int m = nextInt; m < nextInt + 4; m++) {
                     int n8 = m % 2;
                     int n9 = 1 - n8;
                     if (m % 4 >= 2) {
                        n8 = -n8;
                        n9 = -n9;
                     }

                     for (int n10 = 0; n10 < 3; n10++) {
                        for (int i5 = 0; i5 < 4; i5++) {
                           for (int i6 = -1; i6 < 4; i6++) {
                              int n11 = i + (i5 - 1) * n8 + n10 * n9;
                              int n12 = k + i6;
                              int n13 = j + (i5 - 1) * n9 - n10 * n8;
                              mutableBlockPos.set(n11, n12, n13);
                              if (i6 < 0 && !this.worldServerInstance.getBlockState(mutableBlockPos).getBlock().getMaterial().isSolid()
                                 || i6 >= 0 && !this.worldServerInstance.isAirBlock(mutableBlockPos)) {
                                 continue label294;
                              }
                           }
                        }
                     }

                     double d4 = k + 0.5 - entityIn.posY;
                     double d5 = d2 * d2 + d4 * d4 + d3 * d3;
                     if (d < 0.0 || d5 < d) {
                        d = d5;
                        n4 = i;
                        n5 = k;
                        n6 = j;
                        n7 = m % 4;
                     }
                  }
               }
            }
         }
      }

      if (d < 0.0) {
         for (int i = n - b; i <= n + b; i++) {
            double d2 = i + 0.5 - entityIn.posX;

            for (int j = n3 - b; j <= n3 + b; j++) {
               double d3 = j + 0.5 - entityIn.posZ;

               label231:
               for (int k = this.worldServerInstance.getActualHeight() - 1; k >= 0; k--) {
                  if (this.worldServerInstance.isAirBlock(mutableBlockPos.set(i, k, j))) {
                     while (k > 0 && this.worldServerInstance.isAirBlock(mutableBlockPos.set(i, k - 1, j))) {
                        k--;
                     }

                     for (int m = nextInt; m < nextInt + 2; m++) {
                        int n8 = m % 2;
                        int n9 = 1 - n8;

                        for (int n10 = 0; n10 < 4; n10++) {
                           for (int i5 = -1; i5 < 4; i5++) {
                              int n11 = i + (n10 - 1) * n8;
                              int n12 = k + i5;
                              int n13 = j + (n10 - 1) * n9;
                              mutableBlockPos.set(n11, n12, n13);
                              if (i5 < 0 && !this.worldServerInstance.getBlockState(mutableBlockPos).getBlock().getMaterial().isSolid()
                                 || i5 >= 0 && !this.worldServerInstance.isAirBlock(mutableBlockPos)) {
                                 continue label231;
                              }
                           }
                        }

                        double d4 = k + 0.5 - entityIn.posY;
                        double d5 = d2 * d2 + d4 * d4 + d3 * d3;
                        if (d < 0.0 || d5 < d) {
                           d = d5;
                           n4 = i;
                           n5 = k;
                           n6 = j;
                           n7 = m % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int n8 = n4;
      int n9 = n5;
      int n10 = n6;
      int n11 = n7 % 2;
      int n12 = 1 - n11;
      if (n7 % 4 >= 2) {
         n11 = -n11;
         n12 = -n12;
      }

      if (d < 0.0) {
         n5 = MathHelper.clamp_int(n5, 70, this.worldServerInstance.getActualHeight() - 10);
         n9 = n5;

         for (int i = -1; i <= 1; i++) {
            for (int j = 1; j < 3; j++) {
               for (int k = -1; k < 3; k++) {
                  int n13 = n8 + (j - 1) * n11 + i * n12;
                  int n14 = n9 + k;
                  int n15 = n10 + (j - 1) * n12 - i * n11;
                  boolean flag = k < 0;
                  this.worldServerInstance
                     .setBlockState(new BlockPos(n13, n14, n15), flag ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
               }
            }
         }
      }

      IBlockState withProperty = Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, n11 != 0 ? Axis.X : Axis.Z);

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            for (int k = -1; k < 4; k++) {
               int n13 = n8 + (j - 1) * n11;
               int n14 = n9 + k;
               int n15 = n10 + (j - 1) * n12;
               boolean flag = j == 0 || j == 3 || k == -1 || k == 3;
               this.worldServerInstance.setBlockState(new BlockPos(n13, n14, n15), flag ? Blocks.obsidian.getDefaultState() : withProperty, 2);
            }
         }

         for (int j = 0; j < 4; j++) {
            for (int k = -1; k < 4; k++) {
               int n13 = n8 + (j - 1) * n11;
               int n14 = n9 + k;
               int n15 = n10 + (j - 1) * n12;
               BlockPos blockPos = new BlockPos(n13, n14, n15);
               this.worldServerInstance.notifyNeighborsOfStateChange(blockPos, this.worldServerInstance.getBlockState(blockPos).getBlock());
            }
         }
      }

      return true;
   }

   public void removeStalePortalLocations(long worldTime) {
      if (worldTime % 100L == 0L) {
         Iterator iterator = this.destinationCoordinateKeys.iterator();
         long l = worldTime - 300L;

         while (iterator.hasNext()) {
            Long longValue = (Long)iterator.next();
            Teleporter.PortalPosition valueByKey = this.destinationCoordinateCache.getValueByKey(longValue);
            if (valueByKey == null || valueByKey.lastUpdateTime < l) {
               iterator.remove();
               this.destinationCoordinateCache.remove(longValue);
            }
         }
      }
   }

   public class PortalPosition extends BlockPos {
      public long lastUpdateTime;

      public PortalPosition(BlockPos pos, long lastUpdate) {
         super(pos.getX(), pos.getY(), pos.getZ());
         this.lastUpdateTime = lastUpdate;
      }
   }
}
