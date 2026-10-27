package net.minecraft.client.resources;

import java.io.File;
import java.io.FileNotFoundException;

public class ResourcePackFileNotFoundException extends FileNotFoundException {
   public ResourcePackFileNotFoundException(File resourcePack, String str) {
      super(String.format("'%s' in ResourcePack '%s'", str, resourcePack));
   }
}
