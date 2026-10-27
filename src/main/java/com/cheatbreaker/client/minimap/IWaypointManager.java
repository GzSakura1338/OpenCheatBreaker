package com.cheatbreaker.client.minimap;

import java.util.ArrayList;

public interface IWaypointManager {
   void setDimensionFilter(int n);

   void setSubworldName(String str);

   void updateDeathpoints();

   String getCurrentSubworld();

   void deleteWaypoint(Waypoint waypoint);

   ArrayList<Waypoint> getWaypoints();

   void addWaypoint(Waypoint waypoint);

   void saveWaypoints();

   void updateEntityContainer();

   void handleDeath();

   void setSubworldHash(String str);

   void onWorldChange();
}
