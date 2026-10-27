package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.MinecraftHelper;
import com.cheatbreaker.client.util.Translations;
import java.util.Random;
import java.util.TreeSet;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.server.MinecraftServer;

public class WaypointsScreen extends AbstractMinimapScreen implements GuiYesNoCallback {
   public String title = "Waypoints";
   public boolean editClicked = false;
   public Waypoint selectedWaypoint = null;
   public Waypoint newWaypoint = null;
   public boolean deleteClicked = false;
   public boolean newClicked = false;
   public String tooltip = null;
   public GuiButton minimapWaypointsSortbyname;
   public Random random = new Random();
   public boolean changedSort = false;
   public GuiButton minimapWaypointsTeleportto;
   public IVoxelMap voxelMap;
   public GuiButton selectserverEdit;
   public GuiScreen guiScreen;
   public GuiButton selectserverDelete;
   public MinimapSettings minimapSettings;
   public GuiButton minimapWaypointsSortbycreated;
   public GuiButton minimapWaypointsSortbycolor;
   public WaypointListSlot waypointListSlot;
   public IWaypointManager waypointManager;
   public GuiButton minimapWaypointsSortbydistance;

   public boolean canTeleport() {
      boolean isIntegratedServerRunning = this.minimapSettings.mc.isIntegratedServerRunning();
      return isIntegratedServerRunning ? MinecraftServer.getServer().worldServers[0].getWorldInfo().areCommandsAllowed() : true;
   }

   public WaypointsScreen(GuiScreen guiScreen, IVoxelMap voxelMap) {
      this.voxelMap = voxelMap;
      this.guiScreen = guiScreen;
      this.minimapSettings = voxelMap.getMapOptions();
      this.waypointManager = voxelMap.getWaypointManager();
   }

   public static GuiButton getDeleteButton(WaypointsScreen waypointsScreen) {
      return waypointsScreen.selectserverDelete;
   }

   public void drawTooltip(String str, int n, int n2) {
      if (str != null) {
         int n3 = n + 12;
         int n4 = n2 - 12;
         int stringWidth = this.getFontRendererObj().getStringWidth(str);
         this.drawGradientRect(n3 - 3, n4 - 3, n3 + stringWidth + 3, n4 + 8 + 3, -1073741824, -1073741824);
         this.getFontRendererObj().drawStringWithShadow(str, n3, n4, -1);
      }
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      if (guiButton.enabled) {
         if (guiButton.id > 0) {
            this.minimapSettings.setWaypointSortBy(guiButton.id);
            this.changedSort = true;
            this.updateSortButtons();
         }

         if (guiButton.id == -1) {
            this.editWaypoint(this.selectedWaypoint);
         }

         if (guiButton.id == -2) {
            String name = this.selectedWaypoint.name;
            if (name != null) {
               this.deleteClicked = true;
               String translate = Translations.translate("minimap.waypoints.deleteconfirm");
               String str = "'" + name + "' " + Translations.translate("selectServer.deleteWarning");
               String translate2 = Translations.translate("selectServer.deleteButton");
               String translate3 = Translations.translate("gui.cancel");
               GuiYesNo guiYesNo = new GuiYesNo(this, translate, str, translate2, translate3, this.waypointManager.getWaypoints().indexOf(this.selectedWaypoint));
               this.getMc().displayGuiScreen(guiYesNo);
            }
         }

         if (guiButton.id == -3) {
            if (this.minimapSettings.mc.isIntegratedServerRunning()) {
               this.minimapSettings.mc.thePlayer.sendChatMessage("/ztp " + this.selectedWaypoint.name);
               this.getMc().displayGuiScreen(null);
            } else {
               if (this.selectedWaypoint.getY() > 0) {
                  this.minimapSettings
                     .mc
                     .thePlayer
                     .sendChatMessage(
                        "/tp "
                           + this.minimapSettings.mc.thePlayer.getName()
                           + " "
                           + this.selectedWaypoint.getX()
                           + " "
                           + this.selectedWaypoint.getY()
                           + " "
                           + this.selectedWaypoint.getZ()
                     );
                  this.minimapSettings
                     .mc
                     .thePlayer
                     .sendChatMessage("/tppos " + this.selectedWaypoint.getX() + " " + this.selectedWaypoint.getY() + " " + this.selectedWaypoint.getZ());
               } else {
                  this.minimapSettings
                     .mc
                     .thePlayer
                     .sendChatMessage(
                        "/tp "
                           + this.minimapSettings.mc.thePlayer.getName()
                           + " "
                           + this.selectedWaypoint.getX()
                           + " "
                           + (this.minimapSettings.mc.thePlayer.dimension != -1 ? "128" : "64")
                           + " "
                           + this.selectedWaypoint.getZ()
                     );
                  this.minimapSettings
                     .mc
                     .thePlayer
                     .sendChatMessage(
                        "/tppos "
                           + this.selectedWaypoint.getX()
                           + " "
                           + (this.minimapSettings.mc.thePlayer.dimension != -1 ? "256" : "64")
                           + " "
                           + this.selectedWaypoint.getZ()
                     );
               }

               this.getMc().displayGuiScreen(null);
            }
         }

         if (guiButton.id == -4) {
            this.createWaypoint();
         }

         if (guiButton.id == -5) {
            this.getMc().displayGuiScreen(new WaypointOptionsScreen(this, this.minimapSettings));
         }

         if (guiButton.id == 65336) {
            this.getMc().displayGuiScreen(this.guiScreen);
         }
      }
   }

