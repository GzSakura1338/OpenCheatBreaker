package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class PathFinder {
    public Path path = new Path();
    public IntHashMap pointMap = new IntHashMap();
    public PathPoint[] pathOptions = new PathPoint[32];
    public IBlockAccess worldMap;
    public boolean canPassOpenWoodenDoors;
    public boolean canPassClosedWoodenDoor;
    public boolean avoidsWater;
    public boolean canEntitySwim;

    public PathFinder(IBlockAccess blockAccess, boolean flag, boolean flag2, boolean flag3, boolean flag4) {
        this.worldMap = blockAccess;
        this.canPassOpenWoodenDoors = flag;
        this.canPassClosedWoodenDoor = flag2;
        this.avoidsWater = flag3;
        this.canEntitySwim = flag4;
    }

    public PathEntity createEntityPathTo(Entity entity, Entity entity2, float f) {
        return this.createEntityPathTo(entity, entity2.posX, entity2.boundingBox.minY, entity2.posZ, f);
    }

    public PathEntity createEntityPathTo(Entity entity, int n, int n2, int n3, float f) {
        return this.createEntityPathTo(entity, n + 0.5F, n2 + 0.5F, n3 + 0.5F, f);
    }

    public PathEntity createEntityPathTo(Entity entity, double d, double d2, double d3, float f) {
        this.path.clearPath();
        this.pointMap.clearMap();
        boolean avoidsWater = this.avoidsWater;
        int n = MathHelper.floor_double(entity.boundingBox.minY + 0.5);
        if (this.canEntitySwim && entity.isInWater()) {
            n = (int)entity.boundingBox.minY;
            Block block = this.worldMap.getBlock(MathHelper.floor_double(entity.posX), n, MathHelper.floor_double(entity.posZ));

            while (block == Blocks.flowing_water || block == Blocks.water) {
                block = this.worldMap.getBlock(MathHelper.floor_double(entity.posX), ++n, MathHelper.floor_double(entity.posZ));
            }

            avoidsWater = this.avoidsWater;
            this.avoidsWater = false;
        } else {
            n = MathHelper.floor_double(entity.boundingBox.minY + 0.5);
        }

        PathPoint openPoint = this.openPoint(MathHelper.floor_double(entity.boundingBox.minX), n, MathHelper.floor_double(entity.boundingBox.minZ));
        PathPoint openPoint2 = this.openPoint(
            MathHelper.floor_double(d - entity.width / 2.0F), MathHelper.floor_double(d2), MathHelper.floor_double(d3 - entity.width / 2.0F)
        );
        PathPoint pathPoint = new PathPoint(
            MathHelper.floor_float(entity.width + 1.0F), MathHelper.floor_float(entity.height + 1.0F), MathHelper.floor_float(entity.width + 1.0F)
        );
        PathEntity addToPath = this.addToPath(entity, openPoint, openPoint2, pathPoint, f);
        this.avoidsWater = avoidsWater;
        return addToPath;
    }

    public PathEntity addToPath(Entity entity, PathPoint pathPoint, PathPoint pathPoint2, PathPoint pathPoint3, float f) {
        pathPoint.totalPathDistance = 0.0F;
        pathPoint.distanceToNext = pathPoint.distanceToSquared(pathPoint2);
        pathPoint.distanceToTarget = pathPoint.distanceToNext;
        this.path.clearPath();
        this.path.addPoint(pathPoint);
        PathPoint pathPoint4 = pathPoint;

        while (!this.path.isPathEmpty()) {
            PathPoint dequeue = this.path.dequeue();
            if (dequeue.equals(pathPoint2)) {
                return this.createEntityPath(pathPoint, pathPoint2);
            }

            if (dequeue.distanceToSquared(pathPoint2) < pathPoint4.distanceToSquared(pathPoint2)) {
                pathPoint4 = dequeue;
            }

            dequeue.visited = true;
            int findPathOptions = this.findPathOptions(entity, dequeue, pathPoint3, pathPoint2, f);

            for (int i = 0; i < findPathOptions; i++) {
                PathPoint pathPoint5 = this.pathOptions[i];
                float f2 = dequeue.totalPathDistance + dequeue.distanceToSquared(pathPoint5);
                if (!pathPoint5.isAssigned() || f2 < pathPoint5.totalPathDistance) {
                    pathPoint5.previous = dequeue;
                    pathPoint5.totalPathDistance = f2;
                    pathPoint5.distanceToNext = pathPoint5.distanceToSquared(pathPoint2);
                    if (pathPoint5.isAssigned()) {
                        this.path.changeDistance(pathPoint5, pathPoint5.totalPathDistance + pathPoint5.distanceToNext);
                    } else {
                        pathPoint5.distanceToTarget = pathPoint5.totalPathDistance + pathPoint5.distanceToNext;
                        this.path.addPoint(pathPoint5);
                    }
                }
            }
        }

        return pathPoint4 == pathPoint ? null : this.createEntityPath(pathPoint, pathPoint4);
    }

    public int findPathOptions(Entity entity, PathPoint pathPoint, PathPoint pathPoint2, PathPoint pathPoint3, float f) {
        int n = 0;
        byte b = 0;
        if (this.canEntityStandAt(entity, pathPoint.xCoord, pathPoint.yCoord + 1, pathPoint.zCoord, pathPoint2) == 1) {
            b = 1;
        }

        PathPoint safePoint = this.getSafePoint(entity, pathPoint.xCoord, pathPoint.yCoord, pathPoint.zCoord + 1, pathPoint2, b);
        PathPoint safePoint2 = this.getSafePoint(entity, pathPoint.xCoord - 1, pathPoint.yCoord, pathPoint.zCoord, pathPoint2, b);
        PathPoint safePoint3 = this.getSafePoint(entity, pathPoint.xCoord + 1, pathPoint.yCoord, pathPoint.zCoord, pathPoint2, b);
        PathPoint safePoint4 = this.getSafePoint(entity, pathPoint.xCoord, pathPoint.yCoord, pathPoint.zCoord - 1, pathPoint2, b);
        if (safePoint != null && !safePoint.visited && safePoint.distanceTo(pathPoint3) < f) {
            this.pathOptions[n++] = safePoint;
        }

        if (safePoint2 != null && !safePoint2.visited && safePoint2.distanceTo(pathPoint3) < f) {
            this.pathOptions[n++] = safePoint2;
        }

        if (safePoint3 != null && !safePoint3.visited && safePoint3.distanceTo(pathPoint3) < f) {
            this.pathOptions[n++] = safePoint3;
        }

        if (safePoint4 != null && !safePoint4.visited && safePoint4.distanceTo(pathPoint3) < f) {
            this.pathOptions[n++] = safePoint4;
        }

        return n;
    }

    public PathPoint getSafePoint(Entity entity, int n, int n2, int n3, PathPoint pathPoint, int n4) {
        PathPoint pathPoint2 = null;
        int canEntityStandAt = this.canEntityStandAt(entity, n, n2, n3, pathPoint);
        if (canEntityStandAt == 2) {
            return this.openPoint(n, n2, n3);
        }

        if (canEntityStandAt == 1) {
            pathPoint2 = this.openPoint(n, n2, n3);
        }

        if (pathPoint2 == null && n4 > 0 && canEntityStandAt != -3 && canEntityStandAt != -4 && this.canEntityStandAt(entity, n, n2 + n4, n3, pathPoint) == 1) {
            pathPoint2 = this.openPoint(n, n2 + n4, n3);
            n2 += n4;
        }

        if (pathPoint2 != null) {
            int n5 = 0;
            int n6 = 0;

            while (n2 > 0) {
                n6 = this.canEntityStandAt(entity, n, n2 - 1, n3, pathPoint);
                if (this.avoidsWater && n6 == -1) {
                    return null;
                }

                if (n6 != 1) {
                    break;
                }

                if (n5++ >= entity.getMaxFallHeight()) {
                    return null;
                }

                if (--n2 > 0) {
                    pathPoint2 = this.openPoint(n, n2, n3);
                }
            }

            if (n6 == -2) {
                return null;
            }
        }

        return pathPoint2;
    }

    public PathPoint openPoint(int n, int n2, int n3) {
        int makeHash = PathPoint.makeHash(n, n2, n3);
        PathPoint lookup = (PathPoint)this.pointMap.lookup(makeHash);
        if (lookup == null) {
            lookup = new PathPoint(n, n2, n3);
            this.pointMap.addKey(makeHash, lookup);
        }

        return lookup;
    }

    public int canEntityStandAt(Entity entity, int n, int n2, int n3, PathPoint pathPoint) {
        return canEntityStandAt(entity, n, n2, n3, pathPoint, this.avoidsWater, this.canPassClosedWoodenDoor, this.canPassOpenWoodenDoors);
    }

    public static int canEntityStandAt(Entity entity, int n, int n2, int n3, PathPoint pathPoint, boolean flag, boolean flag2, boolean flag3) {
        boolean flag4 = false;

        for (int i = n; i < n + pathPoint.xCoord; i++) {
            for (int j = n2; j < n2 + pathPoint.yCoord; j++) {
                for (int k = n3; k < n3 + pathPoint.zCoord; k++) {
                    Block block = entity.worldObj.getBlock(i, j, k);
                    if (block.getMaterial() != Material.air) {
                        if (block == Blocks.trapdoor) {
                            flag4 = true;
                        } else if (block != Blocks.flowing_water && block != Blocks.water) {
                            if (!flag3 && block == Blocks.wooden_door) {
                                return 0;
                            }
                        } else {
                            if (flag) {
                                return -1;
                            }

                            flag4 = true;
                        }

                        int renderType = block.getRenderType();
                        if (entity.worldObj.getBlock(i, j, k).getRenderType() == 9) {
                            int n4 = MathHelper.floor_double(entity.posX);
                            int n5 = MathHelper.floor_double(entity.posY);
                            int n6 = MathHelper.floor_double(entity.posZ);
                            if (entity.worldObj.getBlock(n4, n5, n6).getRenderType() != 9
                                && entity.worldObj.getBlock(n4, n5 - 1, n6).getRenderType() != 9) {
                                return -3;
                            }
                        } else if (!block.isPassable(entity.worldObj, i, j, k) && (!flag2 || block != Blocks.wooden_door)) {
                            if (renderType == 11 || block == Blocks.fence_gate || renderType == 32) {
                                return -3;
                            }

                            if (block == Blocks.trapdoor) {
                                return -4;
                            }

                            Material material = block.getMaterial();
                            if (material != Material.lava) {
                                return 0;
                            }

                            if (!entity.handleLavaMovement()) {
                                return -2;
                            }
                        }
                    }
                }
            }
        }

        return flag4 ? 2 : 1;
    }

    public PathEntity createEntityPath(PathPoint pathPoint, PathPoint pathPoint2) {
        int n = 1;

        for (PathPoint pathPoint3 = pathPoint2; pathPoint3.previous != null; pathPoint3 = pathPoint3.previous) {
            n++;
        }

        PathPoint[] pathPoints = new PathPoint[n];
        PathPoint pathPoint3 = pathPoint2;

        for (pathPoints[--n] = pathPoint2; pathPoint3.previous != null; pathPoints[--n] = pathPoint3) {
            pathPoint3 = pathPoint3.previous;
        }

        return new PathEntity(pathPoints);
    }
}
