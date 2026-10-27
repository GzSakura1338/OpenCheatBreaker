package net.optifine.shaders;

import java.io.InputStream;

public class ShaderPackNone implements IShaderPack {
   @Override
   public void close() {
   }

   @Override
   public InputStream getResourceAsStream(String str) {
      return null;
   }

   @Override
   public boolean hasDirectory(String str) {
      return false;
   }

   @Override
   public String getName() {
      return "OFF";
   }
}
