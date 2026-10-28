package net.minecraft.client.renderer.culling;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ClippingHelperImpl extends ClippingHelper {
    public static ClippingHelperImpl instance = new ClippingHelperImpl();
    public FloatBuffer projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
    public FloatBuffer modelviewMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
    public FloatBuffer floatBuffer = GLAllocation.createDirectFloatBuffer(16);

    public static ClippingHelper getInstance() {
        instance.init();
        return instance;
    }

    public void normalize(float[][] floatses, int n) {
        float f = MathHelper.sqrt_float(floatses[n][0] * floatses[n][0] + floatses[n][1] * floatses[n][1] + floatses[n][2] * floatses[n][2]);
        floatses[n][0] = floatses[n][0] / f;
        floatses[n][1] = floatses[n][1] / f;
        floatses[n][2] = floatses[n][2] / f;
        floatses[n][3] = floatses[n][3] / f;
    }

    public void init() {
        ((Buffer)this.projectionMatrixBuffer).clear();
        ((Buffer)this.modelviewMatrixBuffer).clear();
        ((Buffer)this.floatBuffer).clear();
        GL11.glGetFloat(2983, this.projectionMatrixBuffer);
        GL11.glGetFloat(2982, this.modelviewMatrixBuffer);
        ((Buffer)this.projectionMatrixBuffer).flip().limit(16);
        this.projectionMatrixBuffer.get(this.projectionMatrix);
        ((Buffer)this.modelviewMatrixBuffer).flip().limit(16);
        this.modelviewMatrixBuffer.get(this.modelviewMatrix);
        this.clippingMatrix[0] = this.modelviewMatrix[0] * this.projectionMatrix[0]
            + this.modelviewMatrix[1] * this.projectionMatrix[4]
            + this.modelviewMatrix[2] * this.projectionMatrix[8]
            + this.modelviewMatrix[3] * this.projectionMatrix[12];
        this.clippingMatrix[1] = this.modelviewMatrix[0] * this.projectionMatrix[1]
            + this.modelviewMatrix[1] * this.projectionMatrix[5]
            + this.modelviewMatrix[2] * this.projectionMatrix[9]
            + this.modelviewMatrix[3] * this.projectionMatrix[13];
        this.clippingMatrix[2] = this.modelviewMatrix[0] * this.projectionMatrix[2]
            + this.modelviewMatrix[1] * this.projectionMatrix[6]
            + this.modelviewMatrix[2] * this.projectionMatrix[10]
            + this.modelviewMatrix[3] * this.projectionMatrix[14];
        this.clippingMatrix[3] = this.modelviewMatrix[0] * this.projectionMatrix[3]
            + this.modelviewMatrix[1] * this.projectionMatrix[7]
            + this.modelviewMatrix[2] * this.projectionMatrix[11]
            + this.modelviewMatrix[3] * this.projectionMatrix[15];
        this.clippingMatrix[4] = this.modelviewMatrix[4] * this.projectionMatrix[0]
            + this.modelviewMatrix[5] * this.projectionMatrix[4]
            + this.modelviewMatrix[6] * this.projectionMatrix[8]
            + this.modelviewMatrix[7] * this.projectionMatrix[12];
        this.clippingMatrix[5] = this.modelviewMatrix[4] * this.projectionMatrix[1]
            + this.modelviewMatrix[5] * this.projectionMatrix[5]
            + this.modelviewMatrix[6] * this.projectionMatrix[9]
            + this.modelviewMatrix[7] * this.projectionMatrix[13];
        this.clippingMatrix[6] = this.modelviewMatrix[4] * this.projectionMatrix[2]
            + this.modelviewMatrix[5] * this.projectionMatrix[6]
            + this.modelviewMatrix[6] * this.projectionMatrix[10]
            + this.modelviewMatrix[7] * this.projectionMatrix[14];
        this.clippingMatrix[7] = this.modelviewMatrix[4] * this.projectionMatrix[3]
            + this.modelviewMatrix[5] * this.projectionMatrix[7]
            + this.modelviewMatrix[6] * this.projectionMatrix[11]
            + this.modelviewMatrix[7] * this.projectionMatrix[15];
        this.clippingMatrix[8] = this.modelviewMatrix[8] * this.projectionMatrix[0]
            + this.modelviewMatrix[9] * this.projectionMatrix[4]
            + this.modelviewMatrix[10] * this.projectionMatrix[8]
            + this.modelviewMatrix[11] * this.projectionMatrix[12];
        this.clippingMatrix[9] = this.modelviewMatrix[8] * this.projectionMatrix[1]
            + this.modelviewMatrix[9] * this.projectionMatrix[5]
            + this.modelviewMatrix[10] * this.projectionMatrix[9]
            + this.modelviewMatrix[11] * this.projectionMatrix[13];
        this.clippingMatrix[10] = this.modelviewMatrix[8] * this.projectionMatrix[2]
            + this.modelviewMatrix[9] * this.projectionMatrix[6]
            + this.modelviewMatrix[10] * this.projectionMatrix[10]
            + this.modelviewMatrix[11] * this.projectionMatrix[14];
        this.clippingMatrix[11] = this.modelviewMatrix[8] * this.projectionMatrix[3]
            + this.modelviewMatrix[9] * this.projectionMatrix[7]
            + this.modelviewMatrix[10] * this.projectionMatrix[11]
            + this.modelviewMatrix[11] * this.projectionMatrix[15];
        this.clippingMatrix[12] = this.modelviewMatrix[12] * this.projectionMatrix[0]
            + this.modelviewMatrix[13] * this.projectionMatrix[4]
            + this.modelviewMatrix[14] * this.projectionMatrix[8]
            + this.modelviewMatrix[15] * this.projectionMatrix[12];
        this.clippingMatrix[13] = this.modelviewMatrix[12] * this.projectionMatrix[1]
            + this.modelviewMatrix[13] * this.projectionMatrix[5]
            + this.modelviewMatrix[14] * this.projectionMatrix[9]
            + this.modelviewMatrix[15] * this.projectionMatrix[13];
        this.clippingMatrix[14] = this.modelviewMatrix[12] * this.projectionMatrix[2]
            + this.modelviewMatrix[13] * this.projectionMatrix[6]
            + this.modelviewMatrix[14] * this.projectionMatrix[10]
            + this.modelviewMatrix[15] * this.projectionMatrix[14];
        this.clippingMatrix[15] = this.modelviewMatrix[12] * this.projectionMatrix[3]
            + this.modelviewMatrix[13] * this.projectionMatrix[7]
            + this.modelviewMatrix[14] * this.projectionMatrix[11]
            + this.modelviewMatrix[15] * this.projectionMatrix[15];
        this.frustum[0][0] = this.clippingMatrix[3] - this.clippingMatrix[0];
        this.frustum[0][1] = this.clippingMatrix[7] - this.clippingMatrix[4];
        this.frustum[0][2] = this.clippingMatrix[11] - this.clippingMatrix[8];
        this.frustum[0][3] = this.clippingMatrix[15] - this.clippingMatrix[12];
        this.normalize(this.frustum, 0);
        this.frustum[1][0] = this.clippingMatrix[3] + this.clippingMatrix[0];
        this.frustum[1][1] = this.clippingMatrix[7] + this.clippingMatrix[4];
        this.frustum[1][2] = this.clippingMatrix[11] + this.clippingMatrix[8];
        this.frustum[1][3] = this.clippingMatrix[15] + this.clippingMatrix[12];
        this.normalize(this.frustum, 1);
        this.frustum[2][0] = this.clippingMatrix[3] + this.clippingMatrix[1];
        this.frustum[2][1] = this.clippingMatrix[7] + this.clippingMatrix[5];
        this.frustum[2][2] = this.clippingMatrix[11] + this.clippingMatrix[9];
        this.frustum[2][3] = this.clippingMatrix[15] + this.clippingMatrix[13];
        this.normalize(this.frustum, 2);
        this.frustum[3][0] = this.clippingMatrix[3] - this.clippingMatrix[1];
        this.frustum[3][1] = this.clippingMatrix[7] - this.clippingMatrix[5];
        this.frustum[3][2] = this.clippingMatrix[11] - this.clippingMatrix[9];
        this.frustum[3][3] = this.clippingMatrix[15] - this.clippingMatrix[13];
        this.normalize(this.frustum, 3);
        this.frustum[4][0] = this.clippingMatrix[3] - this.clippingMatrix[2];
        this.frustum[4][1] = this.clippingMatrix[7] - this.clippingMatrix[6];
        this.frustum[4][2] = this.clippingMatrix[11] - this.clippingMatrix[10];
        this.frustum[4][3] = this.clippingMatrix[15] - this.clippingMatrix[14];
        this.normalize(this.frustum, 4);
        this.frustum[5][0] = this.clippingMatrix[3] + this.clippingMatrix[2];
        this.frustum[5][1] = this.clippingMatrix[7] + this.clippingMatrix[6];
        this.frustum[5][2] = this.clippingMatrix[11] + this.clippingMatrix[10];
        this.frustum[5][3] = this.clippingMatrix[15] + this.clippingMatrix[14];
        this.normalize(this.frustum, 5);
    }
}
