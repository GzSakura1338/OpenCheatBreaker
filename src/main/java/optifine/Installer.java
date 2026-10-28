package optifine;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import optifine.json.JSONArray;
import optifine.json.JSONObject;
import optifine.json.JSONParser;
import optifine.json.JSONWriter;
import optifine.json.ParseException;

public class Installer {
    public static void main(String[] strs) {
        try {
            File workingDirectory = Utils.getWorkingDirectory();
            doInstall(workingDirectory);
        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null && message.equals("QUIET")) {
                return;
            }

            e.printStackTrace();
            String exceptionStackTrace = Utils.getExceptionStackTrace(e);
            exceptionStackTrace = exceptionStackTrace.replace("\t", "  ");
            JTextArea jtextArea = new JTextArea(exceptionStackTrace);
            jtextArea.setEditable(false);
            Font font = jtextArea.getFont();
            Font font2 = new Font("Monospaced", font.getStyle(), font.getSize());
            jtextArea.setFont(font2);
            JScrollPane jscrollPane = new JScrollPane(jtextArea);
            jscrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog((Component)null, jscrollPane, "Error", 0);
        }
    }

    public static void doInstall(File file) throws Exception {
        Utils.dbg("Dir minecraft: " + file);
        File file2 = new File(file, "libraries");
        Utils.dbg("Dir libraries: " + file2);
        File file3 = new File(file, "versions");
        Utils.dbg("Dir versions: " + file3);
        String optiFineVersion = getOptiFineVersion();
        Utils.dbg("OptiFine Version: " + optiFineVersion);
        String[] tokenize = Utils.tokenize(optiFineVersion, "_");
        String str = tokenize[1];
        Utils.dbg("Minecraft Version: " + str);
        String optiFineEdition = getOptiFineEdition(tokenize);
        Utils.dbg("OptiFine Edition: " + optiFineEdition);
        String str2 = str + "-OptiFine_" + optiFineEdition;
        Utils.dbg("Minecraft_OptiFine Version: " + str2);
        copyMinecraftVersion(str, str2, file3);
        installOptiFineLibrary(str, optiFineEdition, file2, false);
        updateJson(file3, str2, file2, str, optiFineEdition);
        updateLauncherJson(file, str2);
    }

    public static boolean doExtract(File file) throws Exception {
        Utils.dbg("Dir minecraft: " + file);
        File file2 = new File(file, "libraries");
        Utils.dbg("Dir libraries: " + file2);
        File file3 = new File(file, "versions");
        Utils.dbg("Dir versions: " + file3);
        String optiFineVersion = getOptiFineVersion();
        Utils.dbg("OptiFine Version: " + optiFineVersion);
        String[] tokenize = Utils.tokenize(optiFineVersion, "_");
        String str = tokenize[1];
        Utils.dbg("Minecraft Version: " + str);
        String optiFineEdition = getOptiFineEdition(tokenize);
        Utils.dbg("OptiFine Edition: " + optiFineEdition);
        String str2 = str + "-OptiFine_" + optiFineEdition;
        Utils.dbg("Minecraft_OptiFine Version: " + str2);
        return installOptiFineLibrary(str, optiFineEdition, file2, true);
    }

    public static void updateLauncherJson(File file, String str) throws IOException, ParseException {
        File file2 = new File(file, "launcher_profiles.json");
        if (file2.exists() && file2.isFile()) {
            String readFile = Utils.readFile(file2, "UTF-8");
            JSONParser jsonparser = new JSONParser();
            JSONObject parse = (JSONObject)jsonparser.parse(readFile);
            JSONObject jsonobject = (JSONObject)parse.get("profiles");
            JSONObject jsonobject2 = (JSONObject)jsonobject.get("OptiFine");
            if (jsonobject2 == null) {
                jsonobject2 = new JSONObject();
                jsonobject2.put("name", "OptiFine");
                jsonobject.put("OptiFine", jsonobject2);
            }

            jsonobject2.put("lastVersionId", str);
            parse.put("selectedProfile", "OptiFine");
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            JSONWriter jsonwriter = new JSONWriter(outputStreamWriter);
            jsonwriter.writeObject(parse);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } else {
            Utils.showErrorMessage("File not found: " + file2);
            throw new RuntimeException("QUIET");
        }
    }

    public static void updateJson(File file, String str, File file2, String str2, String str3) throws IOException, ParseException {
        File file3 = new File(file, str);
        File file4 = new File(file3, str + ".json");
        String readFile = Utils.readFile(file4, "UTF-8");
        JSONParser jsonparser = new JSONParser();
        JSONObject parse = (JSONObject)jsonparser.parse(readFile);
        parse.put("id", str);
        JSONArray jsonarray = (JSONArray)parse.get("libraries");
        parse.put("inheritsFrom", str2);
        jsonarray = new JSONArray();
        parse.put("libraries", jsonarray);
        String str4 = (String)parse.get("mainClass");
        if (!str4.startsWith("net.minecraft.launchwrapper.")) {
            str4 = "net.minecraft.launchwrapper.Launch";
            parse.put("mainClass", str4);
            String str5 = (String)parse.get("minecraftArguments");
            str5 = str5 + "  --tweakClass optifine.OptiFineTweaker";
            parse.put("minecraftArguments", str5);
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("name", "net.minecraft:launchwrapper:1.12");
            jsonarray.add(0, jsonobject);
        }

        JSONObject jsonobject = new JSONObject();
        jsonobject.put("name", "optifine:OptiFine:" + str2 + "_" + str3);
        jsonarray.add(0, jsonobject);
        FileOutputStream fileOutputStream = new FileOutputStream(file4);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
        JSONWriter jsonwriter = new JSONWriter(outputStreamWriter);
        jsonwriter.writeObject(parse);
        outputStreamWriter.flush();
        outputStreamWriter.close();
    }

    public static String getOptiFineEdition(String[] strs) {
        if (strs.length <= 2) {
            return "";
        }

        String str = "";

        for (int i = 2; i < strs.length; i++) {
            if (i > 2) {
                str = str + "_";
            }

            str = str + strs[i];
        }

        return str;
    }

    public static boolean installOptiFineLibrary(String str, String str2, File file, boolean flag) throws Exception {
        File optiFineZipFile = getOptiFineZipFile();
        File file2 = new File(file, "optifine/OptiFine/" + str + "_" + str2);
        File file3 = new File(file2, "OptiFine-" + str + "_" + str2 + ".jar");
        if (flag) {
            file3 = new File(optiFineZipFile.getParentFile(), "OptiFine_" + str + "_" + str2 + "_MOD.jar");
            JFileChooser jfileChooser = new JFileChooser(file3.getParentFile());
            jfileChooser.setSelectedFile(file3);
            int showSaveDialog = jfileChooser.showSaveDialog((Component)null);
            if (showSaveDialog != 0) {
                return false;
            }

            file3 = jfileChooser.getSelectedFile();
            if (file3.exists()) {
                JOptionPane.setDefaultLocale(Locale.ENGLISH);
                int showConfirmDialog = JOptionPane.showConfirmDialog(
                    (Component)null, "The file \"" + file3.getName() + "\" already exists.\nDo you want to overwrite it?", "Save", 1
                );
                if (showConfirmDialog != 0) {
                    return false;
                }
            }
        }

        if (file3.equals(optiFineZipFile)) {
            JOptionPane.showMessageDialog((Component)null, "Source and target file are the same.", "Save", 0);
            return false;
        }

        Utils.dbg("Source: " + optiFineZipFile);
        Utils.dbg("Dest: " + file3);
        File parentFile = file.getParentFile();
        File file4 = new File(parentFile, "versions/" + str + "/" + str + ".jar");
        if (!file4.exists()) {
            showMessageVersionNotFound(str);
            throw new RuntimeException("QUIET");
        }

        if (file3.getParentFile() != null) {
            file3.getParentFile().mkdirs();
        }

        Patcher.process(file4, optiFineZipFile, file3);
        return true;
    }

    public static File getOptiFineZipFile() throws Exception {
        URL location = Installer.class.getProtectionDomain().getCodeSource().getLocation();
        Utils.dbg("URL: " + location);
        URI toURI = location.toURI();
        return new File(toURI);
    }

    public static boolean isPatchFile() throws Exception {
        File optiFineZipFile = getOptiFineZipFile();
        ZipFile zipFile = new ZipFile(optiFineZipFile);

        try {
            Enumeration entries = zipFile.entries();

            ZipEntry zipEntry;
            do {
                if (!entries.hasMoreElements()) {
                    return false;
                }

                zipEntry = (ZipEntry)entries.nextElement();
            } while (!zipEntry.getName().startsWith("patch/"));
        } finally {
            zipFile.close();
        }

        return true;
    }

    public static void copyMinecraftVersion(String str, String str2, File file) throws IOException {
        File file2 = new File(file, str);
        if (!file2.exists()) {
            showMessageVersionNotFound(str);
            throw new RuntimeException("QUIET");
        }

        File file3 = new File(file, str2);
        file3.mkdirs();
        Utils.dbg("Dir version MC: " + file2);
        Utils.dbg("Dir version MC-OF: " + file3);
        File file4 = new File(file2, str + ".jar");
        File file5 = new File(file3, str2 + ".jar");
        if (!file4.exists()) {
            showMessageVersionNotFound(str);
            throw new RuntimeException("QUIET");
        }

        Utils.copyFile(file4, file5);
        File file6 = new File(file2, str + ".json");
        File file7 = new File(file3, str2 + ".json");
        Utils.copyFile(file6, file7);
    }

    public static void showMessageVersionNotFound(String str) {
        Utils.showErrorMessage("Minecraft version " + str + " not found.\nYou need to start the version " + str + " manually once.");
    }

    public static String getOptiFineVersion() throws IOException {
        InputStream resourceAsStream = Installer.class.getResourceAsStream("/Config.class");
        if (resourceAsStream == null) {
            resourceAsStream = Installer.class.getResourceAsStream("/VersionThread.class");
        }

        return getOptiFineVersion(resourceAsStream);
    }

    public static String getOptiFineVersion(ZipFile zipFile) throws IOException {
        ZipEntry entry = zipFile.getEntry("Config.class");
        if (entry == null) {
            entry = zipFile.getEntry("VersionThread.class");
        }

        if (entry == null) {
            return null;
        }

        InputStream inputStream = zipFile.getInputStream(entry);
        String optiFineVersion = getOptiFineVersion(inputStream);
        inputStream.close();
        return optiFineVersion;
    }

    public static String getOptiFineVersion(InputStream inputStream) throws IOException {
        byte[] readAll = Utils.readAll(inputStream);
        byte[] bytes = "OptiFine_".getBytes("ASCII");
        int find = Utils.find(readAll, bytes);
        if (find < 0) {
            return null;
        }

        int n = find;

        for (find = find; find < readAll.length; find++) {
            byte b = readAll[find];
            if (b < 32 || b > 122) {
                break;
            }
        }

        return new String(readAll, n, find - n, "ASCII");
    }

    public static String getMinecraftVersionFromOfVersion(String str) {
        if (str == null) {
            return null;
        }

        String[] tokenize = Utils.tokenize(str, "_");
        return tokenize.length < 2 ? null : tokenize[1];
    }
}