   @Override
   public void initGui() {
      boolean flag = false;
      this.title = Translations.translate("minimap.waypoints.title");
      this.waypointListSlot = new WaypointListSlot(this);
      this.waypointListSlot.registerScrollButtons(7, 8);
      this.minimapSettings.getClass();
      this.getButtonList()
         .add(this.minimapWaypointsSortbyname = new GuiButton(2, this.getWidth() / 2 - 154, 34, 77, 20, Translations.translate("minimap.waypoints.sortbyname")));
      this.minimapSettings.getClass();
      this.getButtonList()
         .add(
            this.minimapWaypointsSortbydistance = new GuiButton(
               3, this.getWidth() / 2 - 77, 34, 77, 20, Translations.translate("minimap.waypoints.sortbydistance")
            )
         );
      this.minimapSettings.getClass();
      this.getButtonList()
         .add(this.minimapWaypointsSortbycreated = new GuiButton(1, this.getWidth() / 2, 34, 77, 20, Translations.translate("minimap.waypoints.sortbycreated")));
      this.minimapSettings.getClass();
      this.getButtonList()
         .add(
            this.minimapWaypointsSortbycolor = new GuiButton(4, this.getWidth() / 2 + 77, 34, 77, 20, Translations.translate("minimap.waypoints.sortbycolor"))
         );
      this.getButtonList()
         .add(this.selectserverEdit = new GuiButton(-1, this.getWidth() / 2 - 154, this.getHeight() - 52, 100, 20, Translations.translate("selectServer.edit")));
      this.getButtonList()
         .add(
            this.selectserverDelete = new GuiButton(-2, this.getWidth() / 2 - 50, this.getHeight() - 52, 100, 20, Translations.translate("selectServer.delete"))
         );
      this.getButtonList()
         .add(
            this.minimapWaypointsTeleportto = new GuiButton(
               -3, this.getWidth() / 2 + 4 + 50, this.getHeight() - 52, 100, 20, Translations.translate("minimap.waypoints.teleportto")
            )
         );
      this.getButtonList()
         .add(new GuiButton(-4, this.getWidth() / 2 - 154, this.getHeight() - 28, 100, 20, Translations.translate("minimap.waypoints.newwaypoint")));
      this.getButtonList().add(new GuiButton(-5, this.getWidth() / 2 - 50, this.getHeight() - 28, 100, 20, Translations.translate("menu.options")));
      this.getButtonList().add(new GuiButton(65336, this.getWidth() / 2 + 4 + 50, this.getHeight() - 28, 100, 20, Translations.translate("gui.done")));
      boolean flag2 = this.selectedWaypoint != null;
      this.selectserverEdit.enabled = flag2;
      this.selectserverDelete.enabled = flag2;
      this.minimapWaypointsTeleportto.enabled = flag2 && this.canTeleport();
      this.updateSortButtons();
   }

   public static String setTooltip(WaypointsScreen waypointsScreen, String str) {
      return waypointsScreen.tooltip = str;
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      super.run8();
      this.tooltip = null;
      this.waypointListSlot.drawScreen(n, n2, f);
      this.drawCenteredString(this.getFontRendererObj(), this.title, this.getWidth() / 2, 20, 16777215);
      super.drawScreen(n, n2, f);
      if (this.tooltip != null) {
         this.drawTooltip(this.tooltip, n, n2);
      }
   }

   public void setSelectedWaypoint(Waypoint waypoint) {
      this.selectedWaypoint = waypoint;
      boolean flag = this.selectedWaypoint != null;
      this.selectserverEdit.enabled = flag;
      this.selectserverDelete.enabled = flag;
      this.minimapWaypointsTeleportto.enabled = flag && this.canTeleport();
   }

