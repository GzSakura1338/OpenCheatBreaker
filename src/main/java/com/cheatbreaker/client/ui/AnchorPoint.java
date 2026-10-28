package com.cheatbreaker.client.ui;

public enum AnchorPoint {
    LEFT_TOP("LEFT_TOP"),
    LEFT_MIDDLE("LEFT_MIDDLE"),
    LEFT_BOTTOM("LEFT_BOTTOM"),
    MIDDLE_TOP("MIDDLE_TOP"),
    MIDDLE_MIDDLE("MIDDLE_MIDDLE"),
    MIDDLE_BOTTOM_LEFT("MIDDLE_BOTTOM_LEFT"),
    MIDDLE_BOTTOM_RIGHT("MIDDLE_BOTTOM_RIGHT"),
    RIGHT_TOP("RIGHT_TOP"),
    RIGHT_MIDDLE("RIGHT_MIDDLE"),
    RIGHT_BOTTOM("RIGHT_BOTTOM");

    public String displayName;

    public String getDisplayName() {
        return this.displayName;
    }

    AnchorPoint(String str) {
        this.displayName = str;
    }

    public static AnchorPoint findAnchorPoint(String str) {
        for (AnchorPoint anchorPoint : values()) {
            if (anchorPoint.getDisplayName().equalsIgnoreCase(str)) {
                return anchorPoint;
            }
        }

        return RIGHT_TOP;
    }
}
