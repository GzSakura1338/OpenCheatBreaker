package net.minecraft.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;

public class Teleporter {
    public LongHashMap destinationCoordinateCache = new LongHashMap();
    public List destinationCoordinateKeys = new ArrayList();
    public WorldServer worldServerInstance;
    public Random random;

    public Teleporter(WorldServer worldIn) {
        this.worldServerInstance = worldIn;
        this.random = new Random(worldIn.getSeed());
    }

    public void placeInPortal(Entity entity, double d, double d2, double d3, float f) {
        if (this.worldServerInstance.provider.dimensionId != 1) {
            if (!this.placeInExistingPortal(entity, d, d2, d3, f)) {
                this.makePortal(entity);
                this.placeInExistingPortal(entity, d, d2, d3, f);
            }
        } else {
            int n = MathHelper.floor_double(entity.posX);
            int n2 = MathHelper.floor_double(entity.posY) - 1;
            int n3 = MathHelper.floor_double(entity.posZ);
            byte b = 1;
            byte b2 = 0;

            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    for (int k = -1; k < 3; k++) {
                        int n4 = n + j * b + i * b2;
                        int n5 = n2 + k;
                        int n6 = n3 + j * b2 - i * b;
                        boolean flag = k < 0;
                        this.worldServerInstance.setBlock(n4, n5, n6, flag ? Blocks.obsidian : Blocks.air);
                    }
                }
            }

