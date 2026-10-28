package shadersmod.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.optifine.StrUtils;

public class ShaderPackZip implements IShaderPack {
    public File packFile;
    public ZipFile packZipFile;

    public ShaderPackZip(String str, File file) {
        this.packFile = file;
        this.packZipFile = null;
    }

    @Override
    public void close() {
        if (this.packZipFile != null) {
            try {
                this.packZipFile.close();
            } catch (Exception e) {
            }

            this.packZipFile = null;
        }
    }

    @Override
    public InputStream getResourceAsStream(String str) {
        try {
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
            }

            String removePrefix = StrUtils.removePrefix(str, "/");
            ZipEntry entry = this.packZipFile.getEntry(removePrefix);
            return entry == null ? null : this.packZipFile.getInputStream(entry);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean hasDirectory(String str) {
        try {
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
            }

            String removePrefix = StrUtils.removePrefix(str, "/");
            ZipEntry entry = this.packZipFile.getEntry(removePrefix);
            return entry != null;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return this.packFile.getName();
    }
}
