package net.optifine;

import java.awt.Rectangle;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public interface TooltipProvider {
    Rectangle getTooltipBounds(GuiScreen guiScreen, int n, int n2);

    String[] getTooltipLines(GuiButton guiButton, int n);

    boolean isRenderBorder();
}
