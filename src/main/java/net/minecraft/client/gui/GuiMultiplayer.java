package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.GuiScreenServerDataEvent;
import com.cheatbreaker.client.event.ServerDataEvent1;
import com.cheatbreaker.client.event.ServerDataEvent2;
import com.cheatbreaker.client.ui.screen.ClassicMainMenuScreen;
import com.cheatbreaker.client.ui.screen.ServerListEntry;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector.LanServer;
import net.minecraft.client.network.LanServerDetector.LanServerList;
import net.minecraft.client.network.LanServerDetector.ThreadLanServerFind;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiMultiplayer extends GuiScreen implements GuiYesNoCallback {
   public static Logger logger = LogManager.getLogger();
   public OldServerPinger oldServerPinger = new OldServerPinger();
   public GuiScreen parentScreen;
   public ServerSelectionList serverListSelector;
   public ServerList savedServerList;
   public GuiButton btnEditServer;
   public GuiButton btnSelectServer;
   public GuiButton btnDeleteServer;
   public boolean deletingServer;
   public boolean addingServer;
   public boolean editingServer;
   public boolean directConnect;
   public String hoveringText;
   public ServerData selectedServer;
   public LanServerList lanServerList;
   public ThreadLanServerFind lanServerDetector;
   public boolean initialized;

   public GuiMultiplayer(GuiScreen parentScreen) {
      this.parentScreen = parentScreen;
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      if (!this.initialized) {
         this.initialized = true;
         this.savedServerList = new ServerList(this.mc);
         this.savedServerList.loadServerList();
         this.lanServerList = new LanServerList();

         try {
            this.lanServerDetector = new ThreadLanServerFind(this.lanServerList);
            this.lanServerDetector.start();
         } catch (Exception e) {
            logger.warn("Unable to start LAN server detection: " + e.getMessage());
         }

         this.serverListSelector = new ServerSelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
         this.serverListSelector.setServers(this.savedServerList);
      } else {
         this.serverListSelector.setDimensions(this.width, this.height, 32, this.height - 64);
      }

      this.createButtons();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.serverListSelector.handleMouseInput();
   }

   public void createButtons() {
      this.buttonList.add(this.btnEditServer = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit")));
      this.buttonList.add(this.btnDeleteServer = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete")));
      this.buttonList.add(this.btnSelectServer = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select")));
      this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct")));
      this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add")));
      this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh")));
      this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel")));
      this.selectServer(this.serverListSelector.getSelected());
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (this.lanServerList.getWasUpdated()) {
         List lanServers = this.lanServerList.getLanServers();
         this.lanServerList.setWasNotUpdated();
         this.serverListSelector.addServers(lanServers);
      }

      this.oldServerPinger.pingPendingNetworks();
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      if (this.lanServerDetector != null) {
         this.lanServerDetector.interrupt();
         this.lanServerDetector = null;
      }

      this.oldServerPinger.clearPendingNetworks();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         GuiListExtended.IGuiListEntry guiListEntry = this.serverListSelector.getSelected() < 0
            ? null
            : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());
         if (button.id == 2 && guiListEntry instanceof ServerListEntryNormal) {
            String serverName = ((ServerListEntryNormal)guiListEntry).getServerData().serverName;
            if (serverName != null) {
               this.deletingServer = true;
               String format = I18n.format("selectServer.deleteQuestion");
               String str = "'" + serverName + "' " + I18n.format("selectServer.deleteWarning");
               String format2 = I18n.format("selectServer.deleteButton");
               String format3 = I18n.format("gui.cancel");
               GuiYesNo guiYesNo = new GuiYesNo(this, format, str, format2, format3, this.serverListSelector.getSelected());
               this.mc.displayGuiScreen(guiYesNo);
            }
         } else if (button.id == 1) {
            this.connectToSelected();
         } else if (button.id == 4) {
            this.directConnect = true;
            this.mc.displayGuiScreen(new GuiScreenServerList(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
         } else if (button.id == 3) {
            this.addingServer = true;
            this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
         } else if (button.id == 7 && guiListEntry instanceof ServerListEntryNormal) {
            this.editingServer = true;
            ServerData serverData = ((ServerListEntryNormal)guiListEntry).getServerData();
            this.selectedServer = new ServerData(serverData.serverName, serverData.serverIP, false);
            this.selectedServer.copyFrom(serverData);
            this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
         } else if (button.id == 0) {
            if (!this.mc.isSingleplayer() && (this.mc.isIntegratedServerRunning() || this.mc.theWorld == null)) {
               this.mc.displayGuiScreen(new ClassicMainMenuScreen());
            } else {
               this.mc.displayGuiScreen(this.parentScreen);
            }
         } else if (button.id == 8) {
            this.refreshServerList();
         }
      }
   }

   public void refreshServerList() {
      this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      GuiListExtended.IGuiListEntry guiListEntry = this.serverListSelector.getSelected() < 0
         ? null
         : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());
      if (this.deletingServer) {
         this.deletingServer = false;
         if (result && guiListEntry instanceof ServerListEntryNormal) {
            this.savedServerList.removeServerData(this.serverListSelector.getSelected());
            this.savedServerList.saveServerList();
            this.serverListSelector.setSelectedSlotIndex(-1);
            this.serverListSelector.setServers(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      } else if (this.directConnect) {
         this.directConnect = false;
         if (result) {
            this.connectToServer(this.selectedServer);
         } else {
            this.mc.displayGuiScreen(this);
         }
      } else if (this.addingServer) {
         this.addingServer = false;
         if (result) {
            this.savedServerList.addServerData(this.selectedServer);
            this.savedServerList.saveServerList();
            this.serverListSelector.setSelectedSlotIndex(-1);
            this.serverListSelector.setServers(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      } else if (this.editingServer) {
         this.editingServer = false;
         if (result && (guiListEntry instanceof ServerListEntryNormal || guiListEntry instanceof ServerListEntry)) {
            ServerData serverData = ((ServerListEntryNormal)guiListEntry).getServerData();
            serverData.serverName = this.selectedServer.serverName;
            serverData.serverIP = this.selectedServer.serverIP;
            serverData.copyFrom(this.selectedServer);
            this.savedServerList.saveServerList();
            this.serverListSelector.setServers(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      int selected = this.serverListSelector.getSelected();
      GuiListExtended.IGuiListEntry guiListEntry = selected < 0 ? null : this.serverListSelector.getListEntry(selected);
      if (keyCode == 63) {
         this.refreshServerList();
      } else if (selected >= 0) {
         if (keyCode == 200) {
            if (isShiftKeyDown()) {
               if (selected > 0 && guiListEntry instanceof ServerListEntryNormal) {
                  this.savedServerList.swapServers(selected, selected - 1);
                  this.selectServer(this.serverListSelector.getSelected() - 1);
                  this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                  this.serverListSelector.setServers(this.savedServerList);
               }
            } else if (selected > 0) {
               this.selectServer(this.serverListSelector.getSelected() - 1);
               this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
               if (this.serverListSelector.getListEntry(this.serverListSelector.getSelected()) instanceof ServerListEntryLanScan) {
                  if (this.serverListSelector.getSelected() > 0) {
                     this.selectServer(this.serverListSelector.getSize() - 1);
                     this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                  } else {
                     this.selectServer(-1);
                  }
               }
            } else {
               this.selectServer(-1);
            }
         } else if (keyCode == 208) {
            if (isShiftKeyDown()) {
               if (selected < this.savedServerList.countServers() - 1) {
                  this.savedServerList.swapServers(selected, selected + 1);
                  this.selectServer(selected + 1);
                  this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                  this.serverListSelector.setServers(this.savedServerList);
               }
            } else if (selected < this.serverListSelector.getSize()) {
               this.selectServer(this.serverListSelector.getSelected() + 1);
               this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
               if (this.serverListSelector.getListEntry(this.serverListSelector.getSelected()) instanceof ServerListEntryLanScan) {
                  if (this.serverListSelector.getSelected() < this.serverListSelector.getSize() - 1) {
                     this.selectServer(this.serverListSelector.getSize() + 1);
                     this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                  } else {
                     this.selectServer(-1);
                  }
               }
            } else {
               this.selectServer(-1);
            }
         } else if (keyCode != 28 && keyCode != 156) {
            super.keyTyped(typedChar, keyCode);
         } else {
            this.actionPerformed(this.buttonList.get(2));
         }
      } else {
         super.keyTyped(typedChar, keyCode);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.hoveringText = null;
      this.drawDefaultBackground();
      this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.title"), this.width / 2, 20, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.hoveringText != null) {
         this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
      }
   }

   public void connectToSelected() {
      GuiListExtended.IGuiListEntry guiListEntry = this.serverListSelector.getSelected() < 0
         ? null
         : this.serverListSelector.getListEntry(this.serverListSelector.getSelected());
      if (guiListEntry instanceof ServerListEntryNormal) {
         this.connectToServer(((ServerListEntryNormal)guiListEntry).getServerData());
      } else if (guiListEntry instanceof ServerListEntry) {
         this.connectToServer(((ServerListEntry)guiListEntry).getServerData());
      } else if (guiListEntry instanceof ServerListEntryLanDetected) {
         LanServer lanServer = ((ServerListEntryLanDetected)guiListEntry).getLanServer();
         this.connectToServer(new ServerData(lanServer.getServerMotd(), lanServer.getServerIpPort(), true));
      }
   }

   public void connectToServer(ServerData server) {
      ServerDataEvent2 serverDataEvent2 = new ServerDataEvent2(server);
      CheatBreaker.getInstance().getEventBus().handleEvent(serverDataEvent2);
      if (!serverDataEvent2.isFlag()) {
         if (this.mc.getCurrentServerData() != null && this.mc.theWorld != null) {
            Minecraft.getMinecraft().ingameGUI.setDisplayedTitle("");
            Minecraft.getMinecraft().ingameGUI.setDisplayedSubTitle("");
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
         }

         GuiScreenServerDataEvent guiScreenServerDataEvent = new GuiScreenServerDataEvent(server, this);
         CheatBreaker.getInstance().getEventBus().handleEvent(guiScreenServerDataEvent);
         if (!guiScreenServerDataEvent.isFlag()) {
            this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, server));
            CheatBreaker.getInstance().getEventBus().handleEvent(new ServerDataEvent1(server));
         }
      }
   }

   public void selectServer(int index) {
      this.serverListSelector.setSelectedSlotIndex(index);
      GuiListExtended.IGuiListEntry guiListEntry = index < 0 ? null : this.serverListSelector.getListEntry(index);
      this.btnSelectServer.enabled = false;
      this.btnEditServer.enabled = false;
      this.btnDeleteServer.enabled = false;
      if (guiListEntry != null && !(guiListEntry instanceof ServerListEntryLanScan)) {
         this.btnSelectServer.enabled = true;
         if (guiListEntry instanceof ServerListEntryNormal) {
            this.btnEditServer.enabled = true;
            this.btnDeleteServer.enabled = true;
         }
      }
   }

   public OldServerPinger getOldServerPinger() {
      return this.oldServerPinger;
   }

   public void setHoveringText(String str) {
      this.hoveringText = str;
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      this.serverListSelector.mouseReleased(mouseX, mouseY, state);
   }

   public ServerList getServerList() {
      return this.savedServerList;
   }

   public boolean canSortUp(ServerListEntryNormal serverListEntryNormal, int n) {
      return n > 0;
   }

   public boolean canSortDown(ServerListEntryNormal serverListEntryNormal, int n) {
      return n < this.savedServerList.countServers() - 1;
   }

   public void sortUp(ServerListEntryNormal serverListEntryNormal, int n, boolean flag) {
      int n2 = flag ? 0 : n - 1;
      this.savedServerList.swapServers(n, n2);
      if (this.serverListSelector.getSelected() == n) {
         this.selectServer(n2);
      }

      this.serverListSelector.setServers(this.savedServerList);
   }

   public void sortDown(ServerListEntryNormal serverListEntryNormal, int n, boolean flag) {
      int n2 = flag ? this.savedServerList.countServers() - 1 : n + 1;
      this.savedServerList.swapServers(n, n2);
      if (this.serverListSelector.getSelected() == n) {
         this.selectServer(n2);
      }

      this.serverListSelector.setServers(this.savedServerList);
   }
}
