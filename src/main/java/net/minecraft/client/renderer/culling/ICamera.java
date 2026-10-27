package net.minecraft.client.renderer.culling;

import net.minecraft.util.AxisAlignedBB;

public interface ICamera {
   boolean isBoundingBoxInFrustum(AxisAlignedBB axisAlignedBB);

   void setPosition(double d, double d2, double d3);
}
