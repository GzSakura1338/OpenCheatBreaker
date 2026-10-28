import com.cheatbreaker.client.util.OperatingSystemDetector;
import java.util.Arrays;
import net.minecraft.client.main.Main;
import optifine.Utils.OS;

public class Start {
    public static <T> T[] getObjectArray(T[] values, T[] values2) {
        Object[] copyOf = Arrays.copyOf(values, values.length + values2.length);
        System.arraycopy(values2, 0, copyOf, values.length, values2.length);
        return (T[])copyOf;
    }

    public static void main(String[] strs) {
        if (OperatingSystemDetector.getOS() == OS.MACOS) {
            System.setProperty("apple.awt.application.name", "CheatBreaker 1.7.10");
        }

        Main.main(getObjectArray(new String[]{"--version", "CheatBreaker", "--assetIndex", "1.7.10", "--userProperties", "{}"}, strs));
    }
}
