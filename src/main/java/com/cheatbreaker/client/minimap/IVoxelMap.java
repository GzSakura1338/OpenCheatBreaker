package com.cheatbreaker.client.minimap;

public interface IVoxelMap {
   IMap getMap();

   RadarSettings getRadarSettings();

   IDimensionManager getDimensionManager();

   void setPermissions(boolean flag, boolean flag2);

   IWaypointManager getWaypointManager();

   IRadar getRadar();

   IChangeNotifier getChangeNotifier();

   MinimapSettings getMapOptions();

   IColorManager getColorManager();

   void setSubworldName(String str);

   void setSubworldHash(String str);
}
