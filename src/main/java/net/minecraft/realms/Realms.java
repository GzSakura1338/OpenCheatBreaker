package net.minecraft.realms;

import com.cheatbreaker.client.ui.screen.LegacyMainMenuScreen;
import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings.Options;
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
         .fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(str), (String)null), false)
         .getName();
   }

   public static void setScreen(RealmsScreen realmsScreen) {
      Minecraft.getMinecraft().displayGuiScreen(realmsScreen.getProxy());
   }

   public static void setConnectedToRealms(boolean flag) {
      Minecraft.getMinecraft().setConnectedToRealms(flag);
   }

   public static ListenableFuture<Object> downloadResourcePack(String str, String str2) {
      return Minecraft.getMinecraft().getResourcePackRepository().downloadResourcePack(str, str2);
   }

   public static void clearResourcePack() {
      Minecraft.getMinecraft().getResourcePackRepository().clearResourcePack();
   }

   public static boolean getRealmsNotificationsEnabled() {
      return Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(Options.REALMS_NOTIFICATIONS);
   }

   public static boolean inTitleScreen() {
      return Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof LegacyMainMenuScreen;
   }

   public static int getID() {
      return GameType.CREATIVE.getID();
   }

   public static String getText() {
      Session session = Minecraft.getMinecraft().getSession();
      return session == null ? null : session.getSessionID();
   }

   public static int getID2() {
      return GameType.SPECTATOR.getID();
   }

   public static String getText2() {
      return Minecraft.getMinecraft().getSession().getSessionID();
   }

   public static int getID3() {
      return GameType.SURVIVAL.getID();
   }

   public static String getText3() {
      return Minecraft.getMinecraft().getSession().getUsername();
   }

   public static String getText4() {
      Session session = Minecraft.getMinecraft().getSession();
      return session == null ? null : session.getUsername();
   }

   public static String getText5() {
      return Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
   }

   public static String getText6() {
      return Minecraft.getMinecraft().getSession().getPlayerID();
   }

   public static int getID4() {
      return GameType.ADVENTURE.getID();
   }
}
