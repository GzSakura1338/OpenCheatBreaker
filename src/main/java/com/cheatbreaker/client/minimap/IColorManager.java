package com.cheatbreaker.client.minimap;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Set;

public interface IColorManager {
   int UNKNOWN_BLOCK_COLOR = -65025;

   Set<Integer> getBiomeTintedBlocks();

   BufferedImage getBlockIconImage(int n, int n2);

   int colorAdder(int n, int n2);

   int colorMultiplier(int n, int n2);

   int getBlockColor(int n, int n2, int n3);

   boolean isOptifineInstalled();

   int getBlockColorWithDefaultTint(int n, int n2, int n3);

   int getMapImageTextureId();

   int getAirColor();

   boolean getResourcePackChanged();

   HashMap<String, Integer[]> getBiomeColormaps();

   BufferedImage getColorPicker();

   void setSkyColor(int n);
}
