package net.optifine.render;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.AxisAlignedBB;

public class AabbFrame extends AxisAlignedBB {
   public int frameCount = -1;
   public boolean inFrustumFully = false;

   public AabbFrame(double d, double d2, double d3, double d4, double d5, double d6) {
      super(d, d2, d3, d4, d5, d6);
   }

   public boolean isBoundingBoxInFrustumFully(ICamera camera, int n) {
      if (this.frameCount != n) {
         this.inFrustumFully = camera instanceof Frustum
            ? ((Frustum)camera).isBoxInFrustumFully(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ)
            : false;
         this.frameCount = n;
      }

      return this.inFrustumFully;
   }
}
