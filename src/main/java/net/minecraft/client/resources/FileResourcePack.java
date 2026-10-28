package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileResourcePack extends AbstractResourcePack implements Closeable {
    public static Splitter entryNameSplitter = Splitter.on('/').omitEmptyStrings().limit(3);
    public ZipFile resourcePackZipFile;

    public FileResourcePack(File file) {
        super(file);
    }

    public ZipFile getResourcePackZipFile() throws IOException {
        if (this.resourcePackZipFile == null) {
            this.resourcePackZipFile = new ZipFile(this.resourcePackFile);
        }

        return this.resourcePackZipFile;
    }

    @Override
    public InputStream getInputStreamByName(String str) throws IOException {
        ZipFile resourcePackZipFile = this.getResourcePackZipFile();
        ZipEntry entry = resourcePackZipFile.getEntry(str);
        if (entry == null) {
            throw new ResourcePackFileNotFoundException(this.resourcePackFile, str);
        } else {
            return resourcePackZipFile.getInputStream(entry);
        }
    }

    @Override
    public boolean hasResourceName(String str) {
        try {
            return this.getResourcePackZipFile().getEntry(str) != null;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Set getResourceDomains() {
        ZipFile zipFile;
        try {
            zipFile = this.getResourcePackZipFile();
        } catch (IOException e) {
            return Collections.emptySet();
        }

        Enumeration entries = zipFile.entries();
        HashSet newHashSet = Sets.newHashSet();

        while (entries.hasMoreElements()) {
            ZipEntry nextElement = (ZipEntry)entries.nextElement();
            String name = nextElement.getName();
            if (name.startsWith("assets/")) {
                ArrayList newArrayList = Lists.newArrayList(entryNameSplitter.split(name));
                if (newArrayList.size() > 1) {
                    String str = (String)newArrayList.get(1);
                    if (!str.equals(str.toLowerCase())) {
                        this.logNameNotLowercase(str);
                    } else {
                        newHashSet.add(str);
                    }
                }
            }
        }

        return newHashSet;
    }

    @Override
    public void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void close() throws IOException {
        if (this.resourcePackZipFile != null) {
            this.resourcePackZipFile.close();
            this.resourcePackZipFile = null;
        }
    }
}
