package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class GLAllocation {
   public static synchronized int generateDisplayLists(int range) {
      int glGenLists = GL11.glGenLists(range);
      if (glGenLists == 0) {
         int glGetError = GL11.glGetError();
         String str = "No error code reported";
         if (glGetError != 0) {
            str = GLU.gluErrorString(glGetError);
         }

         throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + range + ", GL error (" + glGetError + "): " + str);
      } else {
         return glGenLists;
      }
   }

   public static synchronized void deleteDisplayLists(int list, int range) {
      GL11.glDeleteLists(list, range);
   }

   public static synchronized void deleteDisplayLists(int list) {
      GL11.glDeleteLists(list, 1);
   }

   public static synchronized ByteBuffer createDirectByteBuffer(int capacity) {
      return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
   }

   public static IntBuffer createDirectIntBuffer(int capacity) {
      return createDirectByteBuffer(capacity << 2).asIntBuffer();
   }

   public static FloatBuffer createDirectFloatBuffer(int capacity) {
      return createDirectByteBuffer(capacity << 2).asFloatBuffer();
   }
}
