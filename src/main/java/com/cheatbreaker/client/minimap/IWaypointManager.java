package com.cheatbreaker.client.minimap;

import java.util.ArrayList;

public interface IWaypointManager {
    void setSubworldHash(String str);

    void addWaypoint(Waypoint waypoint);

    void saveWaypoints();

    void onWorldChange();

    String getCurrentSubworld();

    void setDimensionFilter(int n);

    void updateEntityContainer();

    void setSubworldName(String str);

    ArrayList<Waypoint> getWaypoints();

    void handleDeath();

    void deleteWaypoint(Waypoint waypoint);

    void updateDeathpoints();
}
