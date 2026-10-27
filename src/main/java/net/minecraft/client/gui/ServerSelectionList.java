package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.ui.screen.ServerListEntry;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector.LanServer;

public class ServerSelectionList extends GuiListExtended {
   public List serverListInternet = Lists.newArrayList();
   public List<ServerListEntryLanDetected> serverListLan = Lists.newArrayList();
   public GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
   public int selectedSlotIndex = -1;
   public GuiMultiplayer owner;

   public ServerSelectionList(GuiMultiplayer ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
      super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.owner = ownerIn;
   }

   @Override
   public GuiListExtended.IGuiListEntry getListEntry(int index) {
      if (index < this.serverListInternet.size()) {
         return (GuiListExtended.IGuiListEntry)this.serverListInternet.get(index);
      }

      index -= this.serverListInternet.size();
      return index == 0 ? this.lanScanEntry : this.serverListLan.get(--index);
   }

   @Override
   public int getSize() {
      return this.serverListInternet.size() + 1 + this.serverListLan.size();
   }

   public void setSelectedSlotIndex(int selectedSlotIndexIn) {
      this.selectedSlotIndex = selectedSlotIndexIn;
   }

   @Override
   public boolean isSelected(int slotIndex) {
      return slotIndex == this.selectedSlotIndex;
   }

   public int getSelected() {
      return this.selectedSlotIndex;
   }

   public void setServers(ServerList serverList) {
      this.serverListInternet.clear();

      for (int i = 0; i < serverList.countServers(); i++) {
         ServerData serverData = serverList.getServerData(i);
         if (serverData.flag) {
            this.serverListInternet.add(new ServerListEntry(this.owner, serverData));
         } else if (!CheatBreaker.getInstance().getKeybindSettings().getFeaturedServers().stream().anyMatch(strs -> strs[1].equalsIgnoreCase(serverData.serverIP))) {
            this.serverListInternet.add(new ServerListEntryNormal(this.owner, serverData));
         }
      }
   }

   public void addServers(List<LanServer> list) {
      this.serverListLan.clear();

      for (LanServer lanServer : list) {
         this.serverListLan.add(new ServerListEntryLanDetected(this.owner, lanServer));
      }
   }

   @Override
   public int getScrollBarX() {
      return super.getScrollBarX() + 30;
   }

   @Override
   public int getListWidth() {
      return super.getListWidth() + 85;
   }
}
