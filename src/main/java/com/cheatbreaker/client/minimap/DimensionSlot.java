package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class DimensionSlot extends MinimapSlotList {
    public WaypointEditScreen waypointEditScreen;
    public IDimensionManager dimensionManager;

    @Override
    public boolean isSelected(int n) {
        return this.dimensionManager.getDimensions().get(n).equals(this.waypointEditScreen.dimensionContainer);
    }

    @Override
    public void overlayBackground(int n, int n2, int n3, int n4) {
    }

    public DimensionSlot(WaypointEditScreen waypointEditScreen) {
        super(Minecraft.getMinecraft(), waypointEditScreen.getWidth(), waypointEditScreen.getHeight(), waypointEditScreen.getHeight() / 6 + 123 - 14, waypointEditScreen.getHeight() / 6 + 164 + 3, 18);
        this.waypointEditScreen = waypointEditScreen;
        this.setListWidth(175);
        this.setLeftPosition((this.waypointEditScreen.getWidth() - this.listWidth) / 2);
        this.setShowSelectionBox(false);
        this.setInsetSlots(false);
        this.setDrawOverlays(false);
        this.dimensionManager = this.waypointEditScreen.voxelMap.getDimensionManager();
        this.scrollBy(
            this.dimensionManager.getDimensions().indexOf(this.dimensionManager.getDimensionById(this.waypointEditScreen.waypoint.dimensions.first()))
                * this.slotHeight
        );
    }

    @Override
    public void drawBackground() {
    }

    @Override
    public int getSize() {
        return this.dimensionManager.getDimensions().size();
    }

    @Override
    public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
        DimensionContainer dimensionContainer = this.dimensionManager.getDimensions().get(n);
        String name = dimensionContainer.name;
        if (name.equals("notLoaded") || name.equals("failedToLoad")) {
            name = "dimension " + dimensionContainer.id + "(" + Minecraft.getMinecraft().theWorld.provider.getClass().getSimpleName() + ")";
        }

        this.waypointEditScreen
            .drawCenteredString(this.waypointEditScreen.getFontRendererObj(), dimensionContainer.name, this.waypointEditScreen.getWidth() / 2, n3 + 3, 16777215);
        byte b = 4;
        byte b2 = 16;
        n2 = this.waypointEditScreen.getWidth() / 2 - this.listWidth / 2;
        int listWidth = this.listWidth;
        if (this.mouseX >= n2 + b && this.mouseY >= n3 && this.mouseX <= n2 + listWidth + b && this.mouseY <= n3 + this.slotHeight) {
            String str = null;
            if (this.mouseX >= n2 + listWidth - b2 - b && this.mouseX <= n2 + listWidth) {
                str = this.waypointEditScreen.waypoint.dimensions.contains(dimensionContainer.id)
                    ? Translations.translate("minimap.waypoints.dimension.applies")
                    : Translations.translate("minimap.waypoints.dimension.notapplies");
            } else {
                str = null;
            }

            WaypointEditScreen.setTooltip(this.waypointEditScreen, str);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlUtils.bindTextureByName("textures/gui/container/beacon.png");
        int n7 = this.waypointEditScreen.waypoint.dimensions.contains(dimensionContainer.id) ? 91 : 113;
        short s = 222;
        Gui.drawTexturedRect(n2 + listWidth - b2, n3 - 2, n7, s, 16, 16);
    }

    @Override
    public void elementClicked(int n, boolean flag, int n2, int n3) {
        this.waypointEditScreen.setDimensionContainer(this.dimensionManager.getDimensions().get(n));
        int n4 = this.waypointEditScreen.getWidth() / 2 - this.listWidth / 2;
        byte b = 4;
        byte b2 = 16;
        int listWidth = this.listWidth;
        if (this.mouseX >= n4 + listWidth - b2 - b && this.mouseX <= n4 + listWidth) {
            this.waypointEditScreen.toggleDimension();
        } else if (flag) {
            Mouse.next();
            this.waypointEditScreen.toggleDimension();
            return;
        }
    }

    @Override
    public int getContentHeight() {
        return this.getSize() * 18;
    }
}
