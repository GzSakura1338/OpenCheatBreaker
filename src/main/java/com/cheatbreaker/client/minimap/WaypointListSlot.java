package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import java.awt.Color;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;

public class WaypointListSlot extends GuiSlot {
   public WaypointsScreen waypointsScreen;
   public long longValue;
   public Waypoint waypoint;
   public ArrayList<Waypoint> waypointList;

   public void process(int n, boolean flag) {
      final int n2 = flag ? 1 : -1;
      this.waypointsScreen.minimapSettings.getClass();
      if (n == 1) {
         final ArrayList waypoints = this.waypointsScreen.waypointManager.getWaypoints();
         Collections.sort(this.waypointList, new Comparator<Waypoint>() {
            public int compare(Waypoint waypoint, Waypoint waypoint2) {
               return Double.compare(waypoints.indexOf(waypoint), waypoints.indexOf(waypoint2)) * n2;
            }
         });
      } else {
         this.waypointsScreen.minimapSettings.getClass();
         if (n == 3) {
            if (flag) {
               Collections.sort(this.waypointList);
            } else {
               Collections.sort(this.waypointList, Collections.reverseOrder());
            }
         } else {
            this.waypointsScreen.minimapSettings.getClass();
            if (n == 2) {
               final Collator collator = Translations.getCollator();
               Collections.sort(this.waypointList, new Comparator<Waypoint>() {
                  public int compare(Waypoint waypoint, Waypoint waypoint2) {
                     return collator.compare(waypoint.name, waypoint2.name) * n2;
                  }
               });
            } else {
               this.waypointsScreen.minimapSettings.getClass();
               if (n == 4) {
                  Collections.sort(this.waypointList, new Comparator<Waypoint>() {
                     public int compare(Waypoint waypoint, Waypoint waypoint2) {
                        float f = Color.RGBtoHSB((int)(waypoint.red * 255.0F), (int)(waypoint.green * 255.0F), (int)(waypoint.blue * 255.0F), null)[0];
                        float f2 = Color.RGBtoHSB((int)(waypoint2.red * 255.0F), (int)(waypoint2.green * 255.0F), (int)(waypoint2.blue * 255.0F), null)[0];
                        return Double.compare(f, f2) * n2;
                     }
                  });
               }
            }
         }
      }
   }

   public WaypointListSlot(WaypointsScreen waypointsScreen) {
      super(waypointsScreen.minimapSettings.mc, waypointsScreen.getWidth(), waypointsScreen.getHeight(), 54, waypointsScreen.getHeight() - 65 + 4, 18);
      this.waypointsScreen = waypointsScreen;
      this.waypointList = new ArrayList<>();

      for (Waypoint waypoint : this.waypointsScreen.waypointManager.getWaypoints()) {
         if (waypoint.inWorld && waypoint.inDimension) {
            this.waypointList.add(waypoint);
         }
      }
   }

   @Override
   public void elementClicked(int n, boolean flag, int n2, int n3) {
      Waypoint waypoint = this.waypointList.get(n);
      this.waypointsScreen.setSelectedWaypoint(waypoint);
      int n4 = this.waypointsScreen.getWidth() / 2 - 92 - 16;
      byte b = 3;
      short s = 215;
      if (this.mouseX >= n4 + s - 16 - b && this.mouseX <= n4 + s + b) {
         long currentTimeMillis = System.currentTimeMillis();
         if (waypoint != this.waypoint || currentTimeMillis - this.longValue > 50L) {
            this.waypointsScreen.toggleSelectedWaypoint();
            this.waypoint = waypoint;
            this.longValue = currentTimeMillis;
         }
      } else if (flag) {
         Mouse.next();
         this.waypointsScreen.editWaypoint(this.waypointsScreen.selectedWaypoint);
         return;
      }
   }

   @Override
   public void drawSlot(int n, int n2, int n3, int n4, int n5, int n6) {
      Waypoint waypoint = this.waypointList.get(n);
      this.waypointsScreen
         .drawCenteredString(
            this.waypointsScreen.getFontRendererObj(),
            waypoint.deathpoint ? EnumChatFormatting.GRAY + "(Server) " + EnumChatFormatting.RESET + waypoint.name : waypoint.name,
            this.waypointsScreen.getWidth() / 2,
            n3 + 3,
            waypoint.getRgbColor()
         );
      byte b = 3;
      if (this.mouseX >= n2 - b && this.mouseY >= n3 && this.mouseX <= n2 + 215 + b && this.mouseY <= n3 + this.slotHeight) {
         String str;
         if (this.mouseX >= n2 + 215 - 16 - b && this.mouseX <= n2 + 215 + b) {
            str = waypoint.enabled ? Translations.translate("minimap.waypoints.disable") : Translations.translate("minimap.waypoints.enable");
         } else {
            str = "X: " + waypoint.getX() + " Z: " + waypoint.getZ();
            if (waypoint.getY() > 0) {
               str = str + " Y: " + waypoint.getY();
            }
         }

         WaypointsScreen.setTooltip(this.waypointsScreen, str);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlUtils.bindTextureByName("textures/gui/container/inventory.png");
      int n7 = waypoint.enabled ? 72 : 90;
      short s = 216;
      this.waypointsScreen.drawTexturedModalRect(n2 + 198, n3 - 2, n7, s, 16, 16);
   }

   @Override
   public int getSize() {
      return this.waypointList.size();
   }

   @Override
   public boolean isSelected(int n) {
      return this.waypointList.get(n).equals(this.waypointsScreen.selectedWaypoint);
   }

   @Override
   public void drawBackground() {
      this.waypointsScreen.drawDefaultBackground();
   }
}
