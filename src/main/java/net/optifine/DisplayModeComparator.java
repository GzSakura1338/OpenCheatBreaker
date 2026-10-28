package net.optifine;

import java.util.Comparator;
import org.lwjgl.opengl.DisplayMode;

public class DisplayModeComparator implements Comparator {
    @Override
    public int compare(Object obj, Object obj2) {
        DisplayMode displayMode = (DisplayMode)obj;
        DisplayMode displayMode2 = (DisplayMode)obj2;
        return displayMode.getWidth() != displayMode2.getWidth()
            ? displayMode.getWidth() - displayMode2.getWidth()
            : (
                displayMode.getHeight() != displayMode2.getHeight()
                    ? displayMode.getHeight() - displayMode2.getHeight()
                    : (
                        displayMode.getBitsPerPixel() != displayMode2.getBitsPerPixel()
                            ? displayMode.getBitsPerPixel() - displayMode2.getBitsPerPixel()
                            : (displayMode.getFrequency() != displayMode2.getFrequency() ? displayMode.getFrequency() - displayMode2.getFrequency() : 0)
                    )
            );
    }
}
