package net.optifine.shaders;

import java.io.InputStream;

public class ShaderPackDefault implements IShaderPack {
   @Override
   public void close() {
   }

   @Override
   public InputStream getResourceAsStream(String str) {
      return ShaderPackDefault.class.getResourceAsStream(str);
   }

   @Override
   public String getName() {
      return "(internal)";
   }

   @Override
   public boolean hasDirectory(String str) {
      return false;
   }
}