   public static GuiButton getTeleportButton(WaypointsScreen waypointsScreen) {
      return waypointsScreen.minimapWaypointsTeleportto;
   }

   @Override
   public void handleMouseInput() throws java.io.IOException {
      super.handleMouseInput();
      this.waypointListSlot.handleMouseInput();
   }

   public void createWaypoint() {
      this.newClicked = true;
      float f;
      float f2;
      float f3;
      if (this.waypointManager.getWaypoints().size() == 0) {
         f = 0.0F;
         f2 = 1.0F;
         f3 = 0.0F;
      } else {
         f = this.random.nextFloat();
         f2 = this.random.nextFloat();
         f3 = this.random.nextFloat();
      }

      TreeSet treeSet = new TreeSet();
      treeSet.add(this.minimapSettings.mc.thePlayer.dimension);
      this.newWaypoint = new Waypoint(
         "",
         this.minimapSettings.mc.thePlayer.dimension != -1 ? MinecraftHelper.getBlockX() : MinecraftHelper.getBlockX() * 8,
         this.minimapSettings.mc.thePlayer.dimension != -1 ? MinecraftHelper.getBlockZ() : MinecraftHelper.getBlockZ() * 8,
         MinecraftHelper.getPlayerY() - 1,
         true,
         f,
         f2,
         f3,
         "",
         this.voxelMap.getWaypointManager().getCurrentSubworld(),
         treeSet
      );
      this.getMc().displayGuiScreen(new WaypointEditScreen(this.voxelMap, this, this.newWaypoint));
   }

   public void updateSortButtons() {
      int abs = Math.abs(this.minimapSettings.waypointSortBy);
      boolean flag = this.minimapSettings.waypointSortBy > 0;
      this.waypointListSlot.process(abs, flag);
      String str = flag ? "↑" : "↓";
      this.minimapSettings.getClass();
      if (abs == 2) {
         this.minimapWaypointsSortbyname.displayString = str + " " + Translations.translate("mco.configure.world.name") + " " + str;
      } else {
         this.minimapWaypointsSortbyname.displayString = Translations.translate("mco.configure.world.name");
      }

      this.minimapSettings.getClass();
      if (abs == 3) {
         this.minimapWaypointsSortbydistance.displayString = str + " " + Translations.translate("minimap.waypoints.sortbydistance") + " " + str;
      } else {
         this.minimapWaypointsSortbydistance.displayString = Translations.translate("minimap.waypoints.sortbydistance");
      }

      this.minimapSettings.getClass();
      if (abs == 1) {
         this.minimapWaypointsSortbycreated.displayString = str + " " + Translations.translate("minimap.waypoints.sortbycreated") + " " + str;
      } else {
         this.minimapWaypointsSortbycreated.displayString = Translations.translate("minimap.waypoints.sortbycreated");
      }

      this.minimapSettings.getClass();
      if (abs == 4) {
         this.minimapWaypointsSortbycolor.displayString = str + " " + Translations.translate("minimap.waypoints.sortbycolor") + " " + str;
      } else {
         this.minimapWaypointsSortbycolor.displayString = Translations.translate("minimap.waypoints.sortbycolor");
      }
   }

   @Override
   public void onGuiClosed() {
      if (this.changedSort) {
         super.onGuiClosed();
      }
   }

   @Override
   public void confirmClicked(boolean flag, int n) {
      if (this.deleteClicked) {
         this.deleteClicked = false;
         if (flag) {
            this.waypointManager.deleteWaypoint(this.selectedWaypoint);
            this.selectedWaypoint = null;
         }

         this.getMc().displayGuiScreen(this);
      }

      if (this.editClicked) {
         this.editClicked = false;
         if (flag) {
            this.waypointManager.saveWaypoints();
         }

         this.getMc().displayGuiScreen(this);
      }

      if (this.newClicked) {
         this.newClicked = false;
         if (flag) {
            this.waypointManager.addWaypoint(this.newWaypoint);
            this.setSelectedWaypoint(this.newWaypoint);
         }

         this.getMc().displayGuiScreen(this);
      }
   }

   public void editWaypoint(Waypoint waypoint) {
      this.editClicked = true;
      this.getMc().displayGuiScreen(new WaypointEditScreen(this.voxelMap, this, waypoint));
   }

   public static GuiButton getEditButton(WaypointsScreen waypointsScreen) {
      return waypointsScreen.selectserverEdit;
   }

   public void toggleSelectedWaypoint() {
      this.selectedWaypoint.enabled = !this.selectedWaypoint.enabled;
      this.waypointManager.saveWaypoints();
   }
}
