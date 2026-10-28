package net.minecraft.client.resources;

import java.io.File;
import java.io.FileNotFoundException;

public class ResourcePackFileNotFoundException extends FileNotFoundException {
    public ResourcePackFileNotFoundException(File file, String str) {
        super(String.format("'%s' in ResourcePack '%s'", str, file));
    }
}
