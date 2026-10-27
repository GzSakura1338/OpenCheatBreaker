package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform3f extends ShaderUniformBase {
   public float[][] programValues;
   public static final float VALUE_UNKNOWN = -Float.MAX_VALUE;

   public ShaderUniform3f(String str) {
      super(str);
      this.resetValue();
   }

   public void setValue(float f, float f2, float f3) {
      int program = this.getProgram();
      float[] floats = this.programValues[program];
      if (floats[0] != f || floats[1] != f2 || floats[2] != f3) {
         floats[0] = f;
         floats[1] = f2;
         floats[2] = f3;
         int location = this.getLocation();
         if (location >= 0) {
            ARBShaderObjects.glUniform3fARB(location, f, f2, f3);
            this.checkGLError();
         }
      }
   }

   public float[] getValue() {
      int program = this.getProgram();
      return this.programValues[program];
   }

   @Override
   public void onProgramSet(int n) {
      if (n >= this.programValues.length) {
         float[][] programValues = this.programValues;
         float[][] floatses = new float[n + 10][];
         System.arraycopy(programValues, 0, floatses, 0, programValues.length);
         this.programValues = floatses;
      }

      if (this.programValues[n] == null) {
         this.programValues[n] = new float[]{-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
      }
   }

   @Override
   public void resetValue() {
      this.programValues = new float[][]{{-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE}};
   }
}
