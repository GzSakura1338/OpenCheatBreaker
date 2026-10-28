package net.minecraft.client.renderer.culling;

public class ClippingHelper {
    public float[][] frustum = new float[16][16];
    public float[] projectionMatrix = new float[16];
    public float[] modelviewMatrix = new float[16];
    public float[] clippingMatrix = new float[16];

    public boolean isBoxInFrustum(double d, double d2, double d3, double d4, double d5, double d6) {
        for (int i = 0; i < 6; i++) {
            float f = (float)d;
            float f2 = (float)d2;
            float f3 = (float)d3;
            float f4 = (float)d4;
            float f5 = (float)d5;
            float f6 = (float)d6;
            if (this.frustum[i][0] * f + this.frustum[i][1] * f2 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f4 + this.frustum[i][1] * f2 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f + this.frustum[i][1] * f5 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f4 + this.frustum[i][1] * f5 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f + this.frustum[i][1] * f2 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f4 + this.frustum[i][1] * f2 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f + this.frustum[i][1] * f5 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f4 + this.frustum[i][1] * f5 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F) {
                return false;
            }
        }

        return true;
    }

    public boolean isBoxInFrustumFully(double d, double d2, double d3, double d4, double d5, double d6) {
        for (int i = 0; i < 6; i++) {
            float f = (float)d;
            float f2 = (float)d2;
            float f3 = (float)d3;
            float f4 = (float)d4;
            float f5 = (float)d5;
            float f6 = (float)d6;
            if (i < 4) {
                if (this.frustum[i][0] * f + this.frustum[i][1] * f2 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                    || this.frustum[i][0] * f4 + this.frustum[i][1] * f2 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                    || this.frustum[i][0] * f + this.frustum[i][1] * f5 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                    || this.frustum[i][0] * f4 + this.frustum[i][1] * f5 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                    || this.frustum[i][0] * f + this.frustum[i][1] * f2 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F
                    || this.frustum[i][0] * f4 + this.frustum[i][1] * f2 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F
                    || this.frustum[i][0] * f + this.frustum[i][1] * f5 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F
                    || this.frustum[i][0] * f4 + this.frustum[i][1] * f5 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F) {
                    return false;
                }
            } else if (this.frustum[i][0] * f + this.frustum[i][1] * f2 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f4 + this.frustum[i][1] * f2 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f + this.frustum[i][1] * f5 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f4 + this.frustum[i][1] * f5 + this.frustum[i][2] * f3 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f + this.frustum[i][1] * f2 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f4 + this.frustum[i][1] * f2 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f + this.frustum[i][1] * f5 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F
                && this.frustum[i][0] * f4 + this.frustum[i][1] * f5 + this.frustum[i][2] * f6 + this.frustum[i][3] <= 0.0F) {
                return false;
            }
        }

        return true;
    }
}
