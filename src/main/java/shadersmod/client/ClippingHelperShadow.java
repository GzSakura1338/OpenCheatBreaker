package shadersmod.client;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.util.MathHelper;

public class ClippingHelperShadow extends ClippingHelper {
    public static ClippingHelperShadow instance = new ClippingHelperShadow();
    public float[] frustumTest = new float[6];
    public float[][] shadowClipPlanes = new float[10][4];
    public int shadowClipPlaneCount;
    public float[] matInvMP = new float[16];
    public float[] vecIntersection = new float[4];

    @Override
    public boolean isBoxInFrustum(double d, double d2, double d3, double d4, double d5, double d6) {
        for (int i = 0; i < this.shadowClipPlaneCount; i++) {
            float[] floats = this.shadowClipPlanes[i];
            if (this.dot4(floats, d, d2, d3) <= 0.0
                && this.dot4(floats, d4, d2, d3) <= 0.0
                && this.dot4(floats, d, d5, d3) <= 0.0
                && this.dot4(floats, d4, d5, d3) <= 0.0
                && this.dot4(floats, d, d2, d6) <= 0.0
                && this.dot4(floats, d4, d2, d6) <= 0.0
                && this.dot4(floats, d, d5, d6) <= 0.0
                && this.dot4(floats, d4, d5, d6) <= 0.0) {
                return false;
            }
        }

        return true;
    }

    public double dot4(float[] floats, double d, double d2, double d3) {
        return floats[0] * d + floats[1] * d2 + floats[2] * d3 + floats[3];
    }

    public double dot3(float[] floats, float[] floats2) {
        return (double)floats[0] * floats2[0] + (double)floats[1] * floats2[1] + (double)floats[2] * floats2[2];
    }

    public static ClippingHelper getInstance() {
        instance.init();
        return instance;
    }

    public void normalizePlane(float[] floats) {
        float f = MathHelper.sqrt_float(floats[0] * floats[0] + floats[1] * floats[1] + floats[2] * floats[2]);
        floats[0] /= f;
        floats[1] /= f;
        floats[2] /= f;
        floats[3] /= f;
    }

    public void normalize3(float[] floats) {
        float f = MathHelper.sqrt_float(floats[0] * floats[0] + floats[1] * floats[1] + floats[2] * floats[2]);
        if (f == 0.0F) {
            f = 1.0F;
        }

        floats[0] /= f;
        floats[1] /= f;
        floats[2] /= f;
    }

    public void assignPlane(float[] floats, float f, float f2, float f3, float f4) {
        float sqrt = (float)Math.sqrt(f * f + f2 * f2 + f3 * f3);
        floats[0] = f / sqrt;
        floats[1] = f2 / sqrt;
        floats[2] = f3 / sqrt;
        floats[3] = f4 / sqrt;
    }

    public void copyPlane(float[] floats, float[] floats2) {
        floats[0] = floats2[0];
        floats[1] = floats2[1];
        floats[2] = floats2[2];
        floats[3] = floats2[3];
    }

    public void cross3(float[] floats, float[] floats2, float[] floats3) {
        floats[0] = floats2[1] * floats3[2] - floats2[2] * floats3[1];
        floats[1] = floats2[2] * floats3[0] - floats2[0] * floats3[2];
        floats[2] = floats2[0] * floats3[1] - floats2[1] * floats3[0];
    }

