package net.optifine.shaders;

import java.io.InputStream;

public interface IShaderPack {
   String getName();

   InputStream getResourceAsStream(String str);

   boolean hasDirectory(String str);

   void close();
}
