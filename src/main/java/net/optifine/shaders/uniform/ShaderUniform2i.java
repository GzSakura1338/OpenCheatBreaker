package net.optifine.shaders.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniform2i extends ShaderUniformBase {
   public int[][] programValues;
   public static final int VALUE_UNKNOWN = Integer.MIN_VALUE;

   public ShaderUniform2i(String str) {
      super(str);
      this.resetValue();
   }

   public void setValue(int n, int n2) {
      int program = this.getProgram();
      int[] ints = this.programValues[program];
      if (ints[0] != n || ints[1] != n2) {
         ints[0] = n;
         ints[1] = n2;
         int location = this.getLocation();
         if (location >= 0) {
            ARBShaderObjects.glUniform2iARB(location, n, n2);
            this.checkGLError();
         }
      }
   }

   public int[] getValue() {
      int program = this.getProgram();
      return this.programValues[program];
   }

   @Override
   public void onProgramSet(int n) {
      if (n >= this.programValues.length) {
         int[][] programValues = this.programValues;
         int[][] intses = new int[n + 10][];
         System.arraycopy(programValues, 0, intses, 0, programValues.length);
         this.programValues = intses;
      }

      if (this.programValues[n] == null) {
         this.programValues[n] = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE};
      }
   }

   @Override
   public void resetValue() {
      this.programValues = new int[][]{{Integer.MIN_VALUE, Integer.MIN_VALUE}};
   }
}