            entity.setLocationAndAngles(n, n2, n3, entity.rotationYaw, 0.0F);
            entity.motionX = entity.motionY = entity.motionZ = 0.0;
        }
    }

    public boolean placeInExistingPortal(Entity entity, double d, double d2, double d3, float f) {
        short s = 128;
        double d4 = -1.0;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = MathHelper.floor_double(entity.posX);
        int n5 = MathHelper.floor_double(entity.posZ);
        long chunkXZ2Int = ChunkCoordIntPair.chunkXZ2Int(n4, n5);
        boolean flag = true;
        if (this.destinationCoordinateCache.containsItem(chunkXZ2Int)) {
            Teleporter.PortalPosition valueByKey = (Teleporter.PortalPosition)this.destinationCoordinateCache.getValueByKey(chunkXZ2Int);
            d4 = 0.0;
            n = valueByKey.posX;
            n2 = valueByKey.posY;
            n3 = valueByKey.posZ;
            valueByKey.lastUpdateTime = this.worldServerInstance.getTotalWorldTime();
            flag = false;
        } else {
            for (int i = n4 - s; i <= n4 + s; i++) {
                double d5 = i + 0.5 - entity.posX;

                for (int j = n5 - s; j <= n5 + s; j++) {
                    double d6 = j + 0.5 - entity.posZ;

                    for (int k = this.worldServerInstance.getActualHeight() - 1; k >= 0; k--) {
                        if (this.worldServerInstance.getBlock(i, k, j) == Blocks.portal) {
                            while (this.worldServerInstance.getBlock(i, k - 1, j) == Blocks.portal) {
                                k--;
                            }

                            double d7 = k + 0.5 - entity.posY;
                            double d8 = d5 * d5 + d7 * d7 + d6 * d6;
                            if (d4 < 0.0 || d8 < d4) {
                                d4 = d8;
                                n = i;
                                n2 = k;
                                n3 = j;
                            }
                        }
                    }
                }
            }
        }

        if (d4 >= 0.0) {
            if (flag) {
                this.destinationCoordinateCache.add(chunkXZ2Int, new Teleporter.PortalPosition(n, n2, n3, this.worldServerInstance.getTotalWorldTime()));
                this.destinationCoordinateKeys.add(chunkXZ2Int);
            }

            double d5 = n + 0.5;
            double d6 = n2 + 0.5;
            double d7 = n3 + 0.5;
            int n6 = -1;
            if (this.worldServerInstance.getBlock(n - 1, n2, n3) == Blocks.portal) {
                n6 = 2;
            }

            if (this.worldServerInstance.getBlock(n + 1, n2, n3) == Blocks.portal) {
                n6 = 0;
            }

            if (this.worldServerInstance.getBlock(n, n2, n3 - 1) == Blocks.portal) {
                n6 = 3;
            }

            if (this.worldServerInstance.getBlock(n, n2, n3 + 1) == Blocks.portal) {
                n6 = 1;
            }

            int teleportDirection = entity.getTeleportDirection();
            if (n6 > -1) {
                int n7 = Direction.rotateLeft[n6];
                int n8 = Direction.offsetX[n6];
                int n9 = Direction.offsetZ[n6];
                int n10 = Direction.offsetX[n7];
                int n11 = Direction.offsetZ[n7];
                boolean flag2 = !this.worldServerInstance.isAirBlock(n + n8 + n10, n2, n3 + n9 + n11)
                    || !this.worldServerInstance.isAirBlock(n + n8 + n10, n2 + 1, n3 + n9 + n11);
                boolean flag3 = !this.worldServerInstance.isAirBlock(n + n8, n2, n3 + n9)
                    || !this.worldServerInstance.isAirBlock(n + n8, n2 + 1, n3 + n9);
                if (flag2 && flag3) {
                    n6 = Direction.rotateOpposite[n6];
                    n7 = Direction.rotateOpposite[n7];
                    n8 = Direction.offsetX[n6];
                    n9 = Direction.offsetZ[n6];
                    n10 = Direction.offsetX[n7];
                    n11 = Direction.offsetZ[n7];
                    int n12 = n - n10;
                    d5 -= n10;
                    int n13 = n3 - n11;
                    d7 -= n11;
                    flag2 = !this.worldServerInstance.isAirBlock(n12 + n8 + n10, n2, n13 + n9 + n11)
                        || !this.worldServerInstance.isAirBlock(n12 + n8 + n10, n2 + 1, n13 + n9 + n11);
                    flag3 = !this.worldServerInstance.isAirBlock(n12 + n8, n2, n13 + n9)
                        || !this.worldServerInstance.isAirBlock(n12 + n8, n2 + 1, n13 + n9);
                }

                float f2 = 0.5F;
                float f3 = 0.5F;
                if (!flag2 && flag3) {
                    f2 = 1.0F;
                } else if (flag2 && !flag3) {
                    f2 = 0.0F;
                } else if (flag2 && flag3) {
                    f3 = 0.0F;
                }

                d5 += n10 * f2 + f3 * n8;
                d7 += n11 * f2 + f3 * n9;
                float f4 = 0.0F;
                float f5 = 0.0F;
                float f6 = 0.0F;
                float f7 = 0.0F;
                if (n6 == teleportDirection) {
                    f4 = 1.0F;
                    f5 = 1.0F;
                } else if (n6 == Direction.rotateOpposite[teleportDirection]) {
                    f4 = -1.0F;
                    f5 = -1.0F;
                } else if (n6 == Direction.enderEyeMetaToDirection[teleportDirection]) {
                    f6 = 1.0F;
                    f7 = -1.0F;
                } else {
                    f6 = -1.0F;
                    f7 = 1.0F;
                }

                double motionX = entity.motionX;
                double motionZ = entity.motionZ;
                entity.motionX = motionX * f4 + motionZ * f7;
                entity.motionZ = motionX * f6 + motionZ * f5;
                entity.rotationYaw = f - teleportDirection * 90 + n6 * 90;
            } else {
                entity.motionX = entity.motionY = entity.motionZ = 0.0;
            }

            entity.setLocationAndAngles(d5, d6, d7, entity.rotationYaw, entity.rotationPitch);
            return true;
        } else {
            return false;
        }
    }

    public boolean makePortal(Entity entity) {
        byte b = 16;
        double d = -1.0;
        int n = MathHelper.floor_double(entity.posX);
        int n2 = MathHelper.floor_double(entity.posY);
        int n3 = MathHelper.floor_double(entity.posZ);
        int n4 = n;
        int n5 = n2;
        int n6 = n3;
        int n7 = 0;
        int nextInt = this.random.nextInt(4);

        for (int i = n - b; i <= n + b; i++) {
            double d2 = i + 0.5 - entity.posX;

            for (int j = n3 - b; j <= n3 + b; j++) {
                double d3 = j + 0.5 - entity.posZ;

                label297:
                for (int k = this.worldServerInstance.getActualHeight() - 1; k >= 0; k--) {
                    if (this.worldServerInstance.isAirBlock(i, k, j)) {
                        while (k > 0 && this.worldServerInstance.isAirBlock(i, k - 1, j)) {
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
                                        if (i6 < 0 && !this.worldServerInstance.getBlock(n11, n12, n13).getMaterial().isSolid()
                                            || i6 >= 0 && !this.worldServerInstance.isAirBlock(n11, n12, n13)) {
                                            continue label297;
                                        }
                                    }
                                }
                            }

                            double d4 = k + 0.5 - entity.posY;
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
                double d2 = i + 0.5 - entity.posX;

                for (int j = n3 - b; j <= n3 + b; j++) {
                    double d3 = j + 0.5 - entity.posZ;

                    label234:
                    for (int k = this.worldServerInstance.getActualHeight() - 1; k >= 0; k--) {
                        if (this.worldServerInstance.isAirBlock(i, k, j)) {
                            while (k > 0 && this.worldServerInstance.isAirBlock(i, k - 1, j)) {
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
                                        if (i5 < 0 && !this.worldServerInstance.getBlock(n11, n12, n13).getMaterial().isSolid()
                                            || i5 >= 0 && !this.worldServerInstance.isAirBlock(n11, n12, n13)) {
                                            continue label234;
                                        }
                                    }
                                }

                                double d4 = k + 0.5 - entity.posY;
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
            if (n5 < 70) {
                n5 = 70;
            }

            if (n5 > this.worldServerInstance.getActualHeight() - 10) {
                n5 = this.worldServerInstance.getActualHeight() - 10;
            }

            n9 = n5;

            for (int i = -1; i <= 1; i++) {
                for (int j = 1; j < 3; j++) {
                    for (int k = -1; k < 3; k++) {
                        int n13 = n8 + (j - 1) * n11 + i * n12;
                        int n14 = n9 + k;
                        int n15 = n10 + (j - 1) * n12 - i * n11;
                        boolean flag = k < 0;
                        this.worldServerInstance.setBlock(n13, n14, n15, flag ? Blocks.obsidian : Blocks.air);
                    }
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = -1; k < 4; k++) {
                    int n13 = n8 + (j - 1) * n11;
                    int n14 = n9 + k;
                    int n15 = n10 + (j - 1) * n12;
                    boolean flag = j == 0 || j == 3 || k == -1 || k == 3;
                    this.worldServerInstance.setBlock(n13, n14, n15, flag ? Blocks.obsidian : Blocks.portal, 0, 2);
                }
            }

            for (int j = 0; j < 4; j++) {
                for (int k = -1; k < 4; k++) {
                    int n13 = n8 + (j - 1) * n11;
                    int n14 = n9 + k;
                    int n15 = n10 + (j - 1) * n12;
                    this.worldServerInstance.notifyBlocksOfNeighborChange(n13, n14, n15, this.worldServerInstance.getBlock(n13, n14, n15));
                }
            }
        }

        return true;
    }

    public void removeStalePortalLocations(long l) {
        if (l % 100L == 0L) {
            Iterator iterator = this.destinationCoordinateKeys.iterator();
            long l2 = l - 600L;

            while (iterator.hasNext()) {
                Long longValue = (Long)iterator.next();
                Teleporter.PortalPosition valueByKey = (Teleporter.PortalPosition)this.destinationCoordinateCache.getValueByKey(longValue);
                if (valueByKey == null || valueByKey.lastUpdateTime < l2) {
                    iterator.remove();
                    this.destinationCoordinateCache.remove(longValue);
                }
            }
        }
    }

    public class PortalPosition extends ChunkCoordinates {
        public long lastUpdateTime;

        public PortalPosition(int n, int n2, int n3, long l) {
            super(n, n2, n3);
            this.lastUpdateTime = l;
        }
    }
}
