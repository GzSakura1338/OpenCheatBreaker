package com.cheatbreaker.client.minimap;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Set;

public interface IColorManager {
    int UNKNOWN_BLOCK_COLOR = -65025;

    int colorMultiplier(int n, int n2);

    boolean getResourcePackChanged();

    int getBlockColorWithDefaultTint(int n, int n2, int n3);

    Set<Integer> getBiomeTintedBlocks();

    int getMapImageTextureId();

    BufferedImage getColorPicker();

    int getAirColor();

    int colorAdder(int n, int n2);

    int getBlockColor(int n, int n2, int n3);

    HashMap<String, Integer[]> getBiomeColormaps();

    BufferedImage getBlockIconImage(int n, int n2);

    void setSkyColor(int n);

    boolean isOptifineInstalled();
}
