package optifine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import optifine.xdelta.GDiffPatcher;
import optifine.xdelta.PatchException;

public class Patcher {
    public static final String CONFIG_FILE = "patch.cfg";
    public static final String PREFIX_PATCH = "patch/";
    public static final String SUFFIX_DELTA = ".xdelta";
    public static final String SUFFIX_MD5 = ".md5";

    public static void main(String[] strs) throws Exception {
        if (strs.length < 3) {
            Utils.dbg("Usage: Patcher <base.jar> <diff.jar> <mod.jar>");
        } else {
            File file = new File(strs[0]);
            File file2 = new File(strs[1]);
            File file3 = new File(strs[2]);
            if (file.getName().equals("AUTO")) {
                file = Differ.detectBaseFile(file2);
            }

            if (!file.exists() || !file.isFile()) {
                throw new IOException("Base file not found: " + file);
            }

            if (!file2.exists() || !file2.isFile()) {
                throw new IOException("Diff file not found: " + file3);
            }

            process(file, file2, file3);
        }
    }

    public static void process(File file, File file2, File file3) throws Exception {
        ZipFile zipFile = new ZipFile(file2);
        Map configurationMap = getConfigurationMap(zipFile);
        Pattern[] configurationPatterns = getConfigurationPatterns(configurationMap);
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file3));
        ZipFile zipFile2 = new ZipFile(file);
        ZipResourceProvider zipResourceProvider = new ZipResourceProvider(zipFile2);
        Enumeration entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry nextElement = (ZipEntry)entries.nextElement();
            InputStream inputStream = zipFile.getInputStream(nextElement);
            byte[] readAll = Utils.readAll(inputStream);
            String name = nextElement.getName();
            if (name.startsWith("patch/") && name.endsWith(".xdelta")) {
                name = name.substring("patch/".length());
                name = name.substring(0, name.length() - ".xdelta".length());
                byte[] applyPatch2 = applyPatch(name, readAll, configurationPatterns, configurationMap, zipResourceProvider);
                String str = "patch/" + name + ".md5";
                ZipEntry entry = zipFile.getEntry(str);
                if (entry != null) {
                    byte[] readAll2 = Utils.readAll(zipFile.getInputStream(entry));
                    String str2 = new String(readAll2, "ASCII");
                    byte[] hashMd5 = HashUtils.getHashMd5(applyPatch2);
                    String toHexString = HashUtils.toHexString(hashMd5);
                    if (!str2.equals(toHexString)) {
                        throw new Exception("MD5 not matching, name: " + name + ", saved: " + str2 + ", patched: " + toHexString);
                    }
                }

                ZipEntry zipEntry = new ZipEntry(name);
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(applyPatch2);
                zipOutputStream.closeEntry();
                Utils.dbg("Mod: " + name);
            } else if (!name.startsWith("patch/") || !name.endsWith(".md5")) {
                ZipEntry zipEntry = new ZipEntry(name);
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(readAll);
                zipOutputStream.closeEntry();
                Utils.dbg("Same: " + zipEntry.getName());
            }
        }

        zipOutputStream.close();
    }

    public static byte[] applyPatch(String str, byte[] bytes, Pattern[] patterns, Map<String, String> map, IResourceProvider resourceProvider) throws IOException, PatchException {
        str = Utils.removePrefix(str, "/");
        String patchBase = getPatchBase(str, patterns, map);
        if (patchBase == null) {
            throw new IOException("No patch base, name: " + str + ", patterns: " + Utils.arrayToCommaSeparatedString(patterns));
        }

        InputStream resourceStream = resourceProvider.getResourceStream(patchBase);
        if (resourceStream == null) {
            throw new IOException("Base resource not found: " + patchBase);
        }

        byte[] readAll = Utils.readAll(resourceStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new GDiffPatcher(readAll, byteArrayInputStream, byteArrayOutputStream);
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static Pattern[] getConfigurationPatterns(Map<String, String> map) {
        String[] toArray = map.keySet().toArray(new String[0]);
        Pattern[] patterns = new Pattern[toArray.length];

        for (int i = 0; i < toArray.length; i++) {
            String str = toArray[i];
            patterns[i] = Pattern.compile(str);
        }

        return patterns;
    }

    public static Map<String, String> getConfigurationMap(ZipFile zipFile) throws IOException {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (zipFile == null) {
            return linkedHashMap;
        }

        ZipEntry entry = zipFile.getEntry("patch.cfg");
        if (entry == null) {
            return linkedHashMap;
        }

        InputStream inputStream = zipFile.getInputStream(entry);
        String[] readLines = Utils.readLines(inputStream, "ASCII");
        inputStream.close();

        for (int i = 0; i < readLines.length; i++) {
            String trim = readLines[i].trim();
            if (!trim.startsWith("#") && trim.length() > 0) {
                String[] tokenize = Utils.tokenize(trim, "=");
                if (tokenize.length != 2) {
                    throw new IOException("Invalid patch configuration: " + trim);
                }

                String trim2 = tokenize[0].trim();
                String trim3 = tokenize[1].trim();
                linkedHashMap.put(trim2, trim3);
            }
        }

        return linkedHashMap;
    }

    public static String getPatchBase(String str, Pattern[] patterns, Map<String, String> map) {
        str = Utils.removePrefix(str, "/");

        for (int i = 0; i < patterns.length; i++) {
            Pattern pattern = patterns[i];
            Matcher matcher = pattern.matcher(str);
            if (matcher.matches()) {
                String str2 = (String)map.get(pattern.pattern());
                if (str2 != null && str2.trim().equals("*")) {
                    return str;
                }

                return str2;
            }
        }

        return null;
    }
}
