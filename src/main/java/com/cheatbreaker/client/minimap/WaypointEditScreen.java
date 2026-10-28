package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.util.Translations;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class WaypointEditScreen extends AbstractMinimapScreen {
    public DimensionContainer dimensionContainer = null;
    public String tooltip = null;
    public DimensionSlot dimensionSlot;
    public boolean flag = false;
    public GuiTextField xField;
    public Random random = new Random();
    public IVoxelMap voxelMap;
    public GuiTextField yField;
    public IWaypointManager waypointManager;
    public IColorManager colorManager;
    public WaypointsScreen waypointsScreen;
    public Waypoint waypoint;
    public float red;
    public GuiButton enabled;
    public GuiTextField zField;
    public float green;
    public GuiTextField nameField;
    public float blue;
    public boolean waypointEnabled;

    public WaypointEditScreen(IVoxelMap voxelMap, WaypointsScreen waypointsScreen, Waypoint waypoint) {
        this.voxelMap = voxelMap;
        this.waypointManager = voxelMap.getWaypointManager();
        this.colorManager = voxelMap.getColorManager();
        this.waypointsScreen = waypointsScreen;
        this.waypoint = waypoint;
        this.red = this.waypoint.red;
        this.green = this.waypoint.green;
        this.blue = this.waypoint.blue;
        this.waypointEnabled = this.waypoint.enabled;
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        if (guiButton.enabled) {
            if (guiButton.id == 2) {
                this.waypoint.enabled = !this.waypoint.enabled;
            }

            if (guiButton.id == 1) {
                this.waypoint.red = this.red;
                this.waypoint.green = this.green;
                this.waypoint.blue = this.blue;
                this.waypoint.enabled = this.waypointEnabled;
                if (this.waypointsScreen != null) {
                    this.waypointsScreen.confirmClicked(false, 0);
                } else {
                    if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue()) {
                        this.mc.entityRenderer.stopUseShader();
                    }

                    this.getMc().displayGuiScreen(null);
                }
            } else if (guiButton.id == 0) {
                this.waypoint.name = this.nameField.getText();
                this.waypoint.setX(Integer.parseInt(this.xField.getText()));
                this.waypoint.setZ(Integer.parseInt(this.zField.getText()));
                this.waypoint.setY(Integer.parseInt(this.yField.getText()));
                if (this.waypointsScreen != null) {
                    this.waypointsScreen.confirmClicked(true, 0);
                } else {
                    this.waypointManager.addWaypoint(this.waypoint);
                    if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue()) {
                        Minecraft.getMinecraft().entityRenderer.stopUseShader();
                    }

                    this.getMc().displayGuiScreen(null);
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void setDimensionContainer(DimensionContainer dimensionContainer) {
        this.dimensionContainer = dimensionContainer;
    }

    public void drawTooltip(String str, int n, int n2) {
        if (str != null) {
            int n3 = n + 12;
            int n4 = n2 - 12;
            int stringWidth = this.getFontRendererObj().getStringWidth(str);
            drawVerticalGradient(n3 - 3, n4 - 3, n3 + stringWidth + 3, n4 + 8 + 3, -1073741824, -1073741824);
            this.getFontRendererObj().drawShadowedString(str, n3, n4, -1);
        }
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
        if (!this.flag) {
            super.mouseClicked(n, n2, n3);
            this.nameField.mouseClicked(n, n2, n3);
            this.xField.mouseClicked(n, n2, n3);
            this.zField.mouseClicked(n, n2, n3);
            this.yField.mouseClicked(n, n2, n3);
            if (n >= this.getWidth() / 2 + 85
                && n <= this.getWidth() / 2 + 101
                && n2 >= this.getHeight() / 6 + 82 + 11
                && n2 <= this.getHeight() / 6 + 82 + 21) {
                this.flag = true;
            }
        } else if (n >= this.getWidth() / 2 - 128
            && n <= this.getWidth() / 2 + 128
            && n2 >= this.getHeight() / 2 - 128
            && n2 <= this.getHeight() / 2 + 128) {
            int rGB = this.colorManager.getColorPicker().getRGB(n - (this.getWidth() / 2 - 128), n2 - (this.getHeight() / 2 - 128));
            this.waypoint.red = (rGB >> 16 & 0xFF) / 255.0F;
            this.waypoint.green = (rGB >> 8 & 0xFF) / 255.0F;
            this.waypoint.blue = (rGB >> 0 & 0xFF) / 255.0F;
            this.flag = false;
        }
    }

    @Override
    public void keyTyped(char c, int n) throws java.io.IOException {
        this.nameField.textboxKeyTyped(c, n);
        this.xField.textboxKeyTyped(c, n);
        this.zField.textboxKeyTyped(c, n);
        this.yField.textboxKeyTyped(c, n);
        if (c == '\t') {
            if (this.nameField.isFocused()) {
                this.nameField.setFocused(false);
                this.xField.setFocused(true);
                this.zField.setFocused(false);
                this.yField.setFocused(false);
            } else if (this.xField.isFocused()) {
                this.nameField.setFocused(false);
                this.xField.setFocused(false);
                this.zField.setFocused(true);
                this.yField.setFocused(false);
            } else if (this.zField.isFocused()) {
                this.nameField.setFocused(false);
                this.xField.setFocused(false);
                this.zField.setFocused(false);
                this.yField.setFocused(true);
            } else if (this.yField.isFocused()) {
                this.nameField.setFocused(true);
                this.xField.setFocused(false);
                this.zField.setFocused(false);
                this.yField.setFocused(false);
            }
        }

        if (c == '\r') {
            this.actionPerformed((GuiButton)this.getButtonList().get(0));
        }

        boolean flag = this.nameField.getText().length() > 0;

        try {
            int parseInt = Integer.parseInt(this.xField.getText());
            flag = flag;
        } catch (NumberFormatException e) {
            flag = false;
        }

        try {
            int parseInt = Integer.parseInt(this.zField.getText());
            flag = flag;
        } catch (NumberFormatException e) {
            flag = false;
        }

        try {
            int parseInt = Integer.parseInt(this.yField.getText());
            flag = flag;
        } catch (NumberFormatException e) {
            flag = false;
        }

        ((GuiButton)this.getButtonList().get(0)).enabled = flag;
        if (n == 1) {
            this.waypoint.red = this.red;
            this.waypoint.green = this.green;
            this.waypoint.blue = this.blue;
            this.waypoint.enabled = this.waypointEnabled;
        }

        super.keyTyped(c, n);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.getButtonList().clear();
        this.getButtonList().add(new GuiButton(0, this.getWidth() / 2 - 155, this.getHeight() / 6 + 168, 150, 20, Translations.translate("addServer.add")));
        this.getButtonList().add(new GuiButton(1, this.getWidth() / 2 + 5, this.getHeight() / 6 + 168, 150, 20, Translations.translate("gui.cancel")));
        this.nameField = new GuiTextField(this.getFontRendererObj(), this.getWidth() / 2 - 100, this.getHeight() / 6 + 0 + 13, 200, 20);
        this.nameField.setFocused(true);
        this.nameField.setText(this.waypoint.name);
        this.xField = new GuiTextField(this.getFontRendererObj(), this.getWidth() / 2 - 100, this.getHeight() / 6 + 41 + 13, 56, 20);
        this.xField.setMaxStringLength(128);
        this.xField.setText("" + this.waypoint.getX());
        this.zField = new GuiTextField(this.getFontRendererObj(), this.getWidth() / 2 - 28, this.getHeight() / 6 + 41 + 13, 56, 20);
        this.zField.setMaxStringLength(128);
        this.zField.setText("" + this.waypoint.getZ());
        this.yField = new GuiTextField(this.getFontRendererObj(), this.getWidth() / 2 + 44, this.getHeight() / 6 + 41 + 13, 56, 20);
        this.yField.setMaxStringLength(128);
        this.yField.setText("" + this.waypoint.getY());
        this.getButtonList()
            .add(
                this.enabled = new GuiButton(
                    2, this.getWidth() / 2 - 101, this.getHeight() / 6 + 82 + 6, 100, 20, "Enabled: " + (this.waypoint.enabled ? "On" : "Off")
                )
            );
        ((GuiButton)this.getButtonList().get(0)).enabled = this.nameField.getText().length() > 0;
        this.dimensionSlot = new DimensionSlot(this);
        this.dimensionSlot.setScrollButtonIds(7, 8);
    }

    @Override
    public void updateScreen() {
        this.nameField.updateCursorCounter();
        this.xField.updateCursorCounter();
    }

    public static String setTooltip(WaypointEditScreen waypointEditScreen, String str) {
        return waypointEditScreen.tooltip = str;
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        super.drawMinimapBackground();
        this.tooltip = null;
        this.enabled.displayString = Translations.translate("minimap.waypoints.enabled")
            + " "
            + (this.waypoint.enabled ? Translations.translate("options.on") : Translations.translate("options.off"));
        this.drawDefaultBackground();
        this.dimensionSlot.drawScreen(n, n2, f);
        this.drawCenteredString(
            this.getFontRendererObj(),
            this.waypointsScreen != null && this.waypointsScreen.editClicked
                ? Translations.translate("minimap.waypoints.edit")
                : Translations.translate("minimap.waypoints.new"),
            this.getWidth() / 2,
            20,
            16777215
        );
        this.drawString(
            this.getFontRendererObj(), Translations.translate("minimap.waypoints.name"), this.getWidth() / 2 - 100, this.getHeight() / 6 + 0, 10526880
        );
        this.drawString(this.getFontRendererObj(), Translations.translate("X"), this.getWidth() / 2 - 100, this.getHeight() / 6 + 41, 10526880);
        this.drawString(this.getFontRendererObj(), Translations.translate("Z"), this.getWidth() / 2 - 28, this.getHeight() / 6 + 41, 10526880);
        this.drawString(this.getFontRendererObj(), Translations.translate("Y"), this.getWidth() / 2 + 44, this.getHeight() / 6 + 41, 10526880);
        this.drawString(
            this.getFontRendererObj(), Translations.translate("minimap.waypoints.choosecolor"), this.getWidth() / 2 + 10, this.getHeight() / 6 + 82 + 11, 10526880
        );
        this.nameField.drawTextBox();
        this.xField.drawTextBox();
        this.zField.drawTextBox();
        this.yField.drawTextBox();
        GL11.glColor4f(this.waypoint.red, this.waypoint.green, this.waypoint.blue, 1.0F);
        GlUtils.bindTextureId(-1);
        drawTexturedRect(this.getWidth() / 2 + 85, this.getHeight() / 6 + 82 + 11, 0.0F, 0.0F, 16, 10);
        super.drawScreen(n, n2, f);
        if (this.flag) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlUtils.bindTextureLocation(new ResourceLocation("voxelmap/images/colorPicker.png"));
            drawTexturedRect(this.getWidth() / 2 - 128, this.getHeight() / 2 - 128, 0.0F, 0.0F, 256, 256);
        }

        this.drawTooltip(this.tooltip, n, n2);
    }

    public void toggleDimension() {
        if (this.waypoint.dimensions.size() > 1
            && this.waypoint.dimensions.contains(this.dimensionContainer.id)
            && this.dimensionContainer.id != Minecraft.getMinecraft().thePlayer.dimension) {
            this.waypoint.dimensions.remove(new Integer(this.dimensionContainer.id));
        } else if (!this.waypoint.dimensions.contains(this.dimensionContainer.id)) {
            this.waypoint.dimensions.add(new Integer(this.dimensionContainer.id));
        }
    }
}
