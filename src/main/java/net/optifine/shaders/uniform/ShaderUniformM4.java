package net.optifine.shaders.uniform;

import java.nio.FloatBuffer;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShaderUniformM4 extends ShaderUniformBase {
   public boolean transpose;
   public FloatBuffer matrix;

   public ShaderUniformM4(String str) {
      super(str);
   }

   public void setValue(boolean flag, FloatBuffer floatBuffer) {
      this.transpose = flag;
      this.matrix = floatBuffer;
      int location = this.getLocation();
      if (location >= 0) {
         ARBShaderObjects.glUniformMatrix4ARB(location, flag, floatBuffer);
         this.checkGLError();
      }
   }

   public float getValue(int n, int n2) {
      if (this.matrix == null) {
         return 0.0F;
      }

      int n3 = this.transpose ? n2 * 4 + n : n * 4 + n2;
      return this.matrix.get(n3);
   }

   @Override
   public void onProgramSet(int n) {
   }

   @Override
   public void resetValue() {
      this.matrix = null;
   }
}
