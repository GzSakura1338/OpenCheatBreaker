package shadersmod.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SMCLog {
    public static Logger LOGGER = LogManager.getLogger();
    public static final String PREFIX = "[Shaders] ";

    public static void severe(String str) {
        LOGGER.error("[Shaders] " + str);
    }

    public static void warning(String str) {
        LOGGER.warn("[Shaders] " + str);
    }

    public static void info(String str) {
        LOGGER.info("[Shaders] " + str);
    }

    public static void fine(String str) {
        LOGGER.debug("[Shaders] " + str);
    }

    public static void severe(String str, Object... objs) {
        String format = String.format(str, objs);
        LOGGER.error("[Shaders] " + format);
    }

    public static void warning(String str, Object... objs) {
        String format = String.format(str, objs);
        LOGGER.warn("[Shaders] " + format);
    }

    public static void info(String str, Object... objs) {
        String format = String.format(str, objs);
        LOGGER.info("[Shaders] " + format);
    }

    public static void fine(String str, Object... objs) {
        String format = String.format(str, objs);
        LOGGER.debug("[Shaders] " + format);
    }
}
