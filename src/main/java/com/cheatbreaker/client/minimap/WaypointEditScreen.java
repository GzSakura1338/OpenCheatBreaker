package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.util.Translations;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class WaypointEditScreen extends AbstractMinimapScreen {
   public DimensionContainer dimensionContainer = null;
   public GuiTextField guiTextField;
   public String text = null;
   public boolean flag = false;
   public Random random = new Random();
   public IVoxelMap voxelMap;
   public DimensionSlot dimensionSlot;
   public IWaypointManager waypointManager;
   public GuiButton enabled;
   public GuiTextField guiTextField2;
   public GuiTextField guiTextField3;
   public IColorManager colorManager;
   public GuiTextField guiTextField4;
   public WaypointsScreen waypointsScreen;
   public Waypoint waypoint;
   public float floatValue;
   public float floatValue2;
   public float floatValue3;
   public boolean flag2;

   @Override
   public void keyTyped(char c, int n) throws java.io.IOException {
      this.guiTextField2.textboxKeyTyped(c, n);
      this.guiTextField4.textboxKeyTyped(c, n);
      this.guiTextField.textboxKeyTyped(c, n);
      this.guiTextField3.textboxKeyTyped(c, n);
      if (c == '\t') {
         if (this.guiTextField2.isFocused()) {
            this.guiTextField2.setFocused(false);
            this.guiTextField4.setFocused(true);
            this.guiTextField.setFocused(false);
            this.guiTextField3.setFocused(false);
         } else if (this.guiTextField4.isFocused()) {
            this.guiTextField2.setFocused(false);
            this.guiTextField4.setFocused(false);
            this.guiTextField.setFocused(true);
            this.guiTextField3.setFocused(false);
         } else if (this.guiTextField.isFocused()) {
            this.guiTextField2.setFocused(false);
            this.guiTextField4.setFocused(false);
            this.guiTextField.setFocused(false);
            this.guiTextField3.setFocused(true);
         } else if (this.guiTextField3.isFocused()) {
            this.guiTextField2.setFocused(true);
            this.guiTextField4.setFocused(false);
            this.guiTextField.setFocused(false);
            this.guiTextField3.setFocused(false);
         }
      }

      if (c == '\r') {
         this.actionPerformed((GuiButton)this.getButtonList().get(0));
      }

      boolean flag = this.guiTextField2.getText().length() > 0;

      try {
         int parseInt = Integer.parseInt(this.guiTextField4.getText());
         flag = flag;
      } catch (NumberFormatException e) {
         flag = false;
      }

      try {
         int parseInt = Integer.parseInt(this.guiTextField.getText());
         flag = flag;
      } catch (NumberFormatException e) {
         flag = false;
      }

      try {
         int parseInt = Integer.parseInt(this.guiTextField3.getText());
         flag = flag;
      } catch (NumberFormatException e) {
         flag = false;
      }

      ((GuiButton)this.getButtonList().get(0)).enabled = flag;
      if (n == 1) {
         this.waypoint.red = this.floatValue;
         this.waypoint.green = this.floatValue2;
         this.waypoint.blue = this.floatValue3;
         this.waypoint.enabled = this.flag2;
      }

      super.keyTyped(c, n);
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      super.run8();
      this.text = null;
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
      this.guiTextField2.drawTextBox();
      this.guiTextField4.drawTextBox();
      this.guiTextField.drawTextBox();
      this.guiTextField3.drawTextBox();
      GlStateManager.color(this.waypoint.red, this.waypoint.green, this.waypoint.blue, 1.0F);
      GlUtils.bindTextureId(-1);
      this.drawTexturedModalRect(this.getWidth() / 2 + 85, this.getHeight() / 6 + 82 + 11, 0, 0, 16, 10);
      super.drawScreen(n, n2, f);
      if (this.flag) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlUtils.bindTextureLocation(new ResourceLocation("voxelmap/images/colorPicker.png"));
         this.drawTexturedModalRect(this.getWidth() / 2 - 128, this.getHeight() / 2 - 128, 0, 0, 256, 256);
      }

      this.handleString(this.text, n, n2);
   }

   public void setDimensionContainer(DimensionContainer dimensionContainer) {
      this.dimensionContainer = dimensionContainer;
   }

   public static String formatText(WaypointEditScreen waypointEditScreen, String str) {
      return waypointEditScreen.text = str;
   }

   public void handleString(String str, int n, int n2) {
      if (str != null) {
         int n3 = n + 12;
         int n4 = n2 - 12;
         int stringWidth = this.getFontRendererObj().getStringWidth(str);
         this.drawGradientRect(n3 - 3, n4 - 3, n3 + stringWidth + 3, n4 + 8 + 3, -1073741824, -1073741824);
         this.getFontRendererObj().drawStringWithShadow(str, n3, n4, -1);
      }
   }

   public void run10() {
      if (this.waypoint.dimensions.size() > 1
         && this.waypoint.dimensions.contains(this.dimensionContainer.id)
         && this.dimensionContainer.id != Minecraft.getMinecraft().thePlayer.dimension) {
         this.waypoint.dimensions.remove(new Integer(this.dimensionContainer.id));
      } else if (!this.waypoint.dimensions.contains(this.dimensionContainer.id)) {
         this.waypoint.dimensions.add(new Integer(this.dimensionContainer.id));
      }
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      if (guiButton.enabled) {
         if (guiButton.id == 2) {
            this.waypoint.enabled = !this.waypoint.enabled;
         }

         if (guiButton.id == 1) {
            this.waypoint.red = this.floatValue;
            this.waypoint.green = this.floatValue2;
            this.waypoint.blue = this.floatValue3;
            this.waypoint.enabled = this.flag2;
            if (this.waypointsScreen != null) {
               this.waypointsScreen.confirmClicked(false, 0);
            } else {
               if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue()) {
                  this.mc.entityRenderer.run();
               }

               this.getMc().displayGuiScreen(null);
            }
         } else if (guiButton.id == 0) {
            this.waypoint.name = this.guiTextField2.getText();
            this.waypoint.setX(Integer.parseInt(this.guiTextField4.getText()));
            this.waypoint.setZ(Integer.parseInt(this.guiTextField.getText()));
            this.waypoint.setY(Integer.parseInt(this.guiTextField3.getText()));
            if (this.waypointsScreen != null) {
               this.waypointsScreen.confirmClicked(true, 0);
            } else {
               this.waypointManager.addWaypoint(this.waypoint);
               if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue()) {
                  Minecraft.getMinecraft().entityRenderer.run();
               }

               this.getMc().displayGuiScreen(null);
            }
         }
      }
   }

   @Override
   public void updateScreen() {
      this.guiTextField2.updateCursorCounter();
      this.guiTextField4.updateCursorCounter();
   }

   public WaypointEditScreen(IVoxelMap voxelMap, WaypointsScreen waypointsScreen, Waypoint waypoint) {
      this.voxelMap = voxelMap;
      this.waypointManager = voxelMap.getWaypointManager();
      this.colorManager = voxelMap.getColorManager();
      this.waypointsScreen = waypointsScreen;
      this.waypoint = waypoint;
      this.floatValue = this.waypoint.red;
      this.floatValue2 = this.waypoint.green;
      this.floatValue3 = this.waypoint.blue;
      this.flag2 = this.waypoint.enabled;
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.getButtonList().clear();
      this.getButtonList().add(new GuiButton(0, this.getWidth() / 2 - 155, this.getHeight() / 6 + 168, 150, 20, Translations.translate("addServer.add")));
      this.getButtonList().add(new GuiButton(1, this.getWidth() / 2 + 5, this.getHeight() / 6 + 168, 150, 20, Translations.translate("gui.cancel")));
      this.guiTextField2 = new GuiTextField(10, this.getFontRendererObj(), this.getWidth() / 2 - 100, this.getHeight() / 6 + 0 + 13, 200, 20);
      this.guiTextField2.setFocused(true);
      this.guiTextField2.setText(this.waypoint.name);
      this.guiTextField4 = new GuiTextField(11, this.getFontRendererObj(), this.getWidth() / 2 - 100, this.getHeight() / 6 + 41 + 13, 56, 20);
      this.guiTextField4.setMaxStringLength(128);
      this.guiTextField4.setText("" + this.waypoint.getX());
      this.guiTextField = new GuiTextField(12, this.getFontRendererObj(), this.getWidth() / 2 - 28, this.getHeight() / 6 + 41 + 13, 56, 20);
      this.guiTextField.setMaxStringLength(128);
      this.guiTextField.setText("" + this.waypoint.getZ());
      this.guiTextField3 = new GuiTextField(13, this.getFontRendererObj(), this.getWidth() / 2 + 44, this.getHeight() / 6 + 41 + 13, 56, 20);
      this.guiTextField3.setMaxStringLength(128);
      this.guiTextField3.setText("" + this.waypoint.getY());
      this.getButtonList()
         .add(
            this.enabled = new GuiButton(
               2, this.getWidth() / 2 - 101, this.getHeight() / 6 + 82 + 6, 100, 20, "Enabled: " + (this.waypoint.enabled ? "On" : "Off")
            )
         );
      ((GuiButton)this.getButtonList().get(0)).enabled = this.guiTextField2.getText().length() > 0;
      this.dimensionSlot = new DimensionSlot(this);
      this.dimensionSlot.setScrollButtonIds(7, 8);
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) throws java.io.IOException {
      if (!this.flag) {
         super.mouseClicked(n, n2, n3);
         this.guiTextField2.mouseClicked(n, n2, n3);
         this.guiTextField4.mouseClicked(n, n2, n3);
         this.guiTextField.mouseClicked(n, n2, n3);
         this.guiTextField3.mouseClicked(n, n2, n3);
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
}
