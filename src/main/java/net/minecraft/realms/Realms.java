package net.minecraft.realms;

import com.mojang.authlib.GameProfile;
import java.net.Proxy;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.world.WorldSettings.GameType;

public class Realms {
    public static boolean isTouchScreen() {
        return Minecraft.getMinecraft().gameSettings.touchscreen;
    }

    public static Proxy getProxy() {
        return Minecraft.getMinecraft().getProxy();
    }

    public static long currentTimeMillis() {
        return Minecraft.getSystemTime();
    }

    public static String uuidToName(String str) {
        return Minecraft.getMinecraft()
            .getSessionService()
            .fillProfileProperties(new GameProfile(UUID.fromString(str.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")), null), false)
            .getName();
    }

    public static void setScreen(RealmsScreen realmsScreen) throws java.io.IOException {
        Minecraft.getMinecraft().displayGuiScreen(realmsScreen.getProxy());
    }

    public static int getID() {
        return GameType.ADVENTURE.getID();
    }

    public static String getText() {
        Session session = Minecraft.getMinecraft().getSession();
        return session == null ? null : session.getUsername();
    }

    public static String getText2() {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    public static String getText3() {
        Session session = Minecraft.getMinecraft().getSession();
        return session == null ? null : session.getSessionID();
    }

    public static String getText4() {
        return Minecraft.getMinecraft().getSession().getSessionID();
    }

    public static int getID2() {
        return GameType.CREATIVE.getID();
    }

    public static String getText5() {
        return Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
    }

    public static int getID3() {
        return GameType.SURVIVAL.getID();
    }
}
