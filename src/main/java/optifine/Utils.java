package optifine;

import com.cheatbreaker.client.util.OperatingSystem;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

public class Utils {
    public static final String MAC_OS_HOME_PREFIX = "Library/Application Support";
    public static char[] hexTable = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static File getWorkingDirectory() {
        return getWorkingDirectory("minecraft");
    }

    public static File getWorkingDirectory(String str) {
        String property = System.getProperty("user.home", ".");
        File file = null;
        switch (getOperatingSystem()) {
            case LINUX:
            case SOLARIS:
                file = new File(property, '.' + str + '/');
                break;
            case WINDOWS:
                String getenv = System.getenv("APPDATA");
                if (getenv != null) {
                    file = new File(getenv, "." + str + '/');
                } else {
                    file = new File(property, '.' + str + '/');
                }
                break;
            case MACOS:
                file = new File(property, "Library/Application Support/" + str);
                break;
            default:
                file = new File(property, str + '/');
        }

        if (!file.exists() && !file.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + file);
        } else {
            return file;
        }
    }

    public static int find(byte[] bytes, byte[] bytes2) {
        return find(bytes, 0, bytes2);
    }

    public static int find(byte[] bytes, int n, byte[] bytes2) {
        for (int i = n; i < bytes.length - bytes2.length; i++) {
            boolean flag = true;

            for (int j = 0; j < bytes2.length; j++) {
                if (bytes2[j] != bytes[i + j]) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                return i;
            }
        }

        return -1;
    }

    public static byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];

        while (true) {
            int read = inputStream.read(bytes);
            if (read < 0) {
                inputStream.close();
                return byteArrayOutputStream.toByteArray();
            }

            byteArrayOutputStream.write(bytes, 0, read);
        }
    }

    public static void dbg(String str) {
        System.out.println(str);
    }

    public static String[] tokenize(String str, String str2) {
        ArrayList list = new ArrayList();
        StringTokenizer stringTokenizer = new StringTokenizer(str, str2);

        while (stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();
            list.add(nextToken);
        }

        return (String[])list.toArray(new String[list.size()]);
    }

    public static String getExceptionStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.close();

        try {
            stringWriter.close();
        } catch (IOException e) {
        }

        return stringWriter.getBuffer().toString();
    }

    public static void copyFile(File file, File file2) throws IOException {
        if (!file.getCanonicalPath().equals(file2.getCanonicalPath())) {
            FileInputStream fileInputStream = new FileInputStream(file);
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            copyAll(fileInputStream, fileOutputStream);
            fileOutputStream.flush();
            fileInputStream.close();
            fileOutputStream.close();
        }
    }

    public static void copyAll(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bytes = new byte[1024];

        while (true) {
            int read = inputStream.read(bytes);
            if (read < 0) {
                return;
            }

            outputStream.write(bytes, 0, read);
        }
    }

    public static void showMessage(String str) {
        JOptionPane.showMessageDialog((Component)null, str, "OptiFine", 1);
    }

    public static void showErrorMessage(String str) {
        JOptionPane.showMessageDialog((Component)null, str, "Error", 0);
    }

    public static String readFile(File file) throws IOException {
        return readFile(file, "ASCII");
    }

    public static String readFile(File file, String str) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        return readText(fileInputStream, str);
    }

    public static String readText(InputStream inputStream, String str) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, str);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer();

        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
                return sb.toString();
            }

            sb.append(readLine);
            sb.append("\n");
        }
    }

    public static String[] readLines(InputStream inputStream, String str) throws IOException {
        String readText2 = readText(inputStream, str);
        return tokenize(readText2, "\n\r");
    }

    public static void centerWindow(Component component, Component component2) {
        if (component != null) {
            Rectangle bounds = component.getBounds();
            Rectangle rectangle;
            if (component2 != null && component2.isVisible()) {
                rectangle = component2.getBounds();
            } else {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                rectangle = new Rectangle(0, 0, screenSize.width, screenSize.height);
            }

            int n = rectangle.x + (rectangle.width - bounds.width) / 2;
            int n2 = rectangle.y + (rectangle.height - bounds.height) / 2;
            if (n < 0) {
                n = 0;
            }

            if (n2 < 0) {
                n2 = 0;
            }

            component.setBounds(n, n2, bounds.width, bounds.height);
        }
    }

    public static String byteArrayToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            sb.append(hexTable[b >> 4 & 15]);
            sb.append(hexTable[b & 15]);
        }

        return sb.toString();
    }

    public static String arrayToCommaSeparatedString(Object[] objs) {
        if (objs == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < objs.length; i++) {
            Object obj = objs[i];
            if (i > 0) {
                sb.append(", ");
            }

            if (obj == null) {
                sb.append("null");
            } else if (!obj.getClass().isArray()) {
                sb.append(objs[i]);
            } else {
                sb.append("[");
                if (obj instanceof Object[]) {
                    Object[] objs2 = (Object[])obj;
                    sb.append(arrayToCommaSeparatedString(objs2));
                } else {
                    for (int j = 0; j < Array.getLength(obj); j++) {
                        if (j > 0) {
                            sb.append(", ");
                        }

                        sb.append(Array.get(obj, j));
                    }
                }

                sb.append("]");
            }
        }

        return sb.toString();
    }

    public static String removePrefix(String str, String str2) {
        if (str != null && str2 != null) {
            if (str.startsWith(str2)) {
                str = str.substring(str2.length());
            }

            return str;
        } else {
            return str;
        }
    }

    public static String ensurePrefix(String str, String str2) {
        if (str != null && str2 != null) {
            if (!str.startsWith(str2)) {
                str = str2 + str;
            }

            return str;
        } else {
            return str;
        }
    }

    public static boolean equals(Object obj, Object obj2) {
        return obj == obj2 ? true : (obj == null ? false : obj.equals(obj2));
    }

    public static OperatingSystem getOperatingSystem() {
        String toLowerCase = System.getProperty("os.name").toLowerCase();
        return toLowerCase.contains("win")
            ? OperatingSystem.WINDOWS
            : (
                toLowerCase.contains("mac")
                    ? OperatingSystem.MACOS
                    : (
                        toLowerCase.contains("solaris")
                            ? OperatingSystem.SOLARIS
                            : (
                                toLowerCase.contains("sunos")
                                    ? OperatingSystem.SOLARIS
                                    : (toLowerCase.contains("linux") ? OperatingSystem.LINUX : (toLowerCase.contains("unix") ? OperatingSystem.LINUX : OperatingSystem.UNKNOWN))
                            )
                    )
            );
    }

    public enum OS {
        LINUX,
        MACOS,
        SOLARIS,
        UNKNOWN,
        WINDOWS;
    }
}
