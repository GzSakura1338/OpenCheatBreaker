package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform1f extends ShaderUniformBase {
   public float[] programValues;
   public static final float VALUE_UNKNOWN = -Float.MAX_VALUE;

   public ShaderUniform1f(String str) {
      super(str);
      this.resetValue();
   }

   public void setValue(float f) {
      int program = this.getProgram();
      float f2 = this.programValues[program];
      if (f != f2) {
         this.programValues[program] = f;
         int location = this.getLocation();
         if (location >= 0) {
            ARBShaderObjects.glUniform1fARB(location, f);
            this.checkGLError();
         }
      }
   }

   public float getValue() {
      int program = this.getProgram();
      return this.programValues[program];
   }

   @Override
   public void onProgramSet(int n) {
      if (n >= this.programValues.length) {
         float[] programValues = this.programValues;
         float[] floats = new float[n + 10];
         System.arraycopy(programValues, 0, floats, 0, programValues.length);

         for (int length = programValues.length; length < floats.length; length++) {
            floats[length] = -Float.MAX_VALUE;
         }

         this.programValues = floats;
      }
   }

   @Override
   public void resetValue() {
      this.programValues = new float[]{-Float.MAX_VALUE};
   }
}