    public void addShadowClipPlane(float[] floats) {
        this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], floats);
    }

    public float length(float f, float f2, float f3) {
        return (float)Math.sqrt(f * f + f2 * f2 + f3 * f3);
    }

    public float distance(float f, float f2, float f3, float f4, float f5, float f6) {
        return this.length(f - f4, f2 - f5, f3 - f6);
    }

    public void makeShadowPlane(float[] floats, float[] floats2, float[] floats3, float[] floats4) {
        this.cross3(this.vecIntersection, floats2, floats3);
        this.cross3(floats, this.vecIntersection, floats4);
        this.normalize3(floats);
        float dot3 = (float)this.dot3(floats2, floats3);
        float dot32 = (float)this.dot3(floats, floats3);
        float distance = this.distance(floats[0], floats[1], floats[2], floats3[0] * dot32, floats3[1] * dot32, floats3[2] * dot32);
        float distance2 = this.distance(floats2[0], floats2[1], floats2[2], floats3[0] * dot3, floats3[1] * dot3, floats3[2] * dot3);
        float f = distance / distance2;
        float dot33 = (float)this.dot3(floats, floats2);
        float distance3 = this.distance(floats[0], floats[1], floats[2], floats2[0] * dot33, floats2[1] * dot33, floats2[2] * dot33);
        float distance4 = this.distance(floats3[0], floats3[1], floats3[2], floats2[0] * dot3, floats2[1] * dot3, floats2[2] * dot3);
        float f2 = distance3 / distance4;
        floats[3] = floats2[3] * f + floats3[3] * f2;
    }

    public void init() {
        float[] projectionMatrix = this.projectionMatrix;
        float[] modelviewMatrix = this.modelviewMatrix;
        float[] clippingMatrix = this.clippingMatrix;
        System.arraycopy(Shaders.faProjection, 0, projectionMatrix, 0, 16);
        System.arraycopy(Shaders.faModelView, 0, modelviewMatrix, 0, 16);
        SMath.multiplyMat4xMat4(clippingMatrix, modelviewMatrix, projectionMatrix);
        this.assignPlane(this.frustum[0], clippingMatrix[3] - clippingMatrix[0], clippingMatrix[7] - clippingMatrix[4], clippingMatrix[11] - clippingMatrix[8], clippingMatrix[15] - clippingMatrix[12]);
        this.assignPlane(this.frustum[1], clippingMatrix[3] + clippingMatrix[0], clippingMatrix[7] + clippingMatrix[4], clippingMatrix[11] + clippingMatrix[8], clippingMatrix[15] + clippingMatrix[12]);
        this.assignPlane(this.frustum[2], clippingMatrix[3] + clippingMatrix[1], clippingMatrix[7] + clippingMatrix[5], clippingMatrix[11] + clippingMatrix[9], clippingMatrix[15] + clippingMatrix[13]);
        this.assignPlane(this.frustum[3], clippingMatrix[3] - clippingMatrix[1], clippingMatrix[7] - clippingMatrix[5], clippingMatrix[11] - clippingMatrix[9], clippingMatrix[15] - clippingMatrix[13]);
        this.assignPlane(this.frustum[4], clippingMatrix[3] - clippingMatrix[2], clippingMatrix[7] - clippingMatrix[6], clippingMatrix[11] - clippingMatrix[10], clippingMatrix[15] - clippingMatrix[14]);
        this.assignPlane(this.frustum[5], clippingMatrix[3] + clippingMatrix[2], clippingMatrix[7] + clippingMatrix[6], clippingMatrix[11] + clippingMatrix[10], clippingMatrix[15] + clippingMatrix[14]);
        float[] shadowLightPositionVector = Shaders.shadowLightPositionVector;
        float dot3 = (float)this.dot3(this.frustum[0], shadowLightPositionVector);
        float dot32 = (float)this.dot3(this.frustum[1], shadowLightPositionVector);
        float dot33 = (float)this.dot3(this.frustum[2], shadowLightPositionVector);
        float dot34 = (float)this.dot3(this.frustum[3], shadowLightPositionVector);
        float dot35 = (float)this.dot3(this.frustum[4], shadowLightPositionVector);
        float dot36 = (float)this.dot3(this.frustum[5], shadowLightPositionVector);
        this.shadowClipPlaneCount = 0;
        if (dot3 >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0]);
            if (dot3 > 0.0F) {
                if (dot33 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[2], shadowLightPositionVector);
                }

                if (dot34 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[3], shadowLightPositionVector);
                }

                if (dot35 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[4], shadowLightPositionVector);
                }

                if (dot36 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[5], shadowLightPositionVector);
                }
            }
        }

        if (dot32 >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1]);
            if (dot32 > 0.0F) {
                if (dot33 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[2], shadowLightPositionVector);
                }

                if (dot34 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[3], shadowLightPositionVector);
                }

                if (dot35 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[4], shadowLightPositionVector);
                }

                if (dot36 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[5], shadowLightPositionVector);
                }
            }
        }

        if (dot33 >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2]);
            if (dot33 > 0.0F) {
                if (dot3 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[0], shadowLightPositionVector);
                }

                if (dot32 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[1], shadowLightPositionVector);
                }

                if (dot35 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[4], shadowLightPositionVector);
                }

                if (dot36 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[5], shadowLightPositionVector);
                }
            }
        }

        if (dot34 >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3]);
            if (dot34 > 0.0F) {
                if (dot3 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[0], shadowLightPositionVector);
                }

                if (dot32 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[1], shadowLightPositionVector);
                }

                if (dot35 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[4], shadowLightPositionVector);
                }

                if (dot36 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[5], shadowLightPositionVector);
                }
            }
        }

        if (dot35 >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4]);
            if (dot35 > 0.0F) {
                if (dot3 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[0], shadowLightPositionVector);
                }

                if (dot32 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[1], shadowLightPositionVector);
                }

                if (dot33 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[2], shadowLightPositionVector);
                }

                if (dot34 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[3], shadowLightPositionVector);
                }
            }
        }

        if (dot36 >= 0.0F) {
            this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5]);
            if (dot36 > 0.0F) {
                if (dot3 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[0], shadowLightPositionVector);
                }

                if (dot32 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[1], shadowLightPositionVector);
                }

                if (dot33 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[2], shadowLightPositionVector);
                }

                if (dot34 < 0.0F) {
                    this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[3], shadowLightPositionVector);
                }
            }
        }
    }
}
