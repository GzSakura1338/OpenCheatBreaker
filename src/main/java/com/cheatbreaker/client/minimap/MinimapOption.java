package com.cheatbreaker.client.minimap;

public enum MinimapOption {
    COORDS("options.minimap.showcoordinates", false, true, false),
    HIDE("options.minimap.hideminimap", false, true, false),
    CAVEMODE("options.minimap.cavemode", false, true, false),
    LIGHTING("options.minimap.dynamiclighting", false, true, false),
    TERRAIN("options.minimap.terraindepth", false, false, true),
    SQUARE("options.minimap.squaremap", false, true, false),
    OLDNORTH("options.minimap.oldnorth", false, true, false),
    BEACONS("options.minimap.ingamewaypoints", false, false, true),
    WELCOME("Welcome Screen", false, true, false),
    ZOOM("option.minimapZoom", false, true, false),
    LOCATION("options.minimap.location", false, false, true),
    SIZE("options.minimap.size", false, false, true),
    FILTERING("options.minimap.filtering", false, true, false),
    WATERTRANSPARENCY("options.minimap.watertransparency", false, true, false),
    BLOCKTRANSPARENCY("options.minimap.blocktransparency", false, true, false),
    BIOMES("options.minimap.biomes", false, true, false),
    BIOMEOVERLAY("options.minimap.biomeoverlay", false, false, true),
    CHUNKGRID("options.minimap.chunkgrid", false, true, false),
    SHOWRADAR("options.minimap.radar.showradar", false, true, false),
    SHOWHOSTILES("options.minimap.radar.showhostiles", false, true, false),
    SHOWPLAYERS("options.minimap.radar.showplayers", false, true, false),
    SHOWNEUTRALS("options.minimap.radar.showneutrals", false, true, false),
    SHOWPLAYERHELMETS("options.minimap.radar.showplayerhelmets", false, true, false),
    SHOWMOBHELMETS("options.minimap.radar.showmobhelmets", false, true, false),
    SHOWPLAYERNAMES("options.minimap.radar.showplayernames", false, true, false),
    RADAROUTLINES("options.minimap.radar.iconoutlines", false, true, false),
    RADARFILTERING("options.minimap.radar.iconfiltering", false, true, false),
    RANDOMOBS("options.minimap.radar.randomobs", false, true, false),
    WAYPOINTDISTANCE("options.minimap.waypoints.distance", true, false, false),
    DEATHPOINTS("options.minimap.waypoints.deathpoints", false, false, true),
    WAYPOINTBORDER("options.minimap.waypoints.border", false, true, false),
    WAYPOINTTEXTSHADOW("options.minimap.waypoints.textshadow", false, true, false);

    public String translationKey;
    public boolean floatValue;
    public boolean booleanValue;
    public boolean listValue;

    MinimapOption(String str, boolean flag, boolean flag2, boolean flag3) {
        this.translationKey = str;
        this.floatValue = flag;
        this.booleanValue = flag2;
        this.listValue = flag3;
    }

    public boolean isFloat() {
        return this.floatValue;
    }

    public static MinimapOption fromName(String str) {
        return Enum.valueOf(MinimapOption.class, str);
    }

    public boolean isList() {
        return this.listValue;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public boolean isBoolean() {
        return this.booleanValue;
    }

    public int getId() {
        return this.ordinal();
    }

    public static MinimapOption fromId(int n) {
        for (MinimapOption minimapOption : values()) {
            if (minimapOption.getId() == n) {
                return minimapOption;
            }
        }

        return null;
    }
}
