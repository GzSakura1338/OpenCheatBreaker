package net.minecraft.client.renderer.culling;

import net.minecraft.util.AxisAlignedBB;

public class Frustrum implements ICamera {
    public ClippingHelper clippingHelper = ClippingHelperImpl.getInstance();
    public double xPosition;
    public double yPosition;
    public double zPosition;

    public Frustrum(ClippingHelper clippingHelper) {
        this.clippingHelper = clippingHelper;
    }

    public Frustrum() {
    }

    @Override
    public void setPosition(double d, double d2, double d3) {
        this.xPosition = d;
        this.yPosition = d2;
        this.zPosition = d3;
    }

    public boolean isBoxInFrustum(double d, double d2, double d3, double d4, double d5, double d6) {
        return this.clippingHelper
            .isBoxInFrustum(
                d - this.xPosition, d2 - this.yPosition, d3 - this.zPosition, d4 - this.xPosition, d5 - this.yPosition, d6 - this.zPosition
            );
    }

    @Override
    public boolean isBoundingBoxInFrustum(AxisAlignedBB axisAlignedBB) {
        return this.isBoxInFrustum(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
    }

    public boolean isBoxInFrustumFully(double d, double d2, double d3, double d4, double d5, double d6) {
        return this.clippingHelper
            .isBoxInFrustumFully(
                d - this.xPosition, d2 - this.yPosition, d3 - this.zPosition, d4 - this.xPosition, d5 - this.yPosition, d6 - this.zPosition
            );
    }

    @Override
    public boolean isBoundingBoxInFrustumFully(AxisAlignedBB axisAlignedBB) {
        return this.isBoxInFrustumFully(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
    }
}
