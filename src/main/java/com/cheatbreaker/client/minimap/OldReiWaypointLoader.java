package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.chat.ChatLogger;
import com.cheatbreaker.client.util.DirectoryHelper;
import com.cheatbreaker.client.util.MinecraftHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

public class OldReiWaypointLoader implements IWaypointManager {
   public MinimapSettings minimapSettings = null;
   public ArrayList<Waypoint> waypoints = new ArrayList<>();
   public ArrayList<Waypoint> deathpoints = new ArrayList<>();
   public File waypointFile;
   public String subworldName = "";
   public ArrayList<Waypoint> visibleDeathpoints;
   public String subworldHash = "";
   public String currentSubworld = "";
   public EntityWaypointContainer entityWaypointContainer = null;
   public Object lock = new Object();
   public IVoxelMap voxelMap;

   public String escapeText(String str) {
      str = str.replace(":", "~colon~");
      return str.replace(",", "~comma~");
   }

   public void addDimensionToFile(File file, int n) {
      try {
         if (file.exists()) {
            TreeSet treeSet = new TreeSet();
            treeSet.add(n);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String str;
            while ((str = bufferedReader.readLine()) != null) {
               String[] split = str.split(":");
               if (split.length == 6) {
                  int parseInt = Integer.parseInt(split[5], 16);
                  float f = (parseInt >> 16 & 0xFF) / 255.0F;
                  float f2 = (parseInt >> 8 & 0xFF) / 255.0F;
                  float f3 = (parseInt >> 0 & 0xFF) / 255.0F;
                  int parseInt2 = Integer.parseInt(split[1]);
                  int parseInt3 = Integer.parseInt(split[3]);
                  if (n == -1) {
                     parseInt2 *= 8;
                     parseInt3 *= 8;
                  }

                  this.addLoadedWaypoint(split[0], parseInt2, parseInt3, Integer.parseInt(split[2]), Boolean.parseBoolean(split[4]), f, f2, f3, "", "", treeSet);
               }
            }

            bufferedReader.close();
         }
      } catch (Exception e) {
         ChatLogger.handleString(EnumChatFormatting.YELLOW + "Error Loading Old Rei Waypoints");
         System.err.println("waypoint load error: " + e.getLocalizedMessage());
      }
   }

   @Override
   public String getCurrentSubworld() {
      return this.currentSubworld;
   }

   public void loadWaypointsFor(String str) {
      this.currentSubworld = str;
      synchronized (this.lock) {
         String escapeText = this.escapeText(str);

         for (Waypoint waypoint : this.waypoints) {
            waypoint.inWorld = escapeText == "" || waypoint.world == "" || escapeText.equals(waypoint.world);
         }
      }
   }

   public void collectDeathpoints() {
      this.deathpoints = new ArrayList<>();

      for (Waypoint waypoint : this.waypoints) {
         if (waypoint.getY() <= 0) {
            this.deathpoints.add(waypoint);
         }
      }
   }

   @Override
   public void onWorldChange() {
      synchronized (this.lock) {
         boolean flag = false;
         this.waypoints = new ArrayList<>();
         String currentWorldName = this.voxelMap.getMap().getCurrentWorldName();
         if (currentWorldName.endsWith(":25565")) {
            int lastIndexOf = currentWorldName.lastIndexOf(":");
            if (lastIndexOf != -1) {
               currentWorldName = currentWorldName.substring(0, lastIndexOf);
            }
         }

         currentWorldName = this.sanitizeFileName(currentWorldName);
         String sanitizeFileName = this.sanitizeFileName(this.voxelMap.getMap().getCurrentWorldName());
         String currentWorldName2 = this.voxelMap.getMap().getCurrentWorldName();
         int lastIndexOf = currentWorldName2.lastIndexOf(":");
         if (lastIndexOf != -1) {
            currentWorldName2 = currentWorldName2.substring(0, lastIndexOf);
         }

         currentWorldName2 = this.sanitizeFileName(currentWorldName2);
         String sanitizeFileName2 = this.sanitizeFileName(currentWorldName2 + "~colon~25565");
         flag = this.importLegacyWaypoints(currentWorldName);
         if (!flag) {
            flag = this.loadZanWaypoints(currentWorldName2, sanitizeFileName2, sanitizeFileName);
         }

         if (!flag) {
            flag = this.loadReiWaypoints(currentWorldName2);
         }

         if (!flag) {
            ChatLogger.handleString("§ENo waypoints exist for this world/server.");
         } else {
            this.collectDeathpoints();
         }
      }

      this.loadWaypointsFor(this.currentSubworld);
   }

