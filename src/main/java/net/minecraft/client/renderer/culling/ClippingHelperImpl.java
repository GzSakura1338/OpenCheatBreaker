package net.minecraft.client.renderer.culling;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class ClippingHelperImpl extends ClippingHelper {
   public static ClippingHelperImpl instance = new ClippingHelperImpl();
   public FloatBuffer projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
   public FloatBuffer modelviewMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
   public FloatBuffer floatBuffer = GLAllocation.createDirectFloatBuffer(16);

   public static ClippingHelper getInstance() {
      instance.init();
      return instance;
   }

   public void normalize(float[] floats) {
      float f = MathHelper.sqrt_float(floats[0] * floats[0] + floats[1] * floats[1] + floats[2] * floats[2]);
      floats[0] /= f;
      floats[1] /= f;
      floats[2] /= f;
      floats[3] /= f;
   }

   public void init() {
      ((Buffer)this.projectionMatrixBuffer).clear();
      ((Buffer)this.modelviewMatrixBuffer).clear();
      ((Buffer)this.floatBuffer).clear();
      GlStateManager.getFloat(2983, this.projectionMatrixBuffer);
      GlStateManager.getFloat(2982, this.modelviewMatrixBuffer);
      float[] projectionMatrix = this.projectionMatrix;
      float[] modelviewMatrix = this.modelviewMatrix;
      ((Buffer)this.projectionMatrixBuffer).flip().limit(16);
      this.projectionMatrixBuffer.get(projectionMatrix);
      ((Buffer)this.modelviewMatrixBuffer).flip().limit(16);
      this.modelviewMatrixBuffer.get(modelviewMatrix);
      this.clippingMatrix[0] = modelviewMatrix[0] * projectionMatrix[0] + modelviewMatrix[1] * projectionMatrix[4] + modelviewMatrix[2] * projectionMatrix[8] + modelviewMatrix[3] * projectionMatrix[12];
      this.clippingMatrix[1] = modelviewMatrix[0] * projectionMatrix[1] + modelviewMatrix[1] * projectionMatrix[5] + modelviewMatrix[2] * projectionMatrix[9] + modelviewMatrix[3] * projectionMatrix[13];
      this.clippingMatrix[2] = modelviewMatrix[0] * projectionMatrix[2] + modelviewMatrix[1] * projectionMatrix[6] + modelviewMatrix[2] * projectionMatrix[10] + modelviewMatrix[3] * projectionMatrix[14];
      this.clippingMatrix[3] = modelviewMatrix[0] * projectionMatrix[3] + modelviewMatrix[1] * projectionMatrix[7] + modelviewMatrix[2] * projectionMatrix[11] + modelviewMatrix[3] * projectionMatrix[15];
      this.clippingMatrix[4] = modelviewMatrix[4] * projectionMatrix[0] + modelviewMatrix[5] * projectionMatrix[4] + modelviewMatrix[6] * projectionMatrix[8] + modelviewMatrix[7] * projectionMatrix[12];
      this.clippingMatrix[5] = modelviewMatrix[4] * projectionMatrix[1] + modelviewMatrix[5] * projectionMatrix[5] + modelviewMatrix[6] * projectionMatrix[9] + modelviewMatrix[7] * projectionMatrix[13];
      this.clippingMatrix[6] = modelviewMatrix[4] * projectionMatrix[2] + modelviewMatrix[5] * projectionMatrix[6] + modelviewMatrix[6] * projectionMatrix[10] + modelviewMatrix[7] * projectionMatrix[14];
      this.clippingMatrix[7] = modelviewMatrix[4] * projectionMatrix[3] + modelviewMatrix[5] * projectionMatrix[7] + modelviewMatrix[6] * projectionMatrix[11] + modelviewMatrix[7] * projectionMatrix[15];
      this.clippingMatrix[8] = modelviewMatrix[8] * projectionMatrix[0] + modelviewMatrix[9] * projectionMatrix[4] + modelviewMatrix[10] * projectionMatrix[8] + modelviewMatrix[11] * projectionMatrix[12];
      this.clippingMatrix[9] = modelviewMatrix[8] * projectionMatrix[1] + modelviewMatrix[9] * projectionMatrix[5] + modelviewMatrix[10] * projectionMatrix[9] + modelviewMatrix[11] * projectionMatrix[13];
      this.clippingMatrix[10] = modelviewMatrix[8] * projectionMatrix[2] + modelviewMatrix[9] * projectionMatrix[6] + modelviewMatrix[10] * projectionMatrix[10] + modelviewMatrix[11] * projectionMatrix[14];
      this.clippingMatrix[11] = modelviewMatrix[8] * projectionMatrix[3] + modelviewMatrix[9] * projectionMatrix[7] + modelviewMatrix[10] * projectionMatrix[11] + modelviewMatrix[11] * projectionMatrix[15];
      this.clippingMatrix[12] = modelviewMatrix[12] * projectionMatrix[0] + modelviewMatrix[13] * projectionMatrix[4] + modelviewMatrix[14] * projectionMatrix[8] + modelviewMatrix[15] * projectionMatrix[12];
      this.clippingMatrix[13] = modelviewMatrix[12] * projectionMatrix[1] + modelviewMatrix[13] * projectionMatrix[5] + modelviewMatrix[14] * projectionMatrix[9] + modelviewMatrix[15] * projectionMatrix[13];
      this.clippingMatrix[14] = modelviewMatrix[12] * projectionMatrix[2] + modelviewMatrix[13] * projectionMatrix[6] + modelviewMatrix[14] * projectionMatrix[10] + modelviewMatrix[15] * projectionMatrix[14];
      this.clippingMatrix[15] = modelviewMatrix[12] * projectionMatrix[3] + modelviewMatrix[13] * projectionMatrix[7] + modelviewMatrix[14] * projectionMatrix[11] + modelviewMatrix[15] * projectionMatrix[15];
      float[] floats = this.frustum[0];
      floats[0] = this.clippingMatrix[3] - this.clippingMatrix[0];
      floats[1] = this.clippingMatrix[7] - this.clippingMatrix[4];
      floats[2] = this.clippingMatrix[11] - this.clippingMatrix[8];
      floats[3] = this.clippingMatrix[15] - this.clippingMatrix[12];
      this.normalize(floats);
      float[] floats2 = this.frustum[1];
      floats2[0] = this.clippingMatrix[3] + this.clippingMatrix[0];
      floats2[1] = this.clippingMatrix[7] + this.clippingMatrix[4];
      floats2[2] = this.clippingMatrix[11] + this.clippingMatrix[8];
      floats2[3] = this.clippingMatrix[15] + this.clippingMatrix[12];
      this.normalize(floats2);
      float[] floats3 = this.frustum[2];
      floats3[0] = this.clippingMatrix[3] + this.clippingMatrix[1];
      floats3[1] = this.clippingMatrix[7] + this.clippingMatrix[5];
      floats3[2] = this.clippingMatrix[11] + this.clippingMatrix[9];
      floats3[3] = this.clippingMatrix[15] + this.clippingMatrix[13];
      this.normalize(floats3);
      float[] floats4 = this.frustum[3];
      floats4[0] = this.clippingMatrix[3] - this.clippingMatrix[1];
      floats4[1] = this.clippingMatrix[7] - this.clippingMatrix[5];
      floats4[2] = this.clippingMatrix[11] - this.clippingMatrix[9];
      floats4[3] = this.clippingMatrix[15] - this.clippingMatrix[13];
      this.normalize(floats4);
      float[] floats5 = this.frustum[4];
      floats5[0] = this.clippingMatrix[3] - this.clippingMatrix[2];
      floats5[1] = this.clippingMatrix[7] - this.clippingMatrix[6];
      floats5[2] = this.clippingMatrix[11] - this.clippingMatrix[10];
      floats5[3] = this.clippingMatrix[15] - this.clippingMatrix[14];
      this.normalize(floats5);
      float[] floats6 = this.frustum[5];
      floats6[0] = this.clippingMatrix[3] + this.clippingMatrix[2];
      floats6[1] = this.clippingMatrix[7] + this.clippingMatrix[6];
      floats6[2] = this.clippingMatrix[11] + this.clippingMatrix[10];
      floats6[3] = this.clippingMatrix[15] + this.clippingMatrix[14];
      this.normalize(floats6);
   }
}
