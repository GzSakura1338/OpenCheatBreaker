package optifine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import optifine.xdelta.Delta;
import optifine.xdelta.DeltaException;
import optifine.xdelta.GDiffWriter;

public class Differ {
    public static void main(String[] strs) throws Exception {
        if (strs.length < 3) {
            Utils.dbg("Usage: Differ <base.jar> <mod.jar> <diff.jar>");
        } else {
            File file = new File(strs[0]);
            File file2 = new File(strs[1]);
            File file3 = new File(strs[2]);
            if (file.getName().equals("AUTO")) {
                file = detectBaseFile(file2);
            }

            if (!file.exists() || !file.isFile()) {
                throw new IOException("Base file not found: " + file);
            }

            if (!file2.exists() || !file2.isFile()) {
                throw new IOException("Mod file not found: " + file2);
            }

            process(file, file2, file3);
        }
    }

    public static void process(File file, File file2, File file3) throws IOException, NoSuchAlgorithmException, DeltaException {
        ZipFile zipFile = new ZipFile(file2);
        Map configurationMap = Patcher.getConfigurationMap(zipFile);
        Pattern[] configurationPatterns = Patcher.getConfigurationPatterns(configurationMap);
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file3));
        ZipFile zipFile2 = new ZipFile(file);
        Enumeration entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry nextElement = (ZipEntry)entries.nextElement();
            InputStream inputStream = zipFile.getInputStream(nextElement);
            byte[] readAll = Utils.readAll(inputStream);
            String name = nextElement.getName();
            byte[] makeDiff2 = makeDiff(name, readAll, configurationPatterns, configurationMap, zipFile2);
            if (makeDiff2 != readAll) {
                ZipEntry zipEntry = new ZipEntry("patch/" + name + ".xdelta");
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(makeDiff2);
                zipOutputStream.closeEntry();
                Utils.dbg("Delta: " + zipEntry.getName());
                byte[] hashMd5 = HashUtils.getHashMd5(readAll);
                String toHexString = HashUtils.toHexString(hashMd5);
                byte[] bytes = toHexString.getBytes("ASCII");
                ZipEntry zipEntry2 = new ZipEntry("patch/" + name + ".md5");
                zipOutputStream.putNextEntry(zipEntry2);
                zipOutputStream.write(bytes);
                zipOutputStream.closeEntry();
            } else {
                ZipEntry zipEntry = new ZipEntry(name);
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(readAll);
                zipOutputStream.closeEntry();
                Utils.dbg("Same: " + zipEntry.getName());
            }
        }

        zipOutputStream.close();
    }

    public static byte[] makeDiff(String str, byte[] bytes, Pattern[] patterns, Map<String, String> map, ZipFile zipFile) throws IOException, DeltaException {
        String patchBase = Patcher.getPatchBase(str, patterns, map);
        if (patchBase == null) {
            return bytes;
        }

        ZipEntry entry = zipFile.getEntry(patchBase);
        if (entry == null) {
            throw new IOException("Base entry not found: " + patchBase + " in: " + zipFile.getName());
        }

        InputStream inputStream = zipFile.getInputStream(entry);
        byte[] readAll = Utils.readAll(inputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GDiffWriter gdiffWriter = new GDiffWriter(new DataOutputStream(byteArrayOutputStream));
        Delta.computeDelta(readAll, byteArrayInputStream, bytes.length, gdiffWriter);
        gdiffWriter.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static File detectBaseFile(File file) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        String optiFineVersion = Installer.getOptiFineVersion(zipFile);
        if (optiFineVersion == null) {
            throw new IOException("Version not found");
        }

        zipFile.close();
        String minecraftVersionFromOfVersion = Installer.getMinecraftVersionFromOfVersion(optiFineVersion);
        if (minecraftVersionFromOfVersion == null) {
            throw new IOException("Version not found");
        }

        File workingDirectory = Utils.getWorkingDirectory();
        return new File(workingDirectory, "versions/" + minecraftVersionFromOfVersion + "/" + minecraftVersionFromOfVersion + ".jar");
    }
}
