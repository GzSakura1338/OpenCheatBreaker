package net.minecraft.world.pathfinder;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public abstract class NodeProcessor {
   public IBlockAccess blockaccess;
   public IntHashMap<PathPoint> pointMap = new IntHashMap<>();
   public int entitySizeX;
   public int entitySizeY;
   public int entitySizeZ;

   public void initProcessor(IBlockAccess iblockaccessIn, Entity entityIn) {
      this.blockaccess = iblockaccessIn;
      this.pointMap.clearMap();
      this.entitySizeX = MathHelper.floor_float(entityIn.width + 1.0F);
      this.entitySizeY = MathHelper.floor_float(entityIn.height + 1.0F);
      this.entitySizeZ = MathHelper.floor_float(entityIn.width + 1.0F);
   }

   public void postProcess() {
   }

   public PathPoint openPoint(int x, int y, int z) {
      int makeHash = PathPoint.makeHash(x, y, z);
      PathPoint lookup = this.pointMap.lookup(makeHash);
      if (lookup == null) {
         lookup = new PathPoint(x, y, z);
         this.pointMap.addKey(makeHash, lookup);
      }

      return lookup;
   }

   public abstract PathPoint getPathPointTo(Entity entity);

   public abstract PathPoint getPathPointToCoords(Entity entity, double d, double d2, double d3);

   public abstract int findPathOptions(PathPoint[] pathPoints, Entity entity, PathPoint pathPoint, PathPoint pathPoint2, float f);
}
