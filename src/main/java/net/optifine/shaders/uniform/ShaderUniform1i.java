package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform1i extends ShaderUniformBase {
   public int[] programValues;
   public static final int VALUE_UNKNOWN = Integer.MIN_VALUE;

   public ShaderUniform1i(String str) {
      super(str);
      this.resetValue();
   }

   public void setValue(int n) {
      int program = this.getProgram();
      int n2 = this.programValues[program];
      if (n != n2) {
         this.programValues[program] = n;
         int location = this.getLocation();
         if (location >= 0) {
            ARBShaderObjects.glUniform1iARB(location, n);
            this.checkGLError();
         }
      }
   }

   public int getValue() {
      int program = this.getProgram();
      return this.programValues[program];
   }

   @Override
   public void onProgramSet(int n) {
      if (n >= this.programValues.length) {
         int[] programValues = this.programValues;
         int[] ints = new int[n + 10];
         System.arraycopy(programValues, 0, ints, 0, programValues.length);

         for (int length = programValues.length; length < ints.length; length++) {
            ints[length] = Integer.MIN_VALUE;
         }

         this.programValues = ints;
      }
   }

   @Override
   public void resetValue() {
      this.programValues = new int[]{Integer.MIN_VALUE};
   }
}
