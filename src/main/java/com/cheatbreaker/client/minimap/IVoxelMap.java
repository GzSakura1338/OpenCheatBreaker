package com.cheatbreaker.client.minimap;

public interface IVoxelMap {
    void setSubworldName(String str);

    void setSubworldHash(String str);

    IColorManager getColorManager();

    IRadar getRadar();

    IWaypointManager getWaypointManager();

    RadarSettings getRadarSettings();

    MinimapSettings getMapOptions();

    IChangeNotifier getChangeNotifier();

    void setPermissions(boolean flag, boolean flag2);

    IDimensionManager getDimensionManager();

    IMap getMap();
}
