package net.minecraft.util;

public class Matrix4f extends org.lwjgl.util.vector.Matrix4f {
   public Matrix4f(float[] floats) {
      this.m00 = floats[0];
      this.m01 = floats[1];
      this.m02 = floats[2];
      this.m03 = floats[3];
      this.m10 = floats[4];
      this.m11 = floats[5];
      this.m12 = floats[6];
      this.m13 = floats[7];
      this.m20 = floats[8];
      this.m21 = floats[9];
      this.m22 = floats[10];
      this.m23 = floats[11];
      this.m30 = floats[12];
      this.m31 = floats[13];
      this.m32 = floats[14];
      this.m33 = floats[15];
   }

   public Matrix4f() {
      this.m00 = this.m01 = this.m02 = this.m03 = this.m10 = this.m11 = this.m12 = this.m13 = this.m20 = this.m21 = this.m22 = this.m23 = this.m30 = this.m31 = this.m32 = this.m33 = 0.0F;
   }
}