   @Override
   public void updateEntityContainer() {
      List loadedEntityList = Minecraft.getMinecraft().theWorld.getLoadedEntityList();
      synchronized (loadedEntityList) {
         int indexOf = loadedEntityList.indexOf(this.entityWaypointContainer);
         if (indexOf == -1) {
            this.removeEntityContainer();
            this.createEntityContainer();
         } else {
            if (indexOf != loadedEntityList.size() - 1) {
               Collections.swap(loadedEntityList, indexOf, loadedEntityList.size() - 1);
            }
         }
      }
   }

   @Override
   public void handleDeath() {
      TreeSet treeSet = new TreeSet();

      for (Waypoint waypoint : this.waypoints) {
         if (waypoint.name.equals("Latest Death")) {
            waypoint.name = "Previous Death";
         }

         if (waypoint.name.startsWith("Previous Death")) {
            if (this.minimapSettings.deathpoints > 1) {
               int n = 0;

               try {
                  if (waypoint.name.length() > 15) {
                     n = Integer.parseInt(waypoint.name.substring(15));
                  }
               } catch (Exception e) {
                  n = 0;
               }

               waypoint.red = waypoint.red - (waypoint.red - 0.5F) / 8.0F;
               waypoint.green = waypoint.green - (waypoint.green - 0.5F) / 8.0F;
               waypoint.blue = waypoint.blue - (waypoint.blue - 0.5F) / 8.0F;
               waypoint.name = "Previous Death " + (n + 1);
            } else {
               treeSet.add(this.waypoints.indexOf(waypoint));
            }
         }
      }

      if (this.minimapSettings.deathpoints < 2 && treeSet.size() > 0) {
         for (Integer integer : (Iterable<Integer>)(TreeSet)treeSet.descendingSet()) {
            this.deleteWaypointAt(integer);
         }
      }

      if (this.minimapSettings.deathpoints > 0) {
         EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
         TreeSet treeSet2 = new TreeSet();
         treeSet2.add(Minecraft.getMinecraft().thePlayer.dimension);
         this.addWaypoint(
            new Waypoint(
               "Latest Death",
               thePlayer.dimension != -1 ? MinecraftHelper.getBlockX() : MinecraftHelper.getBlockX() * 8,
               thePlayer.dimension != -1 ? MinecraftHelper.getBlockZ() : MinecraftHelper.getBlockZ() * 8,
               MinecraftHelper.getPlayerY() - 1,
               true,
               1.0F,
               1.0F,
               1.0F,
               "skull",
               this.subworldName,
               treeSet2
            )
         );
      }
   }

   public void addLoadedWaypoint(
      String str, int n, int n2, int n3, boolean flag, float f, float f2, float f3, String str2, String str3, TreeSet<Integer> treeSet
   ) {
      Waypoint waypoint = new Waypoint(str, n, n2, n3, flag, f, f2, f3, str2, str3, treeSet);
      this.waypoints.add(waypoint);
   }

   public OldReiWaypointLoader(IVoxelMap voxelMap) {
      this.voxelMap = voxelMap;
      this.minimapSettings = voxelMap.getMapOptions();
   }

   public void deleteWaypointAt(int n) {
      this.deathpoints.remove(this.waypoints.get(n));
      this.entityWaypointContainer.removeWaypoint(this.waypoints.get(n));
      this.waypoints.remove(n);
      this.saveWaypoints();
   }

