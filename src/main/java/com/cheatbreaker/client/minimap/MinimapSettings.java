package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.chat.ChatLogger;
import com.cheatbreaker.client.util.Translations;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class MinimapSettings {
   public final int unusedOne = 1;
   public final int unusedTwo = 2;
   public final int unusedThree = 3;
   public final int unusedFour = 4;
   public boolean showInScreens;
   public boolean changed;
   public RadarSettings radarSettings = null;
   public boolean hide = false;
   public boolean waterTransparency;
   public boolean blockTransparency;
   public boolean squareMap;
   public static MinimapSettings instance;
   public boolean waypointBeacons;
   public boolean waypointSigns;
   public boolean waypointBorder;
   public boolean waypointTextShadow;
   public int waypointBoxPadding;
   public int deathpoints;
   public int waypointMaxDistance;
   public File settingsFile;
   public int zoom;
   public int mapSize;
   public int mapCorner;
   public Boolean radarAllowed;
   public Boolean caveModeAllowed;
   public int waypointSortBy;
   public boolean worldDownloadCompatibility;
   public KeyBinding keyMinimapZoom;
   public KeyBinding keyMinimapTogglefullscreen;
   public KeyBinding keyMinimapVoxelmapmenu;
   public KeyBinding keyMinimapWaypointmenu;
   public KeyBinding keyMinimapWaypointhotkey;
   public KeyBinding keyMinimapTogglemobs;
   public Minecraft mc;
   public boolean showCoordinates;
   public boolean caveMode;
   public boolean dynamicLighting;
   public boolean heightMap;
   public boolean slopeMap;
   public boolean filtering;
   public boolean biomes;
   public int biomeOverlay;
   public boolean chunkGrid;
   public boolean oldNorth;
   public boolean welcomeMessage;
   public int unusedFive;
   public boolean realTimeTorchFlicker;
   public int processorCount;
   public boolean multicore;
   public KeyBinding[] keyBindings;

   public boolean consumeChanged() {
      if (this.changed) {
         this.changed = false;
         return true;
      } else {
         return false;
      }
   }

   public void setKeyBindingKey(int n, int n2) {
      this.keyBindings[n].setKeyCode(n2);
      this.save();
   }

   public String getListOptionLabel(MinimapOption minimapOption) {
      switch (minimapOption) {
         case TERRAIN:
            if (this.slopeMap && this.heightMap) {
               return Translations.translate("options.minimap.terrain.both");
            } else if (this.heightMap) {
               return Translations.translate("options.minimap.terrain.height");
            } else {
               if (this.slopeMap) {
                  return Translations.translate("options.minimap.terrain.slope");
               }

               return Translations.translate("options.off");
            }
         case BEACONS:
            if (this.waypointBeacons && this.waypointSigns) {
               return Translations.translate("options.minimap.ingamewaypoints.both");
            } else if (this.waypointBeacons) {
               return Translations.translate("options.minimap.ingamewaypoints.beacons");
            } else {
               if (this.waypointSigns) {
                  return Translations.translate("options.minimap.ingamewaypoints.signs");
               }

               return Translations.translate("options.off");
            }
         case LOCATION:
            if (this.mapCorner == 0) {
               return Translations.translate("options.minimap.location.topleft");
            } else if (this.mapCorner == 1) {
               return Translations.translate("options.minimap.location.topright");
            } else if (this.mapCorner == 2) {
               return Translations.translate("options.minimap.location.bottomright");
            } else {
               if (this.mapCorner == 3) {
                  return Translations.translate("options.minimap.location.bottomleft");
               }

               return "Error";
            }
         case SIZE:
            if (this.mapSize == -1) {
               return Translations.translate("options.minimap.size.small");
            } else if (this.mapSize == 0) {
               return Translations.translate("options.minimap.size.medium");
            } else {
               if (this.mapSize == 1) {
                  return Translations.translate("options.minimap.size.large");
               }

               return "error";
            }
         case BIOMEOVERLAY:
            if (this.biomeOverlay == 0) {
               return Translations.translate("options.off");
            } else if (this.biomeOverlay == 1) {
               return Translations.translate("options.minimap.biomeoverlay.solid");
            } else {
               if (this.biomeOverlay == 2) {
                  return Translations.translate("options.minimap.biomeoverlay.transparent");
               }

               return "error";
            }
         case DEATHPOINTS:
            if (this.deathpoints == 0) {
               return Translations.translate("options.off");
            } else if (this.deathpoints == 1) {
               return Translations.translate("options.minimap.waypoints.deathpoints.mostrecent");
            } else {
               if (this.deathpoints == 2) {
                  return Translations.translate("options.minimap.waypoints.deathpoints.all");
               }

               return "error";
            }
         default:
            throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + minimapOption.getTranslationKey() + ". (possibly not a list value)");
      }
   }

   public static String getKeyName(int n) {
      return n < 0 ? I18n.format("key.mouseButton", n + 101) : Keyboard.getKeyName(n);
   }

   public void setFloatOption(MinimapOption minimapOption, float f) {
      if (minimapOption == MinimapOption.WAYPOINTDISTANCE) {
         float f2 = f * 9951.0F + 50.0F;
         if (f2 > 10000.0F) {
            f2 = -1.0F;
         }

         this.waypointMaxDistance = (int)f2;
      }
   }

   public String getKeyBindingLabel(int n) {
      return this.keyBindings[n].getKeyDescription().equals("key.minimap.voxelmapmenu")
         ? Translations.translate("key.minimap.menu")
         : Translations.translate(this.keyBindings[n].getKeyDescription());
   }

   public void setWaypointSortBy(int n) {
      if (n != this.waypointSortBy && n != -this.waypointSortBy) {
         this.waypointSortBy = n;
      } else {
         this.waypointSortBy = -this.waypointSortBy;
      }
   }

   public void toggleOption(MinimapOption minimapOption, int n) {
      switch (minimapOption) {
         case COORDS:
            this.showCoordinates = !this.showCoordinates;
            break;
         case HIDE:
            this.hide = !this.hide;
            break;
         case CAVEMODE:
            this.caveMode = !this.caveMode;
            break;
         case LIGHTING:
            this.dynamicLighting = !this.dynamicLighting;
            break;
         case SQUARE:
            this.squareMap = !this.squareMap;
            break;
         case OLDNORTH:
            this.oldNorth = !this.oldNorth;
            break;
         case WELCOME:
            this.welcomeMessage = !this.welcomeMessage;
            break;
         case FILTERING:
            this.filtering = !this.filtering;
            break;
         case WATERTRANSPARENCY:
            this.waterTransparency = !this.waterTransparency;
            break;
         case BLOCKTRANSPARENCY:
            this.blockTransparency = !this.blockTransparency;
            break;
         case BIOMES:
            this.biomes = !this.biomes;
            break;
         case CHUNKGRID:
            this.chunkGrid = !this.chunkGrid;
            break;
         case WAYPOINTBORDER:
            this.waypointBorder = !this.waypointBorder;
            break;
         case WAYPOINTTEXTSHADOW:
            this.waypointTextShadow = !this.waypointTextShadow;
            break;
         case TERRAIN:
            if (this.slopeMap && this.heightMap) {
               this.slopeMap = false;
               this.heightMap = false;
            } else if (this.slopeMap) {
               this.slopeMap = false;
               this.heightMap = true;
            } else if (this.heightMap) {
               this.slopeMap = true;
               this.heightMap = true;
            } else {
               this.slopeMap = true;
               this.heightMap = false;
            }
            break;
         case BEACONS:
            if (this.waypointBeacons && this.waypointSigns) {
               this.waypointBeacons = false;
               this.waypointSigns = false;
            } else if (this.waypointBeacons) {
               this.waypointBeacons = false;
               this.waypointSigns = true;
            } else if (this.waypointSigns) {
               this.waypointSigns = true;
               this.waypointBeacons = true;
            } else {
               this.waypointBeacons = true;
               this.waypointSigns = false;
            }
            break;
         case LOCATION:
            this.mapCorner = this.mapCorner >= 3 ? 0 : this.mapCorner + 1;
            break;
         case SIZE:
            this.mapSize = this.mapSize >= 1 ? -1 : this.mapSize + 1;
            break;
         case BIOMEOVERLAY:
            this.biomeOverlay++;
            if (this.biomeOverlay > 2) {
               this.biomeOverlay = 0;
            }
            break;
         case DEATHPOINTS:
            this.deathpoints++;
            if (this.deathpoints > 2) {
               this.deathpoints = 0;
            }
            break;
         default:
            throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + minimapOption.getTranslationKey());
      }

      this.changed = true;
   }

   public void setRadarSettings(RadarSettings radarSettings) {
      this.radarSettings = radarSettings;
   }

   public String getOptionLabel(MinimapOption minimapOption) {
      String str = Translations.translate(minimapOption.getTranslationKey()) + ": ";
      if (minimapOption.isFloat()) {
         float floatOption = this.getFloatOption(minimapOption);
         if (minimapOption == MinimapOption.ZOOM) {
            return str + (int)floatOption;
         } else if (minimapOption == MinimapOption.WAYPOINTDISTANCE) {
            return floatOption < 0.0F ? str + Translations.translate("options.minimap.waypoints.infinite") : str + (int)floatOption;
         } else {
            return floatOption == 0.0F ? str + Translations.translate("options.off") : str + (int)floatOption + "%";
         }
      } else if (minimapOption.isBoolean()) {
         boolean booleanOption = this.getBooleanOption(minimapOption);
         return booleanOption ? str + Translations.translate("options.on") : str + Translations.translate("options.off");
      } else if (minimapOption.isList()) {
         String listOptionLabel = this.getListOptionLabel(minimapOption);
         return str + listOptionLabel;
      } else {
         return str;
      }
   }

   public void load() {
      this.settingsFile = new File(this.mc.mcDataDir, "mods/VoxelMods/voxelmap.properties");

      try {
         if (this.settingsFile.exists()) {
            BufferedReader bufferedReader;
            String str;
            for (bufferedReader = new BufferedReader(new FileReader(this.settingsFile)); (str = bufferedReader.readLine()) != null; KeyBinding.resetKeyBindingArrayAndHash()) {
               String[] split = str.split(":");
               if (split[0].equals("Zoom Level")) {
                  this.zoom = Integer.parseInt(split[1]);
               } else if (split[0].equals("Hide Minimap")) {
                  this.hide = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Show Coordinates")) {
                  this.showCoordinates = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Enable Cave Mode")) {
                  this.caveMode = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Dynamic Lighting")) {
                  this.dynamicLighting = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Height Map")) {
                  this.heightMap = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Slope Map")) {
                  this.slopeMap = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Filtering")) {
                  this.filtering = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Water Transparency")) {
                  this.waterTransparency = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Block Transparency")) {
                  this.blockTransparency = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Biomes")) {
                  this.biomes = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Biome Overlay")) {
                  this.biomeOverlay = Integer.parseInt(split[1]);
               } else if (split[0].equals("Chunk Grid")) {
                  this.chunkGrid = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Square Map")) {
                  this.squareMap = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Old North")) {
                  this.oldNorth = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Waypoint Beacons")) {
                  this.waypointBeacons = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Waypoint Signs")) {
                  this.waypointSigns = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Waypoint Border")) {
                  this.waypointBorder = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Waypoint Text Shadow")) {
                  this.waypointTextShadow = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Waypoint Box Padding")) {
                  this.waypointBoxPadding = Integer.parseInt(split[1]);
               } else if (split[0].equals("Deathpoints")) {
                  this.deathpoints = Integer.parseInt(split[1]);
               } else if (split[0].equals("Waypoint Max Distance")) {
                  this.waypointMaxDistance = Integer.parseInt(split[1]);
               } else if (split[0].equals("Waypoint Sort By")) {
                  this.waypointSortBy = Integer.parseInt(split[1]);
               } else if (split[0].equals("Welcome Message")) {
                  this.welcomeMessage = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("World Download Compatibility")) {
                  this.worldDownloadCompatibility = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Real Time Torch Flicker")) {
                  this.realTimeTorchFlicker = Boolean.parseBoolean(split[1]);
               } else if (split[0].equals("Map Corner")) {
                  this.mapCorner = Integer.parseInt(split[1]);
               } else if (split[0].equals("Map Size")) {
                  this.mapSize = Integer.parseInt(split[1]);
               } else if (split[0].equals("Zoom Key")) {
                  this.keyMinimapZoom.setKeyCode(Keyboard.getKeyIndex(split[1]));
               } else if (split[0].equals("Fullscreen Key")) {
                  this.keyMinimapTogglefullscreen.setKeyCode(Keyboard.getKeyIndex(split[1]));
               } else if (split[0].equals("Menu Key")) {
                  this.keyMinimapVoxelmapmenu.setKeyCode(Keyboard.getKeyIndex(split[1]));
               } else if (split[0].equals("Waypoint Key")) {
                  this.keyMinimapWaypointhotkey.setKeyCode(Keyboard.getKeyIndex(split[1]));
               } else if (split[0].equals("Mob Key")) {
                  this.keyMinimapTogglemobs.setKeyCode(Keyboard.getKeyIndex(split[1]));
               }
            }

            if (this.radarSettings != null) {
               this.radarSettings.load(this.settingsFile);
            }

            bufferedReader.close();
         }

         this.save();
      } catch (Exception e) {
      }
   }

   public String getBoundKeyName(int n) {
      int keyCode = this.keyBindings[n].getKeyCode();
      return getKeyName(keyCode);
   }

   public float getFloatOption(MinimapOption minimapOption) {
      if (minimapOption == MinimapOption.ZOOM) {
         return this.zoom;
      } else {
         return minimapOption == MinimapOption.WAYPOINTDISTANCE ? this.waypointMaxDistance : 0.0F;
      }
   }

   public MinimapSettings() {
      this.waterTransparency = this.multicore;
      this.blockTransparency = this.multicore;
      this.squareMap = false;
      this.waypointBeacons = true;
      this.waypointSigns = true;
      this.waypointBorder = true;
      this.waypointTextShadow = true;
      this.waypointBoxPadding = 2;
      this.deathpoints = 1;
      this.waypointMaxDistance = 1000;
      this.zoom = 2;
      this.mapSize = 0;
      this.mapCorner = 1;
      this.radarAllowed = Boolean.TRUE;
      this.caveModeAllowed = Boolean.TRUE;
      this.waypointSortBy = 1;
      this.worldDownloadCompatibility = false;
      this.keyMinimapZoom = new KeyBinding("key.minimap.zoom", 44, "controls.minimap.title");
      this.keyMinimapTogglefullscreen = new KeyBinding("key.minimap.togglefullscreen", 45, "controls.minimap.title");
      this.keyMinimapVoxelmapmenu = new KeyBinding("key.minimap.voxelmapmenu", 50, "controls.minimap.title");
      this.keyMinimapWaypointmenu = new KeyBinding("key.minimap.waypointmenu", 0, "controls.minimap.title");
      this.keyMinimapWaypointhotkey = new KeyBinding("key.minimap.waypointhotkey", 48, "controls.minimap.title");
      this.keyMinimapTogglemobs = new KeyBinding("key.minimap.togglemobs", 0, "controls.minimap.title");
      this.mc = null;
      this.showCoordinates = true;
      this.caveMode = true;
      this.dynamicLighting = true;
      this.heightMap = this.multicore;
      this.slopeMap = true;
      this.filtering = true;
      this.biomes = this.multicore;
      this.biomeOverlay = 0;
      this.chunkGrid = false;
      this.oldNorth = false;
      this.welcomeMessage = true;
      this.unusedFive = 2;
      this.realTimeTorchFlicker = false;
      this.processorCount = Runtime.getRuntime().availableProcessors();
      this.multicore = this.processorCount > 0;
      instance = this;
      this.mc = Minecraft.getMinecraft();
      this.keyBindings = new KeyBinding[]{
         this.keyMinimapVoxelmapmenu, this.keyMinimapWaypointhotkey, this.keyMinimapZoom, this.keyMinimapTogglefullscreen, this.keyMinimapTogglemobs
      };
   }

   public boolean getBooleanOption(MinimapOption minimapOption) {
      switch (minimapOption) {
         case COORDS:
            return this.showCoordinates;
         case HIDE:
            return this.hide;
         case CAVEMODE:
            return this.caveModeAllowed && this.caveMode;
         case LIGHTING:
            return this.dynamicLighting;
         case SQUARE:
            return this.squareMap;
         case OLDNORTH:
            return this.oldNorth;
         case WELCOME:
            return this.welcomeMessage;
         case FILTERING:
            return this.filtering;
         case WATERTRANSPARENCY:
            return this.waterTransparency;
         case BLOCKTRANSPARENCY:
            return this.blockTransparency;
         case BIOMES:
            return this.biomes;
         case CHUNKGRID:
            return this.chunkGrid;
         case WAYPOINTBORDER:
            return this.waypointBorder;
         case WAYPOINTTEXTSHADOW:
            return this.waypointTextShadow;
         default:
            throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + minimapOption.getTranslationKey() + ". (possibly not a boolean)");
      }
   }

   public void save() {
      File file = new File(this.mc.mcDataDir, "/mods/VoxelMods/");
      if (!file.exists()) {
         file.mkdirs();
      }

      this.settingsFile = new File(file, "voxelmap.properties");

      try {
         PrintWriter printWriter = new PrintWriter(new FileWriter(this.settingsFile));
         printWriter.println("Zoom Level:" + this.zoom);
         printWriter.println("Hide Minimap:" + this.hide);
         printWriter.println("Show Coordinates:" + this.showCoordinates);
         printWriter.println("Enable Cave Mode:" + this.caveMode);
         printWriter.println("Dynamic Lighting:" + this.dynamicLighting);
         printWriter.println("Height Map:" + this.heightMap);
         printWriter.println("Slope Map:" + this.slopeMap);
         printWriter.println("Filtering:" + this.filtering);
         printWriter.println("Water Transparency:" + this.waterTransparency);
         printWriter.println("Block Transparency:" + this.blockTransparency);
         printWriter.println("Biomes:" + this.biomes);
         printWriter.println("Biome Overlay:" + this.biomeOverlay);
         printWriter.println("Chunk Grid:" + this.chunkGrid);
         printWriter.println("Square Map:" + this.squareMap);
         printWriter.println("Old North:" + this.oldNorth);
         printWriter.println("Waypoint Beacons:" + this.waypointBeacons);
         printWriter.println("Waypoint Signs:" + this.waypointSigns);
         printWriter.println("Waypoint Border:" + this.waypointBorder);
         printWriter.println("Waypoint Text Shadow:" + this.waypointTextShadow);
         printWriter.println("Waypoint Box Padding:" + this.waypointBoxPadding);
         printWriter.println("Deathpoints:" + this.deathpoints);
         printWriter.println("Waypoint Max Distance:" + this.waypointMaxDistance);
         printWriter.println("Waypoint Sort By:" + this.waypointSortBy);
         printWriter.println("Welcome Message:" + this.welcomeMessage);
         printWriter.println("Map Corner:" + this.mapCorner);
         printWriter.println("Map Size:" + this.mapSize);
         printWriter.println("Zoom Key:" + getKeyName(this.keyMinimapZoom.getKeyCode()));
         printWriter.println("Fullscreen Key:" + getKeyName(this.keyMinimapTogglefullscreen.getKeyCode()));
         printWriter.println("Menu Key:" + getKeyName(this.keyMinimapVoxelmapmenu.getKeyCode()));
         printWriter.println("Waypoint Key:" + getKeyName(this.keyMinimapWaypointhotkey.getKeyCode()));
         printWriter.println("Mob Key:" + getKeyName(this.keyMinimapTogglemobs.getKeyCode()));
         if (this.radarSettings != null) {
            this.radarSettings.save(printWriter);
         }

         printWriter.close();
      } catch (Exception e) {
         ChatLogger.handleString(EnumChatFormatting.YELLOW + "Error Saving Settings " + e.getLocalizedMessage());
      }
   }
}
