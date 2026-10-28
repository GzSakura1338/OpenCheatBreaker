package optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourceProvider implements IResourceProvider {
    public ZipFile zipFile = null;

    public ZipResourceProvider(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    @Override
    public InputStream getResourceStream(String str) throws IOException {
        str = Utils.removePrefix(str, "/");
        ZipEntry entry = this.zipFile.getEntry(str);
        return entry == null ? null : this.zipFile.getInputStream(entry);
    }
}
