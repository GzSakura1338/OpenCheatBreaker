package shadersmod.uniform;

import java.util.HashMap;
import java.util.Map;

public class LegacyUniforms {
    public static Map<String, Number> map = new HashMap<>();
    public static Map<String, String> mapKeysX = new HashMap<>();
    public static Map<String, String> mapKeysY = new HashMap<>();
    public static Map<String, String> mapKeysZ = new HashMap<>();
    public static Map<String, String> mapKeysR = new HashMap<>();
    public static Map<String, String> mapKeysG = new HashMap<>();
    public static Map<String, String> mapKeysB = new HashMap<>();

    public static Number getNumber(String str) {
        return map.get(str);
    }

    public static void setFloat(String str, float f) {
        map.put(str, f);
    }

    public static void setInt(String str, int n) {
        map.put(str, n);
    }

    public static void setIntXy(String str, int n, int n2) {
        setInt(getCompoundKey(str, "x", mapKeysX), n);
        setInt(getCompoundKey(str, "y", mapKeysY), n2);
    }

    public static void setFloatXyz(String str, float f, float f2, float f3) {
        setFloat(getCompoundKey(str, "x", mapKeysX), f);
        setFloat(getCompoundKey(str, "y", mapKeysY), f2);
        setFloat(getCompoundKey(str, "z", mapKeysZ), f3);
    }

    public static void setFloatRgb(String str, float f, float f2, float f3) {
        setFloat(getCompoundKey(str, "r", mapKeysR), f);
        setFloat(getCompoundKey(str, "g", mapKeysG), f2);
        setFloat(getCompoundKey(str, "b", mapKeysB), f3);
    }

    public static String getCompoundKey(String str, String str2, Map<String, String> map) {
        String str3 = (String)map.get(str);
        if (str3 != null) {
            return str3;
        }

        str3 = str + "." + str2;
        map.put(str, str3);
        return str3;
    }

    public static void reset() {
        map.clear();
    }
}
