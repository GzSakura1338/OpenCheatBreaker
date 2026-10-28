package shadersmod.client;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import net.optifine.Config;
import shadersmod.common.SMCLog;

public class ShadersBuiltIn {
    public static Reader getShaderReader(String str) {
        return str.endsWith("/deferred_last.vsh")
            ? getCompositeShaderReader(true, true)
            : (
                str.endsWith("/composite_last.vsh")
                    ? getCompositeShaderReader(false, true)
                    : (
                        str.endsWith("/deferred_last.fsh")
                            ? getCompositeShaderReader(true, false)
                            : (str.endsWith("/composite_last.fsh") ? getCompositeShaderReader(false, false) : null)
                    )
            );
    }

    public static Reader getCompositeShaderReader(boolean flag, boolean flag2) {
        if (!hasDeferredPrograms() && !hasSkipClear()) {
            return null;
        }

        int[] lastFlipBuffers = getLastFlipBuffers(flag);
        if (lastFlipBuffers == null) {
            return null;
        }

        if (!flag2) {
            String str = flag ? "deferred" : "composite";
            SMCLog.info("flipped buffers after " + str + ": " + Config.arrayToString(lastFlipBuffers));
        }

        String str;
        if (flag2) {
            str = getCompositeVertexShader(lastFlipBuffers);
        } else {
            str = getCompositeFragmentShader(lastFlipBuffers);
        }

        return new StringReader(str);
    }

    public static Reader getCompositeFragmentShaderReader(boolean flag) {
        if (!hasDeferredPrograms() && !hasSkipClear()) {
            return null;
        }

        int[] lastFlipBuffers = getLastFlipBuffers(flag);
        if (lastFlipBuffers == null) {
            return null;
        }

        String compositeFragmentShader = getCompositeFragmentShader(lastFlipBuffers);
        return new StringReader(compositeFragmentShader);
    }

    public static boolean hasDeferredPrograms() {
        for (int i = 33; i < 41; i++) {
            if (Shaders.programsID[i] != 0) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasSkipClear() {
        for (int i = 0; i < Shaders.gbuffersClear.length; i++) {
            if (!Shaders.gbuffersClear[i]) {
                return true;
            }
        }

        return false;
    }

    public static String getCompositeVertexShader(int[] ints) {
        ArrayList list = new ArrayList();
        list.add("#version 120                        ");
        list.add("varying vec2 texcoord;              ");
        list.add("void main()                         ");
        list.add("{                                   ");
        list.add("  gl_Position = ftransform();       ");
        list.add("  texcoord = gl_MultiTexCoord0.xy;  ");
        list.add("}                                   ");
        return Config.listToString(list, "\n");
    }

    public static String getCompositeFragmentShader(int[] ints) {
        ArrayList list = new ArrayList();
        String arrayToString = Config.arrayToString(ints, "");
        list.add("#version 120                                           ");

        for (int i = 0; i < ints.length; i++) {
            list.add("uniform sampler2D colortex" + ints[i] + ";        ");
        }

        list.add("varying vec2 texcoord;                                 ");
        list.add("/* DRAWBUFFERS:" + arrayToString + " */                  ");
        list.add("void main()                                            ");
        list.add("{                                                      ");

        for (int i = 0; i < ints.length; i++) {
            list.add("  gl_FragData[" + i + "] = texture2D(colortex" + ints[i] + ", texcoord);     ");
        }

        list.add("}                                                      ");
        return Config.listToString(list, "\n");
    }

    public static int[] getLastFlipBuffers(boolean flag) {
        return flag ? getLastFlipBuffers(33, 8) : getLastFlipBuffers(21, 8);
    }

    public static int[] getLastFlipBuffers(int n, int n2) {
        ArrayList list = new ArrayList();
        boolean[] flags = new boolean[8];

        for (int i = n; i < n + n2; i++) {
            if (Shaders.programsID[i] != 0) {
                boolean[] programTogglesTexture = getProgramTogglesTexture(i);

                for (int j = 0; j < programTogglesTexture.length; j++) {
                    boolean flag = programTogglesTexture[j];
                    if (flag) {
                        flags[j] = !flags[j];
                    }
                }
            }
        }

        for (int i = 0; i < flags.length; i++) {
            boolean flag = flags[i];
            if (flag) {
                list.add(new Integer(i));
            }
        }

        if (list.isEmpty()) {
            return null;
        }

        Integer[] toArray = (Integer[])list.toArray(new Integer[list.size()]);
        return Config.toPrimitive(toArray);
    }

    public static boolean[] getProgramTogglesTexture(int n) {
        boolean[] flags = new boolean[8];
        String str = Shaders.programsDrawBufSettings[n];
        if (str == null) {
            return flags;
        }

        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            int n2 = charAt - '0';
            if (n2 >= 0 && n2 < flags.length) {
                flags[n2] = true;
            }
        }

        return flags;
    }
}
