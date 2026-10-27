package com.cheatbreaker.client.util;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class MinecraftHelper {
   public static Minecraft mc = Minecraft.getMinecraft();

   public static double getPlayerZ() {
      return mc.thePlayer.posZ;
   }

   public static int getPlayerY() {
      return (int)mc.thePlayer.posY;
   }

   public static double getPlayerX() {
      return mc.thePlayer.posX;
   }

   public static File getGameDirectory() {
      return mc.mcDataDir;
   }

   public static World getWorld() {
      return mc.theWorld;
   }

   public static Minecraft getMc() {
      return mc;
   }

   public static float getPlayerYaw() {
      return mc.thePlayer.rotationYaw;
   }

   public static int getBlockZ() {
      return (int)(mc.thePlayer.posZ < 0.0 ? mc.thePlayer.posZ - 1.0 : mc.thePlayer.posZ);
   }

   public static int getBlockX() {
      return (int)(mc.thePlayer.posX < 0.0 ? mc.thePlayer.posX - 1.0 : mc.thePlayer.posX);
   }
}
