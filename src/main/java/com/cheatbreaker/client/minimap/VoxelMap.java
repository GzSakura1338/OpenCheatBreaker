package com.cheatbreaker.client.minimap;

import net.minecraft.client.Minecraft;

public class VoxelMap extends AbstractVoxelMap {
   public MinimapSettings minimapSettings = null;
   public RadarSettings radarSettings = null;
   public IMap map = null;
   public IRadar radar = null;
   public IColorManager colorManager = null;
   public IWaypointManager waypointManager = null;
   public IDimensionManager dimensionManager = null;
   public ChangeNotifier changeNotifier = new ChangeNotifier();

   @Override
   public IMap getMap() {
      return this.map;
   }

   public VoxelMap(boolean flag, boolean flag2) {
      AbstractVoxelMap.instance = this;
      this.minimapSettings = new MinimapSettings();
      this.minimapSettings.showInScreens = flag;
      this.radarSettings = new RadarSettings();
      this.minimapSettings.setRadarSettings(this.radarSettings);
      this.colorManager = new ColorManager(this);
      this.waypointManager = new OldReiWaypointLoader(this);
      this.dimensionManager = new DimensionManager(this);
      this.map = new MinimapMap(this);
      this.minimapSettings.load();
   }

   @Override
   public RadarSettings getRadarSettings() {
      return this.radarSettings;
   }

   @Override
   public MinimapSettings getMapOptions() {
      return this.minimapSettings;
   }

   public void onTickInGame(Minecraft mc) {
      this.map.onTickInGame(mc);
   }

   @Override
   public IWaypointManager getWaypointManager() {
      return this.waypointManager;
   }

   @Override
   public void setSubworldHash(String str) {
      this.waypointManager.setSubworldHash(str);
   }

   @Override
   public IRadar getRadar() {
      return this.radar;
   }

   @Override
   public void setPermissions(boolean flag, boolean flag2) {
      this.map.setPermissions(flag, flag2);
   }

   @Override
   public void setSubworldName(String str) {
      this.waypointManager.setSubworldName(str);
   }

   @Override
   public IDimensionManager getDimensionManager() {
      return this.dimensionManager;
   }

   @Override
   public IColorManager getColorManager() {
      return this.colorManager;
   }

   @Override
   public IChangeNotifier getChangeNotifier() {
      return this.changeNotifier;
   }
}
