package net.minecraft.client.resources;

import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.resources.AbstractResourcePack;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

public class FolderResourcePack
extends AbstractResourcePack {
    public FolderResourcePack(File resourcePackFileIn) {
        super(resourcePackFileIn);
    }

    @Override
    public InputStream getInputStreamByName(String name) throws IOException {
        return new BufferedInputStream(new FileInputStream(new File(this.resourcePackFile, name)));
    }

    @Override
    public boolean hasResourceName(String name) {
        return new File(this.resourcePackFile, name).isFile();
    }

    @Override
    public Set<String> getResourceDomains() {
        HashSet<String> hashSet = Sets.newHashSet();
        File file = new File(this.resourcePackFile, "assets/");
        if (file.isDirectory()) {
            for (File file2 : file.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY)) {
                String string = FolderResourcePack.getRelativeName(file, file2);
                if (!string.equals(string.toLowerCase())) {
                    this.logNameNotLowercase(string);
                    continue;
                }
                hashSet.add(string.substring(0, string.length() - 1));
            }
        }
        return hashSet;
    }
}
