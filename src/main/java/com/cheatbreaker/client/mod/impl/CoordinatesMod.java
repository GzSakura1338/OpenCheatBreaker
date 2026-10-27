package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class CoordinatesMod extends AbstractHudMod {
   public List<Long> longValueList = new ArrayList<>();
   public Setting showWhileTyping;
   public Setting coordinateOptions;
   public Setting coordinates;
   public Setting hideXCoordinate;
   public Setting hideYCoordinate;
   public Setting hideZCoordinate;
   public Setting showAxisLabels;
   public Setting showBiome;
   public Setting showChunkCounter;
   public Setting mode;
   public Setting directionOptions;
   public Setting direction;
   public Setting showMarkers;
   public Setting backgroundOptions;
   public Setting showBackground;
   public Setting showBorder;
   public Setting borderThickness;
   public Setting colorOptions;
   public Setting baseColor;
   public Setting xColor;
   public Setting yColor;
   public Setting zColor;
   public Setting biomeColor;
   public Setting chunkCounterColor;
   public Setting directionColor;
   public Setting applyBaseColorToAll;
   public Setting backgroundColor;
   public Setting borderColor;
   public Setting customLine;

   @Override
   public void writeSettingComments(BufferedWriter bufferedWriter, Setting setting) throws java.io.IOException {
      if (setting == this.customLine) {
         bufferedWriter.write("# Customize your HUD info display string.");
         bufferedWriter.newLine();
         bufferedWriter.write("# (& color formatting is allowed)");
         bufferedWriter.newLine();
         bufferedWriter.write("# Optional uses:");
         bufferedWriter.newLine();
         bufferedWriter.write("# %FPS% - Display current FPS.");
         bufferedWriter.newLine();
         bufferedWriter.write("# %DIR% - Display current look direction.");
         bufferedWriter.newLine();
         bufferedWriter.write("# %CPS% - Display CPS.");
         bufferedWriter.newLine();
         bufferedWriter.write("# %COORDS% - Display coordinates.");
         bufferedWriter.newLine();
         bufferedWriter.write("# %IP% - Current server IP.");
         bufferedWriter.newLine();
         bufferedWriter.write("# %X% - Your X location.");
         bufferedWriter.newLine();
         bufferedWriter.write("# %Y% - Your (foot) Y location.");
         bufferedWriter.newLine();
         bufferedWriter.write("# %Z% - Your Z location.");
         bufferedWriter.newLine();
         bufferedWriter.write("# %NL% - Break into a new line.");
         bufferedWriter.newLine();
      }
   }

   public String formatText(String str) {
      String[] strs = new String[]{"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
      double d = MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYaw) + 180.0;
      d += 22.5;
      d %= 360.0;
      String str2 = strs[MathHelper.floor_double(d / 45.0)];
      int n = MathHelper.floor_double(this.mc.thePlayer.posX);
      int minY = (int)this.mc.thePlayer.getEntityBoundingBox().minY;
      int n2 = MathHelper.floor_double(this.mc.thePlayer.posZ);
      str = !this.mc.isIntegratedServerRunning() && this.mc.theWorld != null
         ? str.replaceAll("%IP%", this.mc.getCurrentServerData().serverIP)
         : str.replaceAll("%IP%", "?");
      return str.replaceAll("%FPS%", Minecraft.getDebugFPS() + "")
         .replaceAll("%DIR%", str2)
         .replaceAll("%CPS%", this.longValueList.size() + "")
         .replaceAll("%COORDS%", String.format("%1$d, %2$d, %3$d", n, minY, n2))
         .replaceAll("%X%", n + "")
         .replaceAll("%Y%", minY + "")
         .replaceAll("%Z%", n2 + "");
   }

   public float render(float f, boolean flag, String str, String str2, int n, boolean flag2) {
      if (!flag) {
         f = this.mc.fontRendererObj.drawStringWithShadow(", ", f, 0.0F, this.baseColor.getColorValue());
      }

      if (flag2) {
         f = this.mc.fontRendererObj.drawStringWithShadow(str, f, 0.0F, this.baseColor.getColorValue());
      }

      return this.mc.fontRendererObj.drawStringWithShadow(str2, f, 0.0F, n);
   }

   public CoordinatesMod() {
      super("Coordinates");
      this.setAnchorPointAndDefault(AnchorPoint.LEFT_TOP);
      this.setTranslationAndDefault(-1.0F, 0.0F);
      this.setEnabledByDefault(false);
      this.showWhileTyping = new Setting(this, "Show While Typing")
         .setDescription("Show the mod when opening chat.")
         .changeValue(true)
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.coordinateOptions = new SettingGroup(this, "Coordinate Options").changeValue(true);
      this.coordinates = new Setting(this, "Coordinates")
         .setDescription("Show the coordinates.")
         .withHierarchyParents(this.coordinateOptions)
         .changeValue(true);
      this.hideXCoordinate = new Setting(this, "Hide X Coordinate")
         .setDescription("Hide the X coordinate.")
         .changeValue(false)
         .withHierarchyParents(this.coordinates);
      this.hideYCoordinate = new Setting(this, "Hide Y Coordinate")
         .setDescription("Hide the Y coordinate.")
         .changeValue(false)
         .withHierarchyParents(this.coordinates);
      this.hideZCoordinate = new Setting(this, "Hide Z Coordinate")
         .setDescription("Hide the Z coordinate.")
         .changeValue(false)
         .withHierarchyParents(this.coordinates);
      this.showAxisLabels = new Setting(this, "Show Axis Labels")
         .setDescription("Show \"X: \", \"Y: \", \"Z: \" labels before each value.")
         .changeValue(false)
         .withHierarchyParents(this.coordinates);
      this.showBiome = new Setting(this, "Show Biome")
         .setDescription("Show the biome you're currently standing in.")
         .changeValue(false)
         .withHierarchyParents(this.coordinates);
      this.showChunkCounter = new Setting(this, "Show Chunk Counter")
         .setDescription("Show the number of chunks currently being rendered.")
         .changeValue(false)
         .withHierarchyParents(this.coordinates);
      this.mode = new Setting(this, "Mode")
         .setDescription("Layout the mod should display.")
         .changeValue("Horizontal")
         .setStringValues("Horizontal", "Vertical")
         .withHierarchyParents(this.coordinates);
      this.directionOptions = new SettingGroup(this, "Direction Options").changeValue(true);
      this.direction = new Setting(this, "Direction")
         .setDescription("Show the direction the player is facing.")
         .withHierarchyParents(this.directionOptions)
         .changeValue(true);
      this.showMarkers = new Setting(this, "Show Markers").setDescription("Shows Directions Markers").changeValue(true).withHierarchyParents(this.direction);
      this.backgroundOptions = new SettingGroup(this, "Background Options").changeValue(false);
      this.showBackground = new Setting(this, "Show Background")
         .setDescription("Draw a background behind the mod.")
         .changeValue(false)
         .withHierarchyParents(this.backgroundOptions);
      this.showBorder = new Setting(this, "Show Border")
         .setDescription("Draw a border around the background.")
         .changeValue(false)
         .withHierarchyParents(this.showBackground);
      this.borderThickness = new Setting(this, "Border Thickness")
         .setDescription("Change the thickness of the border.")
         .changeValue(1.0F)
         .setRange(0.25F, 5.0F)
         .setUnit("px")
         .withHierarchyParents(this.showBorder);
      this.colorOptions = new SettingGroup(this, "Color Options").changeValue(false);
      this.baseColor = new Setting(this, "Base Color")
         .setDescription(
            "Color used for punctuation and labels. While \"Apply Base Color To All\" is on, changing this also live-updates every other coordinate color below."
         )
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorOptions)
         .setOnUpdate(obj -> this.run());
      this.xColor = new Setting(this, "X Color")
         .setDescription("Change the X coordinate text color.")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorOptions);
      this.yColor = new Setting(this, "Y Color")
         .setDescription("Change the Y coordinate text color.")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorOptions);
      this.zColor = new Setting(this, "Z Color")
         .setDescription("Change the Z coordinate text color.")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorOptions);
      this.biomeColor = new Setting(this, "Biome Color")
         .setDescription("Change the biome text color.")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorOptions);
      this.chunkCounterColor = new Setting(this, "Chunk Counter Color")
         .setDescription("Change the chunk counter text color.")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorOptions);
      this.directionColor = new Setting(this, "Direction Color")
         .setDescription("Change the direction text color.")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorOptions);
      this.applyBaseColorToAll = new Setting(this, "Apply Base Color To All")
         .setDescription("While enabled, the Base Color is continuously copied into every coordinate component color above (live while dragging).")
         .changeValue(false)
         .withHierarchyParents(this.colorOptions)
         .setOnUpdate(obj -> this.run());
      this.backgroundColor = new Setting(this, "Background Color")
         .setDescription("Sets the color for the background.")
         .changeValue(1862270976)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorOptions, this.showBackground);
      this.borderColor = new Setting(this, "Border Color")
         .setDescription("Sets the color for the border.")
         .changeValue(-1627389952)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorOptions, this.showBorder);
      this.customLine = new Setting(this, "Custom Line").changeValue("").setCustomizationLevel(SettingsLevel.ADVANCED);
      this.setLabel("(16, 65, 120) NW", 1.0F);
      this.setDescription("Displays your XYZ coordinates and direction.");
      this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
      this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
      this.addEventHandler(IntEvent1.class, this::onIntEvent1);
   }

   public void onIntEvent1(IntEvent1 intEvent1) {
      if (intEvent1.getEventButton() == 0 && ((String)this.customLine.getValue()).contains("%CPS%")) {
         this.longValueList.add(System.currentTimeMillis());
      }
   }

   public void run() {
      if (this.applyBaseColorToAll != null && this.applyBaseColorToAll.getBooleanValue()) {
         int colorValue = this.baseColor.getColorValue();
         this.xColor.changeValue(colorValue);
         this.yColor.changeValue(colorValue);
         this.zColor.changeValue(colorValue);
         this.biomeColor.changeValue(colorValue);
         this.chunkCounterColor.changeValue(colorValue);
         this.directionColor.changeValue(colorValue);
      }
   }

   public String createStringBuilder() {
      if (this.mc.renderGlobal != null && this.mc.renderGlobal.viewFrustum != null) {
         RenderChunk[] renderChunks = this.mc.renderGlobal.viewFrustum.renderChunks;
         int length = renderChunks.length;
         int n = 0;

         for (RenderChunk renderChunk : renderChunks) {
            if (renderChunk.compiledChunk != CompiledChunk.DUMMY && !renderChunk.compiledChunk.isEmpty()) {
               n++;
            }
         }

         return n + "/" + length;
      } else {
         return "0/0";
      }
   }

   public float computeFloat(String str, String str2, int n, float f, boolean flag) {
      float f2 = flag ? this.mc.fontRendererObj.drawStringWithShadow(str, 0.0F, f, this.baseColor.getColorValue()) : 0.0F;
      return this.mc.fontRendererObj.drawStringWithShadow(str2, f2, f, n);
   }

   public void onClientTickEvent(ClientTickEvent clientTickEvent) {
      if (((String)this.customLine.getValue()).contains("%CPS%")) {
         this.longValueList.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
      }
   }

   public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
      if (this.isRenderHud() && (!this.hidden || this.mc.currentScreen instanceof ModsScreen)) {
         GlStateManager.pushMatrix();
         GlStateManager.enableBlend();
         this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
         if (this.showBackground.getBooleanValue()) {
            RenderUtils.drawRoundedRect(0.0F, 0.0F, this.hudWidth, this.hudHeight, 0.0F, this.backgroundColor.getColorValue());
            if (this.showBorder.getBooleanValue()) {
               RenderUtils.drawRoundedBorder(
                  0.0F, 0.0F, this.hudWidth, this.hudHeight, 0.0F, this.borderThickness.getFloatValue(), this.borderColor.getColorValue()
               );
            }
         }

         int n = MathHelper.floor_double(this.mc.thePlayer.posX);
         int minY = (int)this.mc.thePlayer.getEntityBoundingBox().minY;
         int n2 = MathHelper.floor_double(this.mc.thePlayer.posZ);
         if (!this.mc.ingameGUI.getChatGUI().getChatOpen() || (Boolean)this.showWhileTyping.getValue()) {
            if (this.customLine.getValue().equals("")) {
               String[] strs = new String[]{"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
               String[] strs2 = new String[]{"", "+", "+", "+", "", "-", "-", "-"};
               String[] strs3 = new String[]{"-", "-", "", "+", "+", "+", "", "-"};
               double d = MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYaw) + 180.0;
               d += 22.5;
               d %= 360.0;
               int n3 = MathHelper.floor_double(d / 45.0);
               String str = strs[n3];
               String str2 = strs2[n3];
               String str3 = strs3[n3];
               boolean flag = !this.hideXCoordinate.getBooleanValue();
               boolean flag2 = !this.hideYCoordinate.getBooleanValue();
               boolean flag3 = !this.hideZCoordinate.getBooleanValue();
               boolean booleanValue = this.showAxisLabels.getBooleanValue();
               int n4 = 0;
               float f = 4.0F;
               if ((Boolean)this.coordinates.getValue()) {
                  if (!this.mode.getValue().equals("Horizontal")) {
                     float f2 = 0.0F;
                     float f3 = 0.0F;
                     if (flag) {
                        f3 = Math.max(f3, this.computeFloat("X: ", String.valueOf(n), this.xColor.getColorValue(), f2, booleanValue));
                        f2 += 12.0F;
                     }

                     if (flag2) {
                        f3 = Math.max(f3, this.computeFloat("Y: ", String.valueOf(minY), this.yColor.getColorValue(), f2, booleanValue));
                        f2 += 12.0F;
                     }

                     if (flag3) {
                        f3 = Math.max(f3, this.computeFloat("Z: ", String.valueOf(n2), this.zColor.getColorValue(), f2, booleanValue));
                        f2 += 12.0F;
                     }

                     if (this.showChunkCounter.getBooleanValue()) {
                        f3 = Math.max(f3, this.computeFloat("C: ", this.createStringBuilder(), this.chunkCounterColor.getColorValue(), f2, booleanValue));
                        f2 += 12.0F;
                     }

                     if (this.showBiome.getBooleanValue()) {
                        f3 = Math.max(
                           f3, this.computeFloat("Biome: ", this.createBlockPos(n, minY, n2), this.biomeColor.getColorValue(), f2, booleanValue)
                        );
                        f2 += 12.0F;
                     }

                     n4 = (int)Math.max(f3, 50.0F);
                     f = f2 == 0.0F ? 4.0F : f2;
                  } else {
                     String str4 = flag && this.showMarkers.getBooleanValue() && !str2.equals("") ? n + " (" + str2 + ")" : String.valueOf(n);
                     String str5 = flag3 && this.showMarkers.getBooleanValue() && !str3.equals("") ? n2 + " (" + str3 + ")" : String.valueOf(n2);
                     float drawStringWithShadow = this.mc.fontRendererObj.drawStringWithShadow("(", 0.0F, 0.0F, this.baseColor.getColorValue());
                     boolean flag4 = true;
                     if (flag) {
                        drawStringWithShadow = this.render(drawStringWithShadow, true, "X: ", str4, this.xColor.getColorValue(), booleanValue);
                        flag4 = false;
                     }

                     if (flag2) {
                        drawStringWithShadow = this.render(drawStringWithShadow, flag4, "Y: ", String.valueOf(minY), this.yColor.getColorValue(), booleanValue);
                        flag4 = false;
                     }

                     if (flag3) {
                        drawStringWithShadow = this.render(drawStringWithShadow, flag4, "Z: ", str5, this.zColor.getColorValue(), booleanValue);
                        flag4 = false;
                     }

                     if (this.showChunkCounter.getBooleanValue()) {
                        drawStringWithShadow = this.render(drawStringWithShadow, flag4, "C: ", this.createStringBuilder(), this.chunkCounterColor.getColorValue(), booleanValue);
                        flag4 = false;
                     }

                     if (this.showBiome.getBooleanValue()) {
                        drawStringWithShadow = this.render(drawStringWithShadow, flag4, "Biome: ", this.createBlockPos(n, minY, n2), this.biomeColor.getColorValue(), booleanValue);
                        flag4 = false;
                     }

                     drawStringWithShadow = this.mc.fontRendererObj.drawStringWithShadow(")", drawStringWithShadow, 0.0F, this.baseColor.getColorValue());
                     n4 = (int)drawStringWithShadow + ((boolean)this.direction.getValue() ? 4 : 0);
                  }
               }

               if ((Boolean)this.direction.getValue()) {
                  if (this.showMarkers.getBooleanValue() && !this.mode.getValue().equals("Horizontal")) {
                     this.mc
                        .fontRendererObj
                        .drawStringWithShadow(str2, n4, f / 2.0F - 4.0F - this.mc.fontRendererObj.FONT_HEIGHT, this.directionColor.getColorValue());
                     this.mc
                        .fontRendererObj
                        .drawStringWithShadow(str3, n4, f / 2.0F - 4.0F + this.mc.fontRendererObj.FONT_HEIGHT, this.directionColor.getColorValue());
                  }

                  float f2 = this.mode.getValue().equals("Horizontal") ? f - 4.0F : f / 2.0F - 4.0F;
                  this.mc.fontRendererObj.drawStringWithShadow(str, n4, f2, this.directionColor.getColorValue());
                  n4 += this.mc.fontRendererObj.getStringWidth(str);
               }

               this.setHudSize(
                  n4,
                  Math.max(!this.mode.getValue().equals("Horizontal") && (Boolean)this.coordinates.getValue() ? f : f, this.mc.fontRendererObj.FONT_HEIGHT)
               );
            } else {
               String[] split = ((String)this.customLine.getValue()).split("%NL%");
               float f = -1.0F;
               float f2 = split.length * (this.mc.fontRendererObj.FONT_HEIGHT + 1);
               int n3 = 0;

               for (String str : split) {
                  float drawStringWithShadow = this.mc
                     .fontRendererObj
                     .drawStringWithShadow(this.formatText(str), 0.0F, (this.mc.fontRendererObj.FONT_HEIGHT + 1) * n3, -1);
                  if (drawStringWithShadow > f) {
                     f = drawStringWithShadow;
                  }

                  this.setHudSize((int)f, (int)Math.max(f2, this.mc.fontRendererObj.FONT_HEIGHT));
                  n3++;
               }
            }
         }

         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
      }
   }

   public String createBlockPos(int n, int n2, int n3) {
      return this.mc.theWorld == null ? "?" : this.mc.theWorld.getBiomeGenForCoords(new BlockPos(n, n2, n3)).biomeName;
   }
}