   public void createEntityContainer() {
      this.entityWaypointContainer = new EntityWaypointContainer(Minecraft.getMinecraft().theWorld);

      for (Waypoint waypoint : this.waypoints) {
         this.entityWaypointContainer.addWaypoint(waypoint);
      }

      Minecraft.getMinecraft().theWorld.spawnEntityInWorld(this.entityWaypointContainer);
   }

   @Override
   public void setDimensionFilter(int n) {
      synchronized (this.lock) {
         for (Waypoint waypoint : this.waypoints) {
            waypoint.inDimension = waypoint.dimensions.size() == 0 || waypoint.dimensions.contains(n);
         }

         this.createEntityContainer();
      }
   }

   @Override
   public void setSubworldHash(String str) {
      this.subworldHash = str;
      if (this.subworldName.equals("")) {
         this.loadWaypointsFor(this.subworldHash);
      }
   }

   @Override
   public void saveWaypoints() {
      String currentWorldName = this.voxelMap.getMap().getCurrentWorldName();
      if (currentWorldName.endsWith(":25565")) {
         int lastIndexOf = currentWorldName.lastIndexOf(":");
         if (lastIndexOf != -1) {
            currentWorldName = currentWorldName.substring(0, lastIndexOf);
         }
      }

      currentWorldName = this.sanitizeFileName(currentWorldName);
      File absoluteFile = DirectoryHelper.findFile("minecraft", true).getAbsoluteFile();
      File absoluteFile2 = Minecraft.getMinecraft().mcDataDir.getAbsoluteFile();
      if (!absoluteFile.equals(absoluteFile2)) {
         String str = "";
         if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
            str = absoluteFile2.getName();
            if ((str.equalsIgnoreCase("minecraft") || str.equalsIgnoreCase(".")) && absoluteFile2.getParentFile() != null) {
               str = absoluteFile2.getParentFile().getName();
            }

            str = "~" + str;
         }

         this.waypointFile = new File(DirectoryHelper.findFile("minecraft/mods/VoxelMods/voxelMap", true), currentWorldName + str + ".points");
         this.saveToFile(this.waypointFile);
         absoluteFile = new File(Minecraft.getMinecraft().mcDataDir, "/mods/VoxelMods/voxelMap/");
         if (!absoluteFile.exists()) {
            absoluteFile.mkdirs();
         }

         this.waypointFile = new File(Minecraft.getMinecraft().mcDataDir, "/mods/VoxelMods/voxelMap/" + currentWorldName + ".points");
         this.saveToFile(this.waypointFile);
      } else {
         this.waypointFile = new File(DirectoryHelper.findFile("minecraft/mods/VoxelMods/voxelMap", true), currentWorldName + ".points");
         this.saveToFile(this.waypointFile);
      }
   }

   public void saveToFile(File file) {
      try {
         PrintWriter printWriter = new PrintWriter(new FileWriter(file));
         Date date = new Date();
         String format = new SimpleDateFormat("yyyyMMddHHmm").format(date);
         printWriter.println("filetimestamp:" + format);

         for (Waypoint waypoint : this.waypoints) {
            if (!waypoint.deathpoint && !waypoint.name.startsWith("^")) {
               String str = "";

               for (Integer integer : waypoint.dimensions) {
                  str = str + "" + integer + "#";
               }

               if (str.equals("")) {
                  str = "-1#0#";
               }

               printWriter.println(
                  "name:"
                     + this.escapeText(waypoint.name)
                     + ",x:"
                     + waypoint.x
                     + ",z:"
                     + waypoint.z
                     + ",y:"
                     + waypoint.y
                     + ",enabled:"
                     + waypoint.enabled
                     + ",red:"
                     + waypoint.red
                     + ",green:"
                     + waypoint.green
                     + ",blue:"
                     + waypoint.blue
                     + ",suffix:"
                     + waypoint.imageSuffix
                     + ",world:"
                     + this.escapeText(waypoint.world)
                     + ",dimensions:"
                     + str
               );
            }
         }

         printWriter.close();
      } catch (Exception e) {
         ChatLogger.handleString(EnumChatFormatting.YELLOW + "Error Saving Waypoints");
      }
   }

   @Override
   public void deleteWaypoint(Waypoint waypoint) {
      this.deathpoints.remove(waypoint);
      this.entityWaypointContainer.removeWaypoint(waypoint);
      this.waypoints.remove(waypoint);
      this.saveWaypoints();
   }

   @Override
   public void setSubworldName(String str) {
      this.subworldName = str;
      this.loadWaypointsFor(this.subworldName);
   }

   public long readFileTimestamp(File file) {
      if (file.exists()) {
         try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String readLine = bufferedReader.readLine();
            String[] split = readLine.split(":");
            bufferedReader.close();
            if (split[0].equals("filetimestamp")) {
               return Long.parseLong(split[1]);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return -1L;
   }

   public boolean loadZanWaypoints(String str, String str2, String str3) {
      try {
         this.waypointFile = new File(DirectoryHelper.findFile("minecraft/mods/zan", false), str + ".points");
         if (!this.waypointFile.exists()) {
            this.waypointFile = new File(DirectoryHelper.findFile("minecraft/mods/zan", false), str2 + ".points");
         }

         if (!this.waypointFile.exists()) {
            this.waypointFile = new File(DirectoryHelper.findFile("minecraft/mods/zan", false), str3 + ".points");
         }

         if (!this.waypointFile.exists()) {
            this.waypointFile = new File(DirectoryHelper.findFile("minecraft", false), str3 + ".points");
         }

         if (!this.waypointFile.exists()) {
            return false;
         }

         TreeSet treeSet = new TreeSet();
         treeSet.add(-1);
         treeSet.add(0);
         BufferedReader bufferedReader = new BufferedReader(new FileReader(this.waypointFile));

         String str4;
         while ((str4 = bufferedReader.readLine()) != null) {
            String[] split = str4.split(":");
            if (split.length == 4) {
               this.addLoadedWaypoint(
                  split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), -1, Boolean.parseBoolean(split[3]), 0.0F, 1.0F, 0.0F, "", "", treeSet
               );
            } else if (split.length == 7) {
               this.addLoadedWaypoint(
                  split[0],
                  Integer.parseInt(split[1]),
                  Integer.parseInt(split[2]),
                  -1,
                  Boolean.parseBoolean(split[3]),
                  Float.parseFloat(split[4]),
                  Float.parseFloat(split[5]),
                  Float.parseFloat(split[6]),
                  "",
                  "",
                  treeSet
               );
            } else if (split.length == 8) {
               if (!split[3].contains("true") && !split[3].contains("false")) {
                  this.addLoadedWaypoint(
                     split[0],
                     Integer.parseInt(split[1]),
                     Integer.parseInt(split[2]),
                     Integer.parseInt(split[3]),
                     Boolean.parseBoolean(split[4]),
                     Float.parseFloat(split[5]),
                     Float.parseFloat(split[6]),
                     Float.parseFloat(split[7]),
                     "",
                     "",
                     treeSet
                  );
               } else {
                  this.addLoadedWaypoint(
                     split[0],
                     Integer.parseInt(split[1]),
                     Integer.parseInt(split[2]),
                     -1,
                     Boolean.parseBoolean(split[3]),
                     Float.parseFloat(split[4]),
                     Float.parseFloat(split[5]),
                     Float.parseFloat(split[6]),
                     split[7],
                     "",
                     treeSet
                  );
               }
            } else if (split.length == 9) {
               this.addLoadedWaypoint(
                  split[0],
                  Integer.parseInt(split[1]),
                  Integer.parseInt(split[2]),
                  Integer.parseInt(split[3]),
                  Boolean.parseBoolean(split[4]),
                  Float.parseFloat(split[5]),
                  Float.parseFloat(split[6]),
                  Float.parseFloat(split[7]),
                  split[8],
                  "",
                  treeSet
               );
            }
         }

         bufferedReader.close();
         return true;
      } catch (Exception e) {
         ChatLogger.handleString(EnumChatFormatting.YELLOW + "Error Loading Waypoints");
         System.err.println("waypoint load error: " + e.getLocalizedMessage());
         return false;
      }
   }

   @Override
   public void updateDeathpoints() {
      if (Minecraft.getMinecraft().thePlayer.dimension == 0 && this.deathpoints.size() > 0) {
         this.visibleDeathpoints = new ArrayList<>();

         for (Waypoint waypoint : this.deathpoints) {
            if (Math.abs(waypoint.getX() - MinecraftHelper.getBlockX()) < 400
               && Math.abs(waypoint.getZ() - MinecraftHelper.getBlockZ()) < 400
               && Minecraft.getMinecraft().thePlayer.worldObj.getChunkFromBlockCoords(new BlockPos(waypoint.getX(), 0, waypoint.getZ())).isLoaded()) {
               waypoint.setY(Minecraft.getMinecraft().thePlayer.worldObj.getTopSolidOrLiquidBlock(new BlockPos(waypoint.getX(), 0, waypoint.getZ())).getY());
               this.visibleDeathpoints.add(waypoint);
               this.saveWaypoints();
            }
         }

         this.deathpoints.removeAll(this.visibleDeathpoints);
         System.out.println("remaining old 2d waypoints: " + this.deathpoints.size());
      }
   }

   public String unescapeText(String str) {
      str = str.replace("~colon~", ":");
      return str.replace("~comma~", ",");
   }

   public boolean importLegacyWaypoints(String str) {
      File absoluteFile = DirectoryHelper.findFile("minecraft", false).getAbsoluteFile();
      File absoluteFile2 = Minecraft.getMinecraft().mcDataDir.getAbsoluteFile();
      if (!absoluteFile.equals(absoluteFile2)) {
         long l = -1L;
         long l2 = -1L;
         String str2 = "";
         if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
            str2 = absoluteFile2.getName();
            if ((str2.equalsIgnoreCase("minecraft") || str2.equalsIgnoreCase(".")) && absoluteFile2.getParentFile() != null) {
               str2 = absoluteFile2.getParentFile().getName();
            }

            str2 = "~" + str2;
         }

         File file = new File(Minecraft.getMinecraft().mcDataDir, "/mods/VoxelMods/voxelMap/" + str + ".points");
         l2 = this.readFileTimestamp(file);
         File file2 = new File(DirectoryHelper.findFile("minecraft/mods/VoxelMods/voxelMap", false), str + str2 + ".points");
         if (!file2.exists() && !file.exists()) {
            file2 = new File(DirectoryHelper.findFile("minecraft/mods/VoxelMods/voxelMap", false), str + ".points");
         }

         l = this.readFileTimestamp(file2);
         if (!file2.exists() && !file.exists()) {
            return false;
         }

         if (!file2.exists()) {
            this.waypointFile = file;
         } else if (!file.exists()) {
            this.waypointFile = file2;
         } else {
            this.waypointFile = l > l2 ? file2 : file;
         }
      } else {
         this.waypointFile = new File(DirectoryHelper.findFile("minecraft/mods/VoxelMods/voxelMap", false), str + ".points");
      }

      if (this.waypointFile.exists()) {
         try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(this.waypointFile));

            String str2;
            while ((str2 = bufferedReader.readLine()) != null) {
               if (!str2.startsWith("filetimestamp")) {
                  String[] split = str2.split(",");
                  String str3 = "";
                  int n = 0;
                  int n2 = 0;
                  int n3 = -1;
                  boolean flag = false;
                  float f = 0.5F;
                  float f2 = 0.0F;
                  float f3 = 0.0F;
                  String str4 = "";
                  String str5 = "";
                  TreeSet treeSet = new TreeSet();

                  for (int i = 0; i < split.length; i++) {
                     String[] split2 = split[i].split(":");
                     if (split2.length == 2) {
                        String str6 = split2[0];
                        String str7 = split2[1];
                        if (str6.equals("name")) {
                           str3 = this.unescapeText(str7);
                        } else if (str6.equals("x")) {
                           n = Integer.parseInt(str7);
                        } else if (str6.equals("z")) {
                           n2 = Integer.parseInt(str7);
                        } else if (str6.equals("y")) {
                           n3 = Integer.parseInt(str7);
                        } else if (str6.equals("enabled")) {
                           flag = Boolean.parseBoolean(str7);
                        } else if (str6.equals("red")) {
                           f = Float.parseFloat(str7);
                        } else if (str6.equals("green")) {
                           f2 = Float.parseFloat(str7);
                        } else if (str6.equals("blue")) {
                           f3 = Float.parseFloat(str7);
                        } else if (str6.equals("suffix")) {
                           str4 = str7;
                        } else if (str6.equals("world")) {
                           str5 = this.unescapeText(str7);
                        } else if (str6.equals("dimensions")) {
                           String[] split3 = str7.split("#");

                           for (int j = 0; j < split3.length; j++) {
                              treeSet.add(Integer.parseInt(split3[j]));
                           }

                           if (treeSet.size() == 0) {
                              treeSet.add(0);
                              treeSet.add(-1);
                           }
                        }
                     }
                  }

                  if (!str3.equals("")) {
                     this.addLoadedWaypoint(str3, n, n2, n3, flag, f, f2, f3, str4, str5, treeSet);
                  }
               }
            }

            bufferedReader.close();
            return true;
         } catch (Exception e) {
            ChatLogger.handleString(EnumChatFormatting.YELLOW + "Error Loading Waypoints");
            System.err.println("waypoint load error: " + e.getLocalizedMessage());
            return false;
         }
      } else {
         return false;
      }
   }

   public String sanitizeFileName(String str) {
      str = str.replace("<", "~less~");
      str = str.replace(">", "~greater~");
      str = str.replace(":", "~colon~");
      str = str.replace("\"", "~quote~");
      str = str.replace("/", "~slash~");
      str = str.replace("\\", "~backslash~");
      str = str.replace("|", "~pipe~");
      str = str.replace("?", "~question~");
      return str.replace("*", "~star~");
   }

   public void removeEntityContainer() {
      List loadedEntityList = Minecraft.getMinecraft().theWorld.getLoadedEntityList();

      for (int i = 0; i < loadedEntityList.size(); i++) {
         Entity entity = (Entity)loadedEntityList.get(i);
         if (entity instanceof EntityWaypointContainer) {
            entity.setDead();
         }
      }
   }

   public EntityWaypointContainer getEntityWaypointContainer() {
      return this.entityWaypointContainer;
   }

   public boolean loadReiWaypoints(String str) {
      boolean flag = false;
      this.waypointFile = new File(DirectoryHelper.findFile("minecraft/mods/rei_minimap", false), str + ".points");
      if (!this.waypointFile.exists()) {
         this.waypointFile = new File(Minecraft.getMinecraft().mcDataDir, "/mods/rei_minimap/" + str + ".points");
      }

      if (this.waypointFile.exists()) {
         this.addDimensionToFile(this.waypointFile, 0);
         flag = true;
      } else {
         for (int i = -25; i < 25; i++) {
            this.waypointFile = new File(DirectoryHelper.findFile("minecraft/mods/rei_minimap", false), str + ".DIM" + i + ".points");
            if (!this.waypointFile.exists()) {
               this.waypointFile = new File(Minecraft.getMinecraft().mcDataDir, "/mods/rei_minimap/" + str + ".DIM" + i + ".points");
            }

            if (this.waypointFile.exists()) {
               flag = true;
               this.addDimensionToFile(this.waypointFile, i);
            }
         }
      }

      return flag;
   }

   public ArrayList<Waypoint> getAllWaypoints() {
      return this.waypoints;
   }

   public ArrayList<Waypoint> getDeathpoints() {
      return this.deathpoints;
   }

   @Override
   public void addWaypoint(Waypoint waypoint) {
      this.waypoints.add(waypoint);
      if (this.entityWaypointContainer != null) {
         this.entityWaypointContainer.addWaypoint(waypoint);
      }

      this.saveWaypoints();
   }

   @Override
   public ArrayList<Waypoint> getWaypoints() {
      return this.waypoints;
   }
}
