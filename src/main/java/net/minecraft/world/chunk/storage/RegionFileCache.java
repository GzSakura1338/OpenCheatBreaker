package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegionFileCache {
    public static Map regionsByFilename = new HashMap();

    public static synchronized RegionFile createOrLoadRegionFile(File file, int n, int n2) {
        File file2 = new File(file, "region");
        File file3 = new File(file2, "r." + (n >> 5) + "." + (n2 >> 5) + ".mca");
        RegionFile regionFile = (RegionFile)regionsByFilename.get(file3);
        if (regionFile != null) {
            return regionFile;
        }

        if (!file2.exists()) {
            file2.mkdirs();
        }

        if (regionsByFilename.size() >= 256) {
            clearRegionFileReferences();
        }

        RegionFile regionFile2 = new RegionFile(file3);
        regionsByFilename.put(file3, regionFile2);
        return regionFile2;
    }

    public static synchronized void clearRegionFileReferences() {
        for (RegionFile regionFile : (Iterable<RegionFile>)regionsByFilename.values()) {
            try {
                if (regionFile != null) {
                    regionFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        regionsByFilename.clear();
    }

    public static DataInputStream getChunkInputStream(File file, int n, int n2) {
        RegionFile createOrLoadRegionFile2 = createOrLoadRegionFile(file, n, n2);
        return createOrLoadRegionFile2.getChunkDataInputStream(n & 31, n2 & 31);
    }

    public static DataOutputStream getChunkOutputStream(File file, int n, int n2) {
        RegionFile createOrLoadRegionFile2 = createOrLoadRegionFile(file, n, n2);
        return createOrLoadRegionFile2.getChunkDataOutputStream(n & 31, n2 & 31);
    }
}
